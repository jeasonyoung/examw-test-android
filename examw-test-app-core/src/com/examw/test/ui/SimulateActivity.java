package com.examw.test.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;



/**
 * 模拟考试
 * @author fengwei.
 * @since 2014年11月26日 下午3:17:13.
 */
public class SimulateActivity extends FragmentActivity implements OnClickListener{
	private static final String TAG = "SimulateActivity";
	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;

	private List<Fragment> mDatas;

	private TextView mRealPaperTextView;
	private TextView mSimulatePaperTextView;

	private ImageView mTabLine;
	private int mScreen1_3;

	private int mCurrentPageIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_paper_list);
		initTabLine();
		initView();
	}

	private void initTabLine() {
		mTabLine = (ImageView) findViewById(R.id.tabline);
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		mScreen1_3 = outMetrics.widthPixels / 2;
		LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
		lp.width = mScreen1_3 - 20;
		lp.leftMargin = 10;
		lp.rightMargin = 10;
		mTabLine.setLayoutParams(lp);
	}

	private void initView() {
		((TextView)this.findViewById(R.id.title)).setText("模拟考场");;
		mRealPaperTextView = (TextView) findViewById(R.id.real_paper_tv);
		mSimulatePaperTextView = (TextView) findViewById(R.id.simulate_paper_tv);

		findViewById(R.id.real_paper_layout).setOnClickListener(this);
		findViewById(R.id.simulate_paper_layout).setOnClickListener(this); 
		
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

		mDatas = new ArrayList<Fragment>();
		RealPaperFragment tab01 = new RealPaperFragment();
		SimulatePaperFragment tab02 = new SimulatePaperFragment();

		mDatas.add(tab01);
		mDatas.add(tab02);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mDatas.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return mDatas.get(arg0);
			}
		};
		Log.d(TAG,"ViewPager设置适配器");
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				resetTextView();
				switch (position) {
				case 0:
					mRealPaperTextView.setTextColor(Color.parseColor("#065bea"));
					break;
				case 1:
					mSimulatePaperTextView.setTextColor(Color.parseColor("#065bea"));
					break;
				}
				mCurrentPageIndex = position;
			}

			@Override
			public void onPageScrolled(int position, float positonoffset,
					int positonoffsetPx) {
				LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine
						.getLayoutParams();
				if (mCurrentPageIndex == 0 && position == 0) { // 0 -> 1

					lp.leftMargin = (int) (positonoffset * mScreen1_3 + mCurrentPageIndex
							* mScreen1_3);

				} else if (mCurrentPageIndex == 1 && position == 0) { // 1 -> 0

					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + (positonoffset - 1)
							* mScreen1_3);
				} else if (mCurrentPageIndex == 1 && position == 1) { // 1->2

					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + positonoffset
							* mScreen1_3);
				} else if (mCurrentPageIndex == 2 && position == 1) { // 2->1

					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + (positonoffset - 1)
							* mScreen1_3);

				}
				lp.leftMargin = lp.leftMargin+10;
				mTabLine.setLayoutParams(lp);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}
	@Override
	public void onClick(View v) {
		Log.d(TAG,"onclick "+v.getId());
		switch(v.getId())
		{
		case R.id.real_paper_layout:
			Log.d(TAG,"onclick");
			mViewPager.setCurrentItem(0);
			break;
		case R.id.simulate_paper_layout:
			Log.d(TAG,"onclick");
			mViewPager.setCurrentItem(1);
			break;
		}
	}
	protected void resetTextView() {
		// TODO Auto-generated method stub
		mRealPaperTextView.setTextColor(Color.BLACK);
		mSimulatePaperTextView.setTextColor(Color.BLACK);
	}

}
