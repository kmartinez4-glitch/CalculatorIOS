package com.example.mycalculator.ui.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CalculatorState(
    val display: String = "0",
    val expression: String = "",
)

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> = _history.asStateFlow()

    private var firstNumber: Double? = null
    private var operator: String? = null
    private var isNewNumber = true
    private var hasDecimal = false

    fun onNumberClick(number: String) {
        if (isNewNumber) {
            _state.value = _state.value.copy(display = number)
            isNewNumber = false
            hasDecimal = false
        } else {
            val current = _state.value.display
            if (current == "0" && number != ".") {
                _state.value = _state.value.copy(display = number)
            } else {
                _state.value = _state.value.copy(display = current + number)
            }
        }
    }

    fun onDecimalClick() {
        if (isNewNumber) {
            _state.value = _state.value.copy(display = "0.")
            isNewNumber = false
            hasDecimal = true
            return
        }
        if (!hasDecimal) {
            _state.value = _state.value.copy(display = _state.value.display + ".")
            hasDecimal = true
        }
    }

    fun onOperatorClick(op: String) {
        val currentNumber = _state.value.display.toDoubleOrNull() ?: return

        if (firstNumber != null && operator != null && !isNewNumber) {
            val result = calculate(firstNumber!!, currentNumber, operator!!)
            if (result == null) {
                showError()
                return
            }
            firstNumber = result
            _state.value = _state.value.copy(
                display = formatResult(result),
                expression = "${formatResult(result)} $op"
            )
        } else {
            firstNumber = currentNumber
            _state.value = _state.value.copy(
                expression = "${formatResult(currentNumber)} $op"
            )
        }

        operator = op
        isNewNumber = true
        hasDecimal = false
    }

    fun onEqualsClick() {
        val secondNumber = _state.value.display.toDoubleOrNull() ?: return
        val op = operator ?: return
        val first = firstNumber ?: return

        val result = calculate(first, secondNumber, op)
        if (result == null) {
            showError()
            return
        }

        val entry = "${formatResult(first)} $op ${formatResult(secondNumber)} = ${formatResult(result)}"
        _history.value = _history.value + entry

        _state.value = _state.value.copy(
            display = formatResult(result),
            expression = entry
        )

        firstNumber = null
        operator = null
        isNewNumber = true
        hasDecimal = false
    }

    fun onClearClick() {
        firstNumber = null
        operator = null
        isNewNumber = true
        hasDecimal = false
        _state.value = CalculatorState()
    }

    fun onPlusMinusClick() {
        val current = _state.value.display.toDoubleOrNull() ?: return
        val toggled = current * -1
        _state.value = _state.value.copy(display = formatResult(toggled))
    }

    fun onPercentClick() {
        val current = _state.value.display.toDoubleOrNull() ?: return
        val result = current / 100
        _state.value = _state.value.copy(display = formatResult(result))
    }

    fun onClearHistory() {
        _history.value = emptyList()
    }

    private fun calculate(a: Double, b: Double, op: String): Double? {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) null else a / b
            else -> null
        }
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }

    private fun showError() {
        _state.value = _state.value.copy(display = "Error", expression = "")
        firstNumber = null
        operator = null
        isNewNumber = true
    }
}