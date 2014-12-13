package com.examw.test.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.adapter.AnswerCardStructureListAdatper;
import com.examw.test.adapter.AnswerScoreGridAdatper;
import com.examw.test.app.AppConstant;
import com.examw.test.model.StructureInfo;
import com.examw.test.support.DataConverter;
import com.examw.test.util.GsonUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 答题卡
 * @author fengwei.
 * @since 2014年12月6日 下午3:42:19.
 */
public class AnswerCardActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "AnswerCardActivity";
	private ImageButton scoreFlexImg;
	private LinearLayout scoreLayout,loadingLayout,nodataLayout,lookBtn,doAgainBtn;
	private GridView scoreGridView;
	private ListView questionListView;
	private List<StructureInfo> ruleList;
	private String ruleListJson;
	private String[] data ;
	private int[] trueOfFalse;
	private Intent intent;
	private String paperId;
	private String recordId;
	private int action;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_answer_card);
		//初始化界面
		this.findView();
		//初始化数据
		this.initData();
	}
	@Override
	protected void onStart() {
		Log.e(TAG, "onStart");
		//初始化界面
		this.initView();
		super.onStart();
	}
	private void findView()
	{
		((TextView)this.findViewById(R.id.title)).setText("答题情况");
		this.scoreFlexImg = (ImageButton) this.findViewById(R.id.scoreFlexImg);
		this.scoreLayout = (LinearLayout) this.findViewById(R.id.exam_scoreLayout);
		this.loadingLayout = (LinearLayout) this.findViewById(R.id.loadingLayout);
		this.nodataLayout = (LinearLayout) this.findViewById(R.id.nodataLayout);
		this.scoreGridView = (GridView) this.findViewById(R.id.scoreGridView);
		this.questionListView = (ListView) this.findViewById(R.id.question_directoryListView);
		this.lookBtn = (LinearLayout) this.findViewById(R.id.question_directory_lookBtn_Layout);
		this.doAgainBtn = (LinearLayout) this.findViewById(R.id.quesiton_directory_repeatBtn_layout);
		this.scoreFlexImg.setOnClickListener(this);
		this.findViewById(R.id.btn_goback).setOnClickListener(this);
		this.lookBtn.setOnClickListener(this);
		this.doAgainBtn.setOnClickListener(this);
	}
	private void initData()
	{
		intent = this.getIntent();
		this.action = intent.getIntExtra("action",AppConstant.ACTION_CHOOSE_ITEM);
		this.paperId = intent.getStringExtra("paperId");
		this.recordId = intent.getStringExtra("recordId");
		this.ruleListJson = intent.getStringExtra("ruleListJson");
		this.ruleList = GsonUtil.getGson().fromJson(ruleListJson, new TypeToken<ArrayList<StructureInfo>>(){}.getType());
		this.trueOfFalse = GsonUtil.getGson().fromJson(intent.getStringExtra("trueOfFalse"), int[].class);
	}
	private void initView()
	{
		switch(action)
		{
		case AppConstant.ACTION_CHOOSE_ITEM:
			((ImageView)this.findViewById(R.id.colorTipsIV)).setImageResource(R.drawable.answer_color_tips2);
			this.scoreLayout.setVisibility(View.GONE);
			this.loadingLayout.setVisibility(View.GONE);
			if(this.ruleList!=null&&this.ruleList.size()>0)
			{
				this.questionListView.setAdapter(new AnswerCardStructureListAdatper(this,this,ruleList,trueOfFalse,false));
			}else
			{
				this.nodataLayout.setVisibility(View.VISIBLE);
			}
			break;
		case  AppConstant.ACTION_SHOW_ANSWER:
		case AppConstant.ACTION_SUBMIT:
			this.scoreLayout.setVisibility(View.VISIBLE);
			this.data = new String[10];
			int hasDone = DataConverter.getHasDone(trueOfFalse);
			this.data[0] = "试题总分:"+intent.getDoubleExtra("paperScore",0)+"分";//总分
			this.data[1] = "试题限时:"+intent.getIntExtra("paperTime",0)+"分钟";//总时
			this.data[2] = "本次得分:"+intent.getDoubleExtra("userScore",0)+"分";//本次得分[红色]
			this.data[3] = "答题耗时:"+intent.getIntExtra("useTime",0)+"分钟";//耗时
			this.data[4] = "已做:"+hasDone+"题";//已做
			this.data[5] = "未做:"+(trueOfFalse.length-hasDone)+"题";//未做
			int right = DataConverter.getRightNum(trueOfFalse);
			this.data[6] = "做对:"+right+"题";	//做对
			this.data[7] = "做错:"+(hasDone - right)+"题";	//做错
			this.data[8] = "共计:"+trueOfFalse.length+"题";		//共计题
			if(hasDone ==0)
			{
				this.data[9] = "正确率:0.0%";
			}else
				this.data[9] = "正确率:"+(((int)(right*10000/hasDone)/100.0))+"%";//正确率
			this.scoreGridView.setAdapter(new AnswerScoreGridAdatper(this,data));
			this.questionListView.setAdapter(new AnswerCardStructureListAdatper(this,this,ruleList,trueOfFalse,true));
			this.loadingLayout.setVisibility(View.GONE);
			break;
		default:
			this.scoreLayout.setVisibility(View.GONE);
			this.questionListView.setAdapter(new AnswerCardStructureListAdatper(this,this,ruleList,trueOfFalse,true));
			this.loadingLayout.setVisibility(View.GONE);
			break;
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btn_goback:
			returnMethod();
			break;
		case R.id.scoreFlexImg:
			toggleShowView();
			break;
		case R.id.quesiton_directory_repeatBtn_layout:
			doItAgain();
			break;
		case R.id.question_directory_lookBtn_Layout:
			showAnswer(0);
			break;
		}
	}
	private void returnMethod()
	{
		if(action == AppConstant.ACTION_CHOOSE_ITEM)
		{
			this.setResult(50);
			this.finish();
		}else
		{
			this.finish();
		}
	}
	private void toggleShowView()
	{
		if(this.scoreGridView.getVisibility()==View.GONE)
		{
			this.scoreFlexImg.setImageResource(R.drawable.shrink);
			this.scoreGridView.setVisibility(View.VISIBLE);
			return;
		}
		if(this.scoreGridView.getVisibility()==View.VISIBLE)
		{
//			this.scoreFlexImg.setImageResource(R.drawable.unfold);
			this.scoreGridView.setVisibility(View.GONE);
			return;
		}
	}
	private void doItAgain()
	{
		if(action == AppConstant.ACTION_SUBMIT)
		{
			Intent data = new Intent();
			data.putExtra("action", AppConstant.ACTION_DO_EXAM);
			this.setResult(30, data);
			this.finish();
		}else
		{
			//启动DoExamQuestion
			Intent mIntent = new Intent(this,PaperDoPaperActivity.class);
			mIntent.putExtra("action", AppConstant.ACTION_DO_EXAM);
			mIntent.putExtra("paperId", paperId);
			this.startActivity(mIntent);
		}
	}
	//查看题目
	public void showAnswer(int cursor)
	{
		Intent data = null;
		switch(action)
		{
		case AppConstant.ACTION_SUBMIT:
			data = new Intent();
			data.putExtra("action", AppConstant.ACTION_SHOW_ANSWER);  
        	data.putExtra("cursor", 0);  
         	//设置请求代码 
        	this.setResult(20, data);
        	this.finish();
        	break;
		case AppConstant.ACTION_CHOOSE_ITEM:
			data=new Intent();  
         	data.putExtra("action", AppConstant.ACTION_DO_EXAM);  
         	data.putExtra("cursor", cursor);  
         	//设置请求代码  
         	this.setResult(20, data);  
         	//结束Activity
         	this.finish();
         	break;
		case AppConstant.ACTION_CHOOSE_ITEM_WITH_ANSWER:
			data=new Intent();  
         	data.putExtra("action", AppConstant.ACTION_SHOW_ANSWER);  
         	data.putExtra("cursor", cursor);  
         	//设置请求代码  
         	this.setResult(20, data);  
         	//结束Activity
         	this.finish();
         	break;
         default:
         	//启动DoExamQuestion,显示答案
    		Intent mIntent = new Intent(this,PaperDoPaperActivity.class);
    		mIntent.putExtra("action", AppConstant.ACTION_SHOW_ANSWER);
    		mIntent.putExtra("paperId", paperId);
    		mIntent.putExtra("recordId", recordId);
    		mIntent.putExtra("cursor", cursor);
    		this.startActivity(mIntent);
		}
	}
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
	    if ((paramKeyEvent.getKeyCode() == 4) && (paramKeyEvent.getRepeatCount() == 0))
	    {
	    	if(action == AppConstant.ACTION_CHOOSE_ITEM)
	    	{
	    		this.setResult(50);
				this.finish();
	    		return true;
	    	}
	    }
	    return super.onKeyDown(paramInt, paramKeyEvent);
	}
	
	@Override
	protected void onStop() {
		Log.e(TAG, "onStop");
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
	}
}