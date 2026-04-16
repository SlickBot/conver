package eu.slickbot.conver.domain.converter.converters

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Savings
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.MeasureUnit
import eu.slickbot.conver.domain.converter.MeasurementConverter
import eu.slickbot.conver.domain.converter.TextConverter
import java.util.Locale
import kotlin.math.pow

// ---------- Currency (hardcoded seed rates — upgrade to live via Retrofit later) ----------------

/**
 * Rates relative to 1 USD (approximate snapshot). Once Retrofit + Room are wired in Phase 3,
 * this will be replaced by dynamically built units from the rates DB.
 */
private val currencyRates: Map<String, Pair<String, Double>> = mapOf(
  "USD" to ("US Dollar" to 1.0),
  "EUR" to ("Euro" to 0.92),
  "GBP" to ("British Pound" to 0.79),
  "JPY" to ("Japanese Yen" to 149.50),
  "CNY" to ("Chinese Yuan" to 7.24),
  "INR" to ("Indian Rupee" to 83.12),
  "CAD" to ("Canadian Dollar" to 1.36),
  "AUD" to ("Australian Dollar" to 1.53),
  "CHF" to ("Swiss Franc" to 0.88),
  "KRW" to ("South Korean Won" to 1330.0),
  "SEK" to ("Swedish Krona" to 10.42),
  "NOK" to ("Norwegian Krone" to 10.58),
  "DKK" to ("Danish Krone" to 6.87),
  "PLN" to ("Polish Zloty" to 3.97),
  "CZK" to ("Czech Koruna" to 23.10),
  "HUF" to ("Hungarian Forint" to 356.0),
  "RUB" to ("Russian Ruble" to 92.0),
  "BRL" to ("Brazilian Real" to 4.97),
  "MXN" to ("Mexican Peso" to 17.15),
  "ZAR" to ("South African Rand" to 18.65),
  "TRY" to ("Turkish Lira" to 30.25),
  "SGD" to ("Singapore Dollar" to 1.34),
  "HKD" to ("Hong Kong Dollar" to 7.82),
  "NZD" to ("New Zealand Dollar" to 1.63),
  "THB" to ("Thai Baht" to 35.50),
  "TWD" to ("Taiwan Dollar" to 31.50),
  "AED" to ("UAE Dirham" to 3.67),
  "SAR" to ("Saudi Riyal" to 3.75),
  "ILS" to ("Israeli Shekel" to 3.62),
  "PHP" to ("Philippine Peso" to 56.0),
)

fun currencyConverter(): MeasurementConverter = MeasurementConverter(
  id = "currency",
  name = "Currency",
  category = Category.Money,
  icon = Icons.Outlined.CurrencyExchange,
  aliases = listOf("dollar", "euro", "pound", "yen", "exchange", "forex", "money"),
  units = currencyRates.map { (code, pair) ->
    val (name, ratePerUsd) = pair
    // toBase: convert from this currency to USD (divide by rate)
    // fromBase: convert from USD to this currency (multiply by rate)
    MeasureUnit.linear(code, "$name ($code)", code, 1.0 / ratePerUsd)
  },
  defaultFromId = "USD",
  defaultToId = "EUR",
)

// ---------- Crypto (hardcoded seed prices) -----------------------------------------------------

private val cryptoPrices: Map<String, Pair<String, Double>> = mapOf(
  "BTC" to ("Bitcoin" to 67500.0),
  "ETH" to ("Ethereum" to 3450.0),
  "SOL" to ("Solana" to 145.0),
  "BNB" to ("BNB" to 580.0),
  "XRP" to ("XRP" to 0.52),
  "ADA" to ("Cardano" to 0.45),
  "DOGE" to ("Dogecoin" to 0.08),
  "DOT" to ("Polkadot" to 7.20),
  "AVAX" to ("Avalanche" to 35.0),
  "MATIC" to ("Polygon" to 0.58),
  "USD" to ("US Dollar" to 1.0),
)

fun cryptoConverter(): MeasurementConverter = MeasurementConverter(
  id = "crypto",
  name = "Crypto",
  category = Category.Money,
  icon = Icons.Outlined.Savings,
  aliases = listOf("bitcoin", "btc", "ethereum", "eth", "cryptocurrency"),
  units = cryptoPrices.map { (code, pair) ->
    val (name, priceUsd) = pair
    MeasureUnit.linear(code, "$name ($code)", code, priceUsd)
  },
  defaultFromId = "BTC",
  defaultToId = "USD",
)

// ---------- Tip calculator ---------------------------------------------------------------------

private fun fmt(n: Double): String = String.format(Locale.US, "%.2f", n)

