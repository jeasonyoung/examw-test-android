package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.adapter.SyllabusListAdapter;
import com.examw.test.adapter.SyllabusListAdapter.OnSyllabusNodeClickListener;
import com.examw.test.domain.Chapter;
import com.examw.test.domain.Subject;
import com.examw.test.utils.ToastUtils;

/**
 * 每日知识大纲
 * @author fengwei.
 * @since 2014年11月26日 下午3:20:16.
 */
@SuppressLint("InflateParams")
public class KnowledgeActivity extends BaseActivity implements OnClickListener {
	//private static final String TAG = "KnowledgeActivity";
	private MyHandler mHandler;
	private PopupWindow coursePop;
	private ImageView menuIn;
	private ArrayList<Subject> subjects;
	private Subject currentCourse;
	private LinearLayout loading, nodataLayout, reloadLayout;
	private SampleAdapter menuAdapter;
	private ListView chapterListView;
	private ArrayList<Chapter> chapters;
	private SyllabusListAdapter syllabusListAdapter;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty);
		//setContentView(R.layout.ui_knowledge);
		//this.initView();
		//this.initData();
	}

//	private void initView() {
//		chapterListView = (ListView) findViewById(R.id.id_tree);
//		menuIn = (ImageView) this.findViewById(R.id.iv_item_bg_in);
//		menuIn.setTag(0);
//		loading = (LinearLayout) this.findViewById(R.id.loadingLayout);
//		nodataLayout = (LinearLayout) this.findViewById(R.id.nodataLayout);
//		reloadLayout = (LinearLayout) this.findViewById(R.id.reload);
//		this.findViewById(R.id.btn_left).setOnClickListener(this);
//		this.findViewById(R.id.menuTitleLayout).setOnClickListener(this);
//		// 重新加载
//		this.findViewById(R.id.btn_reload).setOnClickListener(this);
//	}

