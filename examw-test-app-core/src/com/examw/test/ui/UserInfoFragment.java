package com.examw.test.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.examw.test.R;

public class UserInfoFragment extends Fragment implements android.view.View.OnClickListener {
	//private AppContext appContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.userinfo_fragment, container, false);
		//appContext = (AppContext) this.getActivity().getApplication();
		initViews(v);
		return v;
	}

	private void initViews(View v) {
		
	}

	@Override
	public void onClick(View v) {

	}
}
