package com.libraryteam.appupdate.app;/**
 * Created by PCPC on 2016/7/13.
 */

import android.app.Application;

import com.libraryteam.update.UpdateUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 描述: TODO
 * 名称: Global
 * User: csx
 * Date: 07-13
 */
public class Global extends Application {
    private static Global instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
        UpdateUtil.updateUrl = "xxxxx";
    }
    public static Global getInstance(){
        return instance;
    }
}
