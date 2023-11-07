package com.testintergateai.presentaion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val default: Dp = 0.dp,
    val size1: Dp = 1.dp,
    val size2: Dp = 2.dp,
    val size4: Dp = 4.dp,
    val size6: Dp = 6.dp,
    val size8: Dp = 8.dp,
    val size10: Dp = 10.dp,
    val size12: Dp = 12.dp,
    val size14: Dp = 14.dp,
    val size16: Dp = 16.dp,
    val size18: Dp = 18.dp,
    val size19: Dp = 19.dp,
    val size24: Dp = 24.dp,
    val size32: Dp = 32.dp,
    val size36: Dp = 36.dp,
    val size38: Dp = 38.dp,
    val size48: Dp = 48.dp,
    val size50: Dp = 50.dp,
    val size64: Dp = 64.dp,
    val size75: Dp = 75.dp,
    val size78: Dp = 78.dp,
    val size80: Dp = 80.dp,
    val size90: Dp = 90.dp,
    val size100: Dp = 100.dp,
    val size120: Dp = 120.dp,
    val size128: Dp = 128.dp,
    val size150: Dp = 150.dp,
    val size160: Dp = 160.dp,
    val size170: Dp = 170.dp,
    val size180: Dp = 180.dp,
    val size200: Dp = 200.dp,
    val size220: Dp = 220.dp,
    val size240: Dp = 240.dp,
    val size250: Dp = 250.dp,
    val size300: Dp = 300.dp,
    val size328: Dp = 328.dp,
    val size375: Dp = 375.dp,
)

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current


data class Margin(
    val start: Dp = 0.dp,
    val end: Dp = 0.dp,
    val top: Dp = 0.dp,
    val bottom: Dp = 0.dp
)