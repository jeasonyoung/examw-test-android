package com.examw.test.ui;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.examw.test.R;
import com.examw.test.util.BitmapManager;

/**
 * 图片缩放界面
 * @author fengwei.
 * @since 2014年12月24日 下午4:57:48.
 */
public class ImageZoomActivity extends BaseActivity{
	ImageView imageView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_image_zoom);
        imageView = (ImageView) findViewById(R.id.img);
        String url = getIntent().getStringExtra("url");
        new BitmapManager(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.img_empty)).loadBitmap(url, imageView);
        imageView.setOnTouchListener(new TounchListener());
    }

    private class TounchListener implements OnTouchListener{
    	
    	private PointF startPoint = new PointF();
    	private Matrix matrix = new Matrix();
    	private Matrix currentMaritx = new Matrix();
    	
    	private int mode = 0;//用于标记模式
    	private static final int DRAG = 1;//拖动
    	private static final int ZOOM = 2;//放大
    	private float startDis = 0;
    	private PointF midPoint;//中心点

		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mode = DRAG;
				currentMaritx.set(imageView.getImageMatrix());//记录ImageView当期的移动位置
				startPoint.set(event.getX(),event.getY());//开始点
				break;

			case MotionEvent.ACTION_MOVE://移动事件
				if (mode == DRAG) {//图片拖动事件
					float dx = event.getX() - startPoint.x;//x轴移动距离
					float dy = event.getY() - startPoint.y;
					matrix.set(currentMaritx);//在当前的位置基础上移动
					matrix.postTranslate(dx, dy);
					
				} else if(mode == ZOOM){//图片放大事件
					float endDis = distance(event);//结束距离
					if(endDis > 10f){
						float scale = endDis / startDis;//放大倍数
						matrix.set(currentMaritx);
						matrix.postScale(scale, scale, midPoint.x, midPoint.y);
					}
					
					
				}

				break;
				
			case MotionEvent.ACTION_UP:
				mode = 0;
				break;
			//有手指离开屏幕，但屏幕还有触点(手指)
			case MotionEvent.ACTION_POINTER_UP:
				mode = 0;
				break;
			//当屏幕上已经有触点（手指）,再有一个手指压下屏幕
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = ZOOM;
				startDis = distance(event);
				
				if(startDis > 10f){//避免手指上有两个茧
					midPoint = mid(event);
					currentMaritx.set(imageView.getImageMatrix());//记录当前的缩放倍数
				}
				
				break;


			}
			imageView.setImageMatrix(matrix);
			return true;
		}
    	
    }

    /**
     * 两点之间的距离
     * @param event
     * @return
     */
    private static float distance(MotionEvent event){
    	//两根线的距离
    	float dx = event.getX(1) - event.getX(0);
    	float dy = event.getY(1) - event.getY(0);
    	return FloatMath.sqrt(dx*dx + dy*dy);
    }
    /**
     * 计算两点之间中心点的距离
     * @param event
     * @return
     */
    private static PointF mid(MotionEvent event){
    	float midx = event.getX(1) + event.getX(0);
    	float midy = event.getY(1) - event.getY(0);
    	
    	return new PointF(midx/2, midy/2);
    }
}
