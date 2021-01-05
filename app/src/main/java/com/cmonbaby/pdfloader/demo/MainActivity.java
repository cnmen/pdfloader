package com.cmonbaby.pdfloader.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cmonbaby.pdf.loader.PDFLoader;
import com.cmonbaby.pdf.loader.config.PdfLoaderConfiguration;
import com.cmonbaby.pdf.loader.listener.PdfLoadingListener;
import com.cmonbaby.pdf.loader.model.FailReason;
import com.cmonbaby.pdf.loader.model.LoadedFrom;
import com.cmonbaby.pdf.loader.model.PdfBean;
import com.cmonbaby.pdf.loader.model.Scheme;
import com.cmonbaby.pdf.loader.utils.Cons;
import com.cmonbaby.pdf.sample.down.DownHelper;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView pdfView;
    private TextView page;
    private int pageCount;
    private int index;

    // 图片来源于assets
    String assetsUrl = Scheme.ASSETS.wrap("sample_1.pdf"); // "assets://sample_1.pdf";

    // 图片来源于网络
    String netUrl = "http://121.36.161.235:8080/sample_1.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pdfView = findViewById(R.id.pdfView);
        page = findViewById(R.id.page);

        // 运行时权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }

        // PdfLoader初始化
        PdfLoaderConfiguration.init(this);

        // 图片来源于SDCard，拉起跳转
        // PDFLoader.getInstance().getEngine().launchLocalPdf();

        initClick();
        load();
    }

    private void initClick() {
        findViewById(R.id.before).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                index--;
                if (index < 0) {
                    index = 0;
                    return;
                }
                load();
            }
        });

        findViewById(R.id.after).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if (index > pageCount) {
                    index = pageCount;
                    return;
                }
                load();
            }
        });
    }

    private void load() {
        PDFLoader.with()
                .from(netUrl) // 从哪里加载（仅完成网络路径）
                .into(pdfView) // 放入哪个控件
                .pageIndex(index) // 加载第几页PDF
                .reqWidth(1000) // PDF宽高比重
                .reqHeight(1600) // PDF宽高比重
                .config(Bitmap.Config.RGB_565) // Bitmap设置（默认：RGB_565）
                .downLoader(new DownHelper(this, netUrl)) // 自定义下载器，仅限网络下载场景
                // 加载监听，如不想重写全部方法，可使用：new SampleLoadingImpl();
                .listener(new PdfLoadingListener() {
                    @Override
                    public void onLoadingStarted(String loadUrl) {
                        Log.e(Cons.LOG_TAG, "onLoadingStarted -> " + loadUrl);
                    }

                    @Override
                    public void onLoadingFailed(String loadUrl, FailReason failReason) {
                        Log.e(Cons.LOG_TAG, "onLoadingFailed -> " + failReason.getType().name());
                    }

                    @Override
                    public void onLoadingComplete(String loadUrl, Bitmap loadedImage, LoadedFrom loadedFrom) {
                        Log.e(Cons.LOG_TAG, "onLoadingComplete -> " + loadUrl + " / " + loadedFrom.name());
                        if (pageCount == 0) {
                            PdfBean bean = PDFLoader.getInstance().getEngine().getBean();
                            if (bean == null) return;
                            pageCount = bean.getPdfiumCore().getPageCount(bean.getPdfDocument());
                        }
                        page.setText((index + 1) + File.separator + pageCount);
                    }

                    @Override
                    public void onLoadingCancelled(String loadUrl) {
                        Log.e(Cons.LOG_TAG, "onLoadingCancelled -> " + loadUrl);
                    }
                })
                .display();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Cons.REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) return;
            Uri uri = data.getData();
            if (uri == null) return;

            // 加载SDCard选中的PDF文件
            PDFLoader.with()
                    .from(uri.toString()) // 从哪里加载（仅完成网络路径）
                    .into(pdfView) // 放入哪个控件
                    .pageIndex(3) // 加载第几页PDF
                    .reqWidth(1000) // PDF宽高比重
                    .reqHeight(1600) // PDF宽高比重
                    .display();
        }
    }
}
