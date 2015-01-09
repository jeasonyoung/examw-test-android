package com.examw.test.adapter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppConstant;
import com.examw.test.model.StructureItemInfo;
import com.examw.test.ui.BaseActivity;
import com.examw.test.ui.PaperDoPaperActivity;
import com.examw.test.util.StringUtils;
import com.examw.test.widget.CheckBoxGroup;
import com.examw.test.widget.ImageTextView;
import com.examw.test.widget.OptionLayout;

/**
 * 试题适配器
 * @author fengwei.
 * @since 2014年12月8日 上午11:03:12.
 */
public class QuestionAdapter extends BaseAdapter {
	private static final String TAG = "QuestionAdatper";
	private Context context;
	private PaperDoPaperActivity activity2;
//	private QuestionDoExamActivity1 activity1;
	private BaseActivity activity;
	private ArrayList<StructureItemInfo> questionList;
	private ContentViewHolder contentHolder;
	// 图片保存目录
	private String imageSavePath;
	private SharedPreferences pref;

	private static TextViewLongClickListener tvLongClickListener;
	private static ShowAnswerListener showAnswerLinsener;

	public QuestionAdapter(Context context, BaseActivity activity,
			ArrayList<StructureItemInfo> questionList, String username,
			String paperid) {
		this.context = context;
		this.pref = context.getSharedPreferences("wdkaoshi", 0);
		// /mnt/sdcard/eschool/hahaha/image/1001
		this.imageSavePath = Environment.getExternalStorageDirectory()
				.getPath()+ File.separator+ "examw"+ File.separator+ username+ File.separator+ "image"+ File.separator+ paperid;
		this.activity = activity;
		if (activity instanceof PaperDoPaperActivity)
			this.activity2 = (PaperDoPaperActivity) activity;
//		else if (activity instanceof QuestionDoExamActivity1)
//			this.activity1 = (QuestionDoExamActivity1) activity;
		this.questionList = questionList;
		tvLongClickListener = new TextViewLongClickListener();
		showAnswerLinsener = new ShowAnswerListener(activity);
	}

