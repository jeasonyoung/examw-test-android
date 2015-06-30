package com.examw.test.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.adapter.CategoryAdapter;
import com.examw.test.dao.DownloadResultListener;
import com.examw.test.dao.SwitchProductDao;
import com.examw.test.model.sync.CategoryModel;
import com.examw.test.widget.WaitingViewDialog;
/**
 * 切换产品Activity。
 * 
 * @author jeasonyoung
 * @since 2015年6月26日
 */
public class SwitchActivity extends FragmentActivity {
	private static final String TAG = "SwitchActivity";
	private SubFragmentType subType;
	private MyHandler msgHandler;
	/*
	 * 重载创建。
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//加载布局XML 
		this.setContentView(R.layout.ui_switch_main);
		//
		this.msgHandler = new MyHandler(this);
		//创建考试类别Fragment
		this.createSubFragment(SubFragmentType.Category);
	}
	//创建子Fragment
	private void createSubFragment(SubFragmentType type){
		Log.d(TAG, "创建子Fragment:" + type);
		Fragment fragment = null;
		switch(this.subType = type){
			case Category:{//考试分类。
				Log.d(TAG, "考试分类Fragment...");
				fragment = new CategoryFragment();
				break;
			}
			case Exam:{//考试
				Log.d(TAG, "考试列表Fragment...");
				fragment = new ExamFragment();
				break;
			}
			case Product:{//产品
				Log.d(TAG, "产品列表Fragment...");
				fragment = new ProductFragment();
				break;
			}
		}
		if(fragment != null){
			this.getSupportFragmentManager()
			.beginTransaction()
			.addToBackStack(null)
			.replace(R.id.switch_main_fragment_replace, fragment)
			.commit();
		}
	}
	 
	/*
	 * 键盘响应。
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && this.subType == SubFragmentType.Category){
			///TODO:退出处理
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 主线程消息处理。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月29日
	 */
	private static class MyHandler extends Handler{
		private Context context;
		/**
		 *  构造函数。
		 * @param context
		 */
		public MyHandler(Context context){
			this.context = context;
		}
		/*
		 * 重载。
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			if(msg.obj instanceof String){
				Toast.makeText(this.context, (String)msg.obj, Toast.LENGTH_LONG).show();
				return;
			}
			super.handleMessage(msg);
		}
	}
	/**
	 * 子Fragment类型枚举。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月26日
	 */
	private enum SubFragmentType{ Category,Exam, Product};
	/**
	 * 考试类别Fragment。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月26日
	 */
	class CategoryFragment extends Fragment{
		private ListView listView;
		private WaitingViewDialog waitingViewDialog;
	    private SwitchProductDao dao;
		/*
		 * 重载创建视图。
		 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Log.d(TAG, "加载考试类别视图...");
			//加载界面视图
			final View view = inflater.inflate(R.layout.ui_switch_category, container, false);
			//加载列表
			this.listView = (ListView)view.findViewById(R.id.listSwitchCategory);
			//this.listView.setAdapter(adapter);
			this.dao =  new SwitchProductDao(getActivity());
			return view;
		}
		/*
		 * 重载开始。
		 * @see android.support.v4.app.Fragment#onStart()
		 */
		@Override
		public void onStart() {
			super.onStart();
			//初始化等待动画
			this.waitingViewDialog = new WaitingViewDialog(this.getActivity());
			//显示等待
			this.waitingViewDialog.show();
			//加载数据。
			this.loadDataTask.execute((String)null);
		}
		//异步加载数据。
		private AsyncTask<String, Void, Object[]> loadDataTask = new AsyncTask<String, Void, Object[]>(){
			/*
			 * 异步线程加载数据。
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected Object[] doInBackground(String... params) {
				Log.d(TAG, "异步线程加载数据。");
				//获取查询参数。
				String searchName = params[0];
				if(StringUtils.isNotBlank(searchName)){
					///TODO:模糊查询考试。
					return null;
				}else {
					 Log.d(TAG, "加载考试分类");
					 //检查是否有本地数据缓存
					 if(!dao.hasLocalCategories()){
						 //从网络加载数据
						 dao.loadCategoriesFromNetWorks(new DownloadResultListener() {
							/*
							 * 重载。
							 * @see com.examw.test.dao.DownloadResultListener#onComplete(boolean, java.lang.String)
							 */
							@Override
							public void onComplete(boolean result, String msg) {
								if(!result){
									 Message message = new Message();
									 message.what = -1;
									 message.obj = (StringUtils.isBlank(msg) ? "未知异常！" : msg);
									 msgHandler.sendMessage(message);
								}
							}
						});
					 }
					 //返回数据
					 return (dao.getCategories() == null ? null : dao.getCategories().toArray(new CategoryModel[0]));
				}
			}
			/*
			 * 主线程更新
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(Object result[]) {
				//取消等待动画
				waitingViewDialog.cancel();
				//结果数据处理
				if(result != null && result.length > 0){
					//考试分类数据
					if(result[0] instanceof CategoryModel){
						List<CategoryModel> list = new ArrayList<CategoryModel>();
						for(Object obj : result){
							if(obj == null)continue;
							if(obj instanceof CategoryModel){
								list.add((CategoryModel)obj);
							}
						}
						//装载到适配器
						listView.setAdapter(new CategoryAdapter(getActivity(), list));
					}
					//
				}
				
			};
		};
	}
	/**
	 * 考试Fragment。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月26日
	 */
	private class ExamFragment extends Fragment{
		/*
		 * 重载创建。
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}
		/*
		 * 重载创建视图。
		 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}
	/**
	 * 产品Fragment。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月26日
	 */
	private class ProductFragment extends Fragment{
		/*
		 * 重载创建。
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}
		/*
		 * 重载创建视图。
		 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}
}