package com.examw.test.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppConfig;
import com.examw.test.app.AppContext;
import com.examw.test.support.AppUpdateManager;
import com.examw.test.utils.BrightnessUtil;

public class SettingFragment extends Fragment implements OnClickListener {
	private TextView dateTxt, versionTxt/*, cacheSizeTxt*/,
//					usernameTxt,  loginTxt ,
					//newDataFlag,
					newVersionFlag;
	private CheckBox checkBox;
	private AppConfig appConfig;
	private AppContext appContext;
	private Context mContext;
	private Button logoutBtn;
	private PopupWindow pop;
	//private View parent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.setting_fragment, container, false);
		dateTxt = (TextView) v.findViewById(R.id.txt_date);
		versionTxt = (TextView) v.findViewById(R.id.txt_version);
//		usernameTxt = (TextView) v.findViewById(R.id.txt_username);
		//cacheSizeTxt = (TextView) v.findViewById(R.id.txt_cache_size);
		checkBox = (CheckBox) v.findViewById(R.id.checkWhenStart);
//		loginTxt = (TextView) v.findViewById(R.id.loginStr);
		logoutBtn = (Button) v.findViewById(R.id.btn_logout);
//		newDataFlag = (TextView) v.findViewById(R.id.newDataFlag);
		newVersionFlag = (TextView) v.findViewById(R.id.newVersionFlag);
		//parent = v.findViewById(R.id.setting_parent);
		//appConfig = AppConfig.getAppConfig(getActivity());
		appContext = (AppContext) getActivity().getApplication();
//		if (appContext.getLoginState() == AppContext.LOGINED
//				|| appContext.getLoginState() == AppContext.LOCAL_LOGINED) {
//			loginTxt.setText("注销登录");
			logoutBtn.setText("退出当前帐号");
//			usernameTxt.setText(appContext.getUsername());
	//	} else {
//			loginTxt.setText("登录/注册");
		//	logoutBtn.setText("登录/注册");
//			usernameTxt.setText("未登录");
	//	}
		mContext = getActivity();
		checkBox.setOnClickListener(this);

		v.findViewById(R.id.layout_about_app).setOnClickListener(this);
		v.findViewById(R.id.layout_share).setOnClickListener(this);
		v.findViewById(R.id.layout_edit).setOnClickListener(this);
		v.findViewById(R.id.layout_checkupdate).setOnClickListener(this);
//		v.findViewById(R.id.layout_checkupdata).setOnClickListener(this);
		v.findViewById(R.id.layout_clear_cache).setOnClickListener(this);
		v.findViewById(R.id.layout_checkupdate_when_start).setOnClickListener(
				this);
		v.findViewById(R.id.layout_feedback).setOnClickListener(this);
//		v.findViewById(R.id.layout_logout).setOnClickListener(this);
		v.findViewById(R.id.layout_deal).setOnClickListener(this);
		v.findViewById(R.id.layout_website).setOnClickListener(this);
		v.findViewById(R.id.layout_screenlight).setOnClickListener(this);
		v.findViewById(R.id.layout_sync).setOnClickListener(this);
		logoutBtn.setOnClickListener(this);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("settingFragment",requestCode+" " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onStart() {
		super.onStart();
		String dateStr = null;// appConfig.getFormatExamTime();
		this.dateTxt.setText(dateStr == null ? "设置" : dateStr);
		this.versionTxt.setText(appContext.getVersionName());
		//if(appContext.isHasNewData()) newDataFlag.setVisibility(View.VISIBLE);
		//if(appContext.isHasNewVersion()) newVersionFlag.setVisibility(View.VISIBLE);
//		this.usernameTxt.setText(appContext.getUsername());
//		this.cacheSizeTxt.setText(appContext.calculateCacheSize());
		//checkBox.setChecked(appContext.isCheckUp());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_edit:
			Intent intent = new Intent(mContext, SetTimeActivity.class);
			startActivity(intent);
			break;
		//分享
		case R.id.layout_share:
			share();
			break;
		//关于应用
		case R.id.layout_about_app:
			startActivity(new Intent(mContext, AboutActivity.class));
			break;
		//访问网站
		case R.id.layout_website:
			viewWebsite();
			break;
		//检测应用更新
		case R.id.layout_checkupdate:
			newVersionFlag.setVisibility(View.GONE);
			AppUpdateManager.getUpdateManager().checkAppUpdate(this.getActivity(),
					true);
			break;
		//检测数据更新
//		case R.id.layout_checkupdata:
//			newDataFlag.setVisibility(View.GONE);
//			break;
		//清理缓存
		case R.id.layout_clear_cache:
			// 清理缓存
			//appContext.clearAppCache();
			// 重新计算缓存
//			this.cacheSizeTxt.setText(appContext.calculateCacheSize());
			// 提示消息
			print("缓存清除成功");
			break;
		//隐私协议
		case R.id.layout_deal:
			startActivity(new Intent(mContext, PrivacyAgreementActivity.class));
			break;
		//意见反馈
		case R.id.layout_feedback:
			startActivity(new Intent(mContext, FeedBackActivity.class));
			break;
		//登录退出
		case R.id.btn_logout:
			loginOrLogout();
			break;
		//屏幕亮度
		case R.id.layout_screenlight:
			showPop();
			break;
		//一开始就检测更新
		case R.id.checkWhenStart:
			if (checkBox.isChecked()) {
				//appConfig.set(AppConfig.CONF_CHECKUP, String.valueOf(true));
			} else {
				//appConfig.set(AppConfig.CONF_CHECKUP, String.valueOf(false));
			}
			break;
		case R.id.layout_checkupdate_when_start:
			// 启动时是否检测更新
			checkWhenStart();
			break;
		//数据同步
		case R.id.layout_sync:
			Intent mIntent = new Intent(this.getActivity(), SyncActivity.class);
			mIntent.putExtra("loginFrom", "sysnc");
			startActivity(mIntent);
			break;
		}
	}

