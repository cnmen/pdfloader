### If you don't know, please click here : [CmonBaby](https://www.cmonbaby.com)

## PDFLoader ![Build Status](https://travis-ci.org/greenrobot/EventBus.svg?branch=master)

## About PDFLoader Code

#### * PDFLoader init (must to do first)
```java
PdfLoaderConfiguration.init(this);
```

#### * PDF from where
```java
// from assets
String assetsUrl = Scheme.ASSETS.wrap("sample_1.pdf"); // "assets://sample_1.pdf";

// from net
String netUrl = "http://xxx.xxx.xxx.xxx:8080/sample_1.pdf";

// from sdcard (Intent)
PDFLoader.getInstance().getEngine().launchLocalPdf();
```

#### * PDFLoader API
```java
PDFLoader.with()
        .from(netUrl) // from where
        .into(pdfView) // view
        .pageIndex(index) // pdf position
        .reqWidth(1000) // pdf weight
        .reqHeight(1600) // pdf weight
        .config(Bitmap.Config.RGB_565) // default：RGB_565
        .downLoader(new DownHelper(this, netUrl)) // custom downloader, only net
        // @see new SampleLoadingImpl();
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
            }

            @Override
            public void onLoadingCancelled(String loadUrl) {
                Log.e(Cons.LOG_TAG, "onLoadingCancelled -> " + loadUrl);
            }
        })
        .display();
```

#### * SDCard Callback Result
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Cons.REQUEST_CODE && resultCode == RESULT_OK) {
        if (data == null) return;
        Uri uri = data.getData();
        if (uri == null) return;

        PDFLoader.with()
                .from(uri.toString())
                .into(pdfView)
                .pageIndex(position)
                .reqWidth(1000)
                .reqHeight(1600)
                .display();
    }
}
```

Via Gradle:
```gradle
implementation 'com.cmonbaby:pdf_loader:1.0.1'
implementation 'com.cmonbaby:pdf_draw:1.0.1'
```

Via Maven:
```xml
<dependency>
    <groupId>com.cmonbaby</groupId>
    <artifactId>pdf_loader</artifactId>
    <version>1.0.1</version>
</dependency>
<dependency>
    <groupId>com.cmonbaby</groupId>
    <artifactId>pdf_draw</artifactId>
    <version>1.0.1</version>
</dependency>
```

## License

Copyright (C) 2013-2020 Markus Junginger, Simon (https://www.cmonbaby.com)
PDFLoader binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE).
