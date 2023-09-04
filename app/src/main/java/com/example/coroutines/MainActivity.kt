package com.example.coroutines

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.coroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val tag = "MyTag"
    private val scope = lifecycleScope
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //Functions
        testingCodes()
    }

    private fun testingCodes() {
        //testing how suspend functions affect our app
        binding.apply {
            btnUsingCoroutines.setOnClickListener {
                scope.launch {
                    doWork(3)
                }
            }
            btnSimple.setOnClickListener {
                doWork2(3)
            }
        }

        //Testing how async be helpful for parallel execution
        scope.launch(Dispatchers.Main) {
            val result1 = async { doSomeWork1() }
            val result2 = async { doSomeWork2() }

            Log.d(tag, result1.await().toString())
            Log.d(tag, result2.await().toString())
        }

        //Testing Coroutine builders
        scope.launch {
//            coroutineUsingAsync()
//            coroutineUsingLaunch()

        }
    }

    //Functions to check the parallel execution using async
    private suspend fun doSomeWork1(): Int = withContext(Dispatchers.IO) {
        delay(2000L)
        0
    }

    private suspend fun doSomeWork2(): Int {
        return withContext(Dispatchers.IO) {
            delay(2000L)
            1
        }
    }

    //Learning coroutine builders launch, async-await
    private suspend fun coroutineUsingAsync() {

        val deferredFbFollowers = scope.async {
            getFbFollowers()
        }
        val deferredInstaFollowers = scope.async {
            getInstaFollowers()
        }
        Log.d("$tag from async", "Insta - ${deferredFbFollowers.await()}")
        Log.d("$tag from async", "Insta - ${deferredInstaFollowers.await()}")
    }

    private suspend fun coroutineUsingLaunch() {
        var fbFollowers = 0;
        var instaFollowers = 0

        val job1 = scope.launch {
            fbFollowers = getFbFollowers()
        }
        val job2 = scope.launch {
            instaFollowers = getInstaFollowers()
        }
        job1.join()
        job2.join()
        Log.d("$tag from launch", "Fb - $fbFollowers Insta - $instaFollowers")
    }

    //Mimic like getting api response
    private suspend fun getFbFollowers(): Int {
        return withContext(Dispatchers.IO) {
            delay(1000L)
            100
        }
    }

    private suspend fun getInstaFollowers(): Int {
        return withContext(Dispatchers.IO) {
            delay(1000L)
            150
        }
    }

    //Learning Suspend fun
    private suspend fun doWork(seconds: Long) {
        val seconds2 = seconds * 1000L
        delay(seconds2)
        Toast.makeText(this@MainActivity, "Work Done", Toast.LENGTH_SHORT).show()
        Log.d(tag, "Done from suspend after $seconds seconds")
    }
    private fun doWork2(seconds: Long) {
        val seconds2 = seconds * 1000L
        Thread.sleep(seconds2)
        Toast.makeText(this@MainActivity, "Done", Toast.LENGTH_SHORT).show()
        Log.d(tag, "Done from simple after $seconds seconds")

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }
}