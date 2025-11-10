package com.example.numberlist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var editTextNumber: EditText
    private lateinit var radioGroupNumberType: RadioGroup
    private lateinit var listViewNumbers: ListView
    private lateinit var textViewEmpty: TextView
    private lateinit var adapter: ArrayAdapter<Int>
    private val numbersList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        editTextNumber = findViewById(R.id.editTextNumber)
        radioGroupNumberType = findViewById(R.id.radioGroupNumberType)
        listViewNumbers = findViewById(R.id.listViewNumbers)
        textViewEmpty = findViewById(R.id.textViewEmpty)

        // Setup adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, numbersList)
        listViewNumbers.adapter = adapter

        // Add text change listener to EditText
        editTextNumber.addTextChangedListener(
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
                        updateNumberList()
                    }

                    override fun afterTextChanged(s: Editable?) {}
                }
        )

        // Add listener to RadioGroup
        radioGroupNumberType.setOnCheckedChangeListener { _, _ -> updateNumberList() }

        // Initial update
        updateNumberList()
    }

    private fun updateNumberList() {
        val inputText = editTextNumber.text.toString()

        if (inputText.isEmpty()) {
            numbersList.clear()
            adapter.notifyDataSetChanged()
            showEmptyMessage(true)
            return
        }

        val maxNumber = inputText.toIntOrNull() ?: return

        // Generate numbers based on selected type
        val numbers =
                when (radioGroupNumberType.checkedRadioButtonId) {
                    R.id.radioOdd -> getOddNumbers(maxNumber)
                    R.id.radioEven -> getEvenNumbers(maxNumber)
                    R.id.radioPrime -> getPrimeNumbers(maxNumber)
                    R.id.radioSquare -> getSquareNumbers(maxNumber)
                    R.id.radioPerfect -> getPerfectNumbers(maxNumber)
                    R.id.radioFibonacci -> getFibonacciNumbers(maxNumber)
                    else -> emptyList()
                }

        numbersList.clear()
        numbersList.addAll(numbers)
        adapter.notifyDataSetChanged()

        showEmptyMessage(numbers.isEmpty())
    }

    private fun showEmptyMessage(show: Boolean) {
        if (show) {
            listViewNumbers.visibility = View.GONE
            textViewEmpty.visibility = View.VISIBLE
        } else {
            listViewNumbers.visibility = View.VISIBLE
            textViewEmpty.visibility = View.GONE
        }
    }

    // Get odd numbers less than max
    private fun getOddNumbers(max: Int): List<Int> {
        return (1 until max).filter { it % 2 != 0 }
    }

    // Get even numbers less than max
    private fun getEvenNumbers(max: Int): List<Int> {
        return (2 until max).filter { it % 2 == 0 }
    }

    // Get prime numbers less than max
    private fun getPrimeNumbers(max: Int): List<Int> {
        if (max <= 2) return emptyList()

        val primes = mutableListOf<Int>()
        for (num in 2 until max) {
            if (isPrime(num)) {
                primes.add(num)
            }
        }
        return primes
    }

    private fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        if (n == 2) return true
        if (n % 2 == 0) return false

        val sqrtN = sqrt(n.toDouble()).toInt()
        for (i in 3..sqrtN step 2) {
            if (n % i == 0) return false
        }
        return true
    }

    // Get perfect square numbers less than max
    private fun getSquareNumbers(max: Int): List<Int> {
        val squares = mutableListOf<Int>()
        var i = 0
        while (true) {
            val square = i * i
            if (square >= max) break
            if (square > 0) squares.add(square)
            i++
        }
        return squares
    }

    // Get perfect numbers less than max
    private fun getPerfectNumbers(max: Int): List<Int> {
        val perfects = mutableListOf<Int>()
        for (num in 2 until max) {
            if (isPerfectNumber(num)) {
                perfects.add(num)
            }
        }
        return perfects
    }

    private fun isPerfectNumber(n: Int): Boolean {
        if (n < 2) return false

        var sum = 1
        val sqrtN = sqrt(n.toDouble()).toInt()

        for (i in 2..sqrtN) {
            if (n % i == 0) {
                sum += i
                if (i != n / i) {
                    sum += n / i
                }
            }
        }
        return sum == n
    }

    // Get Fibonacci numbers less than max
    private fun getFibonacciNumbers(max: Int): List<Int> {
        if (max <= 1) return emptyList()

        val fibs = mutableListOf<Int>()
        var a = 0
        var b = 1

        while (a < max) {
            fibs.add(a)
            val temp = a + b
            a = b
            b = temp
        }

        return fibs
    }
}
