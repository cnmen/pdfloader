package com.cmonbaby.pdfloader.demo.down;

import android.content.Context;
import android.util.Log;

import com.cmonbaby.pdf.loader.download.DownLoader;
import com.cmonbaby.pdf.loader.engine.PdfLoaderEngine;
import com.cmonbaby.pdf.loader.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownHelper implements DownLoader {

    private Context context;
    private String url;

    public DownHelper(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    public void download(PdfLoaderEngine engine) {
        // 省略Retrofit2 + OkHttp3下载过程

        down(engine);
    }

    /**
     * 下载请求
     */
    private void down(final PdfLoaderEngine engine) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure ->", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = null;
                BufferedInputStream bis = null;
                FileOutputStream fos = null;
                try {
                    if (call.isCanceled()) {
                        Log.e("Exception ->", "IOException Request Canceled");
                        return;
                    }

                    File dir = FileUtils.getDefaultCacheDir(context);
                    String fileName = FileUtils.getFileName(url);

                    if (response.isSuccessful()) {
                        responseBody = response.body();
                        bis = new BufferedInputStream(responseBody.byteStream());
                        File file = new File(dir, fileName);
                        fos = new FileOutputStream(file);
                        byte[] bytes = new byte[1024 * 8];
                        int len;
                        while ((len = bis.read(bytes)) != -1) {
                            fos.write(bytes, 0, len);
                            fos.flush();
                        }
                        // 下载完，执行重新加载
                        engine.reloader(engine.getDiskCache().save(file));
                    } else {
                        Log.e("Exception ->", "IOException Request Failed");
                    }
                } catch (Exception e) {
                    Log.e("Exception ->", e.getMessage());
                } finally {
                    if (null != responseBody) {
                        responseBody.close();
                    }
                    if (bis != null) bis.close();
                    if (fos != null) fos.close();
                }
            }
        });
    }
}
