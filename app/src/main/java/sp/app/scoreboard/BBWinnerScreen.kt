package sp.app.scoreboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class BBWinnerScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bbwinner_screen)
        val intent = intent
        val extras = intent.extras!!

        val player1name = findViewById<TextView>(R.id.p1nametv)
        val player2name = findViewById<TextView>(R.id.p2nametv)
        val player1score = findViewById<TextView>(R.id.p1scoretv)
        val player2score = findViewById<TextView>(R.id.p2scoretv)
        val winnertv = findViewById<TextView>(R.id.winnertv)
        val roundstv = findViewById<TextView>(R.id.roundstv)
        val exitbtn = findViewById<Button>(R.id.exitbtn)

        winnertv.text= extras.getString("winner")
        player1name.text = extras.getString("p1_name")!!
        player2name.text = extras.getString("p2_name")!!
        player1score.text = extras.getInt("p1_score").toString()
        player2score.text = extras.getInt("p2_score").toString()
        val rounds = extras.getInt("rounds").toString()
        roundstv.text = "TURNS : $rounds"

        exitbtn.setOnClickListener {
            val intentnext = Intent(this,MainActivity::class.java)
            startActivity(intentnext)
        }
    }
}