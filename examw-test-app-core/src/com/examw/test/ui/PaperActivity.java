package com.examw.test.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.taptwo.android.widget.ViewFlow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao.ItemStatus;
import com.examw.test.model.PaperItemModel;
import com.examw.test.widget.WaitingViewDialog;

/**
 * 试卷Activity。
 * 
 * @author jeasonyoung
 * @since 2015年7月20日
 */
public class PaperActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "PaperActivity";
	private WaitingViewDialog waitingViewDialog;
	private PaperDataDelegate dataDelegate;
	
	private boolean displayAnswer;
	private int itemOrder;
	
	private TextView titleView, timeView;
	private ImageButton btnFav, btnSubmit, btnPrev, btnNext;
	private ViewFlow viewFlow;
	
	private final List<PaperItemModel> dataSource = new ArrayList<PaperItemModel>();
	private PaperAdapter adapter;
	/**
	 * 是否显示试题答案。
	 */
	public static final String PAPER_ITEM_ISDISPLAY_ANSWER = "paper_displayAnswer";
	/**
	 * 加载到指定的题序。
	 */
	public static final String PAPER_ITEM_ORDER = "paper_itemOrder";
	
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "重载创建...");
		super.onCreate(savedInstanceState);
		//加载试卷布局		
		this.setContentView(R.layout.ui_main_paper);
		//返回按钮
		final View btnBack = this.findViewById(R.id.main_paper_back);
		btnBack.setOnClickListener(this);
		//标题
		this.titleView = (TextView)this.findViewById(R.id.main_paper_title);
		//答题卡按钮
		final View btnCard = this.findViewById(R.id.main_paper_card);
		btnCard.setOnClickListener(this);
		
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		
		//试题视图
		this.viewFlow = (ViewFlow)this.findViewById(R.id.main_paper_viewflow);
		//初始化数据适配器
		this.adapter = new PaperAdapter(this, this.dataSource);
		//设置数据适配器
		//this.viewFlow.setAdapter(this.adapter);
		
		//上一题按钮
		this.btnPrev = (ImageButton)this.findViewById(R.id.main_paper_prev);
		btnPrev.setOnClickListener(this);
		//倒计时View
		this.timeView = (TextView)this.findViewById(R.id.main_paper_time);
		//收藏按钮
		this.btnFav = (ImageButton)this.findViewById(R.id.main_paper_fav);
		this.btnFav.setOnClickListener(this);
		//提交按钮
		this.btnSubmit = (ImageButton)this.findViewById(R.id.main_paper_submit);
		this.btnSubmit.setOnClickListener(this);
		//下一题按钮处理
		this.btnNext = (ImageButton)this.findViewById(R.id.main_paper_next);
		this.btnNext.setOnClickListener(this);
	}
	/*
	 * 重载开始。
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.d(TAG, "重载开始...");
		//启动等待动画
		this.waitingViewDialog.show();
		
		//初始化数据
		new InitDataAsyncTask().execute(this.getIntent());
		
		super.onStart();
	}
	/*
	 * 重载重新开始。
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.d(TAG, "重载重新开始...");
		// TODO Auto-generated method stub
		super.onResume();
	}
	/*
	 * 重载暂停。
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.d(TAG, "重载暂停...");
		// TODO Auto-generated method stub
		super.onPause();
	}
	/*
	 * 重载停止。
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.d(TAG, "重载停止...");
		// TODO Auto-generated method stub
		super.onStop();
	}
	/*
	 * 重载销毁。
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		Log.d(TAG, "重载销毁...");
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	/*
	 * 处理键盘输入。
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
	/*
	 * 按钮事件处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "按钮事件处理..." + v);
		switch(v.getId()){
			case R.id.main_paper_back:{//返回按钮处理
				Log.d(TAG, "返回按钮处理...");
				break;
			}
			case R.id.main_paper_card:{//答题卡按钮处理
				Log.d(TAG, "答题卡按钮处理...");
				
				break;
			}
			case R.id.main_paper_prev:{//上一题按钮处理
				Log.d(TAG, "上一题按钮处理...");
				break;
			}
			case R.id.main_paper_next:{//下一题按钮处理
				Log.d(TAG, "下一题按钮处理...");
				break;
			}
			case R.id.main_paper_fav:{//收藏按钮处理
				Log.d(TAG, "收藏按钮处理...");
				
				break;
			}
			case R.id.main_paper_submit:{//提交按钮处理
				Log.d(TAG, "提交按钮处理...");
				break;
			}
		}
	}
	/**
	 * 异步初始化数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月20日
	 */
	private class InitDataAsyncTask extends AsyncTask<Intent, Void, Boolean>{
		/*
		 *后台线程异步处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(Intent... params) {
			try{
				Log.d(TAG, "后台线程异步初始化处理...");
				//0.获取意图
				if(params[0] == null){
					Log.d(TAG, "获取意图失败!");
					return false;
				}
				//1.数据监听者类
				dataDelegate =  AppContext.getPaperDataDelegate();
				if(dataDelegate == null){
					Log.d(TAG, "未获取到试卷数据委托!");
					return false;
				}
				//2.是否显示答案
				displayAnswer = params[0].getBooleanExtra(PAPER_ITEM_ISDISPLAY_ANSWER, false);
				Log.d(TAG, "加载是否显示答案..." + displayAnswer);
				//3.指定题序
				itemOrder = params[0].getIntExtra(PAPER_ITEM_ORDER, 0);
				Log.d(TAG, "加载指定题序..." + itemOrder);
				//4.加载试题数据
				Log.d(TAG, "准备加载试题数据...");
				//4.1清空试卷数据
				dataSource.clear();
				//4.2添加数据
				List<PaperItemModel> list = dataDelegate.dataSourceOfPaperViews();
				int count = 0;
				if(list != null && (count = list.size()) > 0){
					dataSource.addAll(list);
				}
				Log.d(TAG, "共加载试题数据..." + count);
			}catch(Exception e){
				Log.e(TAG, "初始化数据异常:" + e.getMessage(), e);
				return false;
			}
			return true;
		}
		/*
		 * 前台主线程处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		 @Override
		protected void onPostExecute(Boolean result) {
			 if(result){
				 //提醒适配器更新
				// adapter.notifyDataSetChanged();
				 //设置选中的题序
//				 if(itemOrder > 0){
//					 viewFlow.setSelection(itemOrder);
//				 }
			 }
			 //关闭等待动画
			 waitingViewDialog.cancel();
			 ///TODO:
		}
	}
	/**
	 *试卷数据适配器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月20日
	 */
	private class PaperAdapter extends BaseAdapter{
		private final List<PaperItemModel> list;
		//private final LayoutInflater mInflater;
		/**
		 * 构造函数。
		 * @param context
		 */
		public PaperAdapter(final Context context, final List<PaperItemModel> list){
			Log.d(TAG, "初始化数据适配器...");
			this.list = list;
			//this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		/*
		 * 获取试题数。
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.list.size();
		}
		/*
		 * 获取试题对象。
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return this.list.get(position);
		}
		/*
		 * 获取试题默认ID。
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}
		/*
		 * 创建试题
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	/**
	 * 试卷数据监听者。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月20日
	 */
	public static abstract class PaperDataDelegate implements Serializable{
		private static final long serialVersionUID = 1L;
		/**
		 * 计算试题记录ID。
		 * @param itemModel
		 * 试题模型。
		 * @return
		 * 试题记录ID(id + "$" + index)
		 */
		protected final String createItemId(PaperItemModel itemModel){
			if(itemModel != null){
				return itemModel.getId() + "$" + itemModel.getIndex();
			}
			return null;
		}
		/**
		 * 加载数据源(异步线程调用)。
		 * @return
		 * 试题集合。
		 */
	    public abstract	List<PaperItemModel> dataSourceOfPaperViews() throws Exception;
		/**
		 * 加载试题答案(异步线程调用)。
		 * @param itemModel
		 * 试题。
		 * @return 记录的答案
		 */
	    public abstract String loadMyAnswer(PaperItemModel itemModel) throws Exception;
		/**
		 * 加载答题卡数据(异步线程调用)
		 * @param cardSections
		 * @param cardSectionItems
		 * @throws Exception
		 */
	    public abstract void loadAnswerCardData(final List<AnswerCardSectionModel> cardSections, final SparseArray<AnswerCardItemModel[]> cardSectionItems) throws Exception;
		/**
		 *加载当前试题题序。
		 * @return
		 * 题序。
		 */
		public int currentOrderOfPaperView() throws Exception {
			return 0;
		}
		/**
		 * 获取考试时长(分钟)。
		 * @return
		 * 考试时长(分钟)。
		 */
		public int timeOfPaperView() throws Exception {
			return -1;
		}
		/**
		 * 更新做题记录到SQL(异步线程中调用)
		 * @param itemModel
		 * 试题。
		 * @param myAnswers
		 * 答案。
		 * @param useTimes
		 * 用时。
		 */
		public void updateRecordAnswer(PaperItemModel itemModel, String myAnswers, int useTimes) throws Exception {
			
		}
		/**
		 * 更新收藏记录(异步线程中被调用)。
		 * @param itemModel
		 * 试题。
		 * @return
		 * true - 已收藏, false - 未收藏。
		 */
		public boolean updateFavorite(PaperItemModel itemModel) throws Exception {
			return false;
		}
		/**
		 * 交卷处理。
		 * @param useTimes
		 * 用时。
		 * @param handler
		 * 交卷结果处理。
		 */
		public void submitPaper(int useTimes, SubmitResultHandler handler) throws Exception {
			
		}
		/**
		 * 交卷结果处理。
		 * 
		 * @author jeasonyoung
		 * @since 2015年7月20日
		 */
		public interface SubmitResultHandler{
			/**
			 * 处理。
			 * @param paperRecordId
			 * 试卷记录ID。
			 */
			void hanlder(String paperRecordId);
		}
		/**
		 * 答题卡分组数据模型。
		 * 
		 * @author jeasonyoung
		 * @since 2015年7月21日
		 */
		public final class AnswerCardSectionModel implements Serializable{
			private static final long serialVersionUID = 1L;
			private String title,desc;
			/**
			 * 构造函数。
			 * @param title
			 * 结构名称。
			 * @param desc
			 * 结构描述。
			 */
			public AnswerCardSectionModel(String title,String desc){
				this.title = title;
				this.desc = desc;
			}
			/**
			 * 获取结构名称。
			 * @return 结构名称。
			 */
			public String getTitle() {
				return title;
			}
			/**
			 * 获取结构描述。
			 * @return 结构描述。
			 */
			public String getDesc() {
				return desc;
			}
		}
		/**
		 * 答案卡试题数据模型。
		 * 
		 * @author jeasonyoung
		 * @since 2015年7月21日
		 */
		public final class AnswerCardItemModel implements Serializable{
			private static final long serialVersionUID = 1L;
			private int order;
			/**
			 * 构造函数。
			 * @param order
			 * 题序。
			 * @param status
			 * 状态。
			 */
			public AnswerCardItemModel(int order, ItemStatus status){
				this.order = order;
				this.status = status;
			}
			/**
			 * 是否显示答案。
			 */
			public boolean displayAnswer;
			/**
			 * 试题状态。
			 */
			public ItemStatus status;
			/**
			 * 获取题序。
			 * @return 题序。
			 */
			public int getOrder() {
				return order;
			}
		}
	}
}