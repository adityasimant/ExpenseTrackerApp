package com.metapointer.expensetrackerapp.ui.screens.main

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

data class TopBarState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val visible: Boolean = true,
    val title: String = "",
    val scrollBehavior: TopAppBarScrollBehavior? = null,
    val navigationIcon: (@Composable (() -> Unit))? = null,
    val actions: @Composable RowScope.() -> Unit = {}
)
