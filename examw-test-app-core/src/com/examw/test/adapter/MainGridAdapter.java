package com.examw.test.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;

public class MainGridAdapter extends BaseAdapter{
	private TypedArray imagebtns;
	private String[] texts;
	private Context context;
	private int i = 0;
	private int j = 0;
	private int k = 0;
	@Override
	public int getCount() {
		return texts.length;
	}
	@Override
	public Object getItem(int position) {
		return Integer.valueOf(this.texts[position]);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder = null;
		if(v==null) 
		{
			v = LayoutInflater.from(context).inflate(R.layout.item_main_grid, null);
			if (this.i != 0)
		      {
				//if(this.k<this.i) this.k = this.i;
		        AbsListView.LayoutParams localLayoutParams = new AbsListView.LayoutParams(this.i, this.k);
		        localLayoutParams.width = this.i;
		        localLayoutParams.height = this.k;
		        v.setLayoutParams(localLayoutParams);
		      }
			holder = new ViewHolder();
			holder.img = (ImageView) v.findViewById(R.id.home_item_icon);
			if(j!=0)
				holder.img.setLayoutParams(new LinearLayout.LayoutParams(j,j));
			holder.txt = (TextView) v.findViewById(R.id.gridview_name);
			v.setTag(holder);
		}
		holder = (ViewHolder) v.getTag();
		try{
			holder.img.setImageResource(this.imagebtns.getResourceId(position, -1));//setImageDrawable(r.getDrawable(this.imagebtns.getResourceId(position, -1)));//
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(position==imagebtns.length()-1)
				imagebtns.recycle();
		}
		holder.txt.setText(this.texts[position]);
		return v;
	}
	public MainGridAdapter(Context context) {
		this.context = context;
		Resources localResources = this.context.getResources();
		imagebtns = localResources.obtainTypedArray(R.array.home_grid_drawable_ids);
		texts = localResources.getStringArray(R.array.home_grid_titles);
		//屏幕的高宽
		WindowManager localWindowManager = (WindowManager)context.getSystemService("window");
		int width = localWindowManager.getDefaultDisplay().getWidth();
	    int height  = localWindowManager.getDefaultDisplay().getHeight();
	    //密度
	    float destiny = context.getResources().getDisplayMetrics().density;
	    this.i = (int)(((width - 22*destiny) / 3)-1);
	    this.j = width/8;
	    int column = texts.length%3 ==0?texts.length/3:texts.length/3+1;
	    this.k = (int) ((height-230*destiny)/column); //计算一个格子的高度
	}
	static class ViewHolder 
	{
		ImageView img;
		TextView txt;
	}
}