fun tipConverter(): TextConverter = TextConverter(
  id = "tip",
  name = "Tip calculator",
  category = Category.Money,
  icon = Icons.Outlined.Receipt,
  aliases = listOf("tip", "gratuity", "bill"),
  placeholder = "Bill, Tip%, People (e.g. 85, 15, 2)",
  modes = listOf(
    TextConverter.Mode("calc", "Calculate") { input ->
      val parts = input.split(",", ";", " ").mapNotNull { it.trim().toDoubleOrNull() }
      require(parts.isNotEmpty()) { "Enter: bill, tip%, people" }
      val bill = parts[0]
      val tipPct = parts.getOrElse(1) { 15.0 }
      val people = parts.getOrElse(2) { 1.0 }.coerceAtLeast(1.0)
      val tip = bill * tipPct / 100.0
      val total = bill + tip
      val perPerson = total / people
      buildString {
        append("Tip:        ").append(fmt(tip)).append('\n')
        append("Total:      ").append(fmt(total))
        if (people > 1) append('\n').append("Per person: ").append(fmt(perPerson))
      }
    },
  ),
)

// ---------- Tax calculator ---------------------------------------------------------------------

fun taxConverter(): TextConverter = TextConverter(
  id = "tax",
  name = "Tax calculator",
  category = Category.Money,
  icon = Icons.Outlined.Percent,
  aliases = listOf("tax", "vat", "gst", "sales tax"),
  placeholder = "Amount, Tax% (e.g. 100, 21)",
  modes = listOf(
    TextConverter.Mode("exclusive", "Tax exclusive (add)") { input ->
      val parts = input.split(",", ";", " ").mapNotNull { it.trim().toDoubleOrNull() }
      require(parts.size >= 2) { "Enter: amount, tax%" }
      val amount = parts[0]
      val rate = parts[1]
      val tax = amount * rate / 100.0
      "Tax:   ${fmt(tax)}\nTotal: ${fmt(amount + tax)}"
    },
    TextConverter.Mode("inclusive", "Tax inclusive (extract)") { input ->
      val parts = input.split(",", ";", " ").mapNotNull { it.trim().toDoubleOrNull() }
      require(parts.size >= 2) { "Enter: total, tax%" }
      val total = parts[0]
      val rate = parts[1]
      val net = total / (1 + rate / 100.0)
      val tax = total - net
      "Net:  ${fmt(net)}\nTax:  ${fmt(tax)}"
    },
  ),
)

// ---------- Discount calculator ----------------------------------------------------------------

fun discountConverter(): TextConverter = TextConverter(
  id = "discount",
  name = "Discount calculator",
  category = Category.Money,
  icon = Icons.Outlined.Discount,
  aliases = listOf("discount", "sale", "off", "savings"),
  placeholder = "Price, Discount% (e.g. 200, 30)",
  modes = listOf(
    TextConverter.Mode("calc", "Calculate") { input ->
      val parts = input.split(",", ";", " ").mapNotNull { it.trim().toDoubleOrNull() }
      require(parts.size >= 2) { "Enter: price, discount%" }
      val price = parts[0]
      val pct = parts[1]
      val savings = price * pct / 100.0
      "Savings:     ${fmt(savings)}\nFinal price: ${fmt(price - savings)}"
    },
  ),
)

// ---------- Loan calculator -------------------------------------------------------------------

fun loanConverter(): TextConverter = TextConverter(
  id = "loan",
  name = "Loan calculator",
  category = Category.Money,
  icon = Icons.Outlined.Calculate,
  aliases = listOf("loan", "mortgage", "interest", "payment"),
  placeholder = "Principal, Annual rate%, Years (e.g. 250000, 5.5, 30)",
  modes = listOf(
    TextConverter.Mode("monthly", "Monthly payment") { input ->
      val parts = input.split(",", ";", " ").mapNotNull { it.trim().toDoubleOrNull() }
      require(parts.size >= 3) { "Enter: principal, rate%, years" }
      val principal = parts[0]
      val annualRate = parts[1] / 100.0
      val years = parts[2]
      val months = (years * 12).toInt()
      if (annualRate == 0.0) {
        val monthly = principal / months
        "Monthly: ${fmt(monthly)}\nTotal:   ${fmt(principal)}\nInterest: 0.00"
      } else {
        val r = annualRate / 12.0
        val monthly = principal * r * (1 + r).pow(months) / ((1 + r).pow(months) - 1)
        val total = monthly * months
        val interest = total - principal
        "Monthly:  ${fmt(monthly)}\nTotal:    ${fmt(total)}\nInterest: ${fmt(interest)}"
      }
    },
  ),
)

// ---------- Compound interest ------------------------------------------------------------------

fun compoundInterestConverter(): TextConverter = TextConverter(
  id = "compound-interest",
  name = "Compound interest",
  category = Category.Money,
  icon = Icons.Outlined.Savings,
  aliases = listOf("compound", "invest", "savings", "growth"),
  placeholder = "Principal, Rate%, Years, Compounds/yr (e.g. 10000, 7, 10, 12)",
  modes = listOf(
    TextConverter.Mode("calc", "Calculate") { input ->
      val parts = input.split(",", ";", " ").mapNotNull { it.trim().toDoubleOrNull() }
      require(parts.size >= 3) { "Enter: principal, rate%, years [, compounds/yr]" }
      val p = parts[0]
      val r = parts[1] / 100.0
      val t = parts[2]
      val n = parts.getOrElse(3) { 12.0 }
      val amount = p * (1 + r / n).pow(n * t)
      val interest = amount - p
      "Final amount:  ${fmt(amount)}\nInterest earned: ${fmt(interest)}"
    },
  ),
)
