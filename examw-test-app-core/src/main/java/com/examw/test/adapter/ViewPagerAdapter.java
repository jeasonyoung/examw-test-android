package com.examw.test.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

import com.examw.test.R;
import com.examw.test.app.AppConfig;
import com.examw.test.app.AppContext;
import com.examw.test.ui.MainActivity;

public class ViewPagerAdapter  extends PagerAdapter {  
  
    // 界面列表  
    private List<View> views;  
    private Activity activity;
    private int code;
    public ViewPagerAdapter(List<View> views, Activity activity) {  
        this.views = views;  
        this.activity = activity;
        try {
			this.code = ((AppContext)activity.getApplication()).getVersionCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
  
    // 销毁arg1位置的界面  
    @Override  
    public void destroyItem(View arg0, int arg1, Object arg2) {  
        ((ViewPager) arg0).removeView(views.get(arg1));  
    }  
  
    @Override  
    public void finishUpdate(View arg0) {  
    }  
  
    // 获得当前界面数  
    @Override  
    public int getCount() {  
        if (views != null) {  
            return views.size();  
        }  
        return 0;  
    }  
  
    // 初始化arg1位置的界面  
    @Override  
    public Object instantiateItem(View arg0, int arg1) {  
        ((ViewPager) arg0).addView(views.get(arg1), 0);  
        if (arg1 == views.size() - 1) {  
//            ImageView mStartWeiboImageButton = (ImageView) arg0  
//                    .findViewById(R.id.iv_start_weibo); 
        	views.get(arg1).findViewById(R.id.start).setOnClickListener(new OnClickListener() {  
  
                @Override  
                public void onClick(View v) {  
                    // 设置已经引导  
                    setGuided();  
                    goMain();  
  
                }  
  
            });  
            views.get(arg1).setOnClickListener(new OnClickListener() {  
  
                @Override  
                public void onClick(View v) {  
                    // 设置已经引导  
                    setGuided();  
                    goMain();  
  
                }  
  
            });  
        }  
        return views.get(arg1);  
    }  
  
    private void goMain() {  
        // 跳转  
        Intent intent = new Intent(activity, MainActivity.class);  
        activity.startActivity(intent);  
        activity.finish();  
    }  
    /** 
     *  
     * method desc：设置已经引导过了，下次启动不用再次引导 
     */  
    private void setGuided() {  
        SharedPreferences preferences = activity.getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);  
        Editor editor = preferences.edit();  
        // 存入数据  
        editor.putBoolean("isFirstIn"+code, false);  
        // 提交修改  
        editor.commit();  
    }  
  
    // 判断是否由对象生成界面  
    @Override  
    public boolean isViewFromObject(View arg0, Object arg1) {  
        return (arg0 == arg1);  
    }  
  
    @Override  
    public void restoreState(Parcelable arg0, ClassLoader arg1) {  
    }  
  
    @Override  
    public Parcelable saveState() {  
        return null;  
    }  
  
    @Override  
    public void startUpdate(View arg0) {  
    }  
  
}  