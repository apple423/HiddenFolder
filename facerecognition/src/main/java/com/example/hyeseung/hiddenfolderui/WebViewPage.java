//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class WebViewPage extends Page {
    @SaveState
    private String url;
    @SaveState
    private HashMap<String, String> cookies;
    private WebView webView;
    int backgroud = 17170445;

    public WebViewPage() {
    }

    public Page init(DefaultUiManager uiManager, String url, Map<String, String> cookies) {
        this.url = url;
        this.cookies = new HashMap<String,String>(cookies);
        return this.init(uiManager);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        this.webView = new WebView(this.getActivity());
        dialog.setContentView(this.webView);
        return dialog;
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CookieSyncManager.createInstance(this.getActivity());
        CookieManager.getInstance().removeAllCookie();
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.setWebViewClient(new WebViewClient() {
            private Dialog progressDialog;

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                this.progressDialog = new Dialog(WebViewPage.this.getActivity()) {
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        this.requestWindowFeature(1);
                        this.getWindow().setBackgroundDrawableResource(0);
                        this.setContentView(new ProgressBar(this.getContext()));
                    }
                };
                this.progressDialog.show();
                Iterator i$ = WebViewPage.this.cookies.entrySet().iterator();

                while(i$.hasNext()) {
                    Entry entry = (Entry)i$.next();
                    WebViewPage.this.setCookie((String)entry.getKey(), (String)entry.getValue());
                }

            }

            public void onPageFinished(WebView view, String url) {
                if(this.progressDialog != null && this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                }

            }
        });
        this.webView.loadUrl(this.url);
    }

    public void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    public void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

    protected boolean isFloating() {
        return false;
    }

    private void setCookie(String name, String value) {
        String cookie = String.format("%s=%s; Domain=%s; Path=/;", new Object[]{name, value, Uri.parse(this.url).getHost()});
        CookieManager.getInstance().setCookie(this.url, cookie);
    }
}
