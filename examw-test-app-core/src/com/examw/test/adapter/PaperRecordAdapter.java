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
import com.examw.test.model.PaperRecordModel;

/**
 * 试卷记录数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月30日
 */
public class PaperRecordAdapter extends BaseAdapter{
	private static final String TAG = "PaperRecordAdapter";
	private final LayoutInflater mInflater;
	private final List<PaperRecordModel> list;
	/**
	 * 构造函数。
	 * @param context
	 * @param list
	 */
	public PaperRecordAdapter(Context context, List<PaperRecordModel> list){
		Log.d(TAG, "初始化...");
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
	}
	/*
	 * 获取数据总数。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return this.list.size();
	}
	/*
	 * 获取数据对象。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}
	/*
	 * 获取数据ID。
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	/*
	 * 创建数据行。
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "创建数据行..." + position);
		ViewHolder holder = null;
		if(convertView == null){
			Log.d(TAG, "新建行..." + position);
			//1.加载布局
			convertView = this.mInflater.inflate(R.layout.ui_main_paper_record_item, parent, false);
			//2.初始化
			holder = new ViewHolder(convertView);
			//3.缓存
			convertView.setTag(holder);
		}else {
			Log.d(TAG, "重用行..." + position);
			holder = (ViewHolder)convertView.getTag();
		}
		//加载数据
		holder.loadData(this.getItem(position));
		//返回
		return convertView;
	}
	//
	private class ViewHolder{
		private TextView titleView,statusView,scoreView,rightsView,useTimeView,timeView;
		/**
		 * 构造函数。
		 * @param convertView
		 */
		public ViewHolder(View convertView){
			//1.标题
			this.titleView = (TextView)convertView.findViewById(R.id.paper_record_title);
			//2.状态
			this.statusView = (TextView)convertView.findViewById(R.id.paper_record_status);
			//3.得分
			this.scoreView = (TextView)convertView.findViewById(R.id.paper_record_score);
			//4.正确数 
			this.rightsView = (TextView)convertView.findViewById(R.id.paper_record_rights);
			//5.用时
			this.useTimeView = (TextView)convertView.findViewById(R.id.paper_record_usetimes);
			//6.时间
			this.timeView = (TextView)convertView.findViewById(R.id.paper_record_time);
		}
		/**
		 * 加载数据。
		 * @param data
		 */
		public void loadData(Object data){
			if(data instanceof PaperRecordModel){
				//1.标题
				if(this.titleView != null){
					this.titleView.setText(((PaperRecordModel)data).getPaperName());
				}
				//2.状态
				if(this.statusView != null){
					this.statusView.setText(((PaperRecordModel)data).isStatus() ? R.string.main_paper_record_yes :  R.string.main_paper_record_no);
				}
				//3.得分
				if(this.scoreView != null){
					this.scoreView.setText(String.format("得分:%.1f", ((PaperRecordModel)data).getScore()));
				}
				//4.正确数 
				if(this.rightsView != null){
					this.rightsView.setText(String.format("正确:%d", ((PaperRecordModel)data).getRights()));
				}
				//5.用时
				if(this.useTimeView != null){
					final int times = ((PaperRecordModel)data).getUseTimes();
					StringBuilder sbBuilder = new StringBuilder();
					if(times > 0){
						final int h = times / 60 / 60;
						final int m = times / 60 % 60;
						final int s = times % 60;
						
						if(h > 0){
							sbBuilder.append(h + "h");
						}
						if(m > 0){
							sbBuilder.append(m + "'");
						}
						if(s > 0){
							sbBuilder.append(s + "''");
						}
						this.useTimeView.setText(String.format("共用时:%s", sbBuilder));
					}else {
						this.useTimeView.setText("");
					}
				}
				//6.时间
				if(this.timeView != null){
					this.timeView.setText(((PaperRecordModel)data).getLastTime());
				}
			}
		}
	}
}