package sp.app.scoreboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ttcv = findViewById<CardView>(R.id.ttcv)
        val fbcv = findViewById<CardView>(R.id.fbcv)
        ttcv.setOnClickListener {
            val intent = Intent(this,TTMatchForm::class.java)
            startActivity(intent)
        }

        fbcv.setOnClickListener {
            val intent = Intent(this,FBMatchForm::class.java)
            startActivity(intent)
        }
    }
}