package com.examw.test.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppConstant;
import com.examw.test.domain.Subject;

/**
 * 
 * @author fengwei.
 * @since 2014年12月12日 下午3:44:36.
 */
public class SubjectListAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<Subject> dataList;
	private int action;
	public SubjectListAdapter(Context context,ArrayList<Subject> datalist,int action) {
		this.mContext = context;
		this.dataList = datalist;
		this.action = action;
	}
	@Override
	public int getCount() {
		return dataList.size();
	}
	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if(v == null)
		{
			v = LayoutInflater.from(mContext).inflate(R.layout.item_choose_subject_list, null);
			holder = new ViewHolder();
//			holder.icon = (ImageView) v.findViewById(R.id.home_list_icon);
			holder.title = (TextView) v.findViewById(R.id.list_title);
//			if(action == AppConstant.ACTION_ERROR || action == AppConstant.ACTION_FAVORITE)
//			{
//				holder.countLayout = (LinearLayout) v.findViewById(R.id.count_layout);
//				holder.count = (TextView) v.findViewById(R.id.txt_count);
//				holder.countLayout.setVisibility(View.VISIBLE);
//			}
//			int rid = action==0?R.drawable.icon_book:action==1?R.drawable.icon_timer:R.drawable.icon_frequent;
//			holder.icon.setImageResource(rid);
			v.setTag(holder);
		}
		holder = (ViewHolder) v.getTag();
		holder.title.setText(dataList.get(position).getName());
//		if(action == AppConstant.ACTION_ERROR || action == AppConstant.ACTION_FAVORITE)
//		{
//			Integer total = dataList.get(position).getTotal();
//			holder.count.setText(total==null?"0":total+"");
//			if(total == null || total.equals(0)) holder.count.setTextColor(mContext.getResources().getColor(R.color.grey));
//		}
		return v;
	}
	static class ViewHolder
	{
		TextView title;
		TextView count;
		LinearLayout countLayout;
	}
}
