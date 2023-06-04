package sp.app.scoreboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get

class TTScoreCollector : AppCompatActivity() {
    private lateinit var mymodel : TtScoreTracker
    var matchover : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_collector)
        class TtScoreTrackerFactory(p1name:String,p2name:String,rounds : Int) : ViewModelProvider.Factory {
            private val extras = intent.extras
            private val p1name = p1name
            private val p2name = p2name
            private val rounds = rounds

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TtScoreTracker::class.java)) {
                    return TtScoreTracker(p1name,p2name, rounds) as T
                }
                return super.create(modelClass)
            }
        }

        val intent = intent
        var p1name=""
        var p2name=""
        var rounds=0
        val extras = intent.extras
        p1name = extras?.getString("p1_name").toString()
        p2name = extras?.getString("p2_name").toString()
        rounds = extras?.getInt("rounds")!!
        mymodel = ViewModelProvider(this,TtScoreTrackerFactory(p1name,p2name,rounds)).get(TtScoreTracker::class.java)

        val player1tv = findViewById<TextView>(R.id.pl1nametv)
        val player2tv = findViewById<TextView>(R.id.pl2nametv)
        val player1scoretv = findViewById<TextView>(R.id.pl1scoretv)
        val player2scoretv = findViewById<TextView>(R.id.pl2scoretv)
        val roundstv = findViewById<TextView>(R.id.roundtv)
        val player1card = findViewById<CardView>(R.id.p1cv)
        val player2card = findViewById<CardView>(R.id.p2cv)

        mymodel.matchover.observe(this) {
            matchover = it
        }

        mymodel.p1_name.observe(this) {
            player1tv.text = it
        }

        mymodel.p2_name.observe(this) {
            player2tv.text = it
        }

        mymodel.p1_score.observe(this) {
            player1scoretv.text = it.toString()
        }

        mymodel.p2_score.observe(this) {
            player2scoretv.text = it.toString()
        }

        mymodel.curr_round.observe(this) {
            roundstv.text = "Turn : ${it.toString()}"
        }


        player1card.setOnClickListener {
            mymodel.increase_p1_Score()
            checkandLoadWinScreen()

        }

        player2card.setOnClickListener {
            mymodel.increase_p2_Score()
            checkandLoadWinScreen()
        }

    }

    fun checkandLoadWinScreen() {
        if (matchover) {
            var intentnext= Intent(this,TTWinnerScreen::class.java)
            intentnext.putExtra("winner",mymodel.getWinner())
            intentnext.putExtra("p1_name",mymodel.p1_name.value)
            intentnext.putExtra("p2_name",mymodel.p2_name.value)
            intentnext.putExtra("p1_score",mymodel.p1_score.value)
            intentnext.putExtra("p2_score",mymodel.p2_score.value)
            intentnext.putExtra("rounds",mymodel.max_rounds.value)
            startActivity(intentnext)
        }
    }
}