	@Override
	public int getCount() {
		if (questionList != null)
			return questionList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (questionList != null)
			return questionList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		int action = AppConstant.ACTION_DO_EXAM;
		if (activity2 != null) {
			action = activity2.getAction();
		} else {
//			action = activity1.getAction();
		}
		Log.d(TAG, "action = "+action);
		float size = this.pref.getFloat("fontsize", 16.0f);
		StructureItemInfo currentQuestion = questionList.get(position); // 当前的题目
		if (v == null) {
			v = LayoutInflater.from(context).inflate(R.layout.single_question,null);
			contentHolder = new ContentViewHolder();
			v.setTag(R.id.tag_first, contentHolder);
			contentHolder.modeLayout = (LinearLayout) v
					.findViewById(R.id.doexam_mode2layout);
			contentHolder.examContent = (ImageTextView) v
					.findViewById(R.id.exam_Content2);// 题目内容
			contentHolder.examContent.setTextSize(size);
			contentHolder.examContent
					.setOnLongClickListener(tvLongClickListener);
			contentHolder.examOption = (CheckBoxGroup) v
					.findViewById(R.id.examOption2);// checkbox组的容器
			contentHolder.modeLayout4 = (LinearLayout) v
					.findViewById(R.id.doexam_mode3layout);
			contentHolder.examImages = (LinearLayout) v
					.findViewById(R.id.examImages2);
			contentHolder.answerEditText = (EditText) v
					.findViewById(R.id.exam_answerEditText);
			contentHolder.submitExamBtn = (Button) v
					.findViewById(R.id.submitExamBtn);
			contentHolder.showAnswerBtn = (Button) v
					.findViewById(R.id.showAnswerBtn);
			contentHolder.scrollView = (ScrollView) v
					.findViewById(R.id.ContentscrollView);
			contentHolder.checkBoxListener = new CheckBoxClickListener(
					activity, contentHolder.examOption);
			contentHolder.showAnswerBtn.setOnClickListener(showAnswerLinsener);
			contentHolder.showAnswerBtn.setVisibility(View.GONE);
			// contentHolder.examAnswerLayout.setVisibility(View.GONE);

		} else {
			contentHolder = (ContentViewHolder) v.getTag(R.id.tag_first);
			contentHolder.examOption.clearCheck();
		}
		AnswerViewHolder answerHolder = new AnswerViewHolder(v);
		//设置TAG
		v.setTag(R.id.tag_second, answerHolder);
		// holder.scrollView.fullScroll(33); //滑动到最开始?
		contentHolder.examImages.removeAllViews();
		String answer = currentQuestion.getUserAnswer(); // 学员的答案
		Integer type = currentQuestion.getType();
		contentHolder.modeLayout4.setVisibility(View.GONE);
		if (currentQuestion.isChoose()) { // 选择题
			contentHolder.modeLayout.setVisibility(0);
			TreeSet<StructureItemInfo> children= new TreeSet<StructureItemInfo>(currentQuestion.getChildren());
			// 显示图片
			contentHolder.examContent.setText(position + 1 + "、"+currentQuestion.getContent());
//			showPics(position, currentQuestion.getContent(), imageSavePath, currentQuestion.getId(),
//					contentHolder.examImages, contentHolder.examContent);
			// this.examOption1.clearCheck();
			if (contentHolder.examOption.getChildCount() > children.size() - 1) {
				for (int j = children.size() - 1; j < contentHolder.examOption
						.getChildCount(); j++) {
					contentHolder.examOption.removeViewAt(j);
				}
			}
			int i = 1;
			for (StructureItemInfo optionItem:children) {
				int viewCount = contentHolder.examOption.getChildCount();
				OptionLayout option;
				if (i > viewCount) {
					option = new OptionLayout(context, null);
					option.setId(i);
					option.setFontColor(context.getResources().getColor(
							R.color.black));
					option.setFontSize(size);
					contentHolder.examOption.addView(option, i - 1);
				}
				option = (OptionLayout) contentHolder.examOption
						.getChildAt(i - 1);
				option.resetColor();
				option.setFontSize(size);
				if(optionItem.getContent().matches("[A-Z]{1}[.][\\W\\w]*"))
					option.setText((char) (64 + i) + "．" + optionItem.getContent().substring(2));
				else
					option.setText((char) (64 + i) + "．" + optionItem.getContent());
				option.setValue(optionItem.getId());
				if (type.equals(AppConstant.ITEM_TYPE_SINGLE)) {
					option.setButtonDrawable(R.drawable.radio_button);
					option.setType(OptionLayout.RADIO_BUTTON);
				} else {
					option.setButtonDrawable(R.drawable.checkbox_button);
					option.setType(OptionLayout.CHECK_BOX);
				}
				if (answer != null && answer.indexOf(optionItem.getId()) != -1) {
					option.setChecked(true);
				}
				option.setOnClickListener(contentHolder.checkBoxListener);
				i++;
			}
		}
		else if (type.equals(AppConstant.ITEM_TYPE_JUDGE)) { // 判断题
			contentHolder.modeLayout.setVisibility(0);
			contentHolder.modeLayout4.setVisibility(8);
			contentHolder.showAnswerBtn.setVisibility(View.GONE);
			// 显示图片
			contentHolder.examContent.setText(position + 1 + "、"+currentQuestion.getContent());
//			showPics(position, currentQuestion.getContent(), imageSavePath, currentQuestion.getId(),
//					contentHolder.examImages, contentHolder.examContent);
			//选项
			OptionLayout rb_t, rb_f;
			if (contentHolder.examOption.getChildCount() == 0) {
				rb_t = new OptionLayout(context, null);
				rb_t.setId(1);
				rb_t.resetColor();
				rb_f = new OptionLayout(context, null);
				rb_f.setId(2);
				rb_f.resetColor();
				rb_t.setText(" √");
				rb_t.setValue("A");
				rb_t.setFontColor(context.getResources()
						.getColor(R.color.black));
				rb_t.setFontSize(size);
				rb_t.setType(OptionLayout.RADIO_BUTTON);
				rb_t.setButtonDrawable(R.drawable.radio_button);
				rb_f.setText(" ×");
				rb_f.setValue("B");
				rb_f.setFontColor(context.getResources()
						.getColor(R.color.black));
				rb_f.setFontSize(size);
				rb_f.setButtonDrawable(R.drawable.radio_button);
				rb_f.setType(OptionLayout.RADIO_BUTTON);
				contentHolder.examOption.addView(rb_t, 0);
				contentHolder.examOption.addView(rb_f, 1);
			}
			// this.examOption1.clearCheck();
			rb_t = (OptionLayout) contentHolder.examOption.getChildAt(0);
			rb_f = (OptionLayout) contentHolder.examOption.getChildAt(1);
			rb_t.setOnClickListener(contentHolder.checkBoxListener);
			rb_f.setOnClickListener(contentHolder.checkBoxListener);
			if (contentHolder.examOption.getChildCount() >= 2) {
				contentHolder.examOption.removeAllViews();
				rb_t.setId(1);
				rb_t.resetColor();
				rb_f.setId(2);
				rb_f.resetColor();
				rb_t.setText(" √");
				rb_t.setFontColor(context.getResources()
						.getColor(R.color.black));
				rb_t.setFontSize(size);
				rb_t.setValue("A");
				rb_t.setButtonDrawable(R.drawable.radio_button);
				rb_t.setType(OptionLayout.RADIO_BUTTON);
				rb_f.setText(" ×");
				rb_f.setFontColor(context.getResources()
						.getColor(R.color.black));
				rb_f.setFontSize(size);
				rb_f.setButtonDrawable(R.drawable.radio_button);
				rb_f.setType(OptionLayout.RADIO_BUTTON);
				rb_f.setValue("B");
				contentHolder.examOption.addView(rb_t, 0);
				contentHolder.examOption.addView(rb_f, 1);
			}
			if (answer != null) {
				if (answer.indexOf("0") != -1) {
					rb_f.setChecked(true);
					rb_t.setChecked(false);
				} else if (answer.indexOf("1") != -1) {
					rb_t.setChecked(true);
					rb_f.setChecked(false);
				} else {
					rb_t.setChecked(false);
					rb_f.setChecked(false);
				}
			}
		} else if (type.equals(AppConstant.ITEM_TYPE_QANDA)) {
			contentHolder.modeLayout.setVisibility(8);
			contentHolder.modeLayout4.setVisibility(8);
			
			//TODO 显示问答题的题干
			
			if (answer != null) {
				contentHolder.answerEditText.setText(answer);
			}
			contentHolder.submitExamBtn.setVisibility(0);
			contentHolder.submitExamBtn
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String txtAnswer = contentHolder.answerEditText
									.getText().toString();
							activity.saveTextAnswer(txtAnswer);
						}
					});
		}
		if (action != AppConstant.ACTION_DO_EXAM) {
			if (action == AppConstant.ACTION_DO_PRACTICE && currentQuestion.getUserAnswer() == null) {
				answerHolder.examAnswerLayout.setVisibility(View.GONE);
				//多选题显示按钮
				if(type.equals(AppConstant.ITEM_TYPE_MULTI) || type.equals(AppConstant.ITEM_TYPE_UNCERTAIN))
					contentHolder.showAnswerBtn.setVisibility(View.VISIBLE);
				else
					contentHolder.showAnswerBtn.setVisibility(View.GONE);
//				contentHolder.examOption.forbidden(true);
			} else if ((action == AppConstant.ACTION_DO_PRACTICE || action == AppConstant.ACTION_SHOW_ANSWER)
							&& currentQuestion.getUserAnswer() != null) {
				answerHolder.examAnswerLayout.setVisibility(View.VISIBLE);
				contentHolder.showAnswerBtn.setVisibility(View.GONE);
				contentHolder.examOption.setFontColor(context.getResources()
						.getColor(R.color.green), currentQuestion.getAnswer(),context.getResources()
						.getColor(R.color.red), currentQuestion.getUserAnswer(),type);
			} else {
				answerHolder.examAnswerLayout.setVisibility(View.VISIBLE);
				contentHolder.showAnswerBtn.setVisibility(View.GONE);
				// 禁用选择
			}
			showAnswer(answerHolder, currentQuestion, answer);
		}
		//设置字体
		setFontSize(answerHolder, size, v);
		return v;
	}

	private String answerToTF(String answer) {
		return "1".equals(answer) ? " √" : "0".equals(answer) ? " ×" : answer;
	}

	public static class ContentViewHolder {
		ScrollView scrollView;
		LinearLayout modeLayout, modeLayout4, examImages;
		EditText answerEditText;
		ImageTextView examContent;
		public CheckBoxGroup examOption;
		Button submitExamBtn;
		Button showAnswerBtn;
		// ImageView answerResultImg;
		Button noteBtn;
		OnClickListener checkBoxListener;
		OnClickListener showAnswerListener;
		// public LinearLayout examAnswerLayout;
	}

	public static class AnswerViewHolder {
		public ImageView answerResultImg;
		com.examw.test.widget.ImageTextView sysAnswerTextView, analysisTextView;
		public LinearLayout examAnswerLayout;
		public TextView myAnswerTextView,answerStr1,answerStr2,analysisStr;
		public AnswerViewHolder(View v) {
			// 答案与解析
			this.examAnswerLayout = (LinearLayout) v
					.findViewById(R.id.exam_answer_layout); // 整个答案的布局
			this.myAnswerTextView = (TextView) v
					.findViewById(R.id.myAnswerTextView); // 我的答案
			this.sysAnswerTextView = (com.examw.test.widget.ImageTextView) v
					.findViewById(R.id.sysAnswerTextView); // 正确答案
			this.answerResultImg = (ImageView) v
					.findViewById(R.id.answerResultImg); // 判断图片
			this.analysisTextView = (com.examw.test.widget.ImageTextView) v
					.findViewById(R.id.exam_analysisTextView); // 解析
			this.analysisTextView.setOnLongClickListener(tvLongClickListener);
			this.answerStr1 = ((TextView)v.findViewById(R.id.myAnswerStr));
			this.answerStr2 = ((TextView)v.findViewById(R.id.sysAnswerStr));
			this.analysisStr = ((TextView)v.findViewById(R.id.examAnalysisStr));
			this.examAnswerLayout.setVisibility(View.GONE); // 隐藏答案
		}
	}
	
	private void setFontSize(AnswerViewHolder answerHolder,float size,View v)
	{
		Log.e(TAG,"getView中设置字体大小:"+size);
		contentHolder.examContent.setTextSize(size);
		contentHolder.examOption.setFontSize(size);
		answerHolder.myAnswerTextView.setTextSize(size);
		answerHolder.sysAnswerTextView.setTextSize(size);
		answerHolder.analysisTextView.setTextSize(size);
		answerHolder.analysisStr.setTextSize(size);
		answerHolder.answerStr1.setTextSize(size);
		answerHolder.answerStr2.setTextSize(size);
	}
	// 显示答案
	public void showAnswer(AnswerViewHolder holder,StructureItemInfo currentQuestion, String userAnswer) {
		String trueAnswer = currentQuestion.getAnswer();
		Integer type = currentQuestion.getType();
		if(currentQuestion.isChoose())
		{
			holder.myAnswerTextView.setText(this.calculateUserAnswer(currentQuestion, userAnswer));
			holder.sysAnswerTextView.setText(this.calculateRightAnswer(currentQuestion));
		}else
		{
			holder.myAnswerTextView.setText(answerToTF(userAnswer));
			holder.sysAnswerTextView.setText(answerToTF(trueAnswer));
		}
		holder.analysisTextView.setText(currentQuestion.getAnalysis());
		if (type.equals(AppConstant.ITEM_TYPE_QANDA)) {
			holder.answerResultImg.setVisibility(View.GONE);
		} else {
			holder.answerResultImg.setVisibility(View.VISIBLE);
			if (trueAnswer.equals(userAnswer)) {
				holder.answerResultImg
						.setImageResource(R.drawable.answer_correct_pto);
			} else if (userAnswer != null && !"".equals(userAnswer)
					&& isContain(trueAnswer, userAnswer)) {
				holder.answerResultImg
						.setImageResource(R.drawable.answer_halfcorrect_pto);
			} else {
				holder.answerResultImg
						.setImageResource(R.drawable.answer_wrong_pto);
			}
		}
	}
	//计算用户答案的选项
	private String calculateUserAnswer(StructureItemInfo currentQuestion, String userAnswer){
		if(StringUtils.isEmpty(userAnswer)) return "";
		String option = "";
		TreeSet<StructureItemInfo> children = new TreeSet<StructureItemInfo>(currentQuestion.getChildren());
		int i = 65;
		for(StructureItemInfo child:children)
		{
			if(userAnswer.contains(child.getId()))
			{
				option += ((char)(i++)) + " "; 
			}else{
				i++;
			}
		}
		return option;
	}
	//计算正确答案的选项
	private String calculateRightAnswer(StructureItemInfo currentQuestion)
	{
		String answer = currentQuestion.getAnswer();
		if(StringUtils.isEmpty(answer)) return "";
		String option = "";
		TreeSet<StructureItemInfo> children = new TreeSet<StructureItemInfo>(currentQuestion.getChildren());
		int i = 65;
		for(StructureItemInfo child:children)
		{
			if(answer.contains(child.getId()))
			{
				option += ((char)(i++)) + " "; 
			}else{
				i++;
			}
		}
		return option;
	}

	// 改变字体大小
	public void setFontSize(ContentViewHolder contentHolder,
			AnswerViewHolder answerHolder, float size) {
		Log.e(TAG,"答题设置中改变字体大小:"+size);
		// 标题
		contentHolder.examContent.setTextSize(size);
		contentHolder.examOption.setFontSize(size);
		// 答案解析等
		answerHolder.analysisStr.setTextSize(size);
		answerHolder.answerStr1.setTextSize(size);
		answerHolder.answerStr2.setTextSize(size);
		answerHolder.myAnswerTextView.setTextSize(size);// 我的答案
		answerHolder.sysAnswerTextView.setTextSize(size); // 正确答案
		answerHolder.analysisTextView.setTextSize(size); // 解析
	}

	// 判断答案是否包含
	private boolean isContain(String trueAnswer, String answer) {
		if (answer.length() == 1) {
			return trueAnswer.contains(answer);
		}
		String[] arr = answer.split(",");
		boolean flag = true;
		for (String s : arr) {
			flag = flag && trueAnswer.contains(s);
		}
		return flag;
	}

	// 显示题目中的图片
	private void showPics(int questionCursor, String title,
			String imageSavePath, String zuheName, LinearLayout examImages,
			TextView examContent) {
		if (title.contains("<IMG ")) // 包含图片
		{
			// String s = currentRule.getRuleId()+"-"+currentQuestion.getQid();
			// 先去sd中找,找到就显示,找不到先下载再显示
			File dir = new File(imageSavePath);
			if (dir.exists()) {
				// dir.mkdirs();
				File[] files = dir.listFiles();
				int count = files.length;
				for (File f : files) {
					if (f.getName().contains(zuheName)) {
						ImageView img = new ImageView(context);
						img.setImageURI(Uri.parse(f.getPath()));
						examImages.addView(img);
						count--;
					}
				}
				if (count == files.length) // 没有图片,没考虑多图片没有加载完全的情况
				{
					String[] imageUrls = parseAddress(title);
					for (int i = 0; i < imageUrls.length; i++) {
						String url = imageUrls[i];
						if ("".equals(url) || url == null) {
							continue;
						}
						new GetImageTask(zuheName + "-" + i, examImages)
								.execute(url);
					}
				}
			} else {
				dir.mkdirs();
				String[] imageUrls = parseAddress(title);
				for (int i = 0; i < imageUrls.length; i++) {
					String url = imageUrls[i];
					if ("".equals(url)) {
						continue;
					}
					new GetImageTask(zuheName + "-" + i, examImages)
							.execute(url);
				}
			}
			examContent.setText(questionCursor + 1 + "、"
					+ title.replaceAll("<IMG[\\S\\s]+>", ""));
		} else
			examContent.setText(questionCursor + 1 + "、" + title);
	}

	// 解析图片下载地址
	private String[] parseAddress(String address) {
		String[] addr = null;
		// if(address.contains("<IMG "))
		// {
		String[] arr = address.split("<IMG ");
		addr = new String[arr.length];
		for (int i = 1; i < arr.length; i++) {
			String s = arr[i];
			if (!"".equals(s)) {
				String right = s.substring(s.indexOf("src=\"") + 5);
				addr[i] = right.substring(0, right.indexOf("\""));
			} else {
				addr[i] = null;
			}
		}
		// }
		return addr;
	}

	// 异步下载图片
	private class GetImageTask extends AsyncTask<String, Void, Bitmap> {
		private String fileName;
		private LinearLayout examImages1;

		public GetImageTask(String fileName, LinearLayout examImages1) {
			// TODO Auto-generated constructor stub
			this.fileName = fileName;
			this.examImages1 = examImages1;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			URL url;
			try {
				fileName = fileName
						+ params[0].substring(params[0].lastIndexOf("."));
				url = new URL(params[0]); // 设置URL
				HttpURLConnection con;
				con = (HttpURLConnection) url.openConnection(); // 打开连接
				con.setRequestMethod("GET"); // 设置请求方法
				// 设置连接超时时间为5s
				con.setConnectTimeout(5000);
				InputStream in = con.getInputStream(); // 取得字节输入流
				int len = 0;
				byte buf[] = new byte[1024];
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				while ((len = in.read(buf)) != -1) {
					out.write(buf, 0, len); // 把数据写入内存
				}
				byte[] data = out.toByteArray();
				out.close(); // 关闭内存输出流
				// 二进制数据生成位图
				Bitmap bit = BitmapFactory
						.decodeByteArray(data, 0, data.length);
				return bit;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			if (result == null) {
				return;
			}
			ImageView img = new ImageView(context);
			img.setScaleType(ImageView.ScaleType.FIT_START);
			examImages1.addView(img);
			try {
				img.setImageURI(Uri.parse(saveFile(result, fileName)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}

		public String saveFile(Bitmap bm, String fileName) throws IOException {
			String filePath = imageSavePath + File.separator + fileName;
			File myCaptureFile = new File(filePath);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bm.recycle();
			bos.flush();
			bos.close();
			return filePath;
		}
	}

	// //显示或者隐藏答案
	// public void showAnswer() {
	// System.out.println("显示或者隐藏答案");
	// if (holder.examAnswerLayout.getVisibility() == View.GONE) {
	// holder.examAnswerLayout.setVisibility(View.VISIBLE);
	// } else {
	// holder.examAnswerLayout.setVisibility(View.GONE);
	// }
	// }
	// 清除选择项
	public void clearCheck() {
		contentHolder.examOption.clearCheck();
	}


	public void setRadioEnable(RadioGroup group, boolean flag) {
		System.out.println("设置禁用radiobutton");
		int viewCount = group.getChildCount();
		for (int i = 0; i < viewCount; i++) {
			group.getChildAt(i).setEnabled(flag);
		}
	}

	private class CheckBoxClickListener implements OnClickListener {
		private BaseActivity activity;
		private CheckBoxGroup group;

		public CheckBoxClickListener(BaseActivity activity, CheckBoxGroup group) {
			this.activity = activity;
			this.group = group;
		}

		@Override
		public void onClick(View v) {
			OptionLayout option = (OptionLayout) v;
			if (option.isChecked()
					&& option.getType() == OptionLayout.CHECK_BOX)
				option.setChecked(false);
			else
				option.setChecked(true);
			if (option.getType() == OptionLayout.RADIO_BUTTON) {
				group.setOnlyOneCheck(option);
			}
			activity.saveChoiceAnswer(group.getValue());
		}
	}
	//多选题显示答案监听
	private class ShowAnswerListener implements OnClickListener {
		private BaseActivity activity;

		public ShowAnswerListener(BaseActivity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			if("显 示 答 案".equals(((Button)v).getText()))
				((Button)v).setText("隐 藏 答 案");
			else
				((Button)v).setText("显 示 答 案");
			activity.submitOrSeeAnswer();
		}
	}
	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "ServiceCast" })
	public void setClipBoard(String content) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("label", content);
			clipboard.setPrimaryClip(clip);
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(content);
		}
	}

	private class TextViewLongClickListener implements View.OnLongClickListener {
		@Override
		public boolean onLongClick(final View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setCancelable(true);
			builder.setItems(new CharSequence[] { "复制内容" },
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							setClipBoard(((TextView) v).getText().toString());
							Toast.makeText(context, "已复制到剪贴板",
									Toast.LENGTH_SHORT).show();
						}
					});
			builder.create().show();
			return true;
		}
	}
}