package sp.app.scoreboard

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AlertDialog
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
import androidx.lifecycle.lifecycleScope
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class TTCamScoreCollector : AppCompatActivity() {
    private lateinit var mymodel: TtScoreTracker
    lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraPermissionDeferred: CompletableDeferred<Boolean>
    lateinit var previewView: PreviewView
    var matchover: Boolean = false
    private val mutex = Mutex()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tt_cam_score_collector)
        previewView = findViewById(R.id.camview)
        class TtScoreTrackerFactory(p1name: String, p2name: String, rounds: Int) :
            ViewModelProvider.Factory {
            private val extras = intent.extras
            private val p1name = p1name
            private val p2name = p2name
            private val rounds = rounds

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TtScoreTracker::class.java)) {
                    return TtScoreTracker(p1name, p2name, rounds) as T
                }
                return super.create(modelClass)
            }
        }


        var p1name = ""
        var p2name = ""
        var rounds = 0
        val intent = intent
        val extras = intent.extras
        p1name = extras?.getString("p1_name").toString()
        p2name = extras?.getString("p2_name").toString()
        rounds = extras?.getInt("rounds")!!
        mymodel = ViewModelProvider(this, TtScoreTrackerFactory(p1name, p2name, rounds)).get(
            TtScoreTracker::class.java
        )
        val turntv = findViewById<TextView>(R.id.turntv)
        val p1scoretv = findViewById<TextView>(R.id.p1scoretv)
        val p2scoretv = findViewById<TextView>(R.id.p2scoretv)
        mymodel.p1_score.observe(this, Observer {
            p1scoretv.text = it.toString()
        })
        mymodel.p2_score.observe(this, Observer {
            p2scoretv.text = it.toString()
        })
        mymodel.curr_round.observe(this, Observer {
            turntv.text = "TURN : $it"
        })
        mymodel.matchover.observe(this, Observer {
            matchover = it
        })

       lifecycleScope.launch(Dispatchers.IO) {
           val job = launch {
               cameraPermissionDeferred = CompletableDeferred()
               getCameraPermission()
               if (cameraPermissionDeferred.await()){
                   startCameraService()
               } else {
                   loadPermisionRequiredDIalog()
               }
            }
           job.join()
           withContext(Dispatchers.Main) {
                while (!matchover) {
                    var bitmap = previewView.bitmap
                    var res = bitmap?.let {
                        PredictOutcome(it)
                    }
                    if (res == 1) {
                        increase_p1_scoe()
                    }
                    else if (res==0) {
                        increase_p2_score()
                    }
                    delay(5000)
                }
               loadWinScreen()
           }
        }
    }

    fun loadWinScreen() {
        if (matchover) {
            val intentnext = Intent(this,TTWinnerScreen::class.java)
            intentnext.putExtra("winner",mymodel.getWinner())
            intentnext.putExtra("p1_name",mymodel.p1_name.value)
            intentnext.putExtra("p2_name",mymodel.p2_name.value)
            intentnext.putExtra("p1_score",mymodel.p1_score.value)
            intentnext.putExtra("p2_score",mymodel.p2_score.value)
            intentnext.putExtra("rounds",mymodel.max_rounds.value)
            startActivity(intentnext)
        }
    }

    @WorkerThread
    fun loadPermisionRequiredDIalog() {
        ContextCompat.getMainExecutor(this).execute {
            val builder = AlertDialog.Builder(this)
            val listener = DialogInterface.OnClickListener { _, _ ->
                finish()
            }
            builder.setTitle("Permission Required")
            builder.setMessage("this app needs camera permission to perform gesture detection")
            builder.setPositiveButton("Ok", listener)
            val dialog = builder.create()
            dialog.show()
        }
    }
    suspend fun increase_p1_scoe() {
        mutex.lock()
        mymodel.increase_p1_Score()
        mutex.unlock()
    }

    suspend fun increase_p2_score() {
        mutex.lock()
        mymodel.increase_p2_Score()
        mutex.unlock()
    }

    suspend fun startCameraService() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        previewView = findViewById(R.id.camview)
        var preview : Preview = Preview.Builder()
            .build()

        var cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.getSurfaceProvider())

        var camera = cameraProvider?.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)

    }

    suspend fun getCameraPermission() {
        val permissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 111)
        } else {
            cameraPermissionDeferred.complete(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraPermissionDeferred.complete(true)
        }
        else {
            cameraPermissionDeferred.complete(false)
        }

    }

    fun PredictOutcome(bitmap: Bitmap) : Int {
        var isThumbsUp = -1
        val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath("gesture_recognizer.task")
        val baseOptions = baseOptionsBuilder.build()
        val optionsBuilder =
            GestureRecognizer.GestureRecognizerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinHandDetectionConfidence(0.7f)
                .setMinTrackingConfidence(0.7f)
                .setMinHandPresenceConfidence(0.7f)
                .setRunningMode(RunningMode.IMAGE)

        val options = optionsBuilder.build()
        val gestureRecognizer =
            GestureRecognizer.createFromOptions(this, options)
        val mpImage = BitmapImageBuilder(bitmap).build()
        val result = gestureRecognizer?.recognize(mpImage)
        if (result != null) {
            if (result.gestures().isNotEmpty()){
                val gesture = result.gestures()[0][0].categoryName()
                if (gesture.toString()=="Thumb_Up") {
                    isThumbsUp=1
                }
                else if(gesture.toString()=="Closed_Fist"){
                    isThumbsUp=0
                }
            }
        }
        return isThumbsUp

    }
}