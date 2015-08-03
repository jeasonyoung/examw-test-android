package com.examw.test.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.examw.test.R;
import com.examw.test.adapter.HomePaperTypeAdapter;
import com.examw.test.dao.PaperDao;
import com.examw.test.dao.PaperDao.SubjectTotalModel;
import com.examw.test.model.PaperModel;
import com.examw.test.model.PaperModel.PaperType;
import com.examw.test.widget.TitleFlowIndicator;
import com.examw.test.widget.ViewFlow;

/**
 * 首页试卷Fragment。
 * 
 * (用于按试卷类型列表试卷)
 * @author jeasonyoung
 * @since 2015年7月3日
 */
public class MainHomePaperFragment extends Fragment{
	private static final String TAG = "MainHomePaperFragment";
	private final MainActivity mainActivity;
	private final SubjectTotalModel subject;
	
	private ViewFlow viewFlow;
	private View convertView;
	//private TitleFlowIndicator titleIndicator;
	
	//数据源
	private final List<PaperModel.PaperType> dataSources;
	private final HomePaperTypeAdapter adapter;
	
	private PaperDao dao;
	/**
	 * 构造函数。
	 * @param mainActivity
	 * @param subject
	 */
	public MainHomePaperFragment(MainActivity mainActivity, SubjectTotalModel subject){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
		this.subject = subject;
		//初始化数据源
		this.dataSources = new ArrayList<PaperModel.PaperType>();
		this.adapter = new HomePaperTypeAdapter(this.mainActivity, (this.subject == null ? null : this.subject.getCode()), this.dataSources);
	}
	/*
	 * 重载UI布局
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "加载动态UI布局...");
		//1.加载布局
		this.convertView = inflater.inflate(R.layout.ui_main_home_paper, container, false);
		//2.加载标题
		//this.titleIndicator = (TitleFlowIndicator)view.findViewById(R.id.home_paper_titlegroups);
		//2.1设置标题数据适配器
		//indicator.setTitleProvider(this.adapter);
		//3.试卷列表
		this.viewFlow = (ViewFlow)this.convertView.findViewById(R.id.home_paper_viewpaper);
		//4.添加试卷适配器
		this.viewFlow.setAdapter(this.adapter);
		//5.设置标题
		///viewPaper.setFlowIndicator(this.titleIndicator);
		//返回
		return this.convertView;
	}
	/*
	 * 重载启动
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		//1.启动等待动画
		this.mainActivity.waitingViewDialog.show();
		//2.加载数据
		new LoadDataAsyncTask().execute(this.mainActivity, this.subject);
	}
	//异步加载数据。
	private class LoadDataAsyncTask extends AsyncTask<Object, Void, PaperModel.PaperType[]>{
		/*
		 * 异步线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected PaperType[] doInBackground(Object... params) {
			try {
				Log.d(TAG, "异步线程加载数据...");
				final SubjectTotalModel subjectModel = (SubjectTotalModel)params[1];
				if(subjectModel == null) return null;
				//惰性加载数据
				if(dao == null){
					dao = new PaperDao((Context)params[0]);
				}
				List<PaperModel.PaperType> paperTypes = dao.findPaperTypes(subjectModel.getCode());
				if(paperTypes != null && paperTypes.size() > 0){
					return paperTypes.toArray(new PaperModel.PaperType[0]);
				}
			} catch (Exception e) {
				Log.e(TAG, "异步加载数据异常:" + e.getMessage(), e);
			}
			return null;
		}
		/*
		 * 前台主线程处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(PaperType[] result) {
			//加载数据
			if(result != null){
				//清空数据源
				if(dataSources.size() > 0) dataSources.clear();
				//添加数据处理
				dataSources.addAll(Arrays.asList(result));
				//通知数据适配器更新数据
				adapter.notifyDataSetChanged();
			}
			if(convertView != null){
				//加载标题
				final TitleFlowIndicator titleIndicator = (TitleFlowIndicator)convertView.findViewById(R.id.home_paper_titlegroups);
				//设置标题数据适配器
				titleIndicator.setTitleProvider(adapter);
				//设置标题View Flow
				viewFlow.setFlowIndicator(titleIndicator);
			}
			//关闭等待动画
			mainActivity.waitingViewDialog.cancel();
		}
	}
}