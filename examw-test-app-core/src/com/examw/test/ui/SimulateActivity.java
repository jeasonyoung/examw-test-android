package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.adapter.PaperListAdapter;
import com.examw.test.domain.Paper;
import com.examw.test.support.ReturnBtnClickListener;
import com.examw.test.widget.NewDataToast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


/**
 * 模拟考试
 * @author fengwei.
 * @since 2014年11月26日 下午3:17:13.
 */
public class SimulateActivity extends BaseActivity{
	private String classid;
	private TextView title;
	private int areacode;
	private PullToRefreshListView paperListView;
	private ArrayList<Paper> papers;
	private ProgressDialog dialog;
	private Handler handler;
	private LinearLayout nodata,loadingLayout,reloadLayout;
	private View lvPapers_footer;
	private ProgressBar lvPapers_foot_progress;
	private TextView lvPapers_foot_more;
//	private PaperDao dao;
	private int page,total;
	private PaperListAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_paper_list);
		initViews();
		initData();
	}
	private void initViews()
	{
		this.title = (TextView) this.findViewById(R.id.title);
		this.nodata = (LinearLayout) this.findViewById(R.id.noneDataLayout);
		this.paperListView = (PullToRefreshListView) this.findViewById(R.id.contentListView);
		this.loadingLayout = (LinearLayout) this.findViewById(R.id.loadingLayout);
		this.reloadLayout = (LinearLayout) this.findViewById(R.id.reload);
		this.lvPapers_footer = getLayoutInflater().inflate(R.layout.listview_footer,null);
		this.lvPapers_foot_more = (TextView) lvPapers_footer.findViewById(R.id.listview_foot_more);
		this.lvPapers_foot_progress = (ProgressBar) lvPapers_footer.findViewById(R.id.listview_foot_progress);
		this.findViewById(R.id.btn_goback).setOnClickListener(new ReturnBtnClickListener(this));
		this.paperListView.getRefreshableView().addFooterView(lvPapers_footer); //在setAdapter之前addFooter
		this.paperListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
					new GetPaperTask().execute();
			}
		});
		this.paperListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(arg2 > papers.size())
				{
					footerClick();
					return;
				}
				Intent intent = new Intent(SimulateActivity.this,PaperInfoActivity.class);
