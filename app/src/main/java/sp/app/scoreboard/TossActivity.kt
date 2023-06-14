package sp.app.scoreboard

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TossActivity : AppCompatActivity() {
    var tossed : Boolean = false
    var p1_name : String =""
    var p2_name : String=""
    var p1_choice=""
    var p2_choice=""
    var usecam = false
    val SupportedGames : List<Int> = listOf(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toss)
        val intent = intent
        val extras = intent.extras!!
        p1_name = extras.getString("p1_name")!!
        p2_name = extras.getString("p2_name")!!
        p1_choice = extras.getString("p1_toss_opt")!!
        p2_choice = extras.getString("p2_toss_opt")!!
        val game = extras.getInt("game")
        val continuebtn = findViewById<Button>(R.id.continuebtn)
        val camtoggle = findViewById<FloatingActionButton>(R.id.togglecam)
        if (enableCamToggle(game)) {
            camtoggle.isEnabled = true
        }
        chooseSide()
        camtoggle.setOnClickListener {
            if (!usecam) {
                camtoggle.backgroundTintList = ColorStateList.valueOf(getColor(R.color.ultra_violet))
                camtoggle.imageTintList = ColorStateList.valueOf(getColor(R.color.white))
                usecam = true
            } else {
                camtoggle.backgroundTintList = ColorStateList.valueOf(getColor(R.color.light_ultra_violet))
                camtoggle.imageTintList = ColorStateList.valueOf(getColor(R.color.black))
                usecam = false

            }
        }
        continuebtn.setOnClickListener {
            if (tossed) {
                if (usecam) {
                    if (game==1) {
                        val intentnext = Intent(this,TTCamScoreCollector::class.java)
                        intentnext.putExtra("p1_name",p1_name)
                        intentnext.putExtra("p2_name",p2_name)
                        intentnext.putExtra("rounds",extras.getInt("rounds").toString().toInt())
                        startActivity(intentnext)
                    }

                } else {
                    if (game==1) {
                        val intentnext = Intent(this,TTScoreCollector::class.java)
                        intentnext.putExtra("p1_name",p1_name)
                        intentnext.putExtra("p2_name",p2_name)
                        intentnext.putExtra("rounds",extras.getInt("rounds").toString().toInt())
                        startActivity(intentnext)
                    }else if (game==2) {
                        val intentnext = Intent(this,FbScoreCollector::class.java)
                        intentnext.putExtra("p1_name",p1_name)
                        intentnext.putExtra("p2_name",p2_name)
                        intentnext.putExtra("rounds",extras.getInt("rounds").toString().toInt())
                        startActivity(intentnext)
                    } else if (game==3) {
                        val intentnext = Intent(this,BBScoreCollector::class.java)
                        intentnext.putExtra("p1_name",p1_name)
                        intentnext.putExtra("p2_name",p2_name)
                        intentnext.putExtra("rounds",extras.getInt("rounds").toString().toInt())
                        startActivity(intentnext)
                    } else if (game==4) {
                        val intentnext = Intent(this,BMScoreCollector::class.java)
                        intentnext.putExtra("p1_name",p1_name)
                        intentnext.putExtra("p2_name",p2_name)
                        intentnext.putExtra("rounds",extras.getInt("rounds").toString().toInt())
                        startActivity(intentnext)
                    }
                }
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

    private fun enableCamToggle(game : Int) : Boolean {
        return SupportedGames.contains(game)
    }
}