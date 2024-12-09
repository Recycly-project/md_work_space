package com.koaladev.recycly.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.koaladev.recycly.R
import androidx.constraintlayout.motion.widget.MotionLayout
import com.airbnb.lottie.LottieAnimationView

class SplashActivity : AppCompatActivity() {

    private lateinit var motionLayout: MotionLayout
    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        motionLayout = findViewById(R.id.motionLayout)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)

        lottieAnimationView.addAnimatorUpdateListener { valueAnimator ->
            if (valueAnimator.animatedFraction == 1f) {
                motionLayout.transitionToEnd()
            }
        }

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                 startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                 finish()
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })

        lottieAnimationView.playAnimation()
    }
}