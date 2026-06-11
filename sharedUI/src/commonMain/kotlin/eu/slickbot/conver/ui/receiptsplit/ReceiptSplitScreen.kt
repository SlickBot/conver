package eu.slickbot.conver.ui.receiptsplit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.fixed
import eu.slickbot.conver.ui.components.ConverScaffold
import eu.slickbot.conver.ui.components.scaffoldBodyPadding
import eu.slickbot.conver.ui.icons.accent
import eu.slickbot.conver.ui.util.rememberClipboardCopy
import org.koin.compose.viewmodel.koinViewModel

private val accent = Category.Money.accent()

/** Shared height so person pills and the Add button stay the same size in every state. */
private val PillHeight = 36.dp

@Composable
fun ReceiptSplitScreen(
  onBack: (() -> Unit)? = null,
  viewModel: ReceiptSplitViewModel = koinViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  LaunchedEffect(state.totalInput, state.items, state.splitMode) {
    if (state.totals.isNotEmpty()) viewModel.recordInHistory()
  }
  ReceiptSplitScreenContent(
    state = state,
    onBack = onBack,
    onSplitModeChange = viewModel::setSplitMode,
    onTotalChange = viewModel::setTotal,
    onTaxChange = viewModel::setTaxPercent,
    onServiceChange = viewModel::setServicePercent,
    onAddPerson = viewModel::addPerson,
    onRemovePerson = viewModel::removePerson,
    onRenamePerson = viewModel::renamePerson,
    onTogglePersonSelected = viewModel::togglePersonSelected,
    onSetShare = viewModel::setShare,
    onAddItem = viewModel::addItem,
    onRemoveItem = viewModel::removeItem,
    onUpdateItemName = viewModel::updateItemName,
    onUpdateItemPrice = viewModel::updateItemPrice,
    onToggleItemAssignment = viewModel::toggleItemAssignment,
    onToggleFavorite = viewModel::toggleFavorite,
  )
}

@Composable
fun ReceiptSplitScreenContent(
  state: ReceiptSplitUiState,
  onSplitModeChange: (SplitMode) -> Unit,
  onTotalChange: (String) -> Unit,
  onTaxChange: (String) -> Unit,
  onServiceChange: (String) -> Unit,
  onAddPerson: () -> Unit,
  onRemovePerson: (String) -> Unit,
  onRenamePerson: (String, String) -> Unit,
  onTogglePersonSelected: (String) -> Unit,
  onSetShare: (String, Int) -> Unit,
  onAddItem: () -> Unit,
  onRemoveItem: (String) -> Unit,
  onUpdateItemName: (String, String) -> Unit,
  onUpdateItemPrice: (String, String) -> Unit,
  onToggleItemAssignment: (String, String) -> Unit,
  onToggleFavorite: () -> Unit,
  modifier: Modifier = Modifier,
  onBack: (() -> Unit)? = null,
) {
  val haptic = LocalHapticFeedback.current
  val copyToClipboard = rememberClipboardCopy()

  ConverScaffold(
    title = "Receipt split",
    modifier = modifier,
    navigationIcon = {
      if (onBack != null) {
        IconButton(onClick = onBack) {
          Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
        }
      }
    },
    actions = {
      IconButton(onClick = onToggleFavorite) {
        if (state.isFavorite) {
          Icon(Icons.Outlined.Star, "Remove from favorites", tint = accent)
        } else {
          Icon(Icons.Outlined.StarOutline, "Add to favorites")
        }
      }
    },
  ) { padding ->
    Column(
      modifier = Modifier
        .scaffoldBodyPadding(padding)
        .verticalScroll(rememberScrollState()),
    ) {
      // -- Split mode tabs --
      SplitModeTabs(state.splitMode, onSplitModeChange)

      Spacer(Modifier.height(12.dp))

      // -- People row --
      PeopleSection(
        people = state.people,
        shares = state.shares,
        excluded = state.excluded,
        showShares = state.splitMode == SplitMode.SHARES,
        onAdd = onAddPerson,
        onRemove = onRemovePerson,
        onRename = onRenamePerson,
        onToggleSelected = onTogglePersonSelected,
        onSetShare = onSetShare,
      )

      Spacer(Modifier.height(16.dp))

      // -- Total / Items --
      AnimatedVisibility(
        visible = state.splitMode != SplitMode.ITEMS,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
      ) {
        TotalField(state.totalInput, onTotalChange)
      }

      AnimatedVisibility(
        visible = state.splitMode == SplitMode.ITEMS,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
      ) {
        ItemsSection(
          items = state.items,
          people = state.people,
          onAddItem = onAddItem,
          onRemoveItem = onRemoveItem,
          onUpdateName = onUpdateItemName,
          onUpdatePrice = onUpdateItemPrice,
          onToggleAssignment = onToggleItemAssignment,
        )
      }

      // -- Tax & Service --
      TaxServiceRow(
        tax = state.taxPercent,
        service = state.servicePercent,
        onTaxChange = onTaxChange,
        onServiceChange = onServiceChange,
      )

      Spacer(Modifier.height(16.dp))

      // -- Result panel --
      ResultPanel(
        totals = state.totals,
        grandTotal = state.grandTotal,
        onCopy = {
          val text = state.totals.joinToString("\n") {
            "${it.person.name}: ${fmt(it.amount)}"
          }
          copyToClipboard(text)
          haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        },
      )

      Spacer(Modifier.height(24.dp))
    }
  }
}

