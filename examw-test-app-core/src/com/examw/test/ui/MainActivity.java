package com.examw.test.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.widget.WaitingViewDialog;

/**
 * App主界面。
 * 
 * @author jeasonyoung
 * @since 2015年7月1日
 */
public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener,FragmentManager.OnBackStackChangedListener {
	private static final  String TAG = "MainActivity";
	private BottomMenuType menuType = BottomMenuType.None;
	protected WaitingViewDialog waitingViewDialog;
	/*
	 * 创建Activity。
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "初始化创建Activity...");
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		//加载布局文件
		this.setContentView(R.layout.ui_main);
		//加载底部菜单group
		final RadioGroup group = (RadioGroup)this.findViewById(R.id.main_bottom_groups);
		//添加选中事件
		group.setOnCheckedChangeListener(this);
		//装载Fragment
		this.createSubFragment(BottomMenuType.Home);
		//添加back事件处理
		this.getSupportFragmentManager().addOnBackStackChangedListener(this);
	}
	/*
	 * 菜单选中处理。
	 * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d(TAG, "选中菜单:" + checkedId);
		switch(checkedId){
			case R.id.main_bottom_btn_home:{//1.首页
				Log.d(TAG, "加载首页菜单...");
				this.createSubFragment(BottomMenuType.Home);
				break;
			}
			case R.id.main_bottom_btn_favorites:{//2.错题
				Log.d(TAG, "加载错题菜单...");
				this.createSubFragment(BottomMenuType.Favorites);
				break;
			}
			case R.id.main_bottom_btn_my:{//3.我的
				Log.d(TAG, "加载我的菜单...");
				this.createSubFragment(BottomMenuType.My);
				break;
			}
			case R.id.main_bottom_btn_more:{//4.更多
				Log.d(TAG, "加载更多菜单...");
				this.createSubFragment(BottomMenuType.More);
			}
		}
	}
	/*
	 * 返回事件处理。
	 * @see android.support.v4.app.FragmentManager.OnBackStackChangedListener#onBackStackChanged()
	 */
	@Override
	public void onBackStackChanged() {
		Log.d(TAG, "当前菜单:" + this.menuType);
		if(this.menuType != BottomMenuType.None){
			this.menuType = BottomMenuType.None;
		}
	}
	/*
	 * 键盘响应。
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "键盘响应:" + keyCode);
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && this.menuType == BottomMenuType.None){
			if(event.getRepeatCount() == 0){
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				return false;
			}
			Log.d(TAG, "退出程序...");
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	//创建子Fragment
	private void createSubFragment(BottomMenuType type){
		Log.d(TAG, "创建子Fragment:" + type);
		Fragment fragment = null;
		switch(this.menuType = type){
			case None:
			case Home:{//1.首页
				fragment = new MainHomeFragment(this);
				break;
			}
			case Favorites:{//2.错题
				fragment = new MainWrongFragment(this);
				break;
			}
			case My:{//3.我的
				fragment = null;
				break;
			}
			case More:{//4.更多
				fragment = null;
				break;
			}
		}
		if(fragment != null){
			this.getSupportFragmentManager()
			.beginTransaction()
			.addToBackStack(null)
			.replace(R.id.main_fragment_replace, fragment)
			.commit();
		}
	}
	
	/**
	 * 底部菜单类型枚举。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月1日
	 */
	private enum BottomMenuType{ None, Home, Favorites, My, More }
}