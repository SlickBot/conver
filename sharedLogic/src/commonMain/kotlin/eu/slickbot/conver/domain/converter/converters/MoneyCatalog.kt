package eu.slickbot.conver.domain.converter.converters

import eu.slickbot.conver.domain.converter.CalculatorConverter
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.ConverterIcon
import eu.slickbot.conver.domain.converter.MeasureUnit
import eu.slickbot.conver.domain.converter.MeasurementConverter
import eu.slickbot.conver.domain.converter.StandaloneConverter
import eu.slickbot.conver.domain.converter.fixed
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
  icon = ConverterIcon.CurrencyExchange,
  aliases = listOf("dollar", "euro", "pound", "yen", "exchange", "forex", "money"),
  units = currencyRates.map { (code, pair) ->
    val (name, ratePerUsd) = pair
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
  icon = ConverterIcon.Savings,
  aliases = listOf("bitcoin", "btc", "ethereum", "eth", "cryptocurrency"),
  units = cryptoPrices.map { (code, pair) ->
    val (name, priceUsd) = pair
    MeasureUnit.linear(code, "$name ($code)", code, priceUsd)
  },
  defaultFromId = "BTC",
  defaultToId = "USD",
)

// ---------- Receipt split (Splitwise-style — bespoke screen) ----------------------------------

private fun fmt(n: Double): String = fixed(n, 2)

fun receiptSplitConverter(): StandaloneConverter = StandaloneConverter(
  id = "receipt-split",
  name = "Receipt split",
  category = Category.Money,
  icon = ConverterIcon.Receipt,
  aliases = listOf("split", "bill", "receipt", "splitwise", "share", "divide"),
  screenId = "receipt-split",
)

// ---------- Tax calculator ---------------------------------------------------------------------

fun taxConverter(): CalculatorConverter = CalculatorConverter(
  id = "tax",
  name = "Tax calculator",
  category = Category.Money,
  icon = ConverterIcon.Percent,
  aliases = listOf("tax", "vat", "gst", "sales tax"),
  fields = listOf(
    CalculatorConverter.Field("amount", "Amount", "$"),
    CalculatorConverter.Field("rate", "Tax rate", "%"),
  ),
  modes = listOf(
    CalculatorConverter.Mode("add", "Add tax"),
    CalculatorConverter.Mode("extract", "Extract tax"),
  ),
  calculate = { modeId, inputs ->
    val amount = inputs["amount"]!!
    val rate = inputs["rate"]!!
    if (modeId == "extract") {
      val net = amount / (1 + rate / 100.0)
      val tax = amount - net
      listOf(
        CalculatorConverter.Result("Net", fmt(net)),
        CalculatorConverter.Result("Tax", fmt(tax)),
      )
    } else {
      val tax = amount * rate / 100.0
      listOf(
        CalculatorConverter.Result("Tax", fmt(tax)),
        CalculatorConverter.Result("Total", fmt(amount + tax)),
      )
    }
  },
)

// ---------- Discount calculator ----------------------------------------------------------------

fun discountConverter(): CalculatorConverter = CalculatorConverter(
  id = "discount",
  name = "Discount calculator",
  category = Category.Money,
  icon = ConverterIcon.Discount,
  aliases = listOf("discount", "sale", "off", "savings"),
  fields = listOf(
    CalculatorConverter.Field("price", "Price", "$"),
    CalculatorConverter.Field("pct", "Discount", "%"),
  ),
  calculate = { _, inputs ->
    val price = inputs["price"]!!
    val pct = inputs["pct"]!!
    val savings = price * pct / 100.0
    listOf(
      CalculatorConverter.Result("Savings", fmt(savings)),
      CalculatorConverter.Result("Final price", fmt(price - savings)),
    )
  },
)

// ---------- Loan calculator -------------------------------------------------------------------

fun loanConverter(): CalculatorConverter = CalculatorConverter(
  id = "loan",
  name = "Loan calculator",
  category = Category.Money,
  icon = ConverterIcon.Calculate,
  aliases = listOf("loan", "mortgage", "interest", "payment"),
  fields = listOf(
    CalculatorConverter.Field("principal", "Principal", "$"),
    CalculatorConverter.Field("rate", "Annual rate", "%"),
    CalculatorConverter.Field("years", "Term", "years"),
  ),
  calculate = { _, inputs ->
    val principal = inputs["principal"]!!
    val annualRate = inputs["rate"]!! / 100.0
    val years = inputs["years"]!!
    val months = (years * 12).toInt()
    if (annualRate == 0.0) {
      val monthly = principal / months
      listOf(
        CalculatorConverter.Result("Monthly", fmt(monthly)),
        CalculatorConverter.Result("Total", fmt(principal)),
        CalculatorConverter.Result("Interest", "0.00"),
      )
    } else {
      val r = annualRate / 12.0
      val monthly = principal * r * (1 + r).pow(months) / ((1 + r).pow(months) - 1)
      val total = monthly * months
      listOf(
        CalculatorConverter.Result("Monthly", fmt(monthly)),
        CalculatorConverter.Result("Total", fmt(total)),
        CalculatorConverter.Result("Interest", fmt(total - principal)),
      )
    }
  },
)

// ---------- Compound interest ------------------------------------------------------------------

fun compoundInterestConverter(): CalculatorConverter = CalculatorConverter(
  id = "compound-interest",
  name = "Compound interest",
  category = Category.Money,
  icon = ConverterIcon.Savings,
  aliases = listOf("compound", "invest", "savings", "growth"),
  fields = listOf(
    CalculatorConverter.Field("principal", "Principal", "$"),
    CalculatorConverter.Field("rate", "Annual rate", "%"),
    CalculatorConverter.Field("years", "Years"),
    CalculatorConverter.Field("n", "Compounds / year", default = 12.0),
  ),
  calculate = { _, inputs ->
    val p = inputs["principal"]!!
    val r = inputs["rate"]!! / 100.0
    val t = inputs["years"]!!
    val n = inputs["n"]!!
    val amount = p * (1 + r / n).pow(n * t)
    listOf(
      CalculatorConverter.Result("Final amount", fmt(amount)),
      CalculatorConverter.Result("Interest earned", fmt(amount - p)),
    )
  },
)
