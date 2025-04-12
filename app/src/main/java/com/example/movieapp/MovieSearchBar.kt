
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MovieSearchBar(onSearch: (String) -> Unit) {
    val query = remember { mutableStateOf("") }

    OutlinedTextField(
        value = query.value,
        onValueChange = {
            query.value = it
            onSearch(it)
        },
        label = { Text("Search movies...") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}
