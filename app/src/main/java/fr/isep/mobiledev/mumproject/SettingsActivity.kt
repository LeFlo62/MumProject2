package fr.isep.mobiledev.mumproject

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isep.mobiledev.mumproject.ui.theme.MumProject2Theme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Arrays
import java.util.logging.Level

class SettingsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mLauncher: ActivityResultLauncher<String> = registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->
            if (uris != null) {
                val destinationFolder =
                    File(dataDir, "/images/")
                Arrays.stream(destinationFolder.listFiles())
                    .forEach { obj: File -> obj.delete() }
                uris.forEach { uri ->
                    val path: String = getPath(uri)
                    val src = File(path)
                    val destination =
                        File(destinationFolder, src.name)
                    try {
                        FileInputStream(src).use { fis ->
                            FileOutputStream(destination).use { fos ->
                                val buffer =
                                    ByteArray(1024)
                                var length: Int
                                while (fis.read(buffer)
                                        .also { length = it } > 0
                                ) {
                                    fos.write(buffer, 0, length)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        error(e)
                    }
                }
            }
        }

        setContent {
            MumProject2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(){
                        val preferences  = getSharedPreferences("fr.isep.mobiledev.mumproject", MODE_PRIVATE)
                        var phone by remember { mutableStateOf(preferences.getString("phone", ""))}
                        TextField(value = phone!!,
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            onValueChange = {
                                phone = it
                                preferences.edit().putString("phone", it).apply()
                            }
                        )

                        Button(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            onClick = {
                                mLauncher.launch("image/*")
                            }
                        ) {
                            Text("Select images")
                        }
                    }
                }
            }
        }
    }

    fun getPath(uri: Uri): String {
        var path: String? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor == null) {
            path = uri.path
        } else {
            cursor.moveToFirst()
            val column_index = cursor.getColumnIndexOrThrow(projection[0])
            path = cursor.getString(column_index)
            cursor.close()
        }
        return if (path == null || path.isEmpty()) uri.path!! else path
    }
}