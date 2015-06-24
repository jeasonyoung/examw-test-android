package com.examw.test.ui;

import android.os.Bundle;
import android.webkit.WebView;

import com.examw.test.R;
import com.examw.test.support.URLs;
import com.examw.test.util.LogUtil;

/**
 * 
 * @author fengwei.
 * @since 2015年1月3日 下午2:50:57.
 */
public class WebViewActivity extends BaseActivity{
	//private static final String TAG = "WebViewActivity";
	private static final String content = "<p>某大型公共建筑施工土方开挖、基坑支护、止水帷幕的工程图纸及技术参数如图6.I.1“基坑支护及止水帷幕方案平面布置示意图”图6.I.2“基坑支护及止水帷幕剖面图”所示。护坡桩施工方案采用泥浆护壁成孔混凝土灌桩，其相关项目定额预算单价见下表：</p><p>单位：m3</p><p><img src=\"/examw-test/upload/preview/ac153672-6481-4b38-85cb-a6cfd70f3fe2\" title=\"图片6.png\" alt=\"图片6.png\"/><br/><img src=\"/examw-test/upload/preview/ebfd1fa1-ab87-4e3b-831f-743b4c84c600\" title=\"图片7.jpg\" alt=\"图片7.jpg\"/><br/></p><p>图6.I.1基坑支护及止水帷幕方案平面布置示意图</p><p>说明：</p><p>1.本图采用相对坐标系，±0.00m=49.25m，自然地面标高-0.79m考虑。基坑支护采用砌筑挡墙+护坡桩+预应力锚索。</p><p>2.1-1、2-2剖面基底-15.10m，基坑支护深度14.31m。</p><p>3.1-1剖面护坡桩直径800mm，间距1.50m，共计194根。2-2剖面护坡桩直径800m，间距1.50m，共计156根。</p><p>4.基坑采用旋喷桩止水帷幕。旋喷桩直径800mm，间距1500mm与护坡桩间隔布置，旋喷桩桩顶标高-7.29m，共计350根。</p><p>5、护坡桩桩顶设置800×600连梁，1-1、2-2剖面连梁以上2000mm为370mm厚砌筑挡墙。</p><p>6、护坡桩、连续及压顶梁的混凝土强度设计等级采用C25。</p><p>7.图中标注尺寸以mm计，标高以m计。</p><p>图6.I.2基坑支护及止水帷幕剖面图</p><p><img src=\"/examw-test/upload/preview/f6d5c644-4a7d-4b79-866f-25feb0514d98\" title=\"图片8.jpg\" alt=\"图片8.jpg\"/><br/></p><p>说明：</p><p>1、本图采用相对坐标系，±0.00m=49.25m；</p><p>2.1-1剖面双重锚索共计190列（380根），2-2剖面双重锚索共计154列（308根）。</p><p>3.图中标注尺寸以mm计，标高以m计。</p><p><br/></p><br/>";
	
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_webview);
		this.webView = (WebView) this.findViewById(R.id.webView);
		webView.getSettings().setSupportZoom(false);
		initData(content);
	}
	
	public void initData(String text) {
		// System.out.println(text);
		// 过滤掉 img标签的width,height属性
		text = text.replaceAll(
				"(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
		text = text.replaceAll(
				"(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");

		// 添加点击图片放大支持	(<img[^>]+src\\s*=\\s*['\"])([^'\"]+)(/[^'\"]+)\"
		text = text.replaceAll("(<img[^>]+src=\")(\\S+)\"",
				"$1"+URLs.HOST+"$2\" onClick=\"javascript:mWebViewImageListener.onImageClick('$2')\"");
		LogUtil.d(text);
		webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
	}
}
