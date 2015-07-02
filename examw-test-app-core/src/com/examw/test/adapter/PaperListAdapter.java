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
import com.examw.test.model.PaperModel;

/**
 * 
 * @author fengwei.
 * @since 2014年11月28日 下午4:26:25.
 */
public class PaperListAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private ArrayList<PaperModel> papers;
	public PaperListAdapter(Context context,ArrayList<PaperModel> papers) {
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
			holder.scoreTimeLayout = (LinearLayout) convertView.findViewById(R.id.score_time_layout);
			convertView.setTag(holder);
		}else
		{
			holder = (ViewHolder) convertView.getTag(); 
		}
		final PaperModel p = papers.get(position);
		//holder.title.setText(p.getTitle());
		//每日一练显示情况不同
//		if(p.getType().equals(AppConstant.PAPER_TYPE_DAILY))
//		{
//			holder.info.setText("总题数:"+p.getTotal());
//			holder.scoreTimeLayout.setVisibility(View.GONE);
//		}else
//		{
//			holder.info.setText("总题数: "+p.getTotal());
//			holder.scoreTimeLayout.setVisibility(View.GONE);
//		}
		//holder.publishTime.setText(p.getCreateTime().substring(0, 11));
		return convertView;
	}
	static class ViewHolder
	{
		TextView title,info,userTotal,publishTime;
		LinearLayout scoreTimeLayout;
	}
}
