package com.examw.test.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.domain.Paper;

/**
 * 
 * @author fengwei.
 * @since 2014年11月28日 下午4:26:25.
 */
public class PaperListAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private ArrayList<Paper> papers;
	public PaperListAdapter(Context context,ArrayList<Paper> papers) {
		this.mInflater = LayoutInflater.from(context);
		this.papers = papers;
	}
	@Override
	public int getCount() {
		if(papers!=null)
			return papers.size();
		return 0;
	}
	@Override
	public Object getItem(int position) {
		if(papers!=null)
			return papers.get(position);
		return null;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null)
		{
			convertView = mInflater.inflate(R.layout.simulate_paper_list, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.paper_name_TextView);
			holder.info = (TextView) convertView.findViewById(R.id.paper_info_TextView);
			holder.userTotal = (TextView) convertView.findViewById(R.id.paper_user_total_TextView);
			holder.publishTime = (TextView) convertView.findViewById(R.id.lastTimeTextView);
			convertView.setTag(holder);
		}else
		{
			holder = (ViewHolder) convertView.getTag(); 
		}
		final Paper p = papers.get(position);
		holder.title.setText(p.getName());
		holder.info.setText("考试时间: "+p.getTime()+" 分钟,"+"总分: "+p.getScore()+" 分");
		holder.userTotal.setText(p.getUserTotal()==null?"0":p.getUserTotal()+"");
		holder.publishTime.setText(p.getPublishTime().substring(0, 11));
		return convertView;
	}
	static class ViewHolder
	{
		TextView title,info,userTotal,publishTime;
	}
}
