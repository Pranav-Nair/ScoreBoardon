package sp.app.scoreboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var game=-1
        val ttcv = findViewById<CardView>(R.id.ttcv)
        val fbcv = findViewById<CardView>(R.id.fbcv)
        val bbcv= findViewById<CardView>(R.id.bbcv)
        val bmcv = findViewById<CardView>(R.id.bmcv)
        ttcv.setOnClickListener {
            game=1
            val intent = Intent(this,TTMatchForm::class.java)
            intent.putExtra("game",game)
            startActivity(intent)
        }

        fbcv.setOnClickListener {
            game=2
            val intent = Intent(this,FBMatchForm::class.java)
            intent.putExtra("game",game)
            startActivity(intent)
        }

        bbcv.setOnClickListener {
            game=3
            val intent = Intent(this,BBMatchForm::class.java)
            intent.putExtra("game",game)
            startActivity(intent)
        }

        bmcv.setOnClickListener {
            game=4
            val intent = Intent(this,BMMatchForm::class.java)
            intent.putExtra("game",game)
            startActivity(intent)
        }


    }
}