package eu.slickbot.conver.ui.receiptsplit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.slickbot.conver.data.favorites.FavoritesRepository
import eu.slickbot.conver.data.favorites.HistoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

// ── Data models ──────────────────────────────────────────────────────────────

data class Person(val id: String = UUID.randomUUID().toString(), val name: String)

data class ReceiptItem(
  val id: String = UUID.randomUUID().toString(),
  val name: String = "",
  val price: String = "",
  val assignedTo: Set<String> = emptySet(), // person IDs
)

enum class SplitMode(val label: String) {
  EQUAL("Equal"),
  SHARES("By shares"),
  ITEMS("By items"),
}

data class PersonTotal(val person: Person, val amount: Double)

// ── UI state ─────────────────────────────────────────────────────────────────

data class ReceiptSplitUiState(
  val splitMode: SplitMode = SplitMode.EQUAL,
  val people: List<Person> = listOf(Person(name = "You"), Person(name = "Person 2")),
  val totalInput: String = "",
  val taxPercent: String = "",
  val servicePercent: String = "",
  val items: List<ReceiptItem> = listOf(ReceiptItem()),
  val shares: Map<String, Int> = emptyMap(), // personId → share weight
  val excluded: Set<String> = emptySet(), // personIds left out of the split
  val isFavorite: Boolean = false,
) {

  /** People actually included in the split (deselected ones are left out). */
  val activePeople: List<Person>
    get() = people.filterNot { it.id in excluded }

  val totals: List<PersonTotal>
    get() = when (splitMode) {
      SplitMode.EQUAL -> computeEqual()
      SplitMode.SHARES -> computeShares()
      SplitMode.ITEMS -> computeItems()
    }

  val grandTotal: Double
    get() {
      val base = when (splitMode) {
        SplitMode.ITEMS -> items.sumOf { it.price.toDoubleOrNull() ?: 0.0 }
        else -> totalInput.toDoubleOrNull() ?: 0.0
      }
      return applyExtras(base)
    }

  private fun applyExtras(base: Double): Double {
    val tax = base * (taxPercent.toDoubleOrNull() ?: 0.0) / 100.0
    val service = base * (servicePercent.toDoubleOrNull() ?: 0.0) / 100.0
    return base + tax + service
  }

  private fun computeEqual(): List<PersonTotal> {
    val active = activePeople
    if (active.isEmpty()) return emptyList()
    val base = totalInput.toDoubleOrNull() ?: return emptyList()
    if (base == 0.0) return emptyList()
    val total = applyExtras(base)
    val perPerson = total / active.size
    return active.map { PersonTotal(it, perPerson) }
  }

  private fun computeShares(): List<PersonTotal> {
    val active = activePeople
    if (active.isEmpty()) return emptyList()
    val base = totalInput.toDoubleOrNull() ?: return emptyList()
    if (base == 0.0) return emptyList()
    val total = applyExtras(base)
    val totalShares = active.sumOf { shares[it.id] ?: 1 }
    if (totalShares == 0) return emptyList()
    val perShare = total / totalShares
    return active.map { PersonTotal(it, perShare * (shares[it.id] ?: 1)) }
  }

  private fun computeItems(): List<PersonTotal> {
    val active = activePeople
    if (active.isEmpty()) return emptyList()
    val perPerson = mutableMapOf<String, Double>()
    active.forEach { perPerson[it.id] = 0.0 }
    var subtotal = 0.0
    for (item in items) {
      val price = item.price.toDoubleOrNull() ?: continue
      subtotal += price
      val assigned = item.assignedTo.filter { id -> active.any { it.id == id } }
      if (assigned.isEmpty()) {
        // Unassigned items split equally among the active people
        val share = price / active.size
        active.forEach { perPerson[it.id] = (perPerson[it.id] ?: 0.0) + share }
      } else {
        val share = price / assigned.size
        assigned.forEach { perPerson[it] = (perPerson[it] ?: 0.0) + share }
      }
    }
    if (subtotal == 0.0) return emptyList()
    // Distribute tax/service proportionally
    val multiplier = applyExtras(subtotal) / subtotal
    return active.map { PersonTotal(it, (perPerson[it.id] ?: 0.0) * multiplier) }
  }
}

// ── ViewModel ────────────────────────────────────────────────────────────────