	private void loginOrLogout() {
//		if (appContext.getLoginState() == AppContext.LOGINED) {
//			showLogoutDialog();
//		} else {
//			// 转到登录界面
//			Intent intent = new Intent(getActivity(), LoginActivity.class);
//			intent.putExtra("loginFrom", LoginActivity.LOGIN_SETTING);
//			startActivityForResult(intent, 10);
//		}
	}

	// 分享
	private void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_SUBJECT, "应用分享");
		intent.putExtra(Intent.EXTRA_TEXT,
				"考试一点通，让考试一点就通 (分享自考试一点通Android客户端)");
		// Uri uri = Uri.fromFile(new
		// File("file:///android_asset/other/logo-formobile.png"));
		// intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, getActivity().getTitle()));
	}

	// 访问网站
	private void viewWebsite() {
		Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources()
				.getString(R.string.website)));
		startActivity(in);
	}


	// 是否启动时检测更新
	private void checkWhenStart() {
		if (checkBox.isChecked()) {
			checkBox.setChecked(false);
			//appConfig.set(AppConfig.CONF_CHECKUP, String.valueOf(false));
		} else {
			checkBox.setChecked(true);
			//appConfig.set(AppConfig.CONF_CHECKUP, String.valueOf(true));
		}

	}
	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void showPop() {
		if (pop == null) {
			View v = LayoutInflater.from(this.getActivity()).inflate(R.layout.brightness_dlg, null);
			final SeekBar seekBar = (SeekBar) v
					.findViewById(R.id.light_seekbar);
			seekBar.setMax(BrightnessUtil.MAX_BRIGHTNESS);
			seekBar.setProgress((BrightnessUtil
					.getScreenBrightness(getActivity())));
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					BrightnessUtil.setBrightness(getActivity(), progress);
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
				}
			});
			CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox1);
			checkBox.setChecked(BrightnessUtil.isAutoBrightness(mContext
					.getContentResolver()));
			if (checkBox.isChecked())
				seekBar.setEnabled(false);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						BrightnessUtil.startAutoBrightness(getActivity());
						seekBar.setEnabled(false);
					} else {
						BrightnessUtil.stopAutoBrightness(getActivity());
						seekBar.setEnabled(true);
					}
				}
			});
			Button sureBtn = (Button) v.findViewById(R.id.btn_sure);
			sureBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (seekBar.isEnabled()) {
						BrightnessUtil.saveBrightness(
								mContext.getContentResolver(),
								seekBar.getProgress());
						pop.dismiss();
					} else {
						pop.dismiss();
					}
				}
			});
			Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
			cancelBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pop.dismiss();
				}
			});
			pop = new PopupWindow(v, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			// 使其聚集
			pop.setFocusable(true);
			// 设置允许在外点击消失
			pop.setOutsideTouchable(true);

			// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
			pop.setBackgroundDrawable(new BitmapDrawable());
		}
		pop.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
	}

	public void setLoginTxt() {
		// TODO Auto-generated method stub
		//if (appContext.getLoginState() == AppContext.LOGINED) {
//			loginTxt.setText("注销登录");
//			usernameTxt.setText(appContext.getUsername());
		//}
	}

