package com.examw.test.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;

/**
 * 大题列表适配器
 * @author fengwei
 */
public class PopRuleListAdapter extends BaseAdapter{
	private Context context;
	private List<?> list;
	public PopRuleListAdapter(Context context, List<?> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {

		
		ViewHolder holder;
		if (convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.pop_rule_item, null);
			holder=new ViewHolder();
			
			convertView.setTag(holder);
			
			holder.groupItem=(TextView) convertView.findViewById(R.id.groupItem);
			
		}
		else{
			holder=(ViewHolder) convertView.getTag();
		}
		//holder.groupItem.setText(list.get(position).getTitle());
		return convertView;
	}

	static class ViewHolder {
		TextView groupItem;
	}

}
