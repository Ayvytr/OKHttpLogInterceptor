package com.ayvytr.okhttploginterceptorproject

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.ayvytr.okhttploginterceptor.LogPriority
import com.ayvytr.okhttploginterceptor.LoggingInterceptor
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val loggingInterceptor = LoggingInterceptor(showLog = true,
                                                isShowAll = true,
                                                logPriority = LogPriority.E) {
        //Log的自定义处理，比如输出到其他地方
    }

    init {
//        loggingInterceptor.tag = "custom tag"
//        loggingInterceptor.showLog = false
//        loggingInterceptor.isShowAll = false
        loggingInterceptor.logPriority = LogPriority.I
    }

    var client: OkHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        Observable.create(ObservableOnSubscribe<String> { e ->
//            val requestString = "{\"_index\":\"book_shop\",\"_type\":\"it_book\",\"_id\":\"1\",\"_score\":1.0,\"_source\":{\"name\": \"Java编程思想（第4版）\",\"author\": \"[美] Bruce Eckel\",\"category\": \"编程语言\",\"price\": 109.0,\"publisher\": \"机械工业出版社\",\"date\": \"2007-06-01\",\"tags\": [ \"Java\", \"编程语言\" ]}}"
            val requestString = "<html>" + "<head>" + "     <title>dom4j解析一个例子</title>" + "     <script>" + "         <username>yangrong</username>" + "         <password>123456</password>" + "     </script>" + "</head>" + "<body>" + "     <result>0</result>" + "     <form>" + "         <banlce>1000</banlce>" + "         <subID>36242519880716</subID>" + "     </form>" + "     <form>" + "         <banlce>200</banlce>" + "         <subID>222222222222</subID>" + "     </form>" + "</body>" + "</html>";

            RequestBody.Companion
            val request = Request.Builder().url("http://wthrcdn.etouch.cn/weather_mini?city=深")
//                .addHeader("a", "a1")
//                .addHeader("b", "a1")
//                .addHeader("c", "a1")
//                .addHeader("d", "a1")
//                .addHeader("e", "a1")
//                .addHeader("f", "a1")
//                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
//                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                .addHeader("Accept-Eocoding", "gzip, deflate")
//                .post(requestString.toRequestBody("application/json".toMediaTypeOrNull())).build()
//                .post(requestString.toRequestBody("text/xml".toMediaTypeOrNull()))
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                e.onNext(response.body?.string()!!)
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ s -> tv.text = s })

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings ->
                return true
            else                 -> super.onOptionsItemSelected(item)
        }
    }
}
