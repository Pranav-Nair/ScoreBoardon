package sp.app.scoreboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class TTMatchForm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttmatch_form)
        val startbtn = findViewById<Button>(R.id.startbtn)
        val p1_name_field = findViewById<EditText>(R.id.etpl1)
        val p2_name_field = findViewById<EditText>(R.id.etpl2)
        val round_field = findViewById<EditText>(R.id.etrounds)
        startbtn.setOnClickListener {
            if(validateInputs()) {
                val intent = Intent(this,TTScoreCollector::class.java)
                intent.putExtra("p1_name",p1_name_field.text.toString())
                intent.putExtra("p2_name",p2_name_field.text.toString())
                intent.putExtra("rounds",round_field.text.toString().toInt())
                startActivity(intent)
            }
        }
    }

    fun validateInputs() : Boolean {
        var valid = true
        val pname1_field = findViewById<EditText>(R.id.etpl1)
        val pname2_field = findViewById<EditText>(R.id.etpl2)
        val round_field = findViewById<EditText>(R.id.etrounds)

        if (pname1_field.text.isNullOrEmpty()) {
            Toast.makeText(this@TTMatchForm,"Please fill Player 1 Name",Toast.LENGTH_LONG).show()
            valid = false
        } else if (pname2_field.text.isNullOrEmpty()) {
            Toast.makeText(this@TTMatchForm,"Please fill Player 2 Name",Toast.LENGTH_LONG).show()
            valid = false
        } else if (round_field.text.isNullOrEmpty() || round_field.text.toString().toInt() < 0) {
            Toast.makeText(this@TTMatchForm,"Rounds must be a valid number",Toast.LENGTH_LONG).show()
            valid = false
        }
        return valid
    }
}