package com.examw.test.widget;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 缩放视图(装饰者模式)。
 * @param <V>
 * @author jeasonyoung
 * @since 2015年12月3日
 */
public abstract class ZoomView<V extends View> {
	private static final int NONE = 0;// 空  
    private static final int DRAG = 1;// 按下第一个点  
    private static final int ZOOM = 2;// 按下第二个点 
    /**
     * 视图对象。
     */
    protected V view; 
    
    //屏幕上点的数量 
    private int mode = NONE;  
  
    //记录按下第二个点距第一个点的距离  
    private float oldDist;
    
    /**
     * 构造函数。
     * @param view
     * 视图对象。
     */
    public ZoomView(V view){  
        this.view = view;  
        this.setTouchListener();  
    }
    
    //设置手势监听
    private void setTouchListener(){
    	this.view.setOnTouchListener(new OnTouchListener() {
			/*
			 * 手势处理。
			 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
			 */
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch(event.getAction() & MotionEvent.ACTION_MASK){
					case MotionEvent.ACTION_DOWN:  
						mode = DRAG;  
                    break;  
					case MotionEvent.ACTION_UP:  
					case MotionEvent.ACTION_POINTER_UP:  
						mode = NONE;  
                    break;  
					case MotionEvent.ACTION_POINTER_DOWN:  
						oldDist = spacing(event);  
						if (oldDist > 10f){  
							mode = ZOOM;  
						}  
						break;  
					case MotionEvent.ACTION_MOVE:  
						if (mode == ZOOM){  
							// 正在移动的点距初始点的距离  
							float newDist = spacing(event);  
							if (newDist > oldDist){  
								zoomOut();  
							}  
							if (newDist < oldDist){  
								zoomIn();  
							}
                    }  
                    break;  
				}
				return true;
			}
			
			//求出2个触点间的 距离 
			private float spacing(MotionEvent event){
				float x = event.getX(0) - event.getX(1);  
                float y = event.getY(0) - event.getY(1);  
                return (float)Math.sqrt(x * x + y * y);
			}
		});
    }

    /**
     * 缩小。
     */
    protected abstract void zoomIn();  
    /**
     * 放大。
     */
    protected abstract void zoomOut();
}