package com.examw.test.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppConstant;
import com.examw.test.domain.PaperRecord;

/**
 * 
 * @author fengwei.
 * @since 2014年12月11日 下午5:12:00.
 */
public class PaperRecordAdapter extends BaseAdapter{
	private Context context;
	private List<PaperRecord> records;
	public PaperRecordAdapter(Context context,List<PaperRecord> records) {
		this.context = context;
		this.records = records;
	}
	@Override
	public int getCount() {
		if(records!=null)
			return records.size();
		return 0;
	}
	@Override
	public Object getItem(int position) {
		if(records!=null)
			return records.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.simulate_paper_list, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.paper_name_TextView);
//			holder.img = (ImageView) convertView.findViewById(R.id.paper_tyep_imgView);
			holder.info = (TextView) convertView.findViewById(R.id.paper_info_TextView);
			holder.lastTime = (TextView) convertView.findViewById(R.id.lastTimeTextView);
			convertView.setTag(holder);
		}else
		{
			holder = (ViewHolder) convertView.getTag(); 
		}
		PaperRecord r = records.get(position);
		holder.title.setText(r.getPaperName());
//		if(r.getPaperType().equals(AppConstant.PAPER_TYPE_DAILY)) holder.img.setImageResource(R.drawable.record_exercise_img);
		if(r.getStatus().equals(AppConstant.STATUS_DONE))
		{
			if(r.getPaperType().equals(AppConstant.PAPER_TYPE_DAILY) || r.getPaperType().equals(AppConstant.PAPER_TYPE_CHAPTER))
				holder.info.setText("答题进度: "+r.getScore().intValue()+"/"+r.getRightNum());
			else
				holder.info.setText("上次得分: "+r.getScore()+"分");
			holder.info.setTextColor(context.getResources().getColor(R.color.red));
		}else
		{
			holder.info.setText("继续考试");
			holder.info.setTextColor(context.getResources().getColor(R.color.blue));
		}
		holder.lastTime.setText(r.getLastTime());
		return convertView;
	}
	static class ViewHolder
	{
		TextView title,info,lastTime;
//		ImageView img;
	}
}
