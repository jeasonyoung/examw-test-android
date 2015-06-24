package com.examw.test.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.app.AppManager;
import com.examw.test.util.LogUtil;

/**
 * 菜单列表
 * @author fengwei.
 * @since 2014年11月26日 下午4:37:51.
 */
public class MenuListFragment extends ListFragment {
	private AppContext appContext;
	private SampleItem accountItem;
	private SampleAdapter adapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.home_menu_list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		adapter = new SampleAdapter(getActivity());
		adapter.add(new SampleItem("首页", R.drawable.slide_menu_home));
//		initAccountItem();
//		adapter.add(accountItem);
//		adapter.add(new SampleItem("关于", R.drawable.slide_menu_qiehuan));
		adapter.add(new SampleItem("设置", R.drawable.slide_menu_set));
		adapter.add(new SampleItem("注销", R.drawable.slide_menu_logout));
		adapter.add(new SampleItem("退出", R.drawable.slide_menu_exit));
		if(LogUtil.showLog)
			adapter.add(new SampleItem("导入数据",R.drawable.slide_menu_qiehuan));
		setListAdapter(adapter);
	}

	private void initAccountItem() {
		if (accountItem == null) {
			accountItem = new SampleItem();
			accountItem.iconRes = R.drawable.slide_menu_picture;
		}
		int flag = appContext.getLoginState();
		String loginStr = "";
		switch (flag) {
		case AppContext.LOGINED:
//			loginStr = "账户(" + appContext.getUsername() + ")";
			loginStr = "账户(abcd)";
			break;
		case AppContext.UNLOGIN:
			loginStr = "账户(未登录)";
			break;
		case AppContext.LOGIN_FAIL:
			loginStr = "账户(未登录)";
			break;
		case AppContext.LOGINING:
			loginStr = "账户(正登录)";
			break;
		case AppContext.LOCAL_LOGINED:
			loginStr = "账户(本地登录)";
		}
		accountItem.tag = loginStr;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		MainActivity main = (MainActivity) getActivity();
		switch (position) {
		case 0:	//首页
			main.showContent();
			Fragment f = null;
			if (main.getFlag() != MainActivity.MAIN_INDEX) {
				MainFragment m =  new MainFragment();
				f = m;
				createFragment(f);
				main.setFlag(MainActivity.MAIN_INDEX);
				main.setMainFragment(m);
			}
			break;
//		case 1:	//用户页
//			if (appContext.getLoginState() == AppContext.UNLOGIN
//					|| appContext.getLoginState() == AppContext.LOGIN_FAIL
//					|| appContext.getLoginState() == AppContext.LOCAL_LOGINED) {
//				Intent intent = new Intent(getActivity(), LoginActivity.class);
//				intent.putExtra("loginFrom", LoginActivity.LOGIN_MAIN);
//				startActivityForResult(intent, 10);
//				// main.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
//			} else {
//				if (main.getFlag() != MainActivity.MAIN_ACCOUNT) {
//					main.showContent();
//					f = new UserInfoFragment();
//					createFragment(f);
//					main.setFlag(MainActivity.MAIN_ACCOUNT);
//				}else
//				{
//					main.showContent();
//				}
//			}
//			break;
//		case 2:	//关于
//			main.showContent();
//			if (main.getFlag() != MainActivity.MAIN_ABOUT) {
//				f = new AboutusFragment();
//				createFragment(f);
//				main.setFlag(MainActivity.MAIN_ABOUT);
//			}
//			break;
		case 1:	//设置
			main.showContent();
			main.hideNewTag();
			if (main.getFlag() != MainActivity.MAIN_SETTING) {
				f = new SettingFragment();
				createFragment(f);
				main.setFlag(MainActivity.MAIN_SETTING);
				main.setMainFragment(null);
			}
			break;
		case 2: //注销
			if (appContext.getLoginState() != AppContext.LOCAL_LOGINED
					&& appContext.getLoginState() != AppContext.LOGINED) {
				Toast.makeText(this.getActivity(), "您还没有登录", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this.getActivity(), "注销成功", Toast.LENGTH_SHORT)
						.show();
				new Thread() {
					@Override
					public void run() {
						try {
//							ApiClient.logout(appContext,
//									appContext.getUsername());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				main.logout();
				appContext.cleanLoginInfo();
				accountItem.tag = "账户(未登录)";
				adapter.notifyDataSetChanged();
			}
			break;
		case 3:	//退出
			AppManager.getAppManager().AppExit();
			break;
		case 4:	//导入数据
			Intent intent = new Intent(getActivity(), ImportDataActivity.class);
			startActivity(intent);
			break;
		}
	}

	public void changeLoginState() {
		initAccountItem();
		adapter.notifyDataSetChanged();
	}

	private void createFragment(Fragment f) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		fm.beginTransaction()
				.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
				.replace(R.id.fragment_replace_layout, f).commit();
	}

	private class SampleItem {
		public String tag;
		public int iconRes;

		public SampleItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}

		public SampleItem() {
			// TODO Auto-generated constructor stub
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.home_menu_row, null);
			}
			TextView title = (TextView) convertView
					.findViewById(R.id.menu_item_textview);
			title.setCompoundDrawablesWithIntrinsicBounds(
					getItem(position).iconRes, 0, 0, 0);
			title.setText(getItem(position).tag);
			return convertView;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		changeLoginState();
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}