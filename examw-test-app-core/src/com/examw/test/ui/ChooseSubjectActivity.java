package com.examw.test.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.adapter.SubjectListAdapter;
import com.examw.test.app.AppConstant;
import com.examw.test.domain.PaperRecord;
import com.examw.test.domain.Subject;
import com.examw.test.utils.ToastUtils;

/**
 * 科目选择
 * @author fengwei.
 * @since 2014年12月4日 上午9:34:33.
 */
public class ChooseSubjectActivity extends BaseActivity implements OnClickListener{
	//private static final String TAG = "ChooseSubjectActivity";
	private ListView courseList;
	private LinearLayout reloadLayout;
	private ProgressDialog proDialog;
	private ArrayList<Subject> subjects;
	
	private Button btnLast,btnShowPop;
	private PopupWindow popWindow;
	private PaperRecord r;
	private int action;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_choose_subject);
		action = getIntent().getIntExtra("action", 0);
		initViews();
		//if(action != AppConstant.ACTION_FAVORITE)
			//initData();
	}
	@SuppressLint("HandlerLeak")
	private void initData()
	{
		//LogUtil.d("初始化科目数据,action == "+action);
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what)
				{
				case 0:
					new GetDataTask().execute();
					break;
				case 1:
					proDialog.dismiss();
					courseList.setAdapter(new SubjectListAdapter(ChooseSubjectActivity.this,subjects,action));
					break;
				case -1:
					proDialog.dismiss();
					ToastUtils.show(ChooseSubjectActivity.this, "暂无记录");
					break;
				}
			}
		};
		//final String username = ((AppContext) getApplication()).getUsername();
		proDialog = ProgressDialog.show(ChooseSubjectActivity.this, null, "加载中...",true, true);
		proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		new Thread(){
			public void run() {
				try{
					//subjects = ExamDao.findSubjects(username);
					if(subjects!=null&&subjects.size()>0)
					{
						//if(action == AppConstant.ACTION_ERROR)
							//subjects = PaperRecordDao.getCount(subjects, username);
						//else if(action == AppConstant.ACTION_FAVORITE)
							//subjects = FavoriteDao.getCount(subjects, username);
						handler.sendEmptyMessage(1);
					}else
						handler.sendEmptyMessage(0);
				}catch(Exception e)
				{
					e.printStackTrace();
					handler.sendEmptyMessage(-1);
				}
			};
		}.start();
	}
	private void initViews()
	{
//		String title = action == AppConstant.ACTION_ERROR?"错题记录":
//				action == AppConstant.ACTION_FAVORITE?"我的收藏":
//			action == AppConstant.ACTION_CHAPTER?"章节练习":"模拟考试";
		//((TextView) this.findViewById(R.id.title)).setText(title);
		this.courseList = (ListView) this.findViewById(R.id.course_list);
		this.reloadLayout = (LinearLayout) this.findViewById(R.id.reload);//重载视图
		this.btnShowPop = (Button) this.findViewById(R.id.showPop);	//显示上一次的练习
		this.findViewById(R.id.btn_goback).setOnClickListener(this);//返回按钮
		this.btnShowPop.setOnClickListener(this);
		this.findViewById(R.id.btn_reload).setOnClickListener(this);//重载按钮
		this.courseList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				itemClick(subjects.get(arg2));
			}
		});
	}
	private void itemClick(Subject subject)
	{
		Intent intent = null;
		//设置当前所在的科目
		//((AppContext)this.getApplication()).setCurrentSubjectCode(subject.getSubjectId());
//		switch(action)
//		{
//		case AppConstant.ACTION_ERROR:
//		case AppConstant.ACTION_FAVORITE:
//			if(subject.getTotal()==null || subject.getTotal().equals(0))
//			{
//				ToastUtils.show(this, "暂无记录");
//				return;
//			}
//			intent = new Intent(ChooseSubjectActivity.this,PaperDoPracticeActivity.class);
//			intent.putExtra("subjectId", subject.getSubjectId());
//			intent.putExtra("action", action);
//			break;
//		case AppConstant.ACTION_CHAPTER:
//			intent = new Intent(ChooseSubjectActivity.this,ChapterActivity.class);
//			intent.putExtra("subjectId", subject.getSubjectId());
//			intent.putExtra("subjectName",subject.getName());
//			intent.putExtra("action", action);
//			break;
//		case AppConstant.ACTION_NONE:
//			intent = new Intent(ChooseSubjectActivity.this,SimulateActivity.class);
//			intent.putExtra("subjectId", subject.getSubjectId());
//			intent.putExtra("subjectName",subject.getName());
//			break;
//		}
		this.startActivity(intent);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btn_goback:
			this.finish();
			break;
		case R.id.btn_reload:
			new GetDataTask().execute();
			break;
		case R.id.showPop:
			showPop(v);
			break;
		case R.id.lastPractice:
			gotoDoExamActivity();
			break;
		}
	}
	private void gotoDoExamActivity()
	{
		if(r == null ){
			popWindow.dismiss();
			return;
		}
		popWindow.dismiss();
		Intent intent = new Intent(this,PaperInfoActivity.class);
		intent.putExtra("paperId",r.getPaperId());
		this.startActivity(intent);
	}
	private class GetDataTask extends AsyncTask<String,Void,ArrayList<Subject>>
	{
		@Override
		protected void onPreExecute() {
			if(proDialog!=null && !proDialog.isShowing())
				proDialog.show();
			super.onPreExecute();
		}
		@Override
		protected ArrayList<Subject> doInBackground(String... params) {
			try
			{
				//ArrayList<Subject> result = ApiClient.getSubjectList((AppContext)(ChooseSubjectActivity.this.getApplication()));
//				ProductDao.saveSubjects(result);
				//return result;
				return null;
			}catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		protected void onPostExecute(ArrayList<Subject> result) {
			if(proDialog!=null)
			{
				proDialog.dismiss();
			}
			if(result!=null)
			{
				subjects = result;
				courseList.setAdapter(new ArrayAdapter<Subject>(ChooseSubjectActivity.this,R.layout.item_choose_subject_list,R.id.list_title,subjects));
				reloadLayout.setVisibility(View.GONE);
			}else
			{
				reloadLayout.setVisibility(View.VISIBLE);
			}
		};
	}
	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void initPop()
	{
		btnShowPop.setVisibility(View.GONE);
		//String userName = ((AppContext)(ChooseSubjectActivity.this.getApplication())).getUsername();
		//r = PaperRecordDao.findLastRecord(userName,AppConstant.PAPER_TYPE_REAL+","+AppConstant.PAPER_TYPE_SIMU+","+AppConstant.PAPER_TYPE_FORECAST+","+AppConstant.PAPER_TYPE_PRACTICE);
		if(r==null) return;
		final Handler mHandler = new Handler();
		if(popWindow == null)
		{
			View v = LayoutInflater.from(this).inflate(R.layout.pop_last_exam, null);
			btnLast = (Button) v.findViewById(R.id.lastPractice);
			btnLast.getBackground().setAlpha(100);
			btnLast.setOnClickListener(this);
			popWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			popWindow.setAnimationStyle(R.style.AnimationPreview);
			popWindow.setBackgroundDrawable(new BitmapDrawable());
			popWindow.setFocusable(true); // 使其聚焦
			popWindow.setOutsideTouchable(true);
			popWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					btnShowPop.setVisibility(View.VISIBLE);
				}
			});
		}
		/***************** 以下代码用来循环检测activity是否初始化完毕 ***************/
		Runnable showPopWindowRunnable = new Runnable() {
			@Override
			public void run() {
				// 得到activity中的根元素
				View view = findViewById(R.id.parent);
				// 如何根元素的width和height大于0说明activity已经初始化完毕
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					// 显示popwindow
					popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
					// 停止检测
					mHandler.removeCallbacks(this);
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					mHandler.postDelayed(this, 50);
				}
			}
		};
		// 开始检测
		mHandler.post(showPopWindowRunnable);
		/****************** 以上代码用来循环检测activity是否初始化完毕 *************/
	}
	private void showPop(View v)
	{
		if(popWindow!=null)
		{
			popWindow.showAtLocation(findViewById(R.id.parent), Gravity.BOTTOM, 0, 0);
		}
		v.setVisibility(View.GONE);
	}
	@Override
	protected void onStart() {
		super.onStart();
//		if(action == AppConstant.ACTION_NONE)
//			initPop();
//		if(action == AppConstant.ACTION_FAVORITE)
//			initData();
	}
	@Override
	protected void onDestroy() {
		if(proDialog != null) proDialog.dismiss();
		super.onDestroy();
	}
}
