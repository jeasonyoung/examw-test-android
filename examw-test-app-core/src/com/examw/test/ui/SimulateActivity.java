package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao;
import com.examw.test.model.FrontPaperInfo;
import com.examw.test.support.ApiClient;



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
	
	private LinearLayout loadingLayout,nodataLayout,reloadLayout;
	
	private ImageView mTabLine;
	private int mScreen1_3;

	private int mCurrentPageIndex;
	
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_paper_list);
		this.handler = new MyHandler(this);
		initView();
		initTabLine();
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
		
		loadingLayout = (LinearLayout) this.findViewById(R.id.loadingLayout);
		nodataLayout = (LinearLayout) this.findViewById(R.id.nodataLayout);
		reloadLayout = (LinearLayout) this.findViewById(R.id.reload);
		
		loadingLayout.setVisibility(View.VISIBLE);
		
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		
		new GetPaperListThread().start();
	}
	
	private void initViewPager(){
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
		mRealPaperTextView.setTextColor(Color.BLACK);
		mSimulatePaperTextView.setTextColor(Color.BLACK);
	}
	
	private class GetPaperListThread extends Thread
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			if(PaperDao.hasPaper())
			{
				//本地数据库中有试卷
				handler.sendEmptyMessage(1);
			}else
			{
				//本地数据库中没有试卷,访问网络
				try{
					ArrayList<FrontPaperInfo> list = ApiClient.getPaperList((AppContext)getApplication());
					PaperDao.insertPaperList(list);
					if(list == null || list.size()==0)
						handler.sendEmptyMessage(2);
					else
						handler.sendEmptyMessage(1);
				}catch(Exception e)
				{
					e.printStackTrace();
					Message msg = handler.obtainMessage();
					msg.what = -1;
					handler.sendMessage(msg);
				}
			}
		}
	}
	static class MyHandler extends Handler {
        WeakReference<SimulateActivity> mActivity;
        MyHandler(SimulateActivity activity) {
                mActivity = new WeakReference<SimulateActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
        	SimulateActivity theActivity = mActivity.get();
        	theActivity.loadingLayout.setVisibility(View.GONE);
                switch (msg.what) {
                case 1:
                	theActivity.nodataLayout.setVisibility(View.GONE);//无数据显示
                	theActivity.loadingLayout.setVisibility(View.GONE);//无数据显示
                	mActivity.get().initViewPager();
                	theActivity.reloadLayout.setVisibility(View.GONE);//无数据显示
                	break;
                case 2:
                	break;
                case -1:
                	//连不上,
            		theActivity.reloadLayout.setVisibility(View.VISIBLE);//无数据显示
            		Toast.makeText(theActivity, "暂时连不上服务器,请稍候", Toast.LENGTH_SHORT).show();//提示
            		break;
                }
        }
	}
}
