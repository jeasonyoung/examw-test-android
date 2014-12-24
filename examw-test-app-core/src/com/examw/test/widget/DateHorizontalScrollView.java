package com.examw.test.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.examw.test.R;

/**
 * 
 * @author fengwei.
 * @since 2014年12月21日 下午3:44:44.
 */
public class DateHorizontalScrollView extends HorizontalScrollView implements OnClickListener {

	public DateHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public DateHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DateHorizontalScrollView(Context context) {
		super(context);
		init(context);
	}
	private FrameLayout mFrameLayout;
	private BaseAdapter mBaseAdapter;
	private SparseArray<View> mSparseArray;
	private int oldPosition;
	private LinearLayout backView;
	private int screenWidth;
	
	@SuppressWarnings("deprecation")
	private void init(Context context) {
		mFrameLayout = new FrameLayout(getContext());
		mFrameLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(mFrameLayout);
		mSparseArray = new SparseArray<View>();
		screenWidth = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
	}

	private void buildItemView() {
		if (mBaseAdapter == null)
			return;
		LinearLayout linearLayout = new LinearLayout(getContext());
		for (int i = 0; i < mBaseAdapter.getCount(); i++) {
			View view = mBaseAdapter.getView(i, mSparseArray.get(i), this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					screenWidth/3,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			view.setLayoutParams(lp);
			view.setOnClickListener(this);
			mSparseArray.put(i, view);
			linearLayout.addView(mSparseArray.get(i));
		}
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				screenWidth/3,LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.gravity = Gravity.CENTER_VERTICAL;
		backView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.daily_date_item, null);
		backView.setBackgroundResource(R.drawable.bg_view);
		backView.setPadding(0, 5, 0, 5);
		mFrameLayout.addView(backView, layoutParams);
		mFrameLayout.addView(linearLayout);
	}

	public void setAdapter(BaseAdapter baseAdapter) {
		if (baseAdapter == null)
			return;
		mBaseAdapter = baseAdapter;
		mBaseAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				oldPosition = 0;
				buildItemView();
				super.onChanged();
			}
		});
		mBaseAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		if (onItemClickListener != null) {
			int position = mSparseArray.indexOfValue(v);
			startAnimation(position);
			oldPosition = position;
			onItemClickListener.click(position);
		}
	}

	public void startAnimation(int position) {
		if(position == oldPosition) return;
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(buildScaleAnimation(oldPosition, position));
		animationSet
				.addAnimation(buildTranslateAnimation(oldPosition, position));
		animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
		/* 移动后不复原,不返回动画前的状态位置 */
		animationSet.setFillAfter(true);
		animationSet.setDuration(500);
		backView.startAnimation(animationSet);
		int v = screenWidth/3;
		scrollTo(v*(position-1));
		oldPosition = position;
		invalidate();
	}

	private Animation buildScaleAnimation(int oldPosition, int position) {
		float oldWidth = getItemView(oldPosition).getWidth();
		float newWidth = getItemView(position).getWidth();
		float fromX = oldWidth / backView.getWidth();
		float toX = newWidth / backView.getWidth();
		ScaleAnimation animation = new ScaleAnimation(fromX, toX, 1f, 1f,
				Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f);
		return animation;
	}

	private Animation buildTranslateAnimation(int oldPosition, int position) {
		TranslateAnimation animation = new TranslateAnimation(getItemView(
				oldPosition).getLeft(), getItemView(position).getLeft(), 0, 0);
		return animation;
	}

	private View getItemView(int position) {
		return mSparseArray.get(position);
	}

	public interface OnItemClickListener {
		void click(int position);
	}

	private OnItemClickListener onItemClickListener;

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public int getCurrentPosition() {
		return oldPosition;
	}

	public int getPrevPosition() {
		if (oldPosition == 0)
			return oldPosition;
		return oldPosition - 1;
	}

	public int getNextPosition() {
		if (oldPosition == mSparseArray.size() - 1)
			return oldPosition;
		return oldPosition + 1;
	}
	
	private void scrollTo(final int x)
	{
		post(new Runnable(){
			@Override
			public void run() {
				smoothScrollTo(x,0);
			}
		});
	}
}
