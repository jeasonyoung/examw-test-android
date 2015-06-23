package com.examw.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;

/**
 * 
 * @author fengwei.
 * @since 2014年12月10日 下午3:06:14.
 */
public class AnswerScoreGridAdatper extends BaseAdapter{
	private Context context;
	private String[] data;
	public AnswerScoreGridAdatper(Context context,String[] data) {
		this.context = context;
		this.data = data;;
	}
	@Override
	public int getCount() {
		return data.length;
	}
	@Override
	public Object getItem(int position) {
		return data[position];
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder = null;
		if(v == null)
		{
			v = LayoutInflater.from(context).inflate(R.layout.answer_card_item, null);
			holder = new ViewHolder();
			holder.textView = (TextView) v.findViewById(R.id.optionTextView);
			v.setTag(holder);
		}
		holder = (ViewHolder) v.getTag();
		holder.textView.setText(data[position]);
		if(position==2)
		{
			holder.textView.setTextColor(context.getResources().getColor(R.color.red));
		}
		return v;
	}
	static class ViewHolder
	{
		TextView textView;
	}
}
