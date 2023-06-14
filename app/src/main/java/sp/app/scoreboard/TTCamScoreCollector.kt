package sp.app.scoreboard

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.common.util.concurrent.ListenableFuture

class TTCamScoreCollector : AppCompatActivity() {
    private lateinit var mymodel : TtScoreTracker
    var matchover : Boolean = false
    lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tt_cam_score_collector)
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

        if (getCameraPermission()) {
            startCameraService()
        }

        var p1name=""
        var p2name=""
        var rounds=0
        val intent = intent
        val extras = intent.extras
        p1name = extras?.getString("p1_name").toString()
        p2name = extras?.getString("p2_name").toString()
        rounds = extras?.getInt("rounds")!!
        mymodel = ViewModelProvider(this,TtScoreTrackerFactory(p1name,p2name,rounds)).get(TtScoreTracker::class.java)
        val turntv = findViewById<TextView>(R.id.turntv)
        val p1scoretv = findViewById<TextView>(R.id.p1scoretv)
        val p2scoretv = findViewById<TextView>(R.id.p2scoretv)
        mymodel.p1_score.observe(this, Observer {
            p1scoretv.text = it.toString()
        })
        mymodel.p1_score.observe(this, Observer {
            p2scoretv.text = it.toString()
        })
        mymodel.curr_round.observe(this, Observer {
            turntv.text = "TURN : $it"
        })

    }

    fun startCameraService() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        val previewView = findViewById<PreviewView>(R.id.camview)
        var preview : Preview = Preview.Builder()
            .build()

        var cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.getSurfaceProvider())

        var camera = cameraProvider?.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)

    }

    fun getCameraPermission() : Boolean {
        var granted = false
        val permissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 111)
        } else {
            granted =true
        }
        granted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        return granted
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCameraService()
        }

    }
}