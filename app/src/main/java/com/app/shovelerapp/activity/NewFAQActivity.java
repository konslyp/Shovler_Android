package com.app.shovelerapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.shovelerapp.R;

public class NewFAQActivity extends AppCompatActivity {

    private TextView mTitle;
    private ImageView mSubLogo;
    private WebView faqWebView;

//    private String faqLinkString= "http://dev6.edreamz2.com/FAQ/";
    private String faqLinkString= "http://carshovel.com/admin/faq.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_faq);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSubLogo= (ImageView) toolbar.findViewById(R.id.logo_image);
        mTitle= (TextView) toolbar.findViewById(R.id.title);

        mSubLogo.setImageResource(R.drawable.settings1);
        mTitle.setText("FAQ");

        faqWebView = (WebView)findViewById(R.id.wv_faq);
        faqWebView.setWebChromeClient(new WebChromeClient());
//        faqWebView.setWebViewClient(new WebViewClient());
        faqWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

//                String url2="http://www.playbuzz.org/";
//                // all links  with in ur site will be open inside the webview
//                //links that start ur domain example(http://www.example.com/)
//                if (url != null && url.startsWith(url2)){
//                    return false;
//                }
//                // all links that points outside the site will be open in a normal android browser
//                else  {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
//                }
            }
        });
        faqWebView.clearCache(true);
        faqWebView.clearHistory();
        faqWebView.getSettings().setJavaScriptEnabled(true);
        faqWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        faqWebView.loadUrl(faqLinkString);
    }
}
