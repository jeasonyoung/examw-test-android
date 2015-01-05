package com.examw.test.widget;

import java.io.File;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppConfig;
import com.examw.test.support.URLs;
import com.examw.test.ui.ImageZoomActivity;
import com.examw.test.util.BitmapManager;
import com.examw.test.util.HtmlUtils;
import com.examw.test.util.StringUtils;

public class ImageTextView1 extends LinearLayout {
	private static final String TAG = "ImageTextView";
	private TextView tv_before;
	private ImageView imageView;
	private TextView tv_after;
	private View view;
	private Context context;
	private static BitmapManager bmpManager;
	private int width, height;
	private boolean hasMeasured;

	public ImageTextView1(Context paramContext) {
		super(paramContext);
	}

	public ImageTextView1(Context paramContext, AttributeSet paramAttributeSet) {
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
		if(bmpManager == null)
			bmpManager = new BitmapManager(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.img_empty));
		addView(this.view);
		this.imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = (String) v.getTag();
				Intent intent = new Intent(context,ImageZoomActivity.class);
				intent.putExtra("url", url);
				context.startActivity(intent);
			}
		});
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
			this.imageView.setVisibility(View.VISIBLE);
			String[] arr = text.split("(<img[^>]+src=\")(\\S+)\"(/?>)");
			String temp = text.substring(text.indexOf("src=\"") + 5,
					text.length());
			String url = temp.substring(0, temp.indexOf("\""));
			if (arr.length == 1) {
				this.tv_before.setText(HtmlUtils.filterImgTag(arr[0]));
				imageView.setTag(URLs.HOST+url);
				if(!loadLocaleImage(url))
				bmpManager.loadBitmap(URLs.HOST+url, imageView);
				this.tv_after.setVisibility(View.GONE);
			} else if (arr.length >= 2) {
				this.tv_before.setText(HtmlUtils.filterImgTag(arr[0]));
				imageView.setTag(URLs.HOST+url);
				if(!loadLocaleImage(url))
//				bmpManager.loadBitmap(url, imageView,BitmapFactory.decodeResource(
//						context.getResources(), R.drawable.img_empty),width,height);
				bmpManager.loadBitmap(URLs.HOST+url, imageView);
				this.tv_after.setVisibility(View.VISIBLE);
				this.tv_after.setText(HtmlUtils.filterImgTag(arr[1]));
			}
		}
	}
	private boolean loadLocaleImage(String url)
	{
		String myJpgPath = url.substring(url.lastIndexOf("/")+1); 
		myJpgPath = AppConfig.DEFAULT_SAVE_IMAGE_PATH+myJpgPath;
        File file = new File(myJpgPath);
        if(file.exists())
        {
        	BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
            imageView.setImageBitmap(bm);
            return true;
        }
        return false;
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
