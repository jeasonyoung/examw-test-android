package com.examw.test.dao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.examw.test.model.sync.CategoryModel;
import com.examw.test.model.sync.ExamModel;
import com.examw.test.model.sync.JSONCallback;
import com.examw.test.model.sync.ProductModel;
import com.examw.test.utils.DigestClientUtil;

import android.content.Context;
import android.util.Log;

/**
 * 切换产品数据Dao.
 * 
 * @author jeasonyoung
 * @since 2015年6月29日
 */
public class SwitchProductDao implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "SwitchProductDao";
	private static final ExecutorService pools = Executors.newCachedThreadPool();
	private static List<CategoryModel> localCategoriesCache;
	private Context context;
	/**
	 * 构造函数。
	 * @param context
	 */
	public SwitchProductDao(Context context){
		Log.d(TAG, "初始化...");
		if(context == null){
			Log.d(TAG, "上下文为空!");
			throw  new IllegalArgumentException("context");
		}
		this.context = context;
	}
	//是否存在本地数据
	public synchronized boolean hasLocalCategories(){
		Log.d(TAG, "检查是否存在本地数据缓存...");
		if(localCategoriesCache == null || localCategoriesCache.size() == 0){
			Log.d(TAG, "加载本地缓存数据...");
			localCategoriesCache = CategoryModel.categoriesFromLocal();
		}
		return (localCategoriesCache != null && localCategoriesCache.size() > 0);
	}
	/**
	 * 获取考试分类数据.
	 * @return 考试分类数据集合。
	 */
	public List<CategoryModel> getCategories(){
		Log.d(TAG, " 获取考试分类数据...");
		if(this.hasLocalCategories()){
			return localCategoriesCache;
		}
		return null;
	}
	/**
	 * 从网络下载数据。
	 * @param handler
	 *  下载监听。
	 */
	public void loadCategoriesFromNetWorks(DownloadResultListener handler){
		try {
			Log.d(TAG, "开始从网络下载数据...");
			//应用上下文
			AppContext appContext = (AppContext)this.context.getApplicationContext();
			if(appContext == null){
				Log.d(TAG, "获取应用上下文失败!");
				if(handler != null){
					handler.onComplete(false, "加载应用上下文失败!");
				}
				return;
			}
			//检查网络状态
			if(!appContext.hasNetworkConnected()){
				Log.d(TAG, "没有网络!");
				if(handler != null){
					handler.onComplete(false, "请检查网络!");
				}
				return;
			}
			//准备开始下载
			final JSONCallback<CategoryModel[]> callback = new DigestClientUtil.CallbackJSON<CategoryModel[]>(CategoryModel[].class)
																									    .sendGETRequest(AppConstant.APP_API_CATEGORY_URL, null);
			if(!callback.getSuccess()){
				Log.d(TAG, callback.getMsg());
				if(handler != null){
					handler.onComplete(false, callback.getMsg());
				}
				return;
			}
			//保存到本地文件中
			boolean saveResult = CategoryModel.saveLocal(Arrays.asList(callback.getData()));
			Log.d(TAG, "保存到本地文件：" + saveResult);
			//下载保存完毕
			if(handler != null){
				handler.onComplete(true, null);
			}
			//加载本地数据到缓存变量中
			localCategoriesCache = CategoryModel.categoriesFromLocal();
		} catch (Exception e) {
			Log.d(TAG, "下载数据异常:" + e.getMessage(), e);
			if(handler != null){
				handler.onComplete(false, e.getMessage());
			}
		}
	}
	/**
	 * 加载考试分类下的考试集合。
	 * @param categoryId
	 * 考试分类ID。
	 * @return
	 * 考试集合。
	 */
	public List<ExamModel> loadExams(String categoryId){
		Log.d(TAG, "加载考试分类["+categoryId+"]下的考试集合...");
		if(StringUtils.isNotBlank(categoryId) && this.hasLocalCategories()){
			for(CategoryModel category : localCategoriesCache){
				if(category == null) continue;
				if(StringUtils.equalsIgnoreCase(categoryId, category.getId())){
					Log.d(TAG, "加载考试分类["+category+"]下的考试集合...");
					return category.getExams();
				}
			}
		}
		return null;
	}
	/**
	 * 根据考试名称模糊查询考试。
	 * @param searchName
	 * 考试名称查询条件。
	 * @param handler
	 * 搜索结果处理。
	 */
	public void findSearchExams(final String searchName,final SearchResultListener handler){
		Log.d(TAG, "根据考试名称模糊查询考试:" + searchName);
		if(StringUtils.isNotBlank(searchName) && this.hasLocalCategories() && handler != null){
			final AtomicInteger count = new AtomicInteger(0);
			final int totalCategory = localCategoriesCache.size();
			int i = 0;
			boolean isLast = false;
 			for(final CategoryModel category : localCategoriesCache){
 				if(totalCategory == (++i)){
 					isLast = true;
 				}
				//开启线程查询结果
				final boolean isLastCategory = isLast;
				pools.execute(new Runnable() {
					@Override
					public void run() {
						List<ExamModel> examModels = category.getExams();
						int totalExam = (examModels == null ? 0 : examModels.size()), index = 0;
						//科目下没有考试
						if(totalExam == 0){
							if(isLastCategory && count.get() == 0){
								Log.d(TAG, "没有找到考试:" + searchName);
								handler.onSearchResult(null);
							}
							return;
						}
						//循环考试
						for(ExamModel exam : examModels){
							//计数
							index++;
							//名称不存在
							if(exam == null || StringUtils.isBlank(exam.getName())){
								continue;
							}
							//比较考试名称
							if(StringUtils.equalsIgnoreCase(searchName, exam.getName())){
								count.incrementAndGet();
								handler.onSearchResult(exam);
							}
							//没有找到匹配的考试名称
							if(isLastCategory && count.get() == 0 && (index == totalExam)){
								//没有找到
								handler.onSearchResult(null);
							}
						}
					}
				});
			}
		}
	}
	/**
	 * 根据考试ID加载产品集合。
	 * @param examId
	 * 考试ID。
	 * @return
	 * 产品集合。
	 */
	public List<ProductModel> loadProducts(String examId){
		Log.d(TAG, "根据考试ID[" + examId + "]加载产品集合...");
		if(StringUtils.isNotBlank(examId) && this.hasLocalCategories()){
			for(CategoryModel category : localCategoriesCache){
				//判断考试分类及分类下考试是否有数据
				if(category == null || category.getExams() == null || category.getExams().size() == 0){
					continue;
				}
				//查找考试
				for(ExamModel exam : category.getExams()){
					if(exam == null) continue;
					if(StringUtils.equalsIgnoreCase(examId, exam.getId())){
						return exam.getProducts();
					}
				}
			}
		}
		return null;
	}
	/**
	 * 搜索结果监听器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月29日
	 */
	public interface SearchResultListener{
		/**
		 * 搜索结果。
		 * @param exam
		 */
		void onSearchResult(ExamModel exam);
	}
}