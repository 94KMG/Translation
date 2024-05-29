package com.example.translation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.translation.ui.theme.TranslationTheme
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TranslationTheme {
                TranslationApp()
            }
        }
    }
}

@Composable
fun TranslationApp() {
    Column(modifier = Modifier.fillMaxSize()) {
        var word by remember { mutableStateOf("") }
        // Create an English-German translator:

        // 변수
        var isDownloaded by remember { mutableStateOf(false) }
//    val englishGermanTranslator = Translation.getClient(options)

        val koEnTranslator = remember {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.KOREAN)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
            Translation.getClient(options)
        }


        DownloadModel(koEnTranslator, onSuccess = {
            isDownloaded = true
        })
        TextField(
            value = word,
            onValueChange = { word = it })
        var output by remember { mutableStateOf("") }
        Button(onClick = {
            koEnTranslator.translate(word)
                .addOnSuccessListener { translatedText ->
                    output = translatedText
                }
                .addOnFailureListener { exception ->
                    // Error.
                    // ...
                }
        }, enabled = isDownloaded) {
            Text(text = "번역하기")
        }
        Text(text = output)
    }
}

@Composable
fun DownloadModel(koEnTranslator: Translator,
                  onSuccess:() -> Unit,
                  ) {
    LaunchedEffect(key1 = koEnTranslator) {
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        koEnTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                onSuccess()
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
            }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TranslationTheme {
        TranslationApp()
    }
}