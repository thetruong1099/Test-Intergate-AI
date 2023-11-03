package com.testintergateai.presentaion.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.testintergateai.presentaion.ui.theme.spacing
import com.testintergateai.presentaion.utils.Screen

@Composable
fun MainBottomBarComponent(
    screens: List<Screen>,
    onNavigateTo: (Screen) -> Unit,
    currentDestination: NavDestination?
) {

    NavigationBar(
        modifier = Modifier
            .graphicsLayer {
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp
                )
                clip = true
            }
            .height(MaterialTheme.spacing.size80),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        screens.forEach { screen ->

            val selected: Boolean = currentDestination?.hierarchy?.any { it.route == screen.route } ?: false

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateTo.invoke(screen) },
                alwaysShowLabel = false,
                icon = {
                    Image(
                        imageVector = if (selected) screen.selectedIcon!! else screen.unselectedIcon!!,
                        contentDescription = screen.tile,
                        modifier = Modifier
                            .height(MaterialTheme.spacing.size32)
                            .width(MaterialTheme.spacing.size32),
                    )
                }
            )
        }
    }
}