package com.examw.test.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.model.StructureInfo;
import com.examw.test.ui.AnswerCardActivity;
import com.examw.test.widget.HomeGrid;

/**
 * 题卡大题分类适配器
 * @author fengwei.
 * @since 2014年12月9日 下午2:36:42.
 */
public class AnswerCardStructureListAdatper extends BaseAdapter{
	private Context context;
	private AnswerCardActivity activity;
	private List<StructureInfo> ruleList;
	private int[] trueOfFalse;
	public AnswerCardStructureListAdatper(Context context,AnswerCardActivity activity,List<StructureInfo> ruleList,int[] trueOfFalse) {
		this.context = context;
		this.activity = activity;
		this.ruleList = ruleList;
		this.trueOfFalse = trueOfFalse;
	}
	@Override
	public int getCount() {
		if(ruleList==null)
			return 1;
		return ruleList.size();
	}
	@Override
	public Object getItem(int position) {
		return null;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder = null;
		final StructureInfo r = ruleList.get(position);
		if(v == null)
		{
			v = LayoutInflater.from(context).inflate(R.layout.answer_card_grid, null);
			holder = new ViewHolder();
			holder.textView = (TextView) v.findViewById(R.id.directory_exam_RulesTextView);
			holder.gridView = (HomeGrid) v.findViewById(R.id.directory_exam_grid);
			v.setTag(holder);
		}
		holder = (ViewHolder) v.getTag();
		holder.textView.setText(r.getTitle());
		int length = r.getTotal();
		String[] data = new String[length];
		int t = measureTotal(position);
		for(int i=0;i<length;i++)
		{
			data[i] = String.valueOf((i+1+t));
		}
		holder.gridView.setAdapter(new AnswerCardItemGridAdapter(context,data,trueOfFalse));
		holder.gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				 Intent data=new Intent();  
		         data.putExtra("ruleTitle", r.getTitle());  
		         data.putExtra("action", "DoExam");
		         data.putExtra("cursor", Integer.parseInt(((TextView)arg1.findViewById(R.id.optionTextView)).getText().toString())-1);  
		         //设置请求代码20,选题
		         activity.setResult(20, data);  
		         //结束Activity  
		         activity.finish();  
			}
		});
		return v;
	}
	static class ViewHolder
	{
		HomeGrid gridView;
		TextView textView;
	}
	private int measureTotal(int position)
	{
		int total = 0;
		for(int i=position-1;i>=0;i--)
		{
			total+= ruleList.get(i).getTotal();
		}
		return total;
	}
}
