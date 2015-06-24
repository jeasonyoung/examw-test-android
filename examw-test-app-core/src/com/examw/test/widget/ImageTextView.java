package com.examw.test.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppConfig;
import com.examw.test.support.URLs;
import com.examw.test.ui.ImageZoomActivity;
import com.examw.test.util.BitmapManager;
import com.examw.test.util.HtmlUtils;
import com.examw.test.util.LogUtil;
import com.examw.test.util.StringUtils;

public class ImageTextView extends LinearLayout {
	//private static final String TAG = "ImageTextView2";
	private ArrayList<TextView> textViews = new ArrayList<TextView>();
	private Context context;
	private static BitmapManager bmpManager;
	private static LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

	public ImageTextView(Context paramContext) {
		super(paramContext);
	}

	public ImageTextView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		this.context = paramContext;
		init();
//		calculateWidth();
	}

	private void init() {
		if(bmpManager == null)
			bmpManager = new BitmapManager(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.img_empty));
		this.setOrientation(LinearLayout.VERTICAL);	//垂直布局
	}

	OnClickListener imageViewOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String url = (String) v.getTag();
			Intent intent = new Intent(context,ImageZoomActivity.class);
			intent.putExtra("url", url);
			context.startActivity(intent);
		}
	};
	
	public void setText(String text) {
		this.removeAllViews();
		this.textViews.clear();
		if (StringUtils.isEmpty(text)) {
			return;
		}
		// Text 过滤掉 P BR 标签
		text = HtmlUtils.filterPTag(text);
		if(!text.matches("[\\S\\s]*<img[^>]+[/?]>[\\S\\s]*")){	//不包含图片
			if(text.contains("<table>"))
			{
				text.replaceAll("\n", "<br/>");
				WebView wv = new WebView(context,null);
				wv.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
				this.addView(wv);
			}else{
				TextView tv = new TextView(context, null, R.style.question_text);
				tv.setText(text);
				tv.setLayoutParams(lp);
				tv.setTextColor(tv.getResources().getColor(R.color.black));
				this.addView(tv);
				this.textViews.add(tv);
			}
		} else {
			Pattern ps = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
			Matcher m = ps.matcher(text);
			ArrayList<String> images = new ArrayList<String>();
			while(m.find()){
				images.add(m.group(1));
			}
			String[] texts = text.split("<img[^>]+[/?]>");
			for(int i=0;i<texts.length;i++)
			{
				if(!StringUtils.isEmpty(texts[i])){
					TextView tv = new TextView(context);
					tv.setLayoutParams(lp);
					tv.setTextColor(tv.getResources().getColor(R.color.black));
					tv.setText(texts[i]);
					this.addView(tv);
					this.textViews.add(tv);
				}
				if(i<images.size())
				{
					ImageView iv = new ImageView(context,null,R.style.question_image);
					iv.setLayoutParams(lp);
					iv.setAdjustViewBounds(true);
					iv.setMaxHeight(300);
					iv.setScaleType(ScaleType.FIT_START);
					String url = images.get(i);
					iv.setTag(URLs.HOST+ url);
					LogUtil.d(URLs.HOST+ url);
					if(!loadLocaleImage(images.get(i),iv)){
						bmpManager.loadBitmap(URLs.HOST+url, iv);
					}
//					iv.setImageResource(R.drawable.welcome);
					iv.setOnClickListener(imageViewOnClick);
					iv.setVisibility(View.VISIBLE);
					this.addView(iv);
				}
			}
		}
	}
	private boolean loadLocaleImage(String url,ImageView iv)
	{
		String myJpgPath = url.substring(url.lastIndexOf("/")+1); 
		myJpgPath = AppConfig.DEFAULT_SAVE_IMAGE_PATH+myJpgPath;
        File file = new File(myJpgPath);
        if(file.exists())
        {
        	BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
            iv.setImageBitmap(bm);
            return true;
        }
        return false;
	}
	public void setTextColor(int rid) {
		for(TextView tv:textViews)
		{
			tv.setTextColor(rid);
		}
	}

	public void setTextSize(float size) {
		for(TextView tv:textViews)
		{
			tv.setTextSize(size);
		}
	}
	public void setTextWithLine()
	{
		for(TextView tv:textViews)
		{
			tv.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG); //中间横线
		}
	}
	public void resetTextStyle()
	{
		for(TextView tv:textViews)
		{
			tv.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
		}
	}
}

