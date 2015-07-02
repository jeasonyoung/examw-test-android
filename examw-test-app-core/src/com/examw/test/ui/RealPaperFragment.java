package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.adapter.PaperListAdapter;
import com.examw.test.dao.PaperDao;
import com.examw.test.model.PaperModel;
import com.examw.test.widget.NewDataToast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 历年真题分页面
 * @author fengwei.
 * @since 2014年12月3日 上午11:07:46.
 */
public class RealPaperFragment extends Fragment{
	private PullToRefreshListView paperListView;
	private ArrayList<PaperModel> papers;
	private Handler handler;
	private LinearLayout nodataLayout,loadingLayout,reloadLayout;
	private View lvPapers_footer;
	private ProgressBar lvPapers_foot_progress;
	private TextView lvPapers_foot_more;
	private int page,total;
	private PaperListAdapter mAdapter;
	private String subjectId;
	private String paperType;
	private String username;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//LogUtil.d("RealPaperFragment 创建");
		View v = inflater.inflate(R.layout.paper_fragment, container, false);
		Bundle data = this.getArguments();
		subjectId = data.getString("subjectId");
		paperType = data.getString("paperType");
		// appContext.recoverLoginStatus();
		//username = ((AppContext) this.getActivity().getApplication()).getUsername();
		initViews(v);
		initData();
		return v;
	}
	@SuppressLint("InflateParams")
	private void initViews(View v)
	{
		this.paperListView = (PullToRefreshListView) v.findViewById(R.id.contentListView);
		this.loadingLayout = (LinearLayout) v.findViewById(R.id.loadingLayout);
		this.nodataLayout = (LinearLayout) v.findViewById(R.id.nodataLayout);
		this.reloadLayout = (LinearLayout) v.findViewById(R.id.reload);
		this.lvPapers_footer = this.getActivity().getLayoutInflater().inflate(R.layout.listview_footer,null);
		this.lvPapers_foot_more = (TextView) lvPapers_footer.findViewById(R.id.listview_foot_more);
		this.lvPapers_foot_progress = (ProgressBar) lvPapers_footer.findViewById(R.id.listview_foot_progress);
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
				if(arg2 > papers.size())
				{
					footerClick();
					return;
				}
				Intent intent = new Intent(RealPaperFragment.this.getActivity(),PaperInfoActivity.class);
				intent.putExtra("paperId",papers.get(arg2-1).getId());
				RealPaperFragment.this.startActivity(intent);
			}
		});
	}
	private void initData()
	{
		/*
		 * 先从数据库中查,
		 * 更多,查数据库
		 */
		handler = new MyHandler(this);
		this.loadingLayout.setVisibility(View.VISIBLE);
		new GetPaperListThread().start();
	}
	private class GetPaperTask extends AsyncTask<String,Void,ArrayList<PaperModel>>
	{
		@Override
		protected ArrayList<PaperModel> doInBackground(String... params) {
			try{
				Thread.sleep(2000);return null;
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
		protected void onPostExecute(ArrayList<PaperModel> result) {
			// TODO Auto-generated method stub
			int newdata = 0;
//			if (result!=null&&result.getPaperlist().size() > 0) {
//				for (Paper paper1 : result.getPaperlist()) {
//					boolean b = false;
//					for (Paper paper2 : papers) {
//						if (paper1.getPaperId() != paper2.getPaperId()) {
//							b = true;
//							break;
//						}
//					}
//					if (!b)
//						newdata++;
//				}
//			} else {
//				newdata = 0;
//			}
			if(newdata == 0)
			{
				NewDataToast.makeText(RealPaperFragment.this.getActivity(), "没有更新", true).show();
			}else
			{
				papers.clear();// 先清除原有数据
//				papers.addAll(result.getPaperlist());
				NewDataToast.makeText(RealPaperFragment.this.getActivity(), newdata+"条更新", true).show();
			}
			paperListView.onRefreshComplete();
		}
	}
	
	private class GetPaperListThread extends Thread
	{
		@Override
		public void run() {
			//papers = PaperDao.findPapers(subjectId,paperType,username);
			handler.sendEmptyMessage(1);
		}
	}
	static class MyHandler extends Handler {
        WeakReference<RealPaperFragment> mActivity;
        MyHandler(RealPaperFragment activity) {
                mActivity = new WeakReference<RealPaperFragment>(activity);
        }
        @SuppressWarnings("unchecked")
		@Override
        public void handleMessage(Message msg) {
        	RealPaperFragment theActivity = mActivity.get();
        	theActivity.loadingLayout.setVisibility(View.GONE);
                switch (msg.what) {
                case 1:
                	if(theActivity.papers!=null&&theActivity.papers.size()>0)
                	{
                		theActivity.mAdapter = new PaperListAdapter(theActivity.getActivity(),theActivity.papers);
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
                		theActivity.nodataLayout.setVisibility(View.VISIBLE);//无数据显示
                	}
                			//theActivity.expandList.setAdapter(new MyExpandableAdapter(theActivity, theActivity.group, theActivity.child));
                			//设置adapter
                	break;
                case -2:
               		theActivity.nodataLayout.setVisibility(View.VISIBLE);//无数据显示
               		Toast.makeText(theActivity.getActivity(), "您没有购买课程", Toast.LENGTH_SHORT).show();//提示
                	break;
                case -1:
                	//连不上,
            		theActivity.reloadLayout.setVisibility(View.VISIBLE);//无数据显示
            		Toast.makeText(theActivity.getActivity(), "暂时连不上服务器,请稍候", Toast.LENGTH_SHORT).show();//提示
            		break;
                case -3:
                	theActivity.nodataLayout.setVisibility(View.VISIBLE);//无数据显示
            		Toast.makeText(theActivity.getActivity(), "本地没有数据", Toast.LENGTH_SHORT).show();//提示
            		break;
                case 4:
                	theActivity.papers.addAll((ArrayList<PaperModel>) msg.obj);
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
