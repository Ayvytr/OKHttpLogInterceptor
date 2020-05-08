[![JCenter](https://img.shields.io/badge/jCenter-3.0.0-re.svg)](https://bintray.com/ayvytr/maven/okhttploginterceptor/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache--2.0%20-blue.svg)](license)

# OKHttpLogInterceptor
	A Pretty OkHttp Logging Interceptor：一款简洁漂亮的OkHttp Logging拦截器。3.0.0进行了大改版，取消以前的多种打印模式，最大化精简配置，并支持了json，xml的格式化打印，提高了可读性



## 依赖：

    implementation 'com.ayvytr:okhttploginterceptor:3.0.0'
    
    //历史版本，推荐用新版
    implementation 'com.ayvytr:okhttploginterceptor:2.1.0'



## 截图

### isShowAll=false：显示除请求头，请求参数，响应头外的所有内容



![](screenshot/request-get.jpg)



![](screenshot/response-get.jpg)



### isShowAll=true：显示所有内容，Get请求会显示url后边附带的Query参数



![](screenshot/request-get-all.jpg)




![](screenshot/response-get-all.jpg)



## 使用配置：

	//全部都为可选参数，
	//showLog：是否显示日志
	//isShowAll：true：显示所有日志；false：显示除请求头，请求参数，响应头外的所有参数
	//priority: Log优先级
	val loggingInterceptor = LoggingInterceptor(showLog = true,
	                              isShowAll = false,
	                              priority = Priority.E,
	                              tag = "自定义tag") {
	        //Log的自定义处理，比如输出到其他地方
	    }
	    
	var client: OkHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
	    .connectTimeout(10, TimeUnit.SECONDS)
	    .readTimeout(10, TimeUnit.SECONDS)
	    .writeTimeout(10, TimeUnit.SECONDS)
	    .build()




## ChangeLog

* 3.0.0 全新改版，取消以前的多种打印模式，最大化精简了配置，并支持了json，xml的格式化打印，提高了可读性

* ~~4.4.0 适配OkHttp 4.4的失败版本，已经删除~~

* 2.1.0 历史版本