//	private void initData() {
//		mHandler = new MyHandler(this);
//		new Thread() {
//			public void run() {
//				try {
//					subjects = ExamDao.findSubjects(null);
//					if (subjects != null && subjects.size() > 0) {
//						currentCourse = subjects.get(0);
//						try {
////							chapters = SyllabusDao
////									.loadAllChapters(currentCourse
////											.getSubjectId());
////							if (chapters == null || chapters.isEmpty()) {
////								String content = ApiClient.loadSyllabusContent(
////										(AppContext) getApplication(),
////										currentCourse.getSubjectId());
////								if (!StringUtils.isEmpty(content)) {
////									chapters = SyllabusDao
////											.insertSyllabusAndLoadChapters(
////													currentCourse, content);
////								}
////							}
//							mHandler.sendEmptyMessage(11);
//						} catch (Exception e) {
//							e.printStackTrace();
//							mHandler.sendEmptyMessage(-11);
//						}
//					} else
//						mHandler.sendEmptyMessage(0);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			};
//		}.start();
//	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			this.finish();
			break;
		case R.id.menuTitleLayout:
			if (subjects == null) {
				return;
			}
			showPop(v);
			break;
		case R.id.btn_reload:
			if (subjects == null || subjects.size() == 0) {
				new GetSubjectTask().execute();
				return;
			}
			if (chapters == null || chapters.isEmpty()) {
				new GetSyllabusThread().start();
			}
			break;
		}
	}

	private void showPop(View v) {
		if (coursePop == null) {
			View view = LayoutInflater.from(this).inflate(R.layout.pop_choose_subject_menu, null);
			ListView listView = (ListView) view
					.findViewById(R.id.choosecourse_listView);
			menuAdapter = new SampleAdapter();
			listView.setAdapter(menuAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						final int arg2, long arg3) {
					coursePop.dismiss();
					final String subjectId = subjects.get(arg2).getSubjectId();
					if (currentCourse.getSubjectId().equals(subjectId)) {
						menuIn.setTag(0);
						return;
					}
					currentCourse = subjects.get(arg2);
					menuIn.setTag(1);
					// TODO 获取其他科目的大纲
					loading.setVisibility(View.VISIBLE);
					new GetSyllabusThread().start();
				}
			});
			coursePop = new PopupWindow(view, -2, -2);
			// 设置点外面消失
			coursePop.setBackgroundDrawable(new ColorDrawable(00000000));
			coursePop.setFocusable(true);
			coursePop.setOutsideTouchable(true);
			coursePop.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					menuIn.setVisibility(View.VISIBLE);
				}
			});
		}
		if (menuIn.getTag().equals(1)) {
			menuAdapter.notifyDataSetChanged(); // 改变选中项
		}
		coursePop.showAsDropDown(v, -9, -2);
		menuIn.setVisibility(View.GONE);
	}

	public class SampleAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return subjects.size();
		}

		@Override
		public Object getItem(int position) {
			return subjects.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(KnowledgeActivity.this).inflate(
						R.layout.pop_choose_subject_menu_item, null);
			}
			Subject c = subjects.get(position);
			TextView title = (TextView) convertView.findViewById(R.id.item_txt);
			title.setText(c.getName());
			if (c.equals(currentCourse)) {
				title.setTextColor(getResources().getColor(R.color.blue));
			} else {
				title.setTextColor(getResources().getColor(R.color.black));
			}
			return convertView;
		}
	}

	private class GetSyllabusThread extends Thread {
		@Override
		public void run() {
			try {
//				String subjectId = currentCourse.getSubjectId();
//				chapters = SyllabusDao.loadAllChapters(subjectId);
//				if (chapters == null || chapters.isEmpty()) {
//					String content = ApiClient.loadSyllabusContent(
//							(AppContext) getApplication(), subjectId);
//					if (!StringUtils.isEmpty(content)) {
//						chapters = SyllabusDao.insertSyllabusAndLoadChapters(
//								currentCourse, content);
//					}
//				}
				mHandler.sendEmptyMessage(11);
			} catch (Exception e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(-11);
			}
		}
	}

	static class MyHandler extends Handler {
		WeakReference<KnowledgeActivity> weak;

		public MyHandler(KnowledgeActivity r) {
			weak = new WeakReference<KnowledgeActivity>(r);
		}

		@Override
		public void handleMessage(Message msg) {
			final KnowledgeActivity k = weak.get();
			k.loading.setVisibility(View.GONE);
			switch (msg.what) {
			case 0:
				k.new GetSubjectTask().execute();
				break;
			case 1:
				if (k.subjects == null || k.subjects.size() > 0) {
					k.nodataLayout.setVisibility(View.VISIBLE);
					k.reloadLayout.setVisibility(View.GONE);
				}
				break;
			case -1:
				ToastUtils.show(k, msg.obj.toString());
				k.reloadLayout.setVisibility(View.VISIBLE);
				break;
			case 11:
				if (k.chapters == null || k.chapters.isEmpty()) {
					k.nodataLayout.setVisibility(View.VISIBLE);
					k.reloadLayout.setVisibility(View.GONE);
				} else {
					if (k.syllabusListAdapter == null) {
						k.syllabusListAdapter = new SyllabusListAdapter(
								k.chapterListView, k, k.chapters);
						k.syllabusListAdapter
								.setOnSyllabusNodeClickListener(new OnSyllabusNodeClickListener() {
									@Override
									public void onClick(Chapter node,
											int position) {
										if (node.isLeaf()) {
											Intent intent = new Intent(k,KnowledgeDetailActivity.class);
											intent.putExtra("chapterId",node.getChapterId());
											intent.putExtra("chapterPid",node.getPid());
											k.startActivity(intent);
										}
									}
								});
						k.chapterListView.setAdapter(k.syllabusListAdapter);
					} else {
						k.syllabusListAdapter.notifyDataSetChanged();
					}
					k.nodataLayout.setVisibility(View.GONE);
					k.reloadLayout.setVisibility(View.GONE);
				}
				break;
			case -11:
				k.reloadLayout.setVisibility(View.VISIBLE);
				break;
			}
		}
	}

	/**
	 * 获取科目
	 * 
	 * @author fengwei
	 * 
	 */
	private class GetSubjectTask extends
			AsyncTask<String, Void, ArrayList<Subject>> {
		@Override
		protected void onPreExecute() {
			loading.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected ArrayList<Subject> doInBackground(String... params) {
			try {
//				ArrayList<Subject> result = ApiClient
//						.getSubjectList((AppContext) (KnowledgeActivity.this
//								.getApplication()));
//				ProductDao.saveSubjects(result);
//				return result;
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(ArrayList<Subject> result) {
			loading.setVisibility(View.GONE);
			if (result != null) {
				subjects = result;
				reloadLayout.setVisibility(View.GONE);
			} else {
				reloadLayout.setVisibility(View.VISIBLE);
			}
		};
	}
}
