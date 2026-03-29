package com.example.mycalculator.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mycalculator.ui.calculator.CalculatorScreen
import com.example.mycalculator.ui.calculator.CalculatorViewModel
import com.example.mycalculator.ui.history.HistoryScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val calculatorViewModel: CalculatorViewModel = viewModel()

    NavHost(navController = navController, startDestination = "calculator") {
        composable("calculator") { CalculatorScreen(
                viewModel = calculatorViewModel,
                onNavigateToHistory = { navController.navigate("history") }
            )
        }
        composable("history") {
            HistoryScreen(
                viewModel = calculatorViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}