// -- Split mode tabs ---------------------------------------------------------

@Composable
private fun SplitModeTabs(current: SplitMode, onChange: (SplitMode) -> Unit) {
  FlowRow(
    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(0.dp),
  ) {
    SplitMode.entries.forEach { mode ->
      FilterChip(
        selected = mode == current,
        onClick = { onChange(mode) },
        label = { Text(mode.label) },
      )
    }
  }
}

// -- People section ----------------------------------------------------------

@Composable
private fun PeopleSection(
  people: List<Person>,
  shares: Map<String, Int>,
  excluded: Set<String>,
  showShares: Boolean,
  onAdd: () -> Unit,
  onRemove: (String) -> Unit,
  onRename: (String, String) -> Unit,
  onToggleSelected: (String) -> Unit,
  onSetShare: (String, Int) -> Unit,
) {
  Column(modifier = Modifier.padding(horizontal = 24.dp)) {
    Text(
      "People",
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    )
    Spacer(Modifier.height(8.dp))
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      itemVerticalAlignment = Alignment.CenterVertically,
    ) {
      people.forEach { person ->
        PersonChip(
          person = person,
          shareWeight = if (showShares) (shares[person.id] ?: 1) else null,
          selected = person.id !in excluded,
          canRemove = people.size > 1,
          onToggleSelected = { onToggleSelected(person.id) },
          onRemove = { onRemove(person.id) },
          onRename = { onRename(person.id, it) },
          onShareChange = { onSetShare(person.id, it) },
        )
      }
      // Add person button
      Surface(
        onClick = onAdd,
        shape = RoundedCornerShape(20.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.2f)),
        modifier = Modifier.height(PillHeight),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            Icons.Outlined.PersonAdd, "Add person",
            modifier = Modifier.size(16.dp),
            tint = accent,
          )
          Spacer(Modifier.width(4.dp))
          Text("Add", style = MaterialTheme.typography.labelMedium, color = accent)
        }
      }
    }
  }
}

