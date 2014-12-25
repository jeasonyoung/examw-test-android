package com.examw.test.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.model.DateInfo;

/**
 * 
 * @author fengwei.
 * @since 2014年12月21日 下午4:07:00.
 */
public class DailyDateAdapter extends BaseAdapter {
	private ArrayList<DateInfo> dates;
	private Context context;
	public DailyDateAdapter(Context context,ArrayList<DateInfo> dates) {
		this.context = context;
		this.dates = dates;
	}
	
	@Override
	public int getCount() {
		return dates.size();
	}

	@Override
	public Object getItem(int position) {
		return dates.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.daily_date_item, null);
			holder = new ViewHolder();
			holder.eee = (TextView) convertView.findViewById(R.id.eee);
			holder.mmdd = (TextView) convertView.findViewById(R.id.mmdd);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.eee.setText((position==getCount()-1)?"今天":dates.get(position).getEee());
		holder.mmdd.setText(dates.get(position).getMmdd());
		return convertView;
	}
	static class ViewHolder{
		TextView eee;
		TextView mmdd;
	}
	
}
