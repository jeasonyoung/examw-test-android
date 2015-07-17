package com.examw.test.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.examw.test.R;
import com.examw.test.dao.PaperDao;
import com.examw.test.model.PaperModel;

/**
 * 首页试卷Fragment。
 * 
 * (用于按试卷类型列表试卷)
 * @author jeasonyoung
 * @since 2015年7月3日
 */
public class MainHomePaperFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
	private static final String TAG = "MainHomePaperFragment";
	private final MainActivity mainActivity;
	private final PaperDao.SubjectTotalModel subject;
	
	private RadioGroup titleGroups;
	private ViewPager viewPaper;
	
	private final List<ViewSubFragmentWrapper> dataSource;
	private final ViewPagerAdapter adapter;
	
	private final int titleNormalColor,titleCheckedColor;
	private final float titleFontSize;
	/**
	 * 构造函数。
	 * @param mainActivity
	 * 主界面Activity。
	 * @param subjectCode
	 * 科目
	 */
	public MainHomePaperFragment(final MainActivity mainActivity,final PaperDao.SubjectTotalModel subject){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
		this.subject = subject;
		
		//数据源
		this.dataSource = new ArrayList<ViewSubFragmentWrapper>();
		//初始化数据适配器
		this.adapter = new ViewPagerAdapter(this.mainActivity.getSupportFragmentManager(), this.dataSource);
		//加载配置颜色
		Resources resources = this.mainActivity.getResources();
		this.titleNormalColor = resources.getColor(R.color.gray);
		this.titleCheckedColor = resources.getColor(R.color.white);
		//标题字体
		this.titleFontSize = resources.getDimension(R.dimen.fontsize_l);
	}
	/*
	 * 重载UI布局
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "加载动态UI布局...");
		//1.加载布局文件
		final View view = inflater.inflate(R.layout.ui_main_home_paper, container, false);
		//2.加载标题
		this.titleGroups = (RadioGroup)view.findViewById(R.id.home_paper_titlegroups);
		this.titleGroups.setOnCheckedChangeListener(this);
		//3.试卷列表
		this.viewPaper = (ViewPager)view.findViewById(R.id.home_paper_viewpaper);
		//4.添加试卷适配器
		this.viewPaper.setAdapter(this.adapter);
		//5.添加事件处理
		this.viewPaper.setOnPageChangeListener(this);
		//返回
		return view;
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
		new LoadDataTask().execute();
	}
	/*
	 * 标题选中处理。
	 * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d(TAG, "选中标题:" + checkedId);
		int count = group.getChildCount(), index = -1;
		if(count > 0){
			for(int i = 0; i < count; i++){
				RadioButton btn = (RadioButton)group.getChildAt(i);
				if(btn != null){
					if(btn.getId() == checkedId){
						index = i;
						btn.setTextColor(titleCheckedColor);
					}else {
						btn.setTextColor(titleNormalColor);
					}
				}
			}
			if(index > -1){
				viewPaper.setCurrentItem(index);
			}
		}
	}
	/*
	 * 重载
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		Log.d(TAG, "onPageScrolled=" + position +"[offset="+positionOffset+",offsetPixels="+positionOffsetPixels+"]");
	}
	/*
	 * 重载翻页选中。
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
	 */
	@Override
	public void onPageSelected(int position) {
		Log.d(TAG, "onPageSelected=" + position);
		if(titleGroups != null && titleGroups.getChildCount() > position){
			RadioButton btn = (RadioButton)titleGroups.getChildAt(position);
			if(btn != null){
				titleGroups.check(btn.getId());
			}
		}
	}
	/*
	 * 重载。
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
	 */
	@Override
	public void onPageScrollStateChanged(int state) {
		 Log.d(TAG, "onPageScrollStateChanged = " + state);
	}
	/**
	 * 加载数据任务。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月3日
	 */
	private class LoadDataTask extends AsyncTask<Void, Void, Void>{
		/*
		 * 后台线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "后台线程加载数据...");
			//查询数据
			if(subject != null){
				PaperDao dao = new PaperDao(mainActivity);
				List<PaperModel.PaperType> paperTypes = dao.findPaperTypes(subject.getCode());
				if(paperTypes != null && paperTypes.size() > 0){
					//清除数据源
					dataSource.clear();
					//
					for(PaperModel.PaperType type : paperTypes){
						//添加数据源
						dataSource.add(new ViewSubFragmentWrapper(subject, type));
					}
				}
			}
			return null;
		}
		/*
		 * 主线程更新UI。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			Log.d(TAG, "主线程更新UI...");
			
			//RadioGroup集合处理
			if(titleGroups != null && mainActivity != null){
				//3.通知数据适配器更新
				adapter.notifyDataSetChanged();
				//1.清空集合选项
				titleGroups.removeAllViews();
				//2.添加标题
				if(dataSource != null && dataSource.size() > 0){
					int index = 0,firstBtnId = -1;
					//
					for(ViewSubFragmentWrapper wrapper : dataSource){
						if(wrapper == null)continue;
						//创建选项
						RadioButton btn = new RadioButton(mainActivity);
						btn.setButtonDrawable(android.R.color.transparent);
						btn.setTextColor(titleNormalColor);
						btn.setTextSize(titleFontSize);
						btn.setText(wrapper.getType().getName() + "  ");
						//添加到集合
						titleGroups.addView(btn,LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
						if(index == 0){
							firstBtnId = btn.getId();
						}
						index ++;
					}
					//4.设置选中
					if(firstBtnId > -1){
						titleGroups.check(firstBtnId);
					}
				}
			}
			//5.关闭等待动画
			mainActivity.waitingViewDialog.cancel();
		}
	}
	/**
	 * 列表视图适配器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月3日
	 */
	private class ViewPagerAdapter extends FragmentStatePagerAdapter{
		private List<ViewSubFragmentWrapper> list;
		/**
		 * 构造函数。
		 * @param fm
		 * @param listFragments
		 */
		public ViewPagerAdapter(final FragmentManager fm, final List<ViewSubFragmentWrapper> listViews) {
			super(fm);
			this.list = listViews;
			Log.d(TAG, "初始化ViewPagerAdpter适配器...");
		}
		/*
		 * 加载选中的fragment。
		 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem(int position) {
			Log.d(TAG, "加载fragment-pos=" + position);
			ViewSubFragmentWrapper wrapper = this.list.get(position);
			return new MainHomePaperPagerFragment(mainActivity, wrapper.getSubject(), wrapper.getType());
		}
		/*
		 * 重载。
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.list.size();
		}
	}
	//
	private class ViewSubFragmentWrapper{
		private PaperDao.SubjectTotalModel subject;
		private PaperModel.PaperType type;
		/**
		 * 构造函数。
		 * @param subject
		 * @param type
		 */
		public ViewSubFragmentWrapper(PaperDao.SubjectTotalModel subject,PaperModel.PaperType type){
			this.subject = subject;
			this.type = type;
		}
		/**
		 * 获取科目。
		 * @return 科目。
		 */
		public PaperDao.SubjectTotalModel getSubject() {
			return subject;
		}
		/**
		 * 获取试卷类型。
		 * @return 试卷类型。
		 */
		public PaperModel.PaperType getType() {
			return type;
		}
	}
}