@Composable
private fun PersonChip(
  person: Person,
  shareWeight: Int?,
  selected: Boolean,
  canRemove: Boolean,
  onToggleSelected: () -> Unit,
  onRemove: () -> Unit,
  onRename: (String) -> Unit,
  onShareChange: (Int) -> Unit,
) {
  var editing by remember { mutableStateOf(false) }
  var editText by remember(person.name) {
    mutableStateOf(TextFieldValue(person.name, TextRange(person.name.length)))
  }
  val shape = RoundedCornerShape(20.dp)
  val contentColor = if (selected) accent else accent.copy(alpha = 0.4f)
  val focusRequester = remember { FocusRequester() }

  Surface(
    shape = shape,
    color = accent.copy(alpha = if (selected) 0.12f else 0.04f),
    border = BorderStroke(1.dp, accent.copy(alpha = if (selected) 0.25f else 0.12f)),
    modifier = Modifier
      .height(PillHeight)
      .clip(shape)
      .then(
        // Tap toggles in/out of the split; long-press renames.
        if (editing) Modifier
        else Modifier.combinedClickable(
          onClick = onToggleSelected,
          onLongClick = {
            editText = editText.copy(selection = TextRange(editText.text.length))
            editing = true
          },
        ),
      ),
  ) {
    Row(
      modifier = Modifier.padding(start = 12.dp, end = if (canRemove || editing) 4.dp else 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (editing) {
        val confirmRename = {
          if (editText.text.isNotBlank()) onRename(editText.text.trim())
          editing = false
        }
        // Focus the field and raise the keyboard so it's clear edit mode is active.
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
        // Size the field to its text so the pill hugs the name instead of a fixed width.
        Box(modifier = Modifier.widthIn(min = 24.dp), contentAlignment = Alignment.CenterStart) {
          Text(
            editText.text.ifEmpty { " " },
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.alpha(0f),
          )
          BasicTextField(
            value = editText,
            onValueChange = { editText = it },
            singleLine = true,
            textStyle = MaterialTheme.typography.labelLarge.copy(color = accent),
            cursorBrush = SolidColor(accent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { confirmRename() }),
            modifier = Modifier
              .matchParentSize()
              .focusRequester(focusRequester),
          )
        }
        IconButton(
          onClick = confirmRename,
          modifier = Modifier.size(24.dp),
        ) {
          Icon(
            Icons.Outlined.Check, "Done",
            modifier = Modifier.size(16.dp),
            tint = accent,
          )
        }
      } else {
        Text(
          person.name,
          style = MaterialTheme.typography.labelLarge,
          color = contentColor,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        if (selected && shareWeight != null) {
          Spacer(Modifier.width(6.dp))
          ShareStepper(weight = shareWeight, onChange = onShareChange)
        }
        if (canRemove) {
          IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp),
          ) {
            Icon(
              Icons.Outlined.Close, "Remove",
              modifier = Modifier.size(14.dp),
              tint = contentColor.copy(alpha = 0.5f),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ShareStepper(weight: Int, onChange: (Int) -> Unit) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = accent.copy(alpha = 0.15f),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 2.dp),
    ) {
      Surface(
        onClick = { if (weight > 1) onChange(weight - 1) },
        shape = CircleShape,
        color = Color.Transparent,
        modifier = Modifier.size(22.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text("−", style = MaterialTheme.typography.labelMedium, color = accent)
        }
      }
      Text(
        "${weight}×",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = accent,
        modifier = Modifier.padding(horizontal = 4.dp),
      )
      Surface(
        onClick = { onChange(weight + 1) },
        shape = CircleShape,
        color = Color.Transparent,
        modifier = Modifier.size(22.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text("+", style = MaterialTheme.typography.labelMedium, color = accent)
        }
      }
    }
  }
}

// -- Total field -------------------------------------------------------------

@Composable
private fun TotalField(value: String, onChange: (String) -> Unit) {
  Column(modifier = Modifier.padding(horizontal = 24.dp)) {
    Text(
      "Total bill",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    )
    Spacer(Modifier.height(4.dp))
    BasicTextField(
      value = value,
      onValueChange = onChange,
      singleLine = true,
      textStyle = MaterialTheme.typography.headlineMedium.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
      ),
      cursorBrush = SolidColor(accent),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
      modifier = Modifier.fillMaxWidth(),
      decorationBox = { inner ->
        Box {
          if (value.isEmpty()) {
            Text(
              "0.00",
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
              fontWeight = FontWeight.Bold,
            )
          }
          inner()
        }
      },
    )
    Spacer(Modifier.height(6.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f)),
    )
    Spacer(Modifier.height(12.dp))
  }
}

// -- Items section -----------------------------------------------------------

@Composable
private fun ItemsSection(
  items: List<ReceiptItem>,
  people: List<Person>,
  onAddItem: () -> Unit,
  onRemoveItem: (String) -> Unit,
  onUpdateName: (String, String) -> Unit,
  onUpdatePrice: (String, String) -> Unit,
  onToggleAssignment: (String, String) -> Unit,
) {
  Column(modifier = Modifier.padding(horizontal = 24.dp)) {
    Text(
      "Items",
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    )
    Spacer(Modifier.height(8.dp))

    items.forEach { item ->
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Item name
            BasicTextField(
              value = item.name,
              onValueChange = { onUpdateName(item.id, it) },
              singleLine = true,
              textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
              ),
              cursorBrush = SolidColor(accent),
              modifier = Modifier.weight(1f),
              decorationBox = { inner ->
                Box {
                  if (item.name.isEmpty()) {
                    Text(
                      "Item name",
                      style = MaterialTheme.typography.bodyLarge,
                      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    )
                  }
                  inner()
                }
              },
            )
            Spacer(Modifier.width(8.dp))
            // Item price
            BasicTextField(
              value = item.price,
              onValueChange = { onUpdatePrice(item.id, it) },
              singleLine = true,
              textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = accent,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
              ),
              cursorBrush = SolidColor(accent),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier.width(80.dp),
              decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterEnd) {
                  if (item.price.isEmpty()) {
                    Text(
                      "0.00",
                      style = MaterialTheme.typography.bodyLarge,
                      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                      fontWeight = FontWeight.SemiBold,
                      textAlign = TextAlign.End,
                      modifier = Modifier.fillMaxWidth(),
                    )
                  }
                  inner()
                }
              },
            )
            if (items.size > 1) {
              IconButton(
                onClick = { onRemoveItem(item.id) },
                modifier = Modifier.size(28.dp),
              ) {
                Icon(
                  Icons.Outlined.Close, "Remove",
                  modifier = Modifier.size(14.dp),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                )
              }
            }
          }
          // Person assignment dots
          Spacer(Modifier.height(6.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            people.forEach { person ->
              val assigned = person.id in item.assignedTo
              FilterChip(
                selected = assigned,
                onClick = { onToggleAssignment(item.id, person.id) },
                label = {
                  Text(
                    person.name,
                    style = MaterialTheme.typography.labelSmall,
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = accent.copy(alpha = 0.2f),
                  selectedLabelColor = accent,
                ),
                border = FilterChipDefaults.filterChipBorder(
                  enabled = true,
                  selected = assigned,
                  borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                  selectedBorderColor = accent.copy(alpha = 0.4f),
                ),
                modifier = Modifier.height(28.dp),
              )
            }
          }
        }
      }
    }

    // Add item button
    Surface(
      onClick = onAddItem,
      shape = RoundedCornerShape(14.dp),
      color = Color.Transparent,
      border = BorderStroke(1.dp, accent.copy(alpha = 0.2f)),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          Icons.Outlined.Add, "Add item",
          modifier = Modifier.size(16.dp),
          tint = accent.copy(alpha = 0.6f),
        )
        Spacer(Modifier.width(4.dp))
        Text(
          "Add item",
          style = MaterialTheme.typography.labelLarge,
          color = accent.copy(alpha = 0.6f),
        )
      }
    }
    Spacer(Modifier.height(12.dp))
  }
}

