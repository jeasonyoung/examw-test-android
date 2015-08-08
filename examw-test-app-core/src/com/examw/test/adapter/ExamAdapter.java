package com.examw.test.adapter;

import java.util.List;

import com.examw.test.R;
import com.examw.test.model.sync.ExamModel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 考试适配器。
 * 
 * @author jeasonyoung
 * @since 2015年6月30日
 */
public class ExamAdapter extends BaseAdapter {
	private static final String TAG = "ExamAdapter";
	private Context context;
	private List<ExamModel> dataSource;
	/**
	 * 构造函数。
	 * @param context
	 * 上下文。
	 * @param dataSource
	 * 数据源。
	 */
	public ExamAdapter(Context context,List<ExamModel> dataSource) {
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
		TextView tvExamView = null;
		if(convertView == null){
			Log.d(TAG, "创建新行:" + position);
			convertView = LayoutInflater.from(this.context).inflate(R.layout.ui_switch_exam_item, parent, false);
			//获取考试名称
			tvExamView = (TextView)convertView.findViewById(R.id.exam_item_name);
			//
			convertView.setTag(tvExamView);
		}else {
			Log.d(TAG, "重复使用行:" + position);
			//
			tvExamView = (TextView)convertView.getTag();
		}
		//加载数据
		ExamModel data = (ExamModel)this.getItem(position);
		//设置考试名称
		tvExamView.setText(data == null ? "" : data.getName());
		//返回行视图
		return convertView;
	}
}