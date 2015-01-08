package com.examw.test.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.examw.test.R;
import com.examw.test.adapter.ViewPagerAdapter;

/**
 * 引导界面
 * @author fengwei.
 * @since 2014年11月25日 下午3:43:13.
 */
public class GuideActivity extends Activity implements OnPageChangeListener {

	private ViewPager vp;
//	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	// 控制小点
	private ImageView[] dots;

	// 当前页号
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("GuideActivity", "on Create");
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(1); // 去标题
		this.setContentView(R.layout.ui_guide);
		// 初始化组件
		this.initViews();
		// 初始化
		this.initDots();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);
		this.views = new ArrayList<View>();
		// 初始化引导图片列表
		this.views.add(inflater.inflate(R.layout.guide_page1, null));
		this.views.add(inflater.inflate(R.layout.guide_page2, null));
		this.views.add(inflater.inflate(R.layout.guide_page3, null));
		// 初始化Adapter
//		vpAdapter = new ViewPagerAdapter(views, this);
		this.vp = (ViewPager) findViewById(R.id.awesomepager);
		this.vp.setAdapter(new ViewPagerAdapter(views, this));
		// 绑定回调
		this.vp.setOnPageChangeListener(this);
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.pagelist);
		dots = new ImageView[views.size()];

		// 循环取得小点图片
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// 都设置为灰色
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为白色,即选中状态
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > views.size() - 1
				|| currentIndex == position) {
			return;
		}

		dots[position].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = position;
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// 当心的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		setCurrentDot(arg0);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("GuideActivity", "on destroy");
		super.onDestroy();
	}
}
