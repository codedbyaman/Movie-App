
package com.example.movietrailerapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MovieSearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {
    val query by SearchState.query

    OutlinedTextField(
        value = query,
        onValueChange = {
            SearchState.query.value = it
            onSearch(it)
        },
        label = { Text("Search movies...") },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}
