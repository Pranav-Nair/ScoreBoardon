package sp.app.scoreboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class TossActivity : AppCompatActivity() {
    var tossed : Boolean = false
    var p1_name : String =""
    var p2_name : String=""
    var p1_choice=""
    var p2_choice=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toss)
        val intent = intent
        val extras = intent.extras!!
        p1_name = extras.getString("p1_name")!!
        p2_name = extras.getString("p2_name")!!
        p1_choice = extras.getString("p1_toss_opt")!!
        p2_choice = extras.getString("p2_toss_opt")!!
        val continuebtn = findViewById<Button>(R.id.continuebtn)
        chooseSide()
        continuebtn.setOnClickListener {
            if (tossed) {
                var intentnext = Intent(this,TTScoreCollector::class.java)
                intentnext.putExtra("p1_name",p1_name)
                intentnext.putExtra("p2_name",p2_name)
                intentnext.putExtra("rounds",extras.getInt("rounds").toString().toInt())
                startActivity(intentnext)
            } else {
                Toast.makeText(this@TossActivity,"Perform the toss",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun chooseSide() {
        val coin = findViewById<ImageView>(R.id.cointossiv)
        coin.setOnClickListener {
            if (!tossed) {
                val randnumber = (0..1).random()
                if (randnumber==1) {
                    flipCoin(R.drawable.coin_head,"HEAD")
                } else {
                    flipCoin(R.drawable.coin_tail,"TAIL")
                }
            }
        }
    }

    private fun flipCoin(imageid : Int,coinside : String) {
        val coin = findViewById<ImageView>(R.id.cointossiv)
        val servertv = findViewById<TextView>(R.id.servertv)
        coin.animate().apply {
            duration=1000
            rotationYBy(1800f)
            coin.isClickable=false
        }.withEndAction {
            coin.setImageResource(imageid)
            tossed = true
            if (p1_choice == coinside) {
                servertv.text = p1_name
            } else if (p2_choice==coinside) {
                servertv.text = p2_name
            }
            coin.isClickable=true

        }.start()
    }
}