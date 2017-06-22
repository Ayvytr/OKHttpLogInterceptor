package com.ayvytr.okhttplogginginterceptorproject

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.ayvytr.okhttplogginginterceptor.HttpLoggingLevel
import com.ayvytr.okhttplogginginterceptor.LoggingInterceptor
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity()
{
    var client: OkHttpClient = OkHttpClient.Builder().addInterceptor(LoggingInterceptor(HttpLoggingLevel.BODY, LoggingInterceptor.Logger.WARN))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView()
    {
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        Observable.create(ObservableOnSubscribe<String> {
            e ->
            val request = Request.Builder().url("http://wthrcdn.etouch.cn/weather_mini?city=深圳").build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful)
            {
                e.onNext(response.body()?.string())
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ s -> tv.text = s })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId)
        {
            R.id.action_settings ->
                return true
            else                 -> super.onOptionsItemSelected(item)
        }
    }
}
