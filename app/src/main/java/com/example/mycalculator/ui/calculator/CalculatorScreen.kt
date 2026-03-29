package com.example.mycalculator.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColorBackground = Color(0xFF000000)
private val ColorOperator = Color(0xFFFF9F0A)
private val ColorTopButton = Color(0xFF505050)
private val ColorNumber = Color(0xFF333333)
private val ColorTextDark = Color(0xFF000000)
private val ColorTextLight = Color(0xFFFFFFFF)

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onNavigateToHistory: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 52.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Historial",
                color = ColorOperator,
                fontSize = 18.sp,
                modifier = Modifier
                    .clickable { onNavigateToHistory() }
                    .padding(8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = state.expression,
                color = Color.Gray,
                fontSize = 22.sp,
                textAlign = TextAlign.End,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            val displayFontSize = when {
                state.display.length > 9 -> 40.sp
                state.display.length > 6 -> 60.sp
                else -> 80.sp
            }
            Text(
                text = state.display,
                color = Color.White,
                fontSize = displayFontSize,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ButtonRow {
                CalcButton("AC", ColorTopButton, ColorTextDark) { viewModel.onClearClick() }
                CalcButton("+/-", ColorTopButton, ColorTextDark) { viewModel.onPlusMinusClick() }
                CalcButton("%", ColorTopButton, ColorTextDark) { viewModel.onPercentClick() }
                CalcButton("÷", ColorOperator, ColorTextLight) { viewModel.onOperatorClick("÷") }
            }
            ButtonRow {
                CalcButton("7", ColorNumber, ColorTextLight) { viewModel.onNumberClick("7") }
                CalcButton("8", ColorNumber, ColorTextLight) { viewModel.onNumberClick("8") }
                CalcButton("9", ColorNumber, ColorTextLight) { viewModel.onNumberClick("9") }
                CalcButton("×", ColorOperator, ColorTextLight) { viewModel.onOperatorClick("×") }
            }
            ButtonRow {
                CalcButton("4", ColorNumber, ColorTextLight) { viewModel.onNumberClick("4") }
                CalcButton("5", ColorNumber, ColorTextLight) { viewModel.onNumberClick("5") }
                CalcButton("6", ColorNumber, ColorTextLight) { viewModel.onNumberClick("6") }
                CalcButton("-", ColorOperator, ColorTextLight) { viewModel.onOperatorClick("-") }
            }
            ButtonRow {
                CalcButton("1", ColorNumber, ColorTextLight) { viewModel.onNumberClick("1") }
                CalcButton("2", ColorNumber, ColorTextLight) { viewModel.onNumberClick("2") }
                CalcButton("3", ColorNumber, ColorTextLight) { viewModel.onNumberClick("3") }
                CalcButton("+", ColorOperator, ColorTextLight) { viewModel.onOperatorClick("+") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .aspectRatio(2.18f)
                        .clip(RoundedCornerShape(50.dp))
                        .background(ColorNumber)
                        .clickable { viewModel.onNumberClick("0") },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "0",
                        color = ColorTextLight,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(start = 26.dp)
                    )
                }
                CalcButton(".", ColorNumber, ColorTextLight, Modifier.weight(1f)) {
                    viewModel.onDecimalClick()
                }
                CalcButton("=", ColorOperator, ColorTextLight, Modifier.weight(1f)) {
                    viewModel.onEqualsClick()
                }
            }
        }
    }
}

@Composable
private fun ButtonRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun RowScope.CalcButton(
    label: String,
    background: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(background)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 34.sp,
            fontWeight = FontWeight.Normal
        )
    }
}