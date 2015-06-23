package com.examw.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

/**
 * 欢迎的Activity
 * 
 * @author jeasonyoung
 * @since 2015年6月19日
 */
public class WelcomeActivity extends Activity {
	
	 /*
	  * 重载创建
	  * @see android.app.Activity#onCreate(android.os.Bundle)
	  */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//加载布局XML
		this.setContentView(R.layout.activity_welcome);
		//加载布局视图
		final View view =  this.findViewById(R.id.layout_welcome);
		//设置动画
		view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_welcome));
	}
}