// -- Tax & Service -----------------------------------------------------------

@Composable
private fun TaxServiceRow(
  tax: String,
  service: String,
  onTaxChange: (String) -> Unit,
  onServiceChange: (String) -> Unit,
) {
  Row(
    modifier = Modifier.padding(horizontal = 24.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    CompactField("Tax %", tax, onTaxChange, Modifier.weight(1f))
    CompactField("Service %", service, onServiceChange, Modifier.weight(1f))
  }
}

@Composable
private fun CompactField(
  label: String,
  value: String,
  onChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Text(
      label,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    )
    Spacer(Modifier.height(4.dp))
    BasicTextField(
      value = value,
      onValueChange = onChange,
      singleLine = true,
      textStyle = MaterialTheme.typography.titleMedium.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Medium,
      ),
      cursorBrush = SolidColor(accent),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
      modifier = Modifier.fillMaxWidth(),
      decorationBox = { inner ->
        Box {
          if (value.isEmpty()) {
            Text(
              "0",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
              fontWeight = FontWeight.Medium,
            )
          }
          inner()
        }
      },
    )
    Spacer(Modifier.height(4.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f)),
    )
  }
}

// -- Result panel ------------------------------------------------------------

@Composable
private fun ResultPanel(
  totals: List<PersonTotal>,
  grandTotal: Double,
  onCopy: () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = accent.copy(alpha = 0.08f),
    border = BorderStroke(1.dp, accent.copy(alpha = 0.12f)),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          "Who pays what",
          style = MaterialTheme.typography.labelLarge,
          color = accent.copy(alpha = 0.7f),
          modifier = Modifier.weight(1f),
        )
        if (totals.isNotEmpty()) {
          IconButton(
            onClick = onCopy,
            modifier = Modifier.size(32.dp),
          ) {
            Icon(
              Icons.Outlined.ContentCopy, "Copy",
              modifier = Modifier.size(18.dp),
              tint = accent.copy(alpha = 0.4f),
            )
          }
        }
      }
      Spacer(Modifier.height(8.dp))
      if (totals.isEmpty()) {
        Text(
          "Enter an amount to split",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        )
      } else {
        totals.forEach { pt ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              pt.person.name,
              style = MaterialTheme.typography.bodyLarge,
              color = accent.copy(alpha = 0.7f),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f),
            )
            Text(
              fmt(pt.amount),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = accent,
            )
          }
        }
        // Grand total divider + total
        Spacer(Modifier.height(4.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(accent.copy(alpha = 0.15f)),
        )
        Spacer(Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            "Total",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = accent.copy(alpha = 0.8f),
          )
          Text(
            fmt(grandTotal),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = accent,
          )
        }
      }
    }
  }
}

private fun fmt(n: Double): String = fixed(n, 2)
