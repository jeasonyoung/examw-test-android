package com.examw.test.ui;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.adapter.PaperListAdapter;
import com.examw.test.domain.Paper;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 
 * @author fengwei.
 * @since 2014年12月3日 上午11:07:46.
 */
public class RealPaperFragment extends Fragment{
	private static final String TAG = "RealPaperFragment";
	private PullToRefreshListView paperListView;
	private ArrayList<Paper> papers;
	private ProgressDialog dialog;
	private Handler handler;
	private LinearLayout nodata,loadingLayout,reloadLayout;
	private View lvPapers_footer;
	private ProgressBar lvPapers_foot_progress;
	private TextView lvPapers_foot_more;
	private int page,total;
	private PaperListAdapter mAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"RealPaperFragment 创建");
		View v = inflater.inflate(R.layout.paper_fragment, null);
		
		return v;
	}
}
