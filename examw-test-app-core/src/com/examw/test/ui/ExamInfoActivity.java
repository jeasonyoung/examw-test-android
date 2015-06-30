package com.examw.test.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.examw.test.R;

/**
 * 
 * @author fengwei.
 * @since 2014年12月2日 下午3:48:20.
 */
public class ExamInfoActivity extends BaseActivity {
	private WebView webView;
	private RelativeLayout loading;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_exam_info);
		webView = (WebView) this.findViewById(R.id.web_view);
		loading = (RelativeLayout) this.findViewById(R.id.loading);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new SampleWebViewClient());
		//webView.loadUrl(AppContext.getMetaInfo("infoUrl"));
	}

	private class SampleWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			loading.setVisibility(View.GONE);
			super.onPageFinished(view, url);
		}
	}
	@Override   
    //设置回退    
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法    
    public boolean onKeyDown(int keyCode, KeyEvent event) {    
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {    
        	webView.goBack(); //goBack()表示返回WebView的上一页面    
            return true;    
        }    
        finish();//结束退出程序  
        return false;    
    }    
}
