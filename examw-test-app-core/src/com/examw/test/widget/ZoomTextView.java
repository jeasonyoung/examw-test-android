package com.examw.test.widget;

import android.widget.TextView;

/**
 * 
 * 
 * @author jeasonyoung
 * @since 2015年12月3日
 */
public class ZoomTextView extends ZoomView<TextView> {
	//最小字体 
    public static final float MIN_TEXT_SIZE = 10f;  
    //最大子图
    public static final float MAX_TEXT_SIZE = 100.0f; 
    
    //缩放比例
    private float scale;
    //设置字体大小 
    private float textSize;

    /**
     * 构造函数。
     * @param view
     * @param scale
     */
    public ZoomTextView(TextView view, float scale){  
        super(view);  
        this.scale = scale;  
        this.textSize = view.getTextSize();  
    } 
    
    /**
     * 构造函数。
     * @param view
     */
    public ZoomTextView(TextView view){
    	this(view, 0.5f);
    }

    /*
	 * 放大
	 * @see com.examw.test.widget.ZoomView#zoomOut()
	 */
	@Override
	protected void zoomOut() {
		this.textSize += scale;  
        if (this.textSize > MAX_TEXT_SIZE){  
            this.textSize = MAX_TEXT_SIZE;  
        }  
       this.view.setTextSize(textSize); 
	}
	
    /*
     * 缩小。
     * @see com.examw.test.widget.ZoomView#zoomIn()
     */
	@Override
	protected void zoomIn() {
		this.textSize -= scale;  
        if (this.textSize < MIN_TEXT_SIZE){  
            this.textSize = MIN_TEXT_SIZE;  
        }  
        this.view.setTextSize(textSize); 
	}
}