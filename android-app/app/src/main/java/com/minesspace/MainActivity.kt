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

// Activité principale de l'application
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configuration de l'écran plein format (sans barre de titre ni barre de navigation)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        setContent {
            MinesSpaceTheme {
                MainScreen() // Affichage de l'écran principal
            }
        }
    }
}

// Interface de l'écran principal
@Composable
fun MainScreen() {
    val context = LocalContext.current // Récupère le contexte pour la navigation

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(50.dp)) // Espacement entre le haut et le logo

        // Affichage du logo
        val logo = painterResource(id = R.drawable.logo_minesspace)
        Image(painter = logo, contentDescription = "Logo MinesSpace", modifier = Modifier.fillMaxWidth().height(300.dp))

        Spacer(modifier = Modifier.height(50.dp)) // Espacement avant le texte

        // Texte de bienvenue
        Text(
            text = "Bienvenue sur l'application Mines Space, où vous pouvez retrouver les données de nos lancers",
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp)) // Espacement avant le bouton

        // Bouton pour accéder à l'écran de sélection des lancers
        Button(
            onClick = {
                val intent = Intent(context, LaunchSelectionActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            content = {
                Text(text = "Accéder aux lancers", color = Color.White, fontSize = 20.sp)
            },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Red)
        )

        Spacer(modifier = Modifier.weight(1f)) // Espacement pour centrer le contenu
    }
}

// Prévisualisation de l'interface dans l'éditeur
@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainScreen()
}
