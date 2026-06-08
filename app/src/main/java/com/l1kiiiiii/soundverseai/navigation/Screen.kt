package com.l1kiiiiii.soundverseai.navigation

/**
 * Centralised definition of all navigation route strings used in the NavHost.
 *
 * Using a sealed class prevents typos and makes refactoring safe.
 */
sealed class Screen(val route: String) {
    /** Main chat / creation screen — "Create - Blank State" */
    object Chat : Screen("chat")

    /** Export / share screen — "Export - State" */
    object ExportState : Screen("export_state")
}
