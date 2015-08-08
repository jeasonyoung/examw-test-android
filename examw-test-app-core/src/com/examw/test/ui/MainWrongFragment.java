package com.examw.test.ui;

import java.util.ArrayList;
import java.util.List;

import com.examw.test.R;
import com.examw.test.adapter.WrongAdapter;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao;
import com.examw.test.dao.WrongItemData;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * 错题Fragment
 * 
 * @author jeasonyoung
 * @since 2015年7月4日
 */
public class MainWrongFragment extends Fragment implements RadioGroup.OnCheckedChangeListener,AdapterView.OnItemClickListener {
	private static final String TAG = "MainWrongFragment";
	private final MainActivity mainActivity;
	
	private WrongOption option = WrongOption.Wrong;
	private List<PaperDao.SubjectTotalModel> dataSource;
	private WrongAdapter adapter;
	private ListView listView;
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainWrongFragment(MainActivity mainActivity){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
		this.dataSource = new ArrayList<PaperDao.SubjectTotalModel>();
		this.adapter = new WrongAdapter(this.mainActivity, this.dataSource);
	}
	/*
	 * 加载UI。
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "加载UI布局...");
		//加载错题UI布局
		final View view = inflater.inflate(R.layout.ui_main_wrong, container, false);
		//标题选项
		final RadioGroup options = (RadioGroup)view.findViewById(R.id.main_wrong_options);
		options.setOnCheckedChangeListener(this);
		//数据列表
		this.listView = (ListView)view.findViewById(R.id.list_main_wrong);
		//设置列表数据源
		this.listView.setAdapter(this.adapter);
		//设置行点击事件处理
		this.listView.setOnItemClickListener(this);
		//返回视图
		return view;
	}
	/*
	 * 加载数据处理。
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		//启动等待动画
		this.mainActivity.waitingViewDialog.show();
		//加载数据处理
		new AsynLoadTask(this.mainActivity).execute();
	}
	/*
	 * 标题选中处理
	 * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d(TAG, "标题选项处理:" + checkedId);
		//启动等待动画
		this.mainActivity.waitingViewDialog.show();
		//选项转换		
		switch(checkedId){
			case R.id.main_wrong_options_favorite:{//收藏
				this.option = WrongOption.Favorite;
				break;
			}
			case R.id.main_wrong_options_wrong:
			default:
			{
				this.option = WrongOption.Wrong;
			}
		}
		//加载数据处理
		new AsynLoadTask(this.mainActivity).execute();
	}
	/*
	 * 数据项选中事件处理
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "选中行..." + position);
		if(this.dataSource.size() > position){
			PaperDao.SubjectTotalModel data = this.dataSource.get(position);
			if(data != null && data.getTotal() > 0){
				try{
					//开启等待动画
					this.mainActivity.waitingViewDialog.show();
					//生成数据源
					AppContext.setPaperDataDelegate(new WrongItemData(this.mainActivity, data.getCode(), this.option));
					//资源
					String title = null;
					final Resources resources = this.getResources();
					if(resources != null){
						title = (this.option == WrongOption.Wrong) ? resources.getString(R.string.main_wrong_options_wrong)
								: resources.getString(R.string.main_wrong_options_favorite);
					}
					//试题查看意图
					Intent intent = new Intent(this.mainActivity, PaperActivity.class);
					intent.putExtra(PaperActivity.PAPER_ITEM_ISDISPLAY_ANSWER, true);
					intent.putExtra(PaperActivity.PAPER_ITEM_TITLE, title);
					//发送意图
					this.startActivity(intent);
					
				}catch(Exception e){
					Log.e(TAG, "查看科目["+ data.getName() +"]下数据["+this.option+"]异常:" + e.getMessage(), e);
					Toast.makeText(this.mainActivity, "发生异常:" + e.getMessage(), Toast.LENGTH_SHORT).show();
				}finally{
					//关闭等待动画
					this.mainActivity.waitingViewDialog.cancel();
				}
			}
		}
	}
	//选项枚举
	public enum WrongOption { Wrong, Favorite };
	/**
	 * 异步线程加载数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月6日
	 */
	private class AsynLoadTask extends AsyncTask<Void, Void, Void>{
		private final PaperDao dao;
		/**
		 * 构造函数。
		 * @param context
		 */
		public AsynLoadTask(final Context context){
			this.dao = new PaperDao(context);
		}
		/*
		 * 台线程数据处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "开始后台线程数据处理...");
			//清空数据源
			dataSource.clear();
			//加载数据
			switch(option){
				case Wrong:{//错题数据
					List<PaperDao.SubjectTotalModel> list = this.dao.totalSubjectWrongRecords();
					if(list != null && list.size() > 0){
						dataSource.addAll(list);
					}
					break;
				}
				case Favorite:{//收藏数据
					List<PaperDao.SubjectTotalModel> list = this.dao.totalFavoriteRecords();
					if(list != null && list.size() > 0){
						dataSource.addAll(list);
					}
					break;
				}
			}
			return null;
		}
		/*
		 * 主线程UI处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			Log.d(TAG, "主线程UI处理...");
			//数据源更新
			adapter.notifyDataSetChanged();
			//关闭等待动画
			mainActivity.waitingViewDialog.cancel();
		};
	}
}