//	private void showLogoutDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(
//				this.getActivity());
//		builder.setIcon(android.R.drawable.ic_dialog_info);
//		builder.setTitle(R.string.app_setting_surelogout);
////		builder.setMessage(" 当前帐号：" + appContext.getUsername());
//		builder.setPositiveButton(R.string.sure,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						// 退出
//						// 退出登录,清除登录信息
//						logoutBtn.setText("登录/注册");
////						usernameTxt.setText("未登录");
//						// to do something...
//						new Thread() {
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								try {
//									//ApiClient.logout(appContext,appContext.getUsername());
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//							}
//						}.start();
////						appContext.cleanLoginInfo();
//						((MainActivity) getActivity()).changeMenu(); // 改变菜单文字
//					}
//				});
//		builder.setNegativeButton(R.string.cancle,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//		builder.show();
//	}

//	private OnItemClickListener itemClick = new OnItemClickListener() {
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// TODO Auto-generated method stub
//			switch (arg2) {
//			case 0:
////				sharePlatform(SHARE_MEDIA.WEIXIN);
//				break;
//			case 1:
////				sharePlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
//				break;
//			case 2:
////				sharePlatform(SHARE_MEDIA.SINA);
//				break;
//			case 3:
////				sharePlatform(SHARE_MEDIA.QQ);
//				break;
//			}
//		}
//	};

	private void print(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

//	private void sharePlatform(final SHARE_MEDIA media) {
//		if (media.equals(SHARE_MEDIA.WEIXIN)
//				|| media.equals(SHARE_MEDIA.WEIXIN_CIRCLE)) {
//			directShare(media);
//		} else {
//			if (!OauthHelper.isAuthenticated(mContext, media)) {
//				mController.doOauthVerify(mContext, media,
//						new UMAuthListener() {
//							@Override
//							public void onStart(SHARE_MEDIA platform) {
//								Toast.makeText(mContext, "授权开始",
//										Toast.LENGTH_SHORT).show();
//							}
//
//							@Override
//							public void onError(SocializeException e,
//									SHARE_MEDIA platform) {
//								Log.e("授权错误",e.getMessage());
//								Toast.makeText(mContext, "授权错误",
//										Toast.LENGTH_SHORT).show();
//							}
//
//							@Override
//							public void onComplete(Bundle value,
//									SHARE_MEDIA platform) {
//								Toast.makeText(mContext, "授权完成",
//										Toast.LENGTH_SHORT).show();
//								// 获取相关授权信息或者跳转到自定义的分享编辑页面
//								String uid = value.getString("uid");
//								directShare(media);
//							}
//
//							@Override
//							public void onCancel(SHARE_MEDIA platform) {
//								Toast.makeText(mContext, "授权取消",
//										Toast.LENGTH_SHORT).show();
//							}
//						});
//			}else
//			{
//				directShare(media);
//			}
//		}
//	}
//	private void directShare(SHARE_MEDIA media)
//	{
//		if(sharePop!=null&&sharePop.isShowing()){sharePop.dismiss();}
//		mController.postShare(mContext, media,
//	            new SnsPostListener() {
//
//	            @Override
//	            public void onStart() {
//	            	sharePop.dismiss();
//	                Toast.makeText(mContext, "分享开始",Toast.LENGTH_SHORT).show();
//	            }
//
//	            @Override
//	            public void onComplete(SHARE_MEDIA platform,int eCode, SocializeEntity entity) {
//	                if(eCode == StatusCode.ST_CODE_SUCCESSED){
//	                    Toast.makeText(mContext, "分享成功",Toast.LENGTH_SHORT).show();
//	                }else{
//	                    Toast.makeText(mContext, "分享失败",Toast.LENGTH_SHORT).show();
//	                }
//	            }
//	    });
//	}
	@Override
	public void onDestroy() {
		if(pop!=null)
		{
			pop.dismiss();
		}
		super.onDestroy();
	}
}
