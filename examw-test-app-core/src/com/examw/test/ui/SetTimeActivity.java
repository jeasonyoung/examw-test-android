package com.examw.test.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.examw.test.R;
import com.examw.test.app.AppConfig;
import com.examw.test.widget.wheel.NumericWheelAdapter;
import com.examw.test.widget.wheel.WheelView;

/**
 * 设置时间
 * @author fengwei.
 * @since 2014年12月2日 下午1:55:40.
 */
public class SetTimeActivity extends BaseActivity implements OnClickListener{
	private PopupWindow datePop;
	private TextView dateTxt,restDaysTxt,topTitle;
	private SimpleDateFormat format;
	private WheelView year,month,day;
	private long now ;
	private long setTime;
	private int curYear,curMonth,curDay;
	private AppConfig appConfig;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_settime);
		format = new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
		appConfig = AppConfig.getAppConfig(this);
		initViews();
	}
	@Override
	protected void onStart() {
		now = System.currentTimeMillis();
		dateTxt.setText(format.format(new Date(setTime==0?now:setTime)));
		restDaysTxt.setText(calculateRestDay(format.format(setTime)));
		super.onStart();
	}
	private void initViews()
	{
		topTitle = (TextView) this.findViewById(R.id.title);
		topTitle.setText(R.string.setExamTime);
		dateTxt = (TextView) this.findViewById(R.id.dateTxt);
		setTime = appConfig.getExamTime();
		restDaysTxt = (TextView) this.findViewById(R.id.restDaysTxt);
		this.findViewById(R.id.btn_goback).setOnClickListener(this);
		dateTxt.setOnClickListener(this);
	}
	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void showPopWindow()
	{
		if(datePop==null)
		{
			View v = LayoutInflater.from(this).inflate(R.layout.date_set_pop, null);
			Calendar calendar = Calendar.getInstance();
			curYear = calendar.get(Calendar.YEAR);	//当前年,今年
			//设置的时间在当前时间之前
			if(setTime > now)
			{
				calendar.setTimeInMillis(setTime);
			}
			//定义wheelview
			month = (WheelView) v.findViewById(R.id.month);
	        year = (WheelView) v.findViewById(R.id.year);
	        day = (WheelView) v.findViewById(R.id.day);
	        //解决屏幕不同大小时,这个view不能自适应大小
	        
			int textSize = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()/32;
	        month.setTextSize(textSize);
	        year.setTextSize(textSize);
	        day.setTextSize(textSize);
	        Button btn = (Button) v.findViewById(R.id.btn_ok);//确定按钮
	        btn.setOnClickListener(this);
	        //year 
	        int setYear = calendar.get(Calendar.YEAR);	//设置的年
	    	year.setAdapter(new NumericWheelAdapter(curYear, curYear+5));
	    	year.setCurrentItem(setYear - curYear);
	    	year.setCyclic(true);
			year.setLabel("年");
			
			//month
			curMonth = calendar.get(Calendar.MONTH);
			month.setAdapter(new NumericWheelAdapter(1, 12));
			month.setCurrentItem(curMonth);
			month.setCyclic(true);
			month.setLabel("月");
			
			//day
			curDay = calendar.get(Calendar.DAY_OF_MONTH);
			day.setAdapter(new NumericWheelAdapter(1, 31));
			day.setCurrentItem(curDay-1);
			day.setCyclic(true);
			day.setLabel("日");
			
			datePop = new PopupWindow(v,-1, ViewGroup.LayoutParams.WRAP_CONTENT);
			datePop.setBackgroundDrawable(new BitmapDrawable());
			datePop.setFocusable(true);
			datePop.setOutsideTouchable(true);
		}
		//datePop.showAtLocation(this.findViewById(R.id.settime), Gravity.BOTTOM, 0, 0);
		datePop.showAtLocation(this.findViewById(R.id.settime),80,0,0);
	}
	@Override
	protected void onPause() {
		if(datePop!=null&&datePop.isShowing())
		{
			datePop.dismiss();
		}
		super.onPause();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btn_ok:
			datePop.dismiss();
			String str = (year.getCurrentItem()+curYear)+"年"+
							(month.getCurrentItem()+1)+"月"+
								(day.getCurrentItem()+1)+"日";
			dateTxt.setText(str);
			restDaysTxt.setText(calculateRestDay(str));
			long value = 0;
			try {
				value = format.parse(str).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			AppConfig.getAppConfig(this).setExamTime(value);
			break;
		case R.id.btn_goback:
			this.finish();
			break;
		case R.id.dateTxt:
			showPopWindow();
			break;
		}
	}
	//计算剩余的天数
	private String calculateRestDay(String dateStr)
	{
		String s = null;
		try {
			Date thatDay = format.parse(dateStr);
			if(thatDay.before(new Date(now))) //日期在今天之前
			{
				s = "考试日期已过";
			}else
			{
				long i = (thatDay.getTime()-now)/1000/60/60/24;
				s = i+" 天";
				if(i==0)
				{
					s = "不到 1 天";
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return s;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((event.getKeyCode() == 4)
				) {
			if(datePop!=null&&datePop.isShowing())
			{
				datePop.dismiss();
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		if(datePop!=null&&datePop.isShowing())
		{
			datePop.dismiss();
		}
		super.onDestroy();
	}
}
