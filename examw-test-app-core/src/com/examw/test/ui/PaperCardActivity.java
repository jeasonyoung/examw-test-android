package com.examw.test.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.adapter.CardSectionAdapter;
import com.examw.test.adapter.CardSectionItemAdapter;
import com.examw.test.app.AppContext;
import com.examw.test.ui.PaperActivity.PaperDataDelegate;
import com.examw.test.ui.PaperActivity.PaperDataDelegate.AnswerCardItemModel;
import com.examw.test.ui.PaperActivity.PaperDataDelegate.AnswerCardSectionModel;
import com.examw.test.widget.WaitingViewDialog;
/**
 * 试卷答题卡Activity。
 * 
 * @author jeasonyoung
 * @since 2015年7月27日
 */
public class PaperCardActivity extends Activity implements View.OnClickListener, CardSectionItemAdapter.CardItemClickListener {
	private static final String TAG = "PaperCardActivity";
	private final List<AnswerCardSectionModel> cardSections;
	private final SparseArray<AnswerCardItemModel[]> cardSectionItems;
	private CardSectionAdapter adapter;
	private WaitingViewDialog waitingViewDialog;
	/**
	 * 构造函数。
	 */
	public PaperCardActivity(){
		Log.d(TAG, "初始化...");
		//初始化分组数据
		this.cardSections = new ArrayList<AnswerCardSectionModel>();
		//初始化分组内容集合
		this.cardSectionItems = new SparseArray<AnswerCardItemModel[]>();
	}
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "重载创建...");
		super.onCreate(savedInstanceState);
		//加载布局
		this.setContentView(R.layout.ui_main_paper_card);
		
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		
		//是否显示答案
		boolean displayAnswer = false;
		Intent intent = this.getIntent();
		if(intent != null){
			displayAnswer = intent.getBooleanExtra(PaperActivity.PAPER_ITEM_ISDISPLAY_ANSWER, displayAnswer);
		}
		
		//标题
		final TextView titleView = (TextView)this.findViewById(R.id.title);
		titleView.setText(R.string.main_paper_card_title); 
		//返回按钮处理
		final View btnBack = this.findViewById(R.id.btn_goback);
		btnBack.setOnClickListener(this);
		
		//初始化数据适配器
		this.adapter = new CardSectionAdapter(this, this.cardSections, this.cardSectionItems, displayAnswer);
		this.adapter.setOnCardItemClickListener(this);
		//列表扩展
		final ListView listView = (ListView)this.findViewById(R.id.list_paper_cards);
		listView.setAdapter(this.adapter);
	}
	/*
	 * 重载加载数据。
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.d(TAG, "重载加载数据...");
		super.onStart();
		
		//开启等待动画
		this.waitingViewDialog.show();
		
		//异步加载数据
		new LoadDataAsyncTask().execute();
	}
	/*
	 * 按钮事件处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "按钮事件处理..." + v);
		switch(v.getId()){
			case R.id.btn_goback:{
				Log.d(TAG, "返回按钮处理...");
				//结果返回
				this.setResult(Activity.RESULT_CANCELED);
				//关闭UI
				this.finish();
				break;
			}
		}
	}
	/*
	 * 答题卡选中事件处理。
	 * @see com.examw.test.adapter.CardSectionItemAdapter.CardItemClickListener#onItemClick(int)
	 */
	@Override
	public void onItemClick(int order) {
		Log.d(TAG, "答题卡选中试题..." + (order + 1));
		//设置反馈数据
		Intent data = new Intent();
		data.putExtra(PaperActivity.PAPER_ITEM_ORDER, order);
		this.setResult(Activity.RESULT_OK, data);
		//关闭当前UI。
		this.finish();
	}
	/**
	 * 异步线程加载数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月28日
	 */
	private class LoadDataAsyncTask extends AsyncTask<Void, Void, Object[]>{
		/*
		 * 异步线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Object[] doInBackground(Void... params) {
			try{
				Log.d(TAG, "异步线程加载答题卡数据...");
				//boolean display = (Boolean)params[0];
				//数据接口
				final PaperDataDelegate dataDelegate = AppContext.getPaperDataDelegate();
				if(dataDelegate != null){
					//初始化分组
					final List<AnswerCardSectionModel> sectionModels = new ArrayList<AnswerCardSectionModel>();
					//初始化内容
					final SparseArray<AnswerCardItemModel[]> sectionItemModels = new SparseArray<AnswerCardItemModel[]>();
					//加载数据
					dataDelegate.loadAnswerCardData(sectionModels, sectionItemModels);
					//返回数据对象
					return new Object[] {sectionModels, sectionItemModels};
				}
			}catch(Exception e){
				Log.e(TAG, "异步线程加载答题卡数据异常:" + e.getMessage(), e);
			}
			return null;
		}
		/*
		 * 前台主线处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object[] result) {
			 Log.d(TAG, "异步数据加载完成...");
			 if(result != null){
				 //清空源数据。
				 cardSections.clear();
				 cardSectionItems.clear();
				 //分组
				@SuppressWarnings("unchecked")
				List<AnswerCardSectionModel> sections = (List<AnswerCardSectionModel>) result[0];				 
				 if(sections != null && sections.size() > 0){
					 cardSections.addAll(sections);
				 }
				 //分组内容
				 @SuppressWarnings("unchecked")
				 SparseArray<AnswerCardItemModel[]> items = (SparseArray<AnswerCardItemModel[]>)result[1];
				 int count = 0;
				 if(items != null && (count = items.size()) > 0){
					  for(int i = 0; i < count; i++){
						  //添加到集合
						  cardSectionItems.put(items.keyAt(i), items.valueAt(i));
					  }
				 }
				 //通知适配器刷新数据
				 adapter.notifyDataSetChanged();
			 }
			 //关闭等待动画
			 waitingViewDialog.cancel();
		}
	}
}