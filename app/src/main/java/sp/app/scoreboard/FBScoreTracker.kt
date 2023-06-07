package sp.app.scoreboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FBScoreTracker(p1_name : String,p2_name : String,rounds : Int) : ViewModel() {
    val p1_name : MutableLiveData<String> = MutableLiveData(p1_name)
    val p2_name : MutableLiveData<String> = MutableLiveData(p2_name)
    val max_rounds : MutableLiveData<Int> = MutableLiveData(rounds)
    var p1_score : MutableLiveData<Int> = MutableLiveData(0)
    var p2_score : MutableLiveData<Int> = MutableLiveData(0)
    var curr_round : MutableLiveData<Int> = MutableLiveData(1)
    var matchover : MutableLiveData<Boolean> = MutableLiveData(false)
    private var winner : MutableLiveData<String> = MutableLiveData("")

    fun setWinner() {
        if (p1_score.value!! > p2_score.value!!) {
            winner.value=p1_name.value
        }else if (p2_score.value!! > p1_score.value!!) {
            winner.value=p2_name.value
        }
    }
    fun increase_p1_Score() {
        if (curr_round.value!! <= max_rounds.value!! && !matchover.value!!) {
            p1_score.value = (p1_score.value)?.plus(1)
            if (curr_round.value!! == max_rounds.value!!) {
                matchover.value=true
                setWinner()
                return
            }
            curr_round.value = (curr_round.value)?.plus(1)

        }

    }

    fun increase_p2_Score() {
        if (curr_round.value!! <= max_rounds.value!! && !matchover.value!!) {
            p2_score.value = (p2_score.value)?.plus(1)
            if (curr_round.value!! == max_rounds.value!!) {
                matchover.value=true
                setWinner()
                return
            }
            curr_round.value = (curr_round.value)?.plus(1)
        }
    }

    fun getWinner(): String? {
        return winner.value
    }
}