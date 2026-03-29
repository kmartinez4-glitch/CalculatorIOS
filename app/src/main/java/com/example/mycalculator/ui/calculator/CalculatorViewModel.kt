package com.example.mycalculator.ui.calculator

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class CalculatorState(
    val display: String = "0",
    val expression: String = "",
)

class CalculatorViewModel : ViewModel() {

    var state = mutableStateOf(CalculatorState())
        private set

    val history = mutableStateListOf<String>()

    private val numbers = mutableListOf<Double>()
    private val operators = mutableListOf<String>()

    private var isNewNumber = true
    private var hasDecimal = false
    private var lastWasEquals = false

    fun onNumberClick(number: String) {
        if (lastWasEquals) {
            numbers.clear()
            operators.clear()
            lastWasEquals = false
            state.value = CalculatorState()
        }
        if (isNewNumber) {
            state.value = state.value.copy(display = number)
            isNewNumber = false
            hasDecimal = false
        } else {
            val current = state.value.display
            if (current == "0" && number != ".") {
                state.value = state.value.copy(display = number)
            } else {
                state.value = state.value.copy(display = current + number)
            }
        }
    }

    fun onDecimalClick() {
        if (lastWasEquals) {
            numbers.clear()
            operators.clear()
            lastWasEquals = false
            state.value = CalculatorState()
        }
        if (isNewNumber) {
            state.value = state.value.copy(display = "0.")
            isNewNumber = false
            hasDecimal = true
            return
        }
        if (!hasDecimal) {
            state.value = state.value.copy(display = state.value.display + ".")
            hasDecimal = true
        }
    }

    fun onOperatorClick(op: String) {
        val currentNumber = state.value.display.toDoubleOrNull() ?: return

        if (isNewNumber && operators.isNotEmpty()) {
            // Solo reemplaza el último operador, no agrega nada
            operators[operators.size - 1] = op
        } else {
            // Agrega el número actual y el operador nuevo
            numbers.add(currentNumber)
            operators.add(op)
            isNewNumber = true
            hasDecimal = false
        }

        val expr = buildExpression()
        state.value = state.value.copy(expression = expr)
    }

    fun onEqualsClick() {
        val currentNumber = state.value.display.toDoubleOrNull() ?: return
        if (operators.isEmpty()) return

        numbers.add(currentNumber)

        val expressionText = buildExpressionFull()

        val result = evaluateWithPrecedence(
            numbers.toMutableList(),
            operators.toMutableList()
        )

        if (result == null) {
            showError()
            return
        }

        val entry = "$expressionText = ${formatResult(result)}"
        history.add(0, entry)

        state.value = state.value.copy(
            display = formatResult(result),
            expression = entry
        )

        numbers.clear()
        operators.clear()
        numbers.add(result)
        isNewNumber = true
        hasDecimal = false
        lastWasEquals = true
    }

    private fun evaluateWithPrecedence(
        nums: MutableList<Double>,
        ops: MutableList<String>
    ): Double? {
        //aplicacio gerarquia de operadores
        var i = 0
        while (i < ops.size) {
            if (ops[i] == "×" || ops[i] == "÷") {
                val result = calculate(nums[i], nums[i + 1], ops[i]) ?: return null
                nums[i] = result
                nums.removeAt(i + 1)
                ops.removeAt(i)
            } else {
                i++
            }
        }

        // Paso 2: resolver + y -
        var result = nums[0]
        for (j in ops.indices) {
            result = calculate(result, nums[j + 1], ops[j]) ?: return null
        }

        return result
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


    private fun buildExpression(): String {
        val sb = StringBuilder()
        for (i in numbers.indices) {
            sb.append(formatResult(numbers[i]))
            if (i < operators.size) {
                sb.append(" ${operators[i]} ")
            }
        }
        return sb.toString().trimEnd()
    }

    // guarda el hiostorial
    private fun buildExpressionFull(): String {
        val sb = StringBuilder()
        for (i in numbers.indices) {
            sb.append(formatResult(numbers[i]))
            if (i < operators.size) {
                sb.append(" ${operators[i]} ")
            }
        }
        return sb.toString().trimEnd()
    }

    fun onClearClick() {
        numbers.clear()
        operators.clear()
        isNewNumber = true
        hasDecimal = false
        lastWasEquals = false
        state.value = CalculatorState()
    }

    fun onPlusMinusClick() {
        val current = state.value.display.toDoubleOrNull() ?: return
        state.value = state.value.copy(display = formatResult(current * -1))
    }

    fun onPercentClick() {
        val current = state.value.display.toDoubleOrNull() ?: return
        state.value = state.value.copy(display = formatResult(current / 100))
    }

    fun onClearHistory() {
        history.clear()
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }

    private fun showError() {
        state.value = state.value.copy(display = "Error", expression = "")
        numbers.clear()
        operators.clear()
        isNewNumber = true
        lastWasEquals = false
    }
}