package ai.leqi.lib_share_center.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.permissionx.guolindev.PermissionX;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import ai.leqi.lib_share_center.BuildConfig;
import ai.leqi.lib_share_center.R;
import ai.leqi.lib_share_center.event.WXCodeEvent;
import ai.leqi.lib_share_center.helper.ShareCenterHelper;
import ai.leqi.lib_share_center.manager.WechatManager;
import ai.leqi.lib_share_center.utils.FileUtil;
import ai.leqi.lib_share_center.utils.NetworkUtil;
import ai.leqi.lib_share_center.webchromeclient.CustomWebChromeClient;

public class ShareCenterActivity extends AppCompatActivity {

    private WebView mWebView;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_center);
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        mWebView = findViewById(R.id.web_view);
        initSettings();
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new CustomWebChromeClient());
        mWebView.addJavascriptInterface(this, "shareCenter");

        mWebView.loadUrl("https://wormhole.leqiai.cn/shareCenter/?app_key=" + ShareCenterHelper.APP_KEY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WXCodeEvent event) {
        String js = "javascript:onAppMessage('" + event.getCode() + "')";
        mWebView.evaluateJavascript(js, null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initSettings() {
        WebView.enableSlowWholeDocumentDraw();
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);
        if (NetworkUtil.isNetworkConnected(this)) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webSettings.setTextZoom(100);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setBlockNetworkImage(false);//??????????????????????????????  ??????http or https
        webSettings.setAllowFileAccess(true); //????????????????????????html  file??????
        webSettings.setAllowFileAccessFromFileURLs(false); //?????? file url ????????? Javascript ??????????????????????????? .????????????
        webSettings.setAllowUniversalAccessFromFileURLs(false);//???????????? file url ????????? Javascript ??????????????????????????????????????????????????? http???https ???????????????
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setNeedInitialFocus(true);
        webSettings.setDefaultTextEncodingName("utf-8");//??????????????????
        webSettings.setDefaultFontSize(16);
        webSettings.setMinimumFontSize(10);//?????? WebView ??????????????????????????????????????? 8
        webSettings.setGeolocationEnabled(true);

        String appCacheDir = getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(appCacheDir);
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 80);
        // ????????????????????????userAgent
        webSettings.setUserAgentString(webSettings.getUserAgentString() + " share/sdk");
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
    }

    //???????????????
    @JavascriptInterface
    public void saveToAlbum(String url) {
        runOnUiThread(() -> PermissionX.init(ShareCenterActivity.this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        //???????????????????????????
                        //???????????????
                        Glide.with(ShareCenterActivity.this)
                                .downloadOnly()
                                .load(url)
                                .listener(new RequestListener<File>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                                        Toast.makeText(ShareCenterActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                                        saveToAlbum(ShareCenterActivity.this, resource.getAbsolutePath());
                                        return false;
                                    }
                                })
                                .preload();
                    }
                }));
    }

    //???????????????
    @JavascriptInterface
    public void shareToFriend(String url) {
        runOnUiThread(() -> PermissionX.init(ShareCenterActivity.this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        //???????????????????????????
                        Glide.with(ShareCenterActivity.this)
                                .asBitmap()
                                .load(url)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                        WechatManager.getInstance(ShareCenterActivity.this).shareImg(bitmap, SendMessageToWX.Req.WXSceneSession);
                                    }
                                });
                    }
                }));
    }

    //??????????????????
    @JavascriptInterface
    public void shareToMoments(String url) {
        runOnUiThread(() -> PermissionX.init(ShareCenterActivity.this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        //???????????????????????????
                        Glide.with(ShareCenterActivity.this)
                                .asBitmap()
                                .load(url)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                        WechatManager.getInstance(ShareCenterActivity.this).shareImg(bitmap, SendMessageToWX.Req.WXSceneTimeline);
                                    }
                                });
                    }
                }));
    }

    //??????
    @JavascriptInterface
    public void cashout() {
        WechatManager.getInstance(this).getWechatCode();
    }

    //??????????????????
    @JavascriptInterface
    public void homeBack() {
        finish();
    }

    private void saveToAlbum(Context context, String srcPath) {
        String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator;
        File file = new File(dcimPath, "content_" + System.currentTimeMillis() + ".png");
        boolean isCopySuccess = FileUtil.copyFile(srcPath, file.getAbsolutePath());
        if (isCopySuccess) {
            //??????????????????
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
            Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
