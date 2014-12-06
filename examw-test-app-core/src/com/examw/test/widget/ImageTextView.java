package com.examw.test.widget;

import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.util.BitmapManager;
import com.examw.test.util.StringUtils;

public class ImageTextView extends LinearLayout {
	private TextView tv_before;
	private ImageView imageView;
	private TextView tv_after;
	private View view;
	private Context context;
	private BitmapManager bmpManager;
	private int width, height;
	private boolean hasMeasured;

	public ImageTextView(Context paramContext) {
		super(paramContext);
	}

	public ImageTextView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		this.context = paramContext;
		this.view = LayoutInflater.from(paramContext).inflate(
				R.layout.image_text_view, this, false);
		init();
//		calculateWidth();
	}

	private void init() {
		this.tv_before = ((TextView) this.view.findViewById(R.id.text_1));
		this.imageView = (ImageView) this.view.findViewById(R.id.image1);
		this.tv_after = ((TextView) this.view.findViewById(R.id.text_2));
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.img_empty));
		addView(this.view);
		this.view = null;
		System.gc();
	}

	private void calculateWidth() {
		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {

					height = view.getMeasuredHeight();
					width = view.getMeasuredWidth();
					// ��ȡ����Ⱥ͸߶Ⱥ󣬿����ڼ���
					hasMeasured = true;
				}
				return true;
			}
		});
		System.out.println("width = "+width +" height = "+height);
	}

	public void setText(String text) {
		// ����ͼƬ����text1��ʾ
		if (StringUtils.isEmpty(text)) {
			return;
		}
		boolean bool1 = Pattern
				.compile(
						"[\\s\\S]*(<img[^>]+src=\")(\\S+)\"[\\s\\S]*(/?>)[\\s\\S]*")
				.matcher(text).matches();
		if (!bool1) {
			this.tv_before.setText(text);
			this.imageView.setVisibility(View.GONE);
			this.tv_after.setVisibility(View.GONE);
		} else {
			String[] arr = text.split("(<img[^>]+src=\")(\\S+)\"(/?>)");
			String temp = text.substring(text.indexOf("src=\"") + 5,
					text.length());
			String url = temp.substring(0, temp.indexOf("\""));
			if (arr.length == 1) {
				this.tv_before.setText(arr[0]);
				bmpManager.loadBitmap(url, imageView);
				this.tv_after.setVisibility(View.GONE);
			} else if (arr.length == 2) {
				this.tv_before.setText(arr[0]);
				bmpManager.loadBitmap(url, imageView,BitmapFactory.decodeResource(
						context.getResources(), R.drawable.img_empty),width,height);
				this.tv_after.setText(arr[1]);
			}
		}
	}

	public void setTextColor(int rid) {
		this.tv_before.setTextColor(rid);
		this.tv_after.setTextColor(rid);
	}

	public void setTextSize(float size) {
		this.tv_before.setTextSize(size);
		this.tv_after.setTextSize(size);
	}
	public void setTextWithLine()
	{
		this.tv_before.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG); //中间横线
		this.tv_after.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG); //中间横线
	}
	public void resetTextStyle()
	{
		this.tv_before.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
		this.tv_after.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
	}
}