class ReceiptSplitViewModel(
  private val favoritesRepo: FavoritesRepository,
) : ViewModel() {

  companion object {
    private val NUM = Regex("^-?\\d*(\\.\\d*)?$")
    private fun fmt(n: Double) = String.format(Locale.US, "%.2f", n)
  }

  private val splitMode = MutableStateFlow(SplitMode.EQUAL)
  private val people = MutableStateFlow(
    listOf(Person(name = "You"), Person(name = "Person 2")),
  )
  private val totalInput = MutableStateFlow("")
  private val taxPercent = MutableStateFlow("")
  private val servicePercent = MutableStateFlow("")
  private val items = MutableStateFlow(listOf(ReceiptItem()))
  private val shares = MutableStateFlow<Map<String, Int>>(emptyMap())
  private val excluded = MutableStateFlow<Set<String>>(emptySet())

  val uiState: StateFlow<ReceiptSplitUiState> = combine(
    splitMode,
    people,
    totalInput,
    combine(taxPercent, servicePercent, items, shares, excluded) { t, s, i, sh, exc ->
      Quint(t, s, i, sh, exc)
    },
    favoritesRepo.observeIsFavorite("receipt-split"),
  ) { mode, ppl, total, (tax, service, itms, shr, exc), fav ->
    ReceiptSplitUiState(
      splitMode = mode,
      people = ppl,
      totalInput = total,
      taxPercent = tax,
      servicePercent = service,
      items = itms,
      shares = shr,
      excluded = exc,
      isFavorite = fav,
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    ReceiptSplitUiState(),
  )

  fun setSplitMode(mode: SplitMode) { splitMode.value = mode }

  fun setTotal(value: String) {
    if (value.isEmpty() || value.matches(NUM)) totalInput.value = value
  }

  fun setTaxPercent(value: String) {
    if (value.isEmpty() || value.matches(NUM)) taxPercent.value = value
  }

  fun setServicePercent(value: String) {
    if (value.isEmpty() || value.matches(NUM)) servicePercent.value = value
  }

  fun addPerson() {
    val current = people.value
    val n = current.size + 1
    people.value = current + Person(name = "Person $n")
  }

  fun removePerson(id: String) {
    if (people.value.size <= 1) return // minimum 1
    people.value = people.value.filter { it.id != id }
    // Clean up item assignments / share / selection state
    items.value = items.value.map { it.copy(assignedTo = it.assignedTo - id) }
    shares.value = shares.value - id
    excluded.value = excluded.value - id
  }

  fun renamePerson(id: String, name: String) {
    people.value = people.value.map { if (it.id == id) it.copy(name = name) else it }
  }

  /** Toggle whether a person is part of the split. Keeps at least one person selected. */
  fun togglePersonSelected(id: String) {
    val current = excluded.value
    if (id in current) {
      excluded.value = current - id
    } else {
      val stillSelected = people.value.count { it.id !in current && it.id != id }
      if (stillSelected == 0) return // can't deselect the last person
      excluded.value = current + id
    }
  }

  fun setShare(personId: String, weight: Int) {
    shares.value = shares.value + (personId to weight.coerceAtLeast(1))
  }

  fun addItem() {
    items.value = items.value + ReceiptItem()
  }

  fun removeItem(id: String) {
    val updated = items.value.filter { it.id != id }
    items.value = updated.ifEmpty { listOf(ReceiptItem()) }
  }

  fun updateItemName(id: String, name: String) {
    items.value = items.value.map { if (it.id == id) it.copy(name = name) else it }
  }

  fun updateItemPrice(id: String, price: String) {
    if (price.isNotEmpty() && !price.matches(NUM)) return
    items.value = items.value.map { if (it.id == id) it.copy(price = price) else it }
  }

  fun toggleItemAssignment(itemId: String, personId: String) {
    items.value = items.value.map { item ->
      if (item.id == itemId) {
        val newAssigned = if (personId in item.assignedTo) {
          item.assignedTo - personId
        } else {
          item.assignedTo + personId
        }
        item.copy(assignedTo = newAssigned)
      } else item
    }
  }

  fun toggleFavorite() {
    viewModelScope.launch {
      favoritesRepo.toggleFavorite("receipt-split", uiState.value.isFavorite)
    }
  }

  fun recordInHistory() {
    val state = uiState.value
    val totals = state.totals
    if (totals.isEmpty()) return
    viewModelScope.launch {
      favoritesRepo.recordConversion(
        HistoryEntity(
          converterId = "receipt-split",
          fromUnitId = state.splitMode.name,
          toUnitId = "",
          input = when (state.splitMode) {
            SplitMode.ITEMS -> state.items.filter { it.price.isNotEmpty() }
              .joinToString(", ") { "${it.name}: ${it.price}" }
            else -> state.totalInput
          },
          output = totals.joinToString(", ") { "${it.person.name}: ${fmt(it.amount)}" },
          at = System.currentTimeMillis(),
        ),
      )
    }
  }
}

private data class Quint<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
