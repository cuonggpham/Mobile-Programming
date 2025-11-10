package com.example.currency

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var etFromAmount: EditText
    private lateinit var etToAmount: EditText
    private lateinit var spinnerFromCurrency: Spinner
    private lateinit var spinnerToCurrency: Spinner
    private lateinit var tvFromCurrencyCode: TextView
    private lateinit var tvToCurrencyCode: TextView
    private lateinit var tvExchangeRate: TextView
    private lateinit var tvUpdateTime: TextView
    private lateinit var btnUpdateRates: Button

    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btn8: Button
    private lateinit var btn9: Button
    private lateinit var btnDot: Button
    private lateinit var btnCE: Button

    private var isFromAmountFocused = true
    private var isUpdating = false

    data class Currency(
            val code: String,
            val name: String,
            val symbol: String,
            val rateToUSD: Double
    )

    private val currencies =
            listOf(
                    Currency("USD", "United States - Dollar", "$", 1.0),
                    Currency("EUR", "Eurozone - Euro", "€", 0.92),
                    Currency("GBP", "United Kingdom - Pound", "£", 0.79),
                    Currency("JPY", "Japan - Yen", "¥", 149.50),
                    Currency("MYR", "Malaysia - Ringgit", "RM", 4.72),
                    Currency("SGD", "Singapore - Dollar", "S$", 1.34),
                    Currency("AUD", "Australia - Dollar", "A$", 1.53),
                    Currency("CAD", "Canada - Dollar", "C$", 1.36),
                    Currency("CNY", "China - Yuan", "¥", 7.24),
                    Currency("THB", "Thailand - Baht", "฿", 35.80),
                    Currency("VND", "Vietnam - Dong", "₫", 24500.0),
                    Currency("FJD", "Fiji - Dollar", "FJ$", 2.14)
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupSpinners()
        setupEditTextListeners()
        setupNumberPad()
        setupUpdateButton()
        updateExchangeRateInfo()
        updateDateTime()
    }

    private fun initializeViews() {
        etFromAmount = findViewById(R.id.etFromAmount)
        etToAmount = findViewById(R.id.etToAmount)
        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrency)
        spinnerToCurrency = findViewById(R.id.spinnerToCurrency)
        tvFromCurrencyCode = findViewById(R.id.tvFromCurrencyCode)
        tvToCurrencyCode = findViewById(R.id.tvToCurrencyCode)
        tvExchangeRate = findViewById(R.id.tvExchangeRate)
        tvUpdateTime = findViewById(R.id.tvUpdateTime)
        btnUpdateRates = findViewById(R.id.btnUpdateRates)

        btn0 = findViewById(R.id.btn0)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btn8 = findViewById(R.id.btn8)
        btn9 = findViewById(R.id.btn9)
        btnDot = findViewById(R.id.btnDot)
        btnCE = findViewById(R.id.btnCE)

        etFromAmount.setOnFocusChangeListener { _, hasFocus -> isFromAmountFocused = hasFocus }

        etToAmount.setOnFocusChangeListener { _, hasFocus -> isFromAmountFocused = !hasFocus }
    }

    private fun setupSpinners() {
        val currencyNames = currencies.map { "${it.name}" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFromCurrency.adapter = adapter
        spinnerToCurrency.adapter = adapter

        spinnerFromCurrency.setSelection(0) 
        spinnerToCurrency.setSelection(10) 

        spinnerFromCurrency.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {
                        tvFromCurrencyCode.text = currencies[position].symbol
                        convertCurrency()
                        updateExchangeRateInfo()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

        spinnerToCurrency.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {
                        tvToCurrencyCode.text = currencies[position].symbol
                        convertCurrency()
                        updateExchangeRateInfo()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
    }

    private fun setupEditTextListeners() {
        etFromAmount.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {}

                    override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                    ) {
                        if (!isUpdating && isFromAmountFocused) {
                            convertCurrency()
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                }
        )

        etToAmount.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {}

                    override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                    ) {
                        if (!isUpdating && !isFromAmountFocused) {
                            convertCurrency()
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                }
        )
    }

    private fun setupNumberPad() {
        val numberClickListener = { number: String ->
            val currentEditText = if (isFromAmountFocused) etFromAmount else etToAmount
            val currentText = currentEditText.text.toString()

            if (currentText == "0" && number != ".") {
                currentEditText.setText(number)
            } else {
                currentEditText.append(number)
            }
        }

        btn0.setOnClickListener { numberClickListener("0") }
        btn1.setOnClickListener { numberClickListener("1") }
        btn2.setOnClickListener { numberClickListener("2") }
        btn3.setOnClickListener { numberClickListener("3") }
        btn4.setOnClickListener { numberClickListener("4") }
        btn5.setOnClickListener { numberClickListener("5") }
        btn6.setOnClickListener { numberClickListener("6") }
        btn7.setOnClickListener { numberClickListener("7") }
        btn8.setOnClickListener { numberClickListener("8") }
        btn9.setOnClickListener { numberClickListener("9") }

        btnDot.setOnClickListener {
            val currentEditText = if (isFromAmountFocused) etFromAmount else etToAmount
            val currentText = currentEditText.text.toString()

            if (!currentText.contains(".")) {
                if (currentText.isEmpty()) {
                    currentEditText.setText("0.")
                } else {
                    currentEditText.append(".")
                }
            }
        }

        btnCE.setOnClickListener {
            val currentEditText = if (isFromAmountFocused) etFromAmount else etToAmount
            currentEditText.setText("")
        }
    }

    private fun setupUpdateButton() {
        btnUpdateRates.setOnClickListener {
            updateDateTime()
            Toast.makeText(this, "Exchange rates updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertCurrency() {
        val fromCurrency = currencies[spinnerFromCurrency.selectedItemPosition]
        val toCurrency = currencies[spinnerToCurrency.selectedItemPosition]

        isUpdating = true

        try {
            if (isFromAmountFocused) {
                val fromAmount = etFromAmount.text.toString().toDoubleOrNull() ?: 0.0
                val toAmount = (fromAmount / fromCurrency.rateToUSD) * toCurrency.rateToUSD
                etToAmount.setText(String.format("%.2f", toAmount))
            } else {
                val toAmount = etToAmount.text.toString().toDoubleOrNull() ?: 0.0
                val fromAmount = (toAmount / toCurrency.rateToUSD) * fromCurrency.rateToUSD
                etFromAmount.setText(String.format("%.2f", fromAmount))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isUpdating = false
        }
    }

    private fun updateExchangeRateInfo() {
        val fromCurrency = currencies[spinnerFromCurrency.selectedItemPosition]
        val toCurrency = currencies[spinnerToCurrency.selectedItemPosition]

        val rate = toCurrency.rateToUSD / fromCurrency.rateToUSD
        tvExchangeRate.text =
                "1 ${fromCurrency.code} = ${String.format("%.4f", rate)} ${toCurrency.code}"
    }

    private fun updateDateTime() {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.getDefault())
        val currentDateTime = dateFormat.format(Date())
        tvUpdateTime.text = "Updated $currentDateTime"
    }
}
