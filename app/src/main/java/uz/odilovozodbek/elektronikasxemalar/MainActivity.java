package uz.odilovozodbek.elektronikasxemalar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final int REQUEST_OPEN_JSON = 1001;
    private static final int REQUEST_SAVE_FILE = 1002;
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private String pendingSaveContent;
    private String pendingSaveMime;
    private String pendingSaveName;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(243, 247, 251));
        getWindow().setNavigationBarColor(Color.rgb(243, 247, 251));
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(243, 247, 251));
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webView.addJavascriptInterface(new ElectroFileBridge(), "ElectroFileBridge");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                Intent pick = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                pick.addCategory(Intent.CATEGORY_OPENABLE);
                pick.setType("*/*");
                pick.putExtra(Intent.EXTRA_MIME_TYPES, new String[] { "application/json", "text/json" });
                try {
                    startActivityForResult(pick, REQUEST_OPEN_JSON);
                    return true;
                } catch (Exception error) {
                    fileCallback = null;
                    Toast.makeText(MainActivity.this, "Fayl tanlash oynasini ochib bo‘lmadi", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, android.webkit.WebResourceRequest request) {
                Uri uri = request.getUrl();
                return !"file".equals(uri.getScheme()) || !uri.getPath().startsWith("/android_asset/");
            }
        });
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void startSavePicker(String fileName, String mimeType, String content) {
        if (content == null || content.length() > 12 * 1024 * 1024) {
            Toast.makeText(this, "Fayl hajmi juda katta", Toast.LENGTH_LONG).show();
            return;
        }
        pendingSaveContent = content;
        pendingSaveMime = mimeType == null ? "text/plain" : mimeType.split(";", 2)[0];
        pendingSaveName = fileName == null ? "elektronika-export.txt" : fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        if (pendingSaveName.length() > 90) pendingSaveName = pendingSaveName.substring(0, 90);
        Intent create = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        create.addCategory(Intent.CATEGORY_OPENABLE);
        create.setType(pendingSaveMime);
        create.putExtra(Intent.EXTRA_TITLE, pendingSaveName);
        try {
            startActivityForResult(create, REQUEST_SAVE_FILE);
        } catch (Exception error) {
            pendingSaveContent = null;
            Toast.makeText(this, "Saqlash oynasini ochib bo‘lmadi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_JSON) {
            if (fileCallback != null) {
                Uri selected = resultCode == RESULT_OK && data != null ? data.getData() : null;
                fileCallback.onReceiveValue(selected == null ? null : new Uri[] { selected });
                fileCallback = null;
            }
            return;
        }
        if (requestCode == REQUEST_SAVE_FILE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null && pendingSaveContent != null) {
                try (OutputStream output = getContentResolver().openOutputStream(data.getData(), "w")) {
                    if (output == null) throw new IllegalStateException("No output stream");
                    output.write(pendingSaveContent.getBytes(StandardCharsets.UTF_8));
                    Toast.makeText(this, "Fayl saqlandi", Toast.LENGTH_SHORT).show();
                } catch (Exception error) {
                    Toast.makeText(this, "Faylni saqlab bo‘lmadi", Toast.LENGTH_LONG).show();
                }
            }
            pendingSaveContent = null;
            pendingSaveMime = null;
            pendingSaveName = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface("ElectroFileBridge");
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    private final class ElectroFileBridge {
        @JavascriptInterface
        public void save(String fileName, String mimeType, String content) {
            runOnUiThread(() -> startSavePicker(fileName, mimeType, content));
        }
    }
}
