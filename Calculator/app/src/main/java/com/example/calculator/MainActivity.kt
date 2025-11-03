package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView

    private var currentNumber = ""
    private var firstOperand = 0
    private var operator = ""
    private var isNewOperation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        val btn0: Button = findViewById(R.id.btn0)
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)
        val btn5: Button = findViewById(R.id.btn5)
        val btn6: Button = findViewById(R.id.btn6)
        val btn7: Button = findViewById(R.id.btn7)
        val btn8: Button = findViewById(R.id.btn8)
        val btn9: Button = findViewById(R.id.btn9)

        val btnPlus: Button = findViewById(R.id.btnPlus)
        val btnMinus: Button = findViewById(R.id.btnMinus)
        val btnMultiply: Button = findViewById(R.id.btnMultiply)
        val btnDivide: Button = findViewById(R.id.btnDivide)
        val btnEquals: Button = findViewById(R.id.btnEquals)

        val btnCE: Button = findViewById(R.id.btnCE)
        val btnC: Button = findViewById(R.id.btnC)
        val btnBS: Button = findViewById(R.id.btnBS)
        val btnPlusMinus: Button = findViewById(R.id.btnPlusMinus)
        val btnDot: Button = findViewById(R.id.btnDot)

        val numberClickListener = { number: String ->
            if (isNewOperation) {
                currentNumber = ""
                isNewOperation = false
            }

            if (currentNumber == "0" && number != ".") {
                currentNumber = number
            } else {
                currentNumber += number
            }

            updateDisplay(currentNumber)
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

        val operatorClickListener = { op: String ->
            if (currentNumber.isNotEmpty()) {
                if (operator.isNotEmpty() && !isNewOperation) {
                    calculateResult()
                } else {
                    firstOperand = currentNumber.toIntOrNull() ?: 0
                }
            }
            operator = op
            isNewOperation = true
        }

        btnPlus.setOnClickListener { operatorClickListener("+") }
        btnMinus.setOnClickListener { operatorClickListener("-") }
        btnMultiply.setOnClickListener { operatorClickListener("*") }
        btnDivide.setOnClickListener { operatorClickListener("/") }

        btnEquals.setOnClickListener {
            if (currentNumber.isNotEmpty() && operator.isNotEmpty()) {
                calculateResult()
                operator = ""
            }
        }

        btnCE.setOnClickListener {
            currentNumber = "0"
            updateDisplay(currentNumber)
            isNewOperation = true
        }

        btnC.setOnClickListener {
            currentNumber = "0"
            firstOperand = 0
            operator = ""
            updateDisplay(currentNumber)
            isNewOperation = true
        }

        // Nút BS: Xóa chữ số hàng đơn vị
        btnBS.setOnClickListener {
            if (currentNumber.isNotEmpty() && currentNumber != "0") {
                currentNumber =
                        if (currentNumber.length == 1) {
                            "0"
                        } else {
                            currentNumber.dropLast(1)
                        }
                updateDisplay(currentNumber)
            }
        }

        btnPlusMinus.setOnClickListener {
            if (currentNumber.isNotEmpty() && currentNumber != "0") {
                val num = currentNumber.toIntOrNull() ?: 0
                currentNumber = (-num).toString()
                updateDisplay(currentNumber)
            }
        }

        btnDot.setOnClickListener {
        }
    }

    private fun calculateResult() {
        val secondOperand = currentNumber.toIntOrNull() ?: 0

        val result =
                when (operator) {
                    "+" -> firstOperand + secondOperand
                    "-" -> firstOperand - secondOperand
                    "*" -> firstOperand * secondOperand
                    "/" -> {
                        if (secondOperand != 0) {
                            firstOperand / secondOperand
                        } else {
                            updateDisplay("Lỗi: Chia cho 0")
                            firstOperand = 0
                            currentNumber = "0"
                            operator = ""
                            isNewOperation = true
                            return
                        }
                    }
                    else -> firstOperand
                }

        firstOperand = result
        currentNumber = result.toString()
        updateDisplay(currentNumber)
        isNewOperation = true
    }

    private fun updateDisplay(value: String) {
        tvDisplay.text = if (value.isEmpty()) "0" else value
    }
}