//				intent.putExtra("paperJson",gson.toJson(papers.get(arg2-1)));
				SimulateActivity.this.startActivity(intent);
			}
		});
	}
	private void initData()
	{
		/*
		 * 先从数据库中查,
		 * 查不到,联网查,再插入数据库
		 * 更新,
		 * 联网更新,插入数据库
		 * 更多,查数据库
		 */
//		dao = new PaperDao(this);
//		gson = new Gson();
//		Intent intent = this.getIntent();
//		String name = intent.getStringExtra("classname");
//		this.title.setText(name==null?"本地试卷列表":name);
//		this.classid = intent.getStringExtra("classid");
//		this.areacode = AreaUtils.areaCode;	//地区代码
		dialog = ProgressDialog.show(SimulateActivity.this,null,"努力加载中请稍候",true,true);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		handler = new MyHandler(this);
		this.loadingLayout.setVisibility(View.VISIBLE);
		new GetPaperListThread().start();
	}
	private class GetPaperTask extends AsyncTask<String,Void,List<Paper>>
	{
		@Override
		protected List<Paper> doInBackground(String... params) {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(3000);return null;
//				PaperList list1 = XMLParseUtil.parsePaperList(ApiClient.getPaperListData((AppContext)getApplication(), classid, String.valueOf(areacode)));
//				dao.insertPaperList(list1.getPaperlist());
//				return list1;
			}catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		@Override
		protected void onPostExecute(List<Paper> result) {
			// TODO Auto-generated method stub
			int newdata = 0;
			if (result!=null&&result.size() > 0) {
				for (Paper paper1 : result) {
					boolean b = false;
					for (Paper paper2 : papers) {
						if (paper1.getPaperId() != paper2.getPaperId()) {
							b = true;
							break;
						}
					}
					if (!b)
						newdata++;
				}
			} else {
				newdata = 0;
			}
			if(newdata == 0)
			{
				NewDataToast.makeText(SimulateActivity.this, "没有更新", true).show();
			}else
			{
				papers.clear();// 先清除原有数据
				papers.addAll(result);
				NewDataToast.makeText(SimulateActivity.this, newdata+"条更新", true).show();
			}
			paperListView.onRefreshComplete();
		}
	}
	
	private class GetPaperListThread extends Thread
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			PaperList list =  dao.findAllPapers(classid,page);
//			if(list.getPaperlist().size()>0)
//			{
//				total = list.getPaperCount(); //试卷总数
//				papers = list.getPaperlist();
//				//本地数据库中有试卷
//				handler.sendEmptyMessage(1);
//			}else
//			{
//				//本地数据库中没有试卷,访问网络
//				try{
//					PaperList list1 = XMLParseUtil.parsePaperList(ApiClient.getPaperListData((AppContext)getApplication(), classid, String.valueOf(areacode)));
//					dao.insertPaperList(list1.getPaperlist());
//					papers = list1.getPaperlist();
//					total = list1.getPaperCount();
//					handler.sendEmptyMessage(1);
//				}catch(Exception e)
//				{
//					Message msg = handler.obtainMessage();
//					msg.what = -1;
//					handler.sendMessage(msg);
//				}
//			}
		}
	}
	static class MyHandler extends Handler {
        WeakReference<SimulateActivity> mActivity;
        MyHandler(SimulateActivity activity) {
                mActivity = new WeakReference<SimulateActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
        	SimulateActivity theActivity = mActivity.get();
        	theActivity.loadingLayout.setVisibility(View.GONE);
                switch (msg.what) {
                case 1:
                	theActivity.dialog.dismiss();
                	if(theActivity.papers!=null&&theActivity.papers.size()>0)
                	{
                		theActivity.mAdapter = new PaperListAdapter(theActivity,theActivity.papers);
                    	theActivity.paperListView.setAdapter(theActivity.mAdapter);
                    	if(theActivity.total<=10)
                    	{
                    		theActivity.lvPapers_footer.setVisibility(View.GONE);
                    	}else{
                    		theActivity.lvPapers_footer.setVisibility(View.VISIBLE);
                    		theActivity.lvPapers_foot_progress.setVisibility(View.GONE);
                    		theActivity.lvPapers_foot_more.setText("更多");
                    	}
                	}else
                	{
                		theActivity.nodata.setVisibility(View.VISIBLE);//无数据显示
                	}
                			//theActivity.expandList.setAdapter(new MyExpandableAdapter(theActivity, theActivity.group, theActivity.child));
                			//设置adapter
                	break;
                case -2:
               		theActivity.dialog.dismiss();
               		theActivity.nodata.setVisibility(View.VISIBLE);//无数据显示
               		Toast.makeText(theActivity, "您没有购买课程", Toast.LENGTH_SHORT).show();//提示
                	break;
                case -1:
                	//连不上,
                	theActivity.dialog.dismiss();
            		theActivity.reloadLayout.setVisibility(View.VISIBLE);//无数据显示
            		Toast.makeText(theActivity, "暂时连不上服务器,请稍候", Toast.LENGTH_SHORT).show();//提示
            		break;
                case -3:
                	theActivity.dialog.dismiss();
                	theActivity.nodata.setVisibility(View.VISIBLE);//无数据显示
            		Toast.makeText(theActivity, "本地没有数据", Toast.LENGTH_SHORT).show();//提示
            		break;
                case 4:
                	theActivity.papers.addAll((ArrayList<Paper>) msg.obj);
                	theActivity.mAdapter.notifyDataSetChanged();
                	//判断剩余加载量
                	if(theActivity.total > theActivity.papers.size())
                	{
                		theActivity.lvPapers_foot_progress.setVisibility(View.GONE);
                		theActivity.lvPapers_foot_more.setText("更多");
                	}else
                	{
                		theActivity.lvPapers_footer.setVisibility(View.GONE);
                	}
                	break;
                case -4:
                	theActivity.lvPapers_foot_progress.setVisibility(View.GONE);
                	theActivity.lvPapers_foot_more.setText("加载失败");
                	break;
                }
        }
	}
	@Override
	protected void onDestroy() {
		if(dialog!=null)
		{
			dialog.dismiss();	
		}
		super.onDestroy();
	}
	@Override
	protected void onPause() {
		super.onPause();
	};
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	private void footerClick()
	{
		lvPapers_foot_progress.setVisibility(View.VISIBLE);
		lvPapers_foot_more.setText("玩命加载中");
		page = page + 1;
		new Thread(){
			public void run() {
				try{
//					PaperList list =  dao.findAllPapers(classid,page);
					Message msg = handler.obtainMessage();
					msg.what = 4;
//					msg.obj = list.getPaperlist();
					handler.sendMessage(msg);
				}catch(Exception e)
				{
					e.printStackTrace();
					handler.sendEmptyMessage(-4);
				}
			};
		}.start();
	}
}
