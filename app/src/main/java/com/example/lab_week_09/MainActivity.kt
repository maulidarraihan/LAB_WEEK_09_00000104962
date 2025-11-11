package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController)
                }
            }
        }
    }
}

// ========== DATA CLASS ==========
@JsonClass(generateAdapter = true)
data class Student(
    var name: String
)

// ========== JSON HELPER ==========
fun List<Student>.toJson(): String {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val type = Types.newParameterizedType(List::class.java, Student::class.java)
    val adapter = moshi.adapter<List<Student>>(type)
    return adapter.toJson(this)
}

fun String.toStudentList(): List<Student> {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val type = Types.newParameterizedType(List::class.java, Student::class.java)
    val adapter = moshi.adapter<List<Student>>(type)
    return adapter.fromJson(this) ?: emptyList()
}

// ========== HOME SCREEN ==========
@Composable
fun Home(navigateFromHomeToResult: (String) -> Unit) {
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    var inputField by remember { mutableStateOf(Student("")) }

    HomeContent(
        listData,
        inputField,
        { input -> inputField = inputField.copy(name = input) },
        {
            if (inputField.name.isNotBlank()) {
                listData.add(inputField)
                inputField = Student("")
            }
        },
        {
            val jsonString = listData.toList().toJson()
            navigateFromHomeToResult(jsonString)
        }
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    onNavigateClick: () -> Unit
) {
    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Enter Item", style = MaterialTheme.typography.titleLarge)

                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = { onInputValueChange(it) }
                )

                Button(onClick = onButtonClick) {
                    Text(text = "Add Student")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = onNavigateClick, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                    Text(text = "Go To Result", color = Color.White)
                }
            }
        }

        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = item.name)
            }
        }
    }
}

// ========== RESULT SCREEN ==========
@Composable
fun ResultContent(jsonString: String) {
    val studentList = remember(jsonString) {
        try {
            URLDecoder.decode(jsonString, "UTF-8").toStudentList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Student List", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (studentList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(studentList) { student ->
                    StudentCard(student = student)
                }
            }
        } else {
            Text(text = "No students found", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

// ========== STUDENT CARD ==========
@Composable
fun StudentCard(student: Student) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Student Name:", style = MaterialTheme.typography.labelSmall)
            Text(text = student.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

// ========== NAVIGATION ==========
@Composable
fun App(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home { jsonString ->
                val encodedJson = URLEncoder.encode(jsonString, "UTF-8")
                navController.navigate("result/$encodedJson")
            }
        }
        composable("result/{jsonString}") { backStackEntry ->
            val jsonString = backStackEntry.arguments?.getString("jsonString") ?: ""
            ResultContent(jsonString)
        }
    }
}

// ========== PREVIEW ==========
@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        Home {}
    }
}
