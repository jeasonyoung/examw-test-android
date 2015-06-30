package com.examw.test.widget;

import com.examw.test.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;

/**
 * 等待加载进度框。
 * 
 * @author jeasonyoung
 * @since 2015年6月26日
 */
public class WaitingViewDialog extends Dialog {
	private static final String TAG = "WaitingViewDialog";
	private ImageView imageView;
	/**
	 * 构造函数。
	 * @param context
	 */
	public WaitingViewDialog(Context context) {
		super(context, R.style.WaitingViewDialog);
		Log.d(TAG, "初始化等待进度框....");
		this.setContentView(R.layout.waiting_view_dialog);
		this.imageView = (ImageView)this.findViewById(R.id.waitingImageView);
	}
	/*
	 * 重载呈现。
	 * @see android.app.Dialog#show()
	 */
	@Override
	public void show() {
		Log.d(TAG, "呈现等待模态窗口...");
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
		super.show();
		if(this.imageView != null){
			//启动动画
			AnimationDrawable animationDrawable = (AnimationDrawable)this.imageView.getBackground();
			if(animationDrawable != null){
				animationDrawable.start();
			}
		}
	}
}