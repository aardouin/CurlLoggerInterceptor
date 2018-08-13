# CurlLoggingInterceptor

An OkHTTP inteceptor to log a request in a curl command to easily reproduce it. 

## Dependencies
```
compile "com.squareup.okhttp3:okhttp:3.2.0"
```

# Quick start 

Simply include : 
```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
compile 'com.github.aardouin:CurlLoggingInterceptor:1.0'
```