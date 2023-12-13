package fr.isep.mobiledev.mumproject

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.isep.mobiledev.mumproject.ui.theme.MumProject2Theme
import java.io.File
import java.util.Collections
import java.util.Timer
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : ComponentActivity() {

    private lateinit var images: Array<String>
    private var imageOrder: List<Int>? = null
    private var imageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var imageId by remember { mutableStateOf(getRandomImage().toString())}

            Timer().scheduleAtFixedRate(4000, 4000) {
                imageId = getRandomImage().toString()
            }

            MumProject2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Crossfade(
                            imageId,
                            animationSpec = tween(1000), label = "BackgroundImage"
                        ) { targetState : String ->
                            AsyncImage(
                                model = targetState,
                                contentDescription = "BackgroundImage",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black)
                            )
                        }

                        Column(modifier = Modifier.fillMaxSize()){
                            val preferences = getSharedPreferences("fr.isep.mobiledev.mumproject", MODE_PRIVATE)
                            Row(modifier = Modifier.fillMaxWidth()){
                                Button(
                                    onClick = {
                                        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                                        startActivity(intent)
                                    }
                                ) {
                                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                                }
                            }
                            Spacer(modifier = Modifier.fillMaxHeight(0.9f))
                            Row(modifier = Modifier.fillMaxWidth()){
                                Box(modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .wrapContentSize(Alignment.Center)){
                                    Button(
                                        modifier = Modifier.width(100.dp),
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL)
                                            intent.data =
                                                Uri.parse("tel:" + preferences.getString("phone", ""))
                                            startActivity(intent)
                                        }
                                    ) {
                                        Icon(Icons.Filled.Call, contentDescription = "Call Button")
                                    }
                                }

                                Box(modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .wrapContentSize(Alignment.Center)){
                                    Button(
                                        modifier = Modifier.width(100.dp),
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_SENDTO)
                                            intent.data =
                                                Uri.parse("smsto:" + preferences.getString("phone", ""))
                                            startActivity(intent)
                                        }
                                    ) {
                                        Icon(Icons.Filled.Send, contentDescription = "Call Button")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val imagesFolder = File(dataDir, "/images/")
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs()
        }

        images = imagesFolder.listFiles()!!.map { obj: File -> obj.absolutePath }.toTypedArray()
        imageOrder = IntStream.rangeClosed(0, images.size - 1).boxed().collect(Collectors.toList()).shuffled()
    }

    fun getRandomImage(): Uri? {
        if(!images.isEmpty()){
            if (imageIndex == images.size) {
                imageIndex = 0
                Collections.shuffle(imageOrder)
            }
            return Uri.parse(images[imageOrder!![imageIndex++]])
        }
        return Uri.parse("android.resource://fr.isep.mobiledev.mumproject/drawable/" + R.drawable.ic_launcher_background)
    }
}