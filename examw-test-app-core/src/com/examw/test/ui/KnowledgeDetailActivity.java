package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.domain.Chapter;
import com.examw.test.support.ReturnBtnClickListener;
import com.examw.test.utils.ToastUtils;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;

/**
 * 知识点详情
 * 
 * @author fengwei.
 * @since 2014年12月2日 下午3:47:42.
 */
public class KnowledgeDetailActivity extends BaseActivity {
	private PullToRefreshWebView webView;
	private WebView mWebView;
	private TextView title;
	private LinearLayout loading, nodata, reload;
	private String chapterId/*, chapterPid*/;
	private int currentIndex;
	private String currentContent;
	private List<Chapter> brothers;
	private ILoadingLayout startLabels, endLabels;
	private MyHandler handler;

	private static final String TIPS = "抱歉,暂无章节详情,小编们正在拼命添加中...";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_knowledge_detail);
		chapterId = getIntent().getStringExtra("chapterId");
		//chapterPid = getIntent().getStringExtra("chapterPid");
		handler = new MyHandler(this);
		initView();
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void initView() {
		webView = (PullToRefreshWebView) findViewById(R.id.webview);
		startLabels = webView.getLoadingLayoutProxy(true, false);
		endLabels = webView.getLoadingLayoutProxy(false, true);

		mWebView = webView.getRefreshableView();
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setWebViewClient(new SampleWebViewClient());
		float size = this.getResources().getDimensionPixelSize(
				R.dimen.text_medium_size);
		int fontsize = px2sp(this, size);
		mWebView.getSettings().setDefaultFontSize(fontsize);

		loading = (LinearLayout) findViewById(R.id.loadingLayout);
		nodata = (LinearLayout) findViewById(R.id.nodataLayout);
		reload = (LinearLayout) findViewById(R.id.reload);
		title = (TextView) findViewById(R.id.title);

		findViewById(R.id.btn_goback).setOnClickListener(
				new ReturnBtnClickListener(this));

		webView.setOnRefreshListener(new OnRefreshListener2<WebView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<WebView> refreshView) {
				if(currentIndex > 0)
				{
					currentIndex = currentIndex - 1;
					chapterId = brothers.get(currentIndex ).getChapterId();
					loading.setVisibility(View.VISIBLE);
					new GetKnowledgeContentThread().start();
				}else
				{
					refreshView.onRefreshComplete();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<WebView> refreshView) {
				//下拉加载上一篇
				if (currentIndex < brothers.size() - 1) {
					currentIndex = currentIndex + 1;
					chapterId = brothers.get(currentIndex).getChapterId();
					loading.setVisibility(View.VISIBLE);
					new GetKnowledgeContentThread().start();
				} else {
					refreshView.onRefreshComplete();
				}
			}
		});
	}

	@Override
	protected void onStart() {
		initData();
		super.onStart();
	}

	private void initData() {
		loading.setVisibility(View.VISIBLE);
		new Thread() {
			// 获取同一级别的章节
			public void run() {
				try {
//					brothers = SyllabusDao.loadChapters(chapterPid); // 加载包含自身的兄弟章节
					if (brothers == null || brothers.isEmpty()) {
						handler.sendEmptyMessage(0); // 没有数据
					} else {
						for (int i = 0; i < brothers.size(); i++) {
							if (brothers.get(i).getChapterId()
									.equals(chapterId)) {
								currentIndex = i; // 查询所在位置
							}
						}
						// 查询知识点的内容
//						currentContent = SyllabusDao
//								.loadKnowledgeContent(chapterId);// 查询知识点的内容
						if (currentContent == null) {
							//String content = ApiClient.loadKnowledgeContent(
								//	(AppContext) getApplication(), chapterId);
							//if (content == null) {
								handler.sendEmptyMessage(10); // 没有内容
							//} else {
//								List<KnowledgeInfo> list = GsonUtil.getGson().fromJson(content, new TypeToken<List<KnowledgeInfo>>(){}.getType());
//								if(list == null || list.isEmpty())
//								{
//									handler.sendEmptyMessage(10); // 没有内容
//								}else
//								{
////									currentContent = SyllabusDao.insertChapters(list.get(0));
//									handler.sendEmptyMessage(1);
//								}
							//}
						}else
						{
							handler.sendEmptyMessage(1);
						}
					}
				} /*catch (AppException e) {
					Message msg = handler.obtainMessage();
					msg.what = -1;
					msg.obj = e;
					handler.sendMessage(msg);
				} */catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(-2);
				}
			};
		}.start();
	}

	static class MyHandler extends Handler {
		WeakReference<KnowledgeDetailActivity> weak;

		public MyHandler(KnowledgeDetailActivity context) {
			weak = new WeakReference<KnowledgeDetailActivity>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			KnowledgeDetailActivity k = weak.get();
			switch (msg.what) {
			case 0:
				k.nodata.setVisibility(View.VISIBLE);
				k.reload.setVisibility(View.GONE);
				k.loading.setVisibility(View.GONE);
				break;
			case 1:
				//设置label,设置内容
				k.setRefreshLabels();
				k.mWebView.loadDataWithBaseURL(null, k.currentContent, "text/html", "utf-8", null);
				k.webView.onRefreshComplete();
				break;
			case 10:
				//设置label,设置内容
				k.setRefreshLabels();
				//当前项没有数据
				k.mWebView.loadDataWithBaseURL(null, TIPS, "text/html", "utf-8", null);
				k.webView.onRefreshComplete();
				break;
			case -1:
				//((AppException)msg.obj).makeToast(k);
				k.nodata.setVisibility(View.GONE);
				k.loading.setVisibility(View.GONE);
				k.reload.setVisibility(View.VISIBLE);
				break;
			case -2:
				ToastUtils.show(k, "获取数据出错");
				k.nodata.setVisibility(View.VISIBLE);
				k.reload.setVisibility(View.GONE);
				k.loading.setVisibility(View.GONE);
				break;
			}
		}
	}
	private void setRefreshLabels()
	{
		int length = brothers.size();
		if(currentIndex == 0) //是第一篇
		{
			startLabels.setLastUpdatedLabel("上一篇:没有了");
			if(length>1)
				endLabels.setLastUpdatedLabel("下一篇:"+brothers.get(1).getTitle());
			else
				endLabels.setLastUpdatedLabel("下一篇:没有了");
		}else if(currentIndex == length -1)
		{
			endLabels.setLastUpdatedLabel("下一篇:没有了");
			if(length>1)
				startLabels.setLastUpdatedLabel("上一篇:"+brothers.get(currentIndex-1).getTitle());
			else
				startLabels.setLastUpdatedLabel("上一篇:没有了");
		}else{
			startLabels.setLastUpdatedLabel("上一篇:"+brothers.get(currentIndex-1).getTitle());
			endLabels.setLastUpdatedLabel("下一篇:"+brothers.get(currentIndex+1).getTitle());
		}
		title.setText(brothers.get(currentIndex).getTitle());
	}
	
	private class GetKnowledgeContentThread extends Thread {
		@Override
		public void run() {
			// 查询知识点的内容
			try {
//				currentContent = SyllabusDao.loadKnowledgeContent(chapterId);// 查询知识点的内容
				if (currentContent == null) {
//					String content = ApiClient.loadKnowledgeContent(
//							(AppContext) getApplication(), chapterId);
//					if (content == null) {
//						handler.sendEmptyMessage(10); // 没有内容
//					} else {
//						List<KnowledgeInfo> list = GsonUtil.getGson().fromJson(content, new TypeToken<List<KnowledgeInfo>>(){}.getType());
//						if(list == null || list.isEmpty())
//						{
//							handler.sendEmptyMessage(10); // 没有内容
//						}else
//						{
////							currentContent = SyllabusDao.insertChapters(list.get(0));
//							handler.sendEmptyMessage(1);
//						}
//					}
				}else
				{
					handler.sendEmptyMessage(1);
				}
			} /*catch (AppException e) {
				Message msg = handler.obtainMessage();
				msg.what = -1;
				msg.obj = e;
				handler.sendMessage(msg);
			} */catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(-2);
			}
		}
	}

	private class SampleWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("file:///") == true) {
				view.loadUrl(url);
				return true;
			} else {
				Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(in);
				return true;
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			loading.setVisibility(View.GONE);
			super.onPageFinished(view, url);
		}
	}

	private int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}
}
