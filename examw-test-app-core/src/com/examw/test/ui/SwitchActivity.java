package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.adapter.CategoryAdapter;
import com.examw.test.adapter.ExamAdapter;
import com.examw.test.adapter.ProductAdapter;
import com.examw.test.app.AppContext;
import com.examw.test.app.AppSettings;
import com.examw.test.dao.DownloadDao;
import com.examw.test.dao.DownloadResultListener;
import com.examw.test.dao.SwitchProductDao;
import com.examw.test.model.sync.CategoryModel;
import com.examw.test.model.sync.ExamModel;
import com.examw.test.model.sync.ProductModel;
import com.examw.test.support.MsgHandler;
import com.examw.test.widget.WaitingViewDialog;
/**
 * 切换产品Activity。
 * 
 * @author jeasonyoung
 * @since 2015年6月26日
 */
public class SwitchActivity extends FragmentActivity implements FragmentManager.OnBackStackChangedListener {
	private static final String TAG = "SwitchActivity";
	private static final ExecutorService signPools = Executors.newSingleThreadExecutor();
	
	private WaitingViewDialog waitingViewDialog;
	private SubFragmentType subType = SubFragmentType.None;
	private MsgHandler msgHandler;
	private String categoryId, categoryName, examId,examCode,examName;
	/*
	 * 重载创建。
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//加载布局XML 
		this.setContentView(R.layout.ui_switch_main);
		//初始化等待框
		this.waitingViewDialog = new WaitingViewDialog(this);
		//初始化通知处理器
		this.msgHandler = new MsgHandler(this);
		//创建考试类别Fragment
		this.createSubFragment(SubFragmentType.Category);
		//添加back事件处理
		this.getSupportFragmentManager().addOnBackStackChangedListener(this);
	}
	/*
	 * 返回事件处理。
	 * @see android.support.v4.app.FragmentManager.OnBackStackChangedListener#onBackStackChanged()
	 */
	@Override
	public void onBackStackChanged() {
		Log.d(TAG, "back前fragment类型：" + this.subType);
		switch(this.subType){
			case None:{//none
				break;
			}
			case Category:{//考试分类
				this.subType = SubFragmentType.None;
				break;
			}
			case Exam:{//考试列表
				this.subType = SubFragmentType.Category;
				break;
			}
			case Product:{//产品列表
				this.subType = SubFragmentType.Exam;
				break;
			}
		}
		Log.d(TAG, "back后fragment类型：" + subType);
	}
	//创建子Fragment
	private void createSubFragment(SubFragmentType type){
		Log.d(TAG, "创建子Fragment:" + type);
		Fragment fragment = null;
		switch(this.subType = type){
			case None://none
			case Category:{//考试分类。
				Log.d(TAG, "考试分类Fragment...");
				fragment = new CategoryFragment(this);
				break;
			}
			case Exam:{//考试
				Log.d(TAG, "考试列表Fragment...");
				fragment = new ExamFragment(this);
				break;
			}
			case Product:{//产品
				Log.d(TAG, "产品列表Fragment...");
				fragment = new ProductFragment(this);
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
		Log.d(TAG, "键盘响应:" + keyCode);
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && this.subType == SubFragmentType.None){
			if(event.getRepeatCount() == 0){
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				return false;
			}
			Log.d(TAG, "退出程序...");
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 子Fragment类型枚举。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月26日
	 */
	private enum SubFragmentType{ None, Category,Exam, Product};
	/**
	 * 考试类别Fragment。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月26日
	 */
	private class CategoryFragment extends Fragment implements AdapterView.OnItemClickListener{
		private ListView listView;
	    private CategoryAdapter categoryAdapter;
	    private ExamAdapter examAdapter;
	    private List<CategoryModel> categoryDataSource;
	    private List<ExamModel> examDataSource;
	    private Context context;
	    private boolean isCategory;
	    /**
	     * 构造函数。
	     * @param context
	     * 上下文。
	     */
	    public CategoryFragment(Context context){
	    	Log.d(TAG, "初始化考试类别Fragment...");
	    	this.context = context;
	    	//初始化考试分类
	    	this.categoryDataSource = new ArrayList<CategoryModel>();
	    	this.categoryAdapter = new CategoryAdapter(this.context, this.categoryDataSource);
	    	//初始化考试查询
	    	this.examDataSource = new ArrayList<ExamModel>();
	    	this.examAdapter = new ExamAdapter(this.context, this.examDataSource);
	    }
		/*
		 * 重载创建视图。
		 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Log.d(TAG, "加载考试类别视图...");
			//加载界面视图
			final View view = inflater.inflate(R.layout.ui_switch_category, container, false);
			//加载搜索框
			final EditText txtSearch = (EditText)view.findViewById(R.id.switch_category_search_text);
			//加载所属按钮
			Button btnSearch = (Button)view.findViewById(R.id.switch_category_search_btn);
			if(btnSearch != null && txtSearch != null){
				//添加点击事件处理
				btnSearch.setOnClickListener(new View.OnClickListener() {
					/*
					 * 重载搜索按钮点击
					 * @see android.view.View.OnClickListener#onClick(android.view.View)
					 */
					@Override
					public void onClick(View v) {
						//启动等待动画
						waitingViewDialog.show();
						//获取搜索文本
						String searchExamName = txtSearch.getText().toString();
						if(StringUtils.isBlank(searchExamName)){
							//设置考试分类适配器
							listView.setAdapter(categoryAdapter);
							isCategory = true;
							//加载考试分类数据
							new CategoryDataAsyncTask().execute();
						}else{
							//设置考试适配器
							listView.setAdapter(examAdapter);
							isCategory = false;
							//加载数据
							loadSearchExamName(searchExamName);
						}
					}
				});
			}
			//加载列表
			this.listView = (ListView)view.findViewById(R.id.list_switch_category);
			//设置数据适配器
			this.listView.setAdapter(this.categoryAdapter);
			this.isCategory = true;
			//设置数据行点击事件处理
			this.listView.setOnItemClickListener(this);
			return view;
		}
		/*
		 * 重载开始。
		 * @see android.support.v4.app.Fragment#onStart()
		 */
		@Override
		public void onStart() {
			super.onStart();
			//显示等待动画
			waitingViewDialog.show();
			//加载考试类别数据。
			new CategoryDataAsyncTask().execute();
		}
		//搜索考试名称
		private void loadSearchExamName(final String examName){
			//初始化主线程数据更新Handler
			final UpdateSearchResultHandler handler = new UpdateSearchResultHandler(this);
			//执行搜索操作
			signPools.execute(new Runnable() {
				/*
				 * 线程处理。
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					try{
						Log.d(TAG, "多线程开始搜索考试名称:" + examName);
						final SwitchProductDao  dao  = new SwitchProductDao(getActivity());
						dao.findSearchExams(examName, new SwitchProductDao.SearchResultListener() {
							/*
							 * 多线程搜索结果反馈
							 * @see com.examw.test.dao.SwitchProductDao.SearchResultListener#onSearchResult(com.examw.test.model.sync.ExamModel)
							 */
							@Override
							public void onSearchResult(ExamModel exam) {
								Log.d(TAG, "查找到考试:" + exam);
								Message msg = new Message();
								msg.obj = exam;
								handler.sendMessage(msg);
							}
						});
					}catch(Exception e){
						Log.e(TAG, "搜索考试名称["+examName+"]异常:" + e.getMessage(), e);
					}
				}
			});
		}
		/*
		 * 数据行点击事件
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, "选中行:" + position);
			if(isCategory){
				CategoryModel categoryModel = this.categoryDataSource.get(position);
				Log.d(TAG, "选中考试分类:" + categoryModel);
				//设置考试分类ID。
				categoryId = categoryModel.getId();
				//设置考试分类名称。
				categoryName = categoryModel.getName();
				//显示提示
				Toast.makeText(this.context, categoryName, Toast.LENGTH_SHORT).show();
				//UI跳转
				createSubFragment(SubFragmentType.Exam);
			}else{
				ExamModel examModel = this.examDataSource.get(position);
				Log.d(TAG, "选中考试:" + examModel);
				//设置考试ID
				examId = examModel.getId();
				//设置考试代码
				examCode = examModel.getCode();
				//设置考试名称
				examName = examModel.getName();
				//显示提示
				Toast.makeText(this.context, examName, Toast.LENGTH_SHORT).show();
				//UI跳转
				createSubFragment(SubFragmentType.Product);
			}
		}
		/**
		 * 考试分类数据异步加载。
		 * 
		 * @author jeasonyoung
		 * @since 2015年6月30日
		 */
		private class CategoryDataAsyncTask extends AsyncTask<Void, Void, List<CategoryModel>>{
			/*
			 * 后台线程加载数据。
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected List<CategoryModel> doInBackground(Void... params) {
				 try{
					Log.d(TAG, "开始异步线程加载考试分类数据...");
					final SwitchProductDao dao = new SwitchProductDao(getActivity());
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
									 Log.d(TAG, "下载数据失败:" + msg);
									 msgHandler.sendMessage(StringUtils.isBlank(msg) ? "未知异常！" : msg);
								}else {
									msgHandler.sendMessage("下载数据完成!");
								}
							}
						});
					}
					return dao.getCategories();
				 }catch(Exception e){
					 Log.e(TAG, "加载考试分类数据异常:" + e.getMessage(), e);
				 }
				 return null;
			}
			/*
			 * 主线程处理
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			protected void onPostExecute(List<CategoryModel> result) {
				Log.d(TAG, "主线线程加载数据...");
				//清空原有数据
				categoryDataSource.clear();
				//添加数据
				if(result != null && result.size() > 0){
					for(CategoryModel model : result){
						if(model != null && model.getExams() != null && model.getExams().size() > 0){
							categoryDataSource.add(model);
						}
					}
				}
				//关闭等待动画
				waitingViewDialog.cancel();
				Log.d(TAG, "通知适配器更新数据...");
				categoryAdapter.notifyDataSetChanged();
			};
		}
	}
	/**
	 * 更新搜索结果处理类。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月30日
	 */
	private static class UpdateSearchResultHandler extends Handler{
		private static final String TAG = "UpdateSearchResultHandler";
		private WeakReference<CategoryFragment> weakRef;
		private boolean isFrist;
		/**
		 * 构造函数。
		 * @param cf
		 */
		public UpdateSearchResultHandler(SwitchActivity.CategoryFragment cf){
			Log.d(TAG, "初始化...");
			this.weakRef = new WeakReference<SwitchActivity.CategoryFragment>(cf);
			this.isFrist = false;
		}
		/*
		 * 主线程处理。
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "主线程处理...");
			CategoryFragment categoryFragment = this.weakRef.get();
			if(categoryFragment == null){
				Log.d(TAG, "CategoryFragment UI已被销毁...");
				return;
			}
			//关闭等待动画
			if(!this.isFrist){
				SwitchActivity switchActivity = (SwitchActivity)categoryFragment.getActivity();
				if(switchActivity != null){
					switchActivity.waitingViewDialog.cancel();
				}
				this.isFrist = true;
			}
			//获取数据
			ExamModel data = (ExamModel)msg.obj;
			if(data == null)return;
			//添加到数据源
			categoryFragment.examDataSource.add(data);
			//通知适配器更新
			categoryFragment.examAdapter.notifyDataSetChanged();
		}
	}
	/**
	 * 考试Fragment。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月26日
	 */
	private class ExamFragment extends Fragment implements AdapterView.OnItemClickListener{
		private ListView listView;
		private ExamAdapter adapter; 
		private List<ExamModel> dataSource;   
		private Context context;
		/*
		 * 构造函数。
		 */
		public ExamFragment(Context context){
			Log.d(TAG, "初始化考试Fragment...");
			//初始化考试
			this.context = context;
	    	this.dataSource = new ArrayList<ExamModel>();
	    	this.adapter = new ExamAdapter(this.context, this.dataSource);
		}
		/*
		 * 重载创建视图。
		 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Log.d(TAG, "加载考试视图...");
			final View view = inflater.inflate(R.layout.ui_switch_exam, container, false);
			//标题
			TextView tvTitle = (TextView)view.findViewById(R.id.title);
			if(tvTitle != null){
				tvTitle.setText(categoryName);
			}
			//返回按钮
			final View btnBack = view.findViewById(R.id.btn_goback);
			if(btnBack != null){
				btnBack.setOnClickListener(new View.OnClickListener() {
					/*
					 * 点击事件。
					 * @see android.view.View.OnClickListener#onClick(android.view.View)
					 */
					@Override
					public void onClick(View v) {
						Log.d(TAG, "关闭考试Fragment...");
						getSupportFragmentManager().popBackStack();
					}
				});
			}
			//加载考试列表
			this.listView = (ListView)view.findViewById(R.id.list_switch_exam);
			//设置数据适配器
			this.listView.setAdapter(this.adapter);
			//设置数据行点击事件处理
			this.listView.setOnItemClickListener(this);
			//返回
			return view;
		}
		/*
		 * 启动加载。
		 * @see android.support.v4.app.Fragment#onStart()
		 */
		@Override
		public void onStart() {
			super.onStart();
			//显示等待动画
			waitingViewDialog.show();
			//异步加载数据
			new LoadDataTask().execute();
		}
		/*
		 * 重载选中列表行数据
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, "选中行:" + position);
			ExamModel data = dataSource.get(position);
			if(data != null){
				Log.d(TAG, "选中考试:" + data);
				//设置考试ID
				examId = data.getId();
				//设置考试代码
				examCode = data.getCode();
				//设置考试名称
				examName = data.getName();
				//显示提示
				Toast.makeText(this.context, examName, Toast.LENGTH_SHORT).show();
				//UI跳转
				createSubFragment(SubFragmentType.Product);
			}
		}
		/*
		 * 加载数据。
		 */
		private class LoadDataTask extends AsyncTask<Void, Void, List<ExamModel>>{
			/*
			 * 后台线程加载数据。
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected List<ExamModel> doInBackground(Void... params) {
				try{
					Log.d(TAG, "异步加载考试分类["+ categoryName +"]下的考试数据...");
					final SwitchProductDao dao = new SwitchProductDao(getActivity());
					return dao.loadExams(categoryId);
				}catch(Exception e){
					Log.e(TAG, "加载考试分类["+ categoryName +"]下的考试数据异常:" + e.getMessage(), e);
				}
				return null;
			}
			/*
			 * 主线程更新。
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(List<ExamModel> result) {
				Log.d(TAG, "主线线程更新考试数据...");
				//清除数据源
				dataSource.clear();
				//装载数据
				if(result != null && result.size() > 0){
					dataSource.addAll(result);
				}
				//关闭等待动画
				waitingViewDialog.cancel();
				Log.d(TAG, "通知适配器更新数据...");
				adapter.notifyDataSetChanged();
			}
		}
	}
	/**
	 * 产品Fragment。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月26日
	 */
	private class ProductFragment extends Fragment  implements AdapterView.OnItemClickListener{
		private ListView listView;
		private ProductAdapter adapter;
		private List<ProductModel> dataSource;
		private Context context;
		/**
		 * 构造函数。
		 * @param context
		 */
		public ProductFragment(Context context){
			Log.d(TAG, "初始化产品Fragment...");
			//初始化
			this.context = context;
			this.dataSource = new ArrayList<ProductModel>();
			this.adapter = new ProductAdapter(this.context, this.dataSource);
		}
		/*
		 * 重载创建视图。
		 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Log.d(TAG, "加载考试视图...");
			final View view = inflater.inflate(R.layout.ui_switch_product, container, false);
			//标题
			TextView tvTitle = (TextView)view.findViewById(R.id.title);
			if(tvTitle != null){
				tvTitle.setText(examName);
			}
			//返回按钮
			final View btnBack = view.findViewById(R.id.btn_goback);
			if(btnBack != null){
				btnBack.setOnClickListener(new View.OnClickListener() {
					/*
					 * 点击事件。
					 * @see android.view.View.OnClickListener#onClick(android.view.View)
					 */
					@Override
					public void onClick(View v) {
						Log.d(TAG, "关闭产品Fragment...");
						getSupportFragmentManager().popBackStack();
					}
				});
			}
			//加载产品列表
			this.listView = (ListView)view.findViewById(R.id.list_switch_product);
			//设置数据适配器
			this.listView.setAdapter(this.adapter);
			//设置数据行点击事件处理
			this.listView.setOnItemClickListener(this);
			//返回
			return view;
		}
		/*
		 * 启动加载。
		 * @see android.support.v4.app.Fragment#onStart()
		 */
		@Override
		public void onStart() {
			super.onStart();
			//显示等待动画
			waitingViewDialog.show();
			//异步加载数据
			new LoadDataTask().execute();
		}
		/*
		 * 数据行点击事件。
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, "选中行:" + position);
			ProductModel data = dataSource.get(position);
			if(data != null){
				Log.d(TAG, "选中产品:" + data);
				//确认对话框
				this.showConfirmDialog(data);
			}
		}
		//显示确认对话框
		private void showConfirmDialog(final ProductModel data){
			Log.d(TAG, "创建二次确认对话框...");
			new AlertDialog.Builder(this.context)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle(data.getName())
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				/**
				 * 确认按钮处理
				 * @param dialog
				 * @param which
				 */
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.d(TAG, "选中产品:" + data);
					//显示等待动画
					waitingViewDialog.show();
					//选中后台处理
					new ConfirmProductTask(context).execute(data);
				}
			})
			.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
				/*
				 * 取消按钮处理。
				 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
				 */
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.d(TAG, "关闭二次确认对话框...");
					dialog.dismiss();
				}
			}).create().show();
		}
		/**
		 * 加载数据。
		 * 
		 * @author jeasonyoung
		 * @since 2015年7月1日
		 */
		private class LoadDataTask extends AsyncTask<Void, Void, List<ProductModel>>{
			/*
			 * 后台线程加载数据。
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected List<ProductModel> doInBackground(Void... params) {
				try{
					Log.d(TAG, "异步加载考试["+ examName +"]下的产品数据...");
					final SwitchProductDao dao = new SwitchProductDao(getActivity());
					return dao.loadProducts(examId);
				}catch(Exception e){
					Log.e(TAG, "加载考试["+ examName +"]下的产品数据异常:" + e.getMessage(), e);
				}
				return null;
			}
			/*
			 * 主线程更新。
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(List<ProductModel> result) {
				Log.d(TAG, "主线线程更新产品数据...");
				//清除数据源
				dataSource.clear();
				//装载数据
				if(result != null && result.size() > 0){
					dataSource.addAll(result);
				}
				//关闭等待动画
				waitingViewDialog.cancel();
				Log.d(TAG, "通知适配器更新数据...");
				adapter.notifyDataSetChanged();
			}
		}
		/**
		 * 确认产品处理。
		 * @author jeasonyoung
		 * @since 2015年7月1日
		 */
		private class ConfirmProductTask extends AsyncTask<ProductModel, Void, Boolean>{
			private Context context;
			/**
			 * 构造函数。
			 * @param context
			 */
			public ConfirmProductTask(Context context){
				Log.d(TAG, "初始化产品确认处理...");
				this.context = context;
			}
			/*
			 * 后台线程处理。
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected Boolean doInBackground(ProductModel... params) {
				Log.d(TAG, "开始产品确认的异步线程处理...");
				//当前上下文
				if(this.context == null){
					Log.d(TAG, "当前上下文为空!");
					return false;
				}
				//获取当前产品
				ProductModel product = params[0];
				if(product == null){
					Log.d(TAG, "获取当前产品失败!");
					return false;
				}
				AppContext appContext =  (AppContext)this.context.getApplicationContext();
				if(appContext == null){
					Log.d(TAG, "获取当前应用appContext失败!");
					return false;
				}
				//创建当前设置
				AppSettings settings = new AppSettings(examId, examCode, examName);
				settings.addProduct(product.getId(), product.getName());
				//更新当前设置
				appContext.updateSettings(settings);
				Log.d(TAG, "更新设置完成，准备下载数据...");
				DownloadDao downloadDao = new DownloadDao(this.context);
				downloadDao.download(true, new DownloadResultListener() {
					/*
					 * 下载结果处理。
					 * @see com.examw.test.dao.DownloadResultListener#onComplete(boolean, java.lang.String)
					 */
					@Override
					public void onComplete(boolean result, String msg) {
						Log.d(TAG, "下载数据:" + result + "[" + msg + "]");
						if(!result && StringUtils.isNotBlank(msg)){
							msgHandler.sendMessage(msg);
						}
					}
				});
				return true;
			}
			/*
			 * 前台主线程处理。
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(Boolean result) {
				Log.d(TAG, "确认产品前台主线程处理...");
				//关闭等待动画
				waitingViewDialog.cancel();
				//
				if(result){
					//UI界面跳转
					startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
					//关闭当前Activity
					finish();
				}else {
					Toast.makeText(getApplicationContext(), "发生未知错误!", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}