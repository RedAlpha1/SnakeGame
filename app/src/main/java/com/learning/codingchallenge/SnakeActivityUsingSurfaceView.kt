package com.learning.codingchallenge

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.learning.codingchallenge.databinding.ActivitySnakeUsingSurfaceViewBinding
import com.learning.codingchallenge.utils.PreferenceUtils
import com.learning.codingchallenge.utils.SnakeUtils
import java.util.*


class SnakeActivityUsingSurfaceView : AppCompatActivity(), SurfaceHolder.Callback {
    private var isCheckFailed: Boolean = true
    private lateinit var binding: ActivitySnakeUsingSurfaceViewBinding
    private var snakePosition: String = "right"
    private var surfaceHolder: SurfaceHolder? = null
    private var snakePoints: ArrayList<SnakeUtils> = ArrayList()
    private var score: Int = 0
    private var positionX: Int? = 0
    private var positionY: Int? = 0
    private var painColor: Paint? = null
    private var alertDialogueBoxResumeGame: AlertDialog? = null
    private var alertDialogueBoxShowGame: AlertDialog? = null
    private var timer: Timer? = null
    lateinit var canva: Canvas


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_snake_using_surface_view)
        initView()
    }

    private fun initView() {
        addCallBackForSurfaceView()
        clickListener()
        setPreferenceData()
        checkNetworkConnectivity()
    }



    private fun checkNetworkConnectivity() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback: NetworkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Network is available
                runOnUiThread { showAlertBoxForNetwork() }

            }

            override fun onLost(network: Network) {
                // Network is lost
                runOnUiThread {
                    if (alertDialogueBoxResumeGame!!.isShowing) {
                        alertDialogueBoxResumeGame!!.dismiss()
                        isCheckFailed = false
                    }
                }

            }
        }

        cm.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun addCallBackForSurfaceView() {
        binding.surfaceView.holder.addCallback(this)
    }

    private fun clickListener() {
        binding.apply {
            leftBtn.setOnClickListener {
                if (snakePosition != "right")
                    snakePosition = "left"
            }
            rightBtn.setOnClickListener {
                if (snakePosition != "left")
                    snakePosition = "right"
            }
            topBtn.setOnClickListener {
                if (snakePosition != "bottom")
                    snakePosition = "top"
            }
            bottomBtn.setOnClickListener {
                // if snake move to bottom snake can't move directly in upward it moves first left or right
                if (snakePosition != "top")
                    snakePosition = "bottom"
            }
            ivPlay.setOnClickListener {
                binding.rlSnakeViewContainer.visibility = View.GONE
                binding.tvScore.visibility = View.VISIBLE
                binding.tvHiScore.visibility = View.VISIBLE
                binding.rlSurfaceView.visibility = View.VISIBLE
                binding.llBottomBar.visibility = View.VISIBLE
            }
        }
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        this.surfaceHolder = surfaceHolder
        initSurfaceView()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder) {}


    @SuppressLint("SetTextI18n")
    private fun initSurfaceView() {
        snakePoints = ArrayList()
        binding.tvScore.text = "Score: 0"
        score = 0
        snakePosition = "right"
        setStartPosition()
        addPoints()
        moveSnake()
    }

    private fun setStartPosition() {
        var startPositionX = (pointSize) * defaultTalePoint
        for (i in 0..defaultTalePoint) {
            val snakePoint = SnakeUtils(startPositionX, pointSize)
            snakePoints.add(snakePoint)
            startPositionX -= (pointSize * 2)
        }
    }

    private fun moveSnake() {
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                var headPositionX = snakePoints[0].pointX
                var headPositionY = snakePoints[0].pointY

                if (headPositionX == positionX && headPositionY == positionY) {
                    growSnake()
                    addPoints()
                }
                snakePosition.apply {
                    when (snakePosition) {
                        "right" -> {
                            snakePoints[0].pointX = headPositionX + (pointSize * 2)
                            snakePoints[0].pointY = headPositionY
                            return@apply
                        }
                        "left" -> {
                            snakePoints[0].pointX = headPositionX - (pointSize * 2)
                            snakePoints[0].pointY = headPositionY
                            return@apply
                        }
                        "top" -> {
                            snakePoints[0].pointX = headPositionX
                            snakePoints[0].pointY = headPositionY - (pointSize * 2)
                            return@apply
                        }
                        "bottom" -> {
                            snakePoints[0].pointX = headPositionX
                            snakePoints[0].pointY = headPositionY + (pointSize * 2)
                            return@apply
                        }
                    }
                }


                if (checkGameOver()) {
                    if (timer != null) {
                        timer!!.cancel()
                        timer!!.purge()
                        timer = null
                    }
                      val builder = AlertDialog.Builder(this@SnakeActivityUsingSurfaceView)
                      builder.setMessage("Your Score = $score")
                      builder.setTitle("Game Over")
                      builder.setCancelable(false)
                      builder.setPositiveButton("Start Again") { _, _ ->
                          initSurfaceView()
                      }
                      runOnUiThread {
                          try {
                              alertDialogueBoxShowGame = builder.create()
                              alertDialogueBoxShowGame?.show()
                          }catch (e : WindowManager.BadTokenException){}

                      }
                } else {
                    try {

                        canva = surfaceHolder!!.lockCanvas()
                        canva.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
                        canva.drawCircle(
                            snakePoints[0].pointX.toFloat(),
                            snakePoints[0].pointY.toFloat(),
                            pointSize.toFloat(), paintColor()!!
                        )
                        canva.drawCircle(
                            positionX!!.toFloat(),
                            positionY!!.toFloat(),
                            pointSize.toFloat(),
                            paintColor()!!
                        )


                        for (i in 1 until snakePoints.size) {
                            val getPositionX = snakePoints[i].pointX
                            val getPositionY = snakePoints[i].pointY

                            snakePoints[i].pointX = headPositionX
                            snakePoints[i].pointY = headPositionY
                            canva.drawCircle(
                                snakePoints[i].pointX.toFloat(),
                                snakePoints[i].pointY.toFloat(),
                                pointSize.toFloat(),
                                paintColor()!!
                            )
                            headPositionX = getPositionX
                            headPositionY = getPositionY
                        }
                        surfaceHolder!!.unlockCanvasAndPost(canva)
                    } catch (e: Exception) {

                    }
                }

            }
        }, 1000 - snakeMovingSpeed, 1000 - snakeMovingSpeed)
    }

    @SuppressLint("SetTextI18n")
    private fun growSnake() {
        val snakeUtils = SnakeUtils(0, 0)
        snakePoints.add(snakeUtils)
        score++
        runOnUiThread {
            binding.tvScore.text = "Score: $score"
            setPreferenceData()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setPreferenceData() {
        if (PreferenceUtils.retrieveData(this@SnakeActivityUsingSurfaceView, "high_score")
                .equals("") ||
            PreferenceUtils.retrieveData(this@SnakeActivityUsingSurfaceView, "high_score")!!
                .toInt() <= score
        ) {
            PreferenceUtils.insertData(this@SnakeActivityUsingSurfaceView, "high_score", score.toString())
            binding.tvHiScore.text = "HI : " + PreferenceUtils.retrieveData(this@SnakeActivityUsingSurfaceView, "high_score")
        } else {
            binding.tvHiScore.text = "HI : " + PreferenceUtils.retrieveData(
                this@SnakeActivityUsingSurfaceView, "high_score"
            )
        }
    }

    private fun checkGameOver(): Boolean {
        var isGameOver = false
        if (snakePoints[0].pointX < 0 ||
            snakePoints[0].pointY < 0 ||
            snakePoints[0].pointX > binding.surfaceView.width ||
            snakePoints[0].pointY > binding.surfaceView.height
        ) {
            isGameOver = true
        }
        return isGameOver
    }

    private fun addPoints() {
        val surfaceWidth = binding.surfaceView.width - (pointSize * 2)
        val surfaceHeight = binding.surfaceView.height - (pointSize * 2)

        var randomXPoint = Random().nextInt(surfaceWidth / pointSize)
        var randomYPoint = Random().nextInt(surfaceHeight / pointSize)

        if ((randomXPoint % 2) != 0)
            randomXPoint += 1

        if ((randomYPoint % 2) != 0)
            randomYPoint += 1

        positionX = (pointSize * randomXPoint) + pointSize
        positionY = (pointSize * randomYPoint) + pointSize

    }

    fun paintColor(): Paint? {
        return if (painColor == null) {
            painColor = Paint()
            painColor!!.color = snakeColor
            painColor!!.style = Paint.Style.FILL
            painColor!!.strokeWidth = 10f
            painColor!!.isAntiAlias = true
            painColor
        } else
            painColor
    }

    companion object {
        var pointSize: Int = 28
        var defaultTalePoint: Int = 3
        var snakeColor = Color.GREEN
        var snakeMovingSpeed: Long = 800
    }



    private fun showAlertBoxForNetwork() {
        try {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@SnakeActivityUsingSurfaceView)
            builder.setMessage("Internet connection detected.")
            builder.setTitle("Internet Available!!")
            builder.setCancelable(false)
            builder.setNegativeButton("Continue Game") { _, _ ->
                if (alertDialogueBoxResumeGame!!.isShowing) {
                    alertDialogueBoxResumeGame!!.dismiss()
                    isCheckFailed = false
                }
            }
            builder.setPositiveButton("Move to App") { _, _ ->
                // RedirectionLogic
                if(alertDialogueBoxShowGame?.isShowing == true){
                    alertDialogueBoxShowGame?.dismiss()
                }
                if(alertDialogueBoxResumeGame?.isShowing == true){
                        alertDialogueBoxResumeGame?.dismiss()
                }
                finish()

            }
            alertDialogueBoxResumeGame = builder.create()
            alertDialogueBoxResumeGame?.show()
        }catch (e : Exception){

        }

    }


}