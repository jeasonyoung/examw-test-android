package com.examw.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;

/**
 * 数据库题卡适配器
 * @author fengwei.
 * @since 2014年12月9日 下午2:43:05.
 */
public class AnswerCardItemGridAdapter extends BaseAdapter{
	private Context context;
	private String[] data;
	private int[] trueOfFalse;
	
	public AnswerCardItemGridAdapter(Context context,String[] data,int[] trueOfFalse) {
		this.context = context;
		this.data = data;
		this.trueOfFalse = trueOfFalse;
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
	public android.view.View getView(int position, android.view.View v, android.view.ViewGroup parent) {
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
		if(trueOfFalse[Integer.parseInt(data[position])-1]!=0)	//已经做了
			v.setBackgroundColor(context.getResources().getColor(R.color.lightbule));
//		int colorid = trueOfFalse[Integer.parseInt(data[position])-1]==1?context.getResources().getColor(R.color.green)
//				:trueOfFalse[Integer.parseInt(data[position])-1]==-1?context.getResources().getColor(R.color.red)
//						:context.getResources().getColor(R.color.transparent);
//		v.setBackgroundColor(colorid);
		return v;
	};
	static class ViewHolder
	{
		TextView textView;
	}
}
