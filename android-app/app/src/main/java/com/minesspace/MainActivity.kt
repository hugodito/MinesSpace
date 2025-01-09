package com.minesspace

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.minesspace.ui.theme.MinesSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cacher la barre supérieure (ActionBar)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Cacher la barre inférieure (NavigationBar)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        setContent {
            MinesSpaceTheme {
                // App UI
                MainScreen()
            }
        }
    }
}


@Composable
fun MainScreen() {
    val context = LocalContext.current

    // Modifier pour centrer le contenu
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Le logo est en haut
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        // Logo
        val logo = painterResource(id = R.drawable.logo_minesspace)
        Image(painter = logo, contentDescription = "Logo MinesSpace", modifier = Modifier.fillMaxWidth().height(300.dp))

        // Espacement entre le logo et le texte/bouton
        Spacer(modifier = Modifier.height(50.dp)) // Espacement pour centrer les autres éléments

        // Texte de bienvenue
        Text(
            text = "Bienvenue sur l'application Mines Space, où vous pouvez suivre tous nos lancers en direct",
            fontSize = 18.sp, // Taille du texte
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
        )

        // Espacement entre le texte et le bouton
        Spacer(modifier = Modifier.height(40.dp))

        // Bouton
        Button(
            onClick = {
                val intent = Intent(context, LiveDataActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            content = {
                Text(text = "Accéder au live", color = Color.White, fontSize = 20.sp) // Texte plus grand
            },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        )

        // Espacement en bas pour centrer l'ensemble
        Spacer(modifier = Modifier.weight(1f)) // Espacement pour centrer les éléments
    }
}




@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainScreen()
}