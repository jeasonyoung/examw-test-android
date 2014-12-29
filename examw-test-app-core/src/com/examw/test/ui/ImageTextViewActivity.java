package com.examw.test.ui;

import android.os.Bundle;

import com.examw.test.R;
import com.examw.test.widget.ImageTextView;

/**
 * 
 * @author fengwei.
 * @since 2014年12月29日 下午3:45:14.
 */
public class ImageTextViewActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_image_text_view);
		String str = "如下图所示，假设梯子的总长度为l。根据结构的对称性，取梯子的一半，则其顶端承受的力为P/2，根据结构竖向受力的平衡，可知地面的竖向支撑力也为P/2。假设绳子的拉力为F．则根据结构水平受力的平衡，梯子左右两部分在顶端的水平压力也为F根据梯子顶端所受弯矩的平衡，可以得到：<img src=\"/examw-test/upload/preview/7df442ea-1966-4f72-aff8-363600a806fd\" title=\"图片54.jpg\" alt=\"图片54.jpg\"/>";
		String str2 = "8、图示人字梯放置在光滑（忽略摩擦）地面上，顶端人体重量为P。关于绳子拉力与梯子和地面的夹角α、绳子位置h的关系的说法，正确的是(  )。<img src=\"/examw-test/upload/preview/72dc8bdb-1a2d-491b-89b6-b4904c128c88\" title=\"图片53.png\" alt=\"图片53.png\"/>";

		((ImageTextView)findViewById(R.id.img)).setText(str2);
	}
}
