package com.examw.test.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.model.sync.CategoryModel;

/**
 * 考试分类数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年6月29日
 */
public class CategoryAdapter extends BaseAdapter{
	private static final String TAG = "CategoryAdapter";
	private Context context;
	private List<CategoryModel> dataSource;
	/**
	 * 构造函数。
	 * @param context
	 * 上下文。
	 * @param dataSource
	 * 数据源。
	 */
	public CategoryAdapter(Context context, List<CategoryModel> dataSource){
		Log.d(TAG, "初始化...");
		this.context = context;
		this.dataSource = dataSource;
	}
	/*
	 * 获取数据总数。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return this.dataSource == null ? 0 : this.dataSource.size();
	}
	/*
	 * 获取数据对象。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return this.dataSource.get(position);
	}
	/*
	 * 重载。
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	/*
	 * 重载创建行。
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "准备创建行..." + position);
		//行视图控件
		ItemViewWrapper itemViewWrapper = null;
		if(convertView == null){
			Log.d(TAG, "创建新行:" + position);
			convertView = LayoutInflater.from(this.context).inflate(R.layout.ui_switch_category_item, parent, false);
			//初始化
			itemViewWrapper = new ItemViewWrapper();
			//考试分类名称
			itemViewWrapper.setCategoryName((TextView)convertView.findViewById(R.id.category_item_name));
			//考试名称1
			itemViewWrapper.setExamName1((TextView)convertView.findViewById(R.id.category_item_examName1));
			//考试名称2
			itemViewWrapper.setExamName2((TextView)convertView.findViewById(R.id.category_item_examName2));
			//存储包装对象
			convertView.setTag(itemViewWrapper);
		}else {
			Log.d(TAG, "重复使用行:" + position);
			//重用包装对象
			itemViewWrapper = (ItemViewWrapper)convertView.getTag();
		}
		//装载数据
		Log.d(TAG, "装载行数据:" + position);
		CategoryModel data = (CategoryModel)this.getItem(position);
		//1.考试分类名称
		itemViewWrapper.getCategoryName().setText(data.getName());
		//2.考试名称
		if(data.getExams() != null && data.getExams().size() > 0){
			//2.1考试名称1
			itemViewWrapper.getExamName1().setText(data.getExams().get(0).getName());
			//2.2考试名称2
			if(data.getExams().size() >= 2){
				itemViewWrapper.getExamName2().setText(data.getExams().get(1).getName());
			}
		}else{
			itemViewWrapper.getExamName1().setText("");
			itemViewWrapper.getExamName2().setText("");
		}
		return convertView;
	}
	/**
	 * 行包装器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月29日
	 */
	private class ItemViewWrapper{
		private TextView categoryName,examName1,examName2;
		/**
		 * 获取categoryName
		 * @return categoryName
		 */
		public TextView getCategoryName() {
			return categoryName;
		}

		/**
		 * 设置 categoryName
		 * @param categoryName 
		 *	  categoryName
		 */
		public void setCategoryName(TextView categoryName) {
			this.categoryName = categoryName;
		}

		/**
		 * 获取examName1
		 * @return examName1
		 */
		public TextView getExamName1() {
			return examName1;
		}

		/**
		 * 设置 examName1
		 * @param examName1 
		 *	  examName1
		 */
		public void setExamName1(TextView examName1) {
			this.examName1 = examName1;
		}

		/**
		 * 获取examName2
		 * @return examName2
		 */
		public TextView getExamName2() {
			return examName2;
		}

		/**
		 * 设置 examName2
		 * @param examName2 
		 *	  examName2
		 */
		public void setExamName2(TextView examName2) {
			this.examName2 = examName2;
		}
	}
}