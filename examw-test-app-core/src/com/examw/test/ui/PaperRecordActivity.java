package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.adapter.PaperRecordAdapter;
import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao;
import com.examw.test.dao.PaperRecordDao;
import com.examw.test.domain.PaperRecord;
import com.examw.test.exception.AppException;
import com.examw.test.model.PaperPreview;
import com.examw.test.support.ReturnBtnClickListener;
import com.examw.test.util.GsonUtil;

/**
 * 考试记录
 * 
 * @author fengwei.
 * @since 2014年12月11日 下午5:03:03.
 */
public class PaperRecordActivity extends BaseActivity {
	private LinearLayout contentLayout, nodataLayout, loadingLayout;
	private ListView paperListView;
	private String username;
	private ArrayList<PaperRecord> recordList;
	private PaperRecord currentRecord;
	private PaperRecordAdapter mAdapter;
	private Handler handler;
	private AppContext appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_paper_record);
		findViews();
		appContext = (AppContext) getApplication();
		// appContext.recoverLoginStatus();
		username = appContext.getUsername();
		handler = new MyHandler(this);
		this.paperListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				currentRecord = recordList.get(arg2);
				itemClickMethod();
			}
		});
//		this.paperListView
//				.setOnItemLongClickListener(new OnItemLongClickListener() {
//					@Override
//					public boolean onItemLongClick(AdapterView<?> arg0,
//							View arg1, int arg2, long arg3) {
//						showDeleteWindow(arg2);
//						return true;
//					}
//				});
	}

	private void findViews() {
		this.nodataLayout = (LinearLayout) this.findViewById(R.id.nodataLayout);
		this.contentLayout = (LinearLayout) this
				.findViewById(R.id.recordListLayout);
		this.loadingLayout = (LinearLayout) this
				.findViewById(R.id.loadingLayout);
		this.paperListView = (ListView) this.findViewById(R.id.contentListView);
		this.findViewById(R.id.btn_goback).setOnClickListener(
				new ReturnBtnClickListener(this));
		((TextView) (findViewById(R.id.title))).setText("学习记录");
	}

	@Override
	protected void onStart() {
		this.loadingLayout.setVisibility(View.GONE);
		super.onStart();
		initData();
	}

	private void initData() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				loadingLayout.setVisibility(View.GONE);
				switch (msg.what) {
				case 1:
					if (recordList == null || recordList.size() == 0) {
						contentLayout.setVisibility(View.GONE);
						nodataLayout.setVisibility(View.VISIBLE);
					} else {
						if(mAdapter == null){
							mAdapter = new PaperRecordAdapter(
									PaperRecordActivity.this, recordList);
							paperListView.setAdapter(mAdapter);
						}else{
							mAdapter.notifyDataSetChanged();
						}
					}
					break;
				case -1:
					Toast.makeText(PaperRecordActivity.this, "加载错误",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		loadingLayout.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				try {
					recordList = PaperRecordDao.findRecordsByUsername(username);
					handler.sendEmptyMessage(1);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(-1);
				}
			};
		}.start();
	}

//	private void showDeleteWindow(final int index) {
//		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
//		localBuilder.setTitle("删除").setMessage("是否删除此记录").setCancelable(false)
//				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int id) {
//						// TODO Auto-generated method stub
//						// 停止下载服务
//						// dao.deleteRecord(recordList.get(index));
//						recordList.remove(index);
//						if (recordList.size() == 0) {
//							contentLayout.setVisibility(View.GONE);
//							nodataLayout.setVisibility(View.VISIBLE);
//						} else
//							mAdapter.notifyDataSetChanged();
//					}
//				})
//				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int id) {
//						// TODO Auto-generated method stub
//						dialog.cancel();
//					}
//				});
//		localBuilder.create().show();
//	}

	private void itemClickMethod() {
		Intent mIntent = null;
		if (AppConstant.STATUS_NONE.equals(currentRecord.getStatus())) {
			mIntent = new Intent(this, PaperDoPaperActivity.class);
			mIntent.putExtra("action", "DoExam");
			mIntent.putExtra("paperId", currentRecord.getPaperId());
			mIntent.putExtra("recordId", currentRecord.getRecordId());
			this.startActivity(mIntent);
			return;
		}
		loadingLayout.setVisibility(View.VISIBLE);
		final String paperId = currentRecord.getPaperId();
		// 没有交卷的
		new Thread() {
			public void run() {
				try {
					String content = PaperDao
							.findPaperStructureContent(paperId);
					PaperPreview paper = GsonUtil.jsonToBean(content,
							PaperPreview.class);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = paper;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(-1);
				}
			};
		}.start();
	}

	private void startMyActivity(PaperPreview paper) {
		Intent mIntent = new Intent(this, AnswerCardActivity.class);
		mIntent.putExtra("paperId", currentRecord.getPaperId());
		mIntent.putExtra("trueOfFalse", currentRecord.getTorf());
		mIntent.putExtra("action", "showResult");
		mIntent.putExtra("paperScore", currentRecord.getScore().doubleValue());
		// findPaper
		mIntent.putExtra("paperTime", paper.getTime());
		mIntent.putExtra("ruleListJson",
				GsonUtil.objectToJson(paper.getStructures()));
		mIntent.putExtra("username", username);
		mIntent.putExtra("useTime",
				currentRecord.getUsedTime() % 60 == 0 ? currentRecord.getUsedTime() / 60
						: currentRecord.getUsedTime() / 60 + 1);
		mIntent.putExtra("userScore", currentRecord.getScore()); // 本次得分
		this.startActivity(mIntent); // 仍然是要启动这个Activity不带结果返回
	}

	static class MyHandler extends Handler {
		WeakReference<PaperRecordActivity> mActivity;

		MyHandler(PaperRecordActivity activity) {
			mActivity = new WeakReference<PaperRecordActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			PaperRecordActivity theActivity = mActivity.get();
			theActivity.loadingLayout.setVisibility(View.GONE);
			switch (msg.what) {
			case 1:
				theActivity.startMyActivity((PaperPreview) msg.obj);
				break;
			case -2:
				Toast.makeText(theActivity,
						((AppException) msg.obj).getMessage(),
						Toast.LENGTH_SHORT).show();
				break;
			case -1:
				Toast.makeText(theActivity, "找不到试卷信息", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	};

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}