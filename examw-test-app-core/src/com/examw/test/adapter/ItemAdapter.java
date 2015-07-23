package com.examw.test.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.model.PaperItemModel;
import com.examw.test.model.PaperItemModel.ItemType;
import com.examw.test.ui.PaperActivity;
import com.examw.test.ui.PaperActivity.PaperDataDelegate;

/**
 * 试卷数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月22日
 */
public class ItemAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
	private static final String TAG = "ItemAdapter";
	private final List<PaperItemTitleModel> items;
	private final LayoutInflater inflater;
	private final ListView listView;
	private final int order;
	/**
	 * 构造函数。
	 * @param inflater
	 * @param item
	 * @param position
	 * @param listView
	 */
	public ItemAdapter( final LayoutInflater inflater, final PaperItemModel item, int position, final boolean displayAnswer,  final ListView listView){
		Log.d(TAG, "初始化试题数据适配器..." + position);
		//初始化
		this.inflater = inflater;
		//初始化数据源
		this.items = new ArrayList<PaperItemTitleModel>();
		//设置题序。
		this.order = position;
		//设置列表对象
		this.listView = listView;
		//设置选项选中监听事件。
		if(this.listView != null){
			this.listView.setOnItemClickListener(this);
		}
		//加载数据
		new PrepareLoadAsyncDataTask(displayAnswer, AppContext.getPaperDataDelegate()).execute(item);
	}
	/*
	 * 获取行数。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return this.items.size();
	}
	/*
	 * 获取行数据。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return this.items.get(position);
	}
	/*
	 * 获取行ID。
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	/*
	 *  创建行。
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "加载试题内容行..." + position);
		TextView contentView = null;
		if(convertView == null){
			convertView = this.inflater.inflate(R.layout.ui_main_paper_items_itemcontent, parent, false);
			contentView = (TextView)convertView.findViewById(R.id.paper_itemcontent);
			convertView.setTag(contentView);
		}else {
			contentView = (TextView)convertView.getTag();
		}
		//加载数据
		PaperItemTitleModel model = this.items.get(position);
		if(model != null){
			if(model.order > 0){
				model.content = model.order + "." + model.content;
			}
			contentView.setText(Html.fromHtml(model.content));
		}
		return convertView;
	}
	/*
	 * 选项选中处理。
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "选中选项..." + position);
		// TODO Auto-generated method stub
		
	}
	/**
	 * 异步加载试题数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月23日
	 */
	private class PrepareLoadAsyncDataTask extends AsyncTask<PaperItemModel, Void, Boolean>{
		private final boolean displayAnswer;
		private final PaperActivity.PaperDataDelegate  dataDelegate; 
		/**
		 * 构造函数。
		 * @param displayAnswer
		 */
		public PrepareLoadAsyncDataTask(boolean displayAnswer, PaperActivity.PaperDataDelegate  dataDelegate){
			Log.d(TAG, "初始化异步数据加载...");
			this.displayAnswer = displayAnswer;
			this.dataDelegate = dataDelegate;
		}
		/*
		 * 后台线程加载数据处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(PaperItemModel... params) {
			try {
				if(params[0] == null || params[0].getType() == null)return null;
				Log.d(TAG, "异步试题数据转换...=>" + order);
				//加载我的答案
				String myAnswers =  null;
				if(this.displayAnswer && this.dataDelegate != null){
					myAnswers = this.dataDelegate.loadMyAnswer(params[0]);
				}
				//结果数据集合
				List<PaperItemTitleModel> result = null;
				//选项集合
				ItemType type = ItemType.values()[params[0].getType() - 1];
				switch(type){
					case Single://单选
					case Multy://多选
					case Uncertain://不定向选
					{
						result = this.createChoiceItem(type, params[0], myAnswers);
						break;
					}
					case Judge:{//判断题
						result = this.createJudgeItem(type, params[0], myAnswers);
						break;
					}
					case Qanda:{//问答题
						result = this.createQandaItem(type, params[0]);
						break;
					}
					case ShareTitle:{//共享题干题
						result = this.createShareTitle(type, params[0], myAnswers);
						break;
					}
					case ShareAnswer:{//共享答案题
						result = this.createShareAnswerItem(type, params[0], myAnswers);
						break;
					}
					default:break;
				}
				//返回结果数据集合
				if(result != null){
					//清空数据源
					items.clear();
					//装载数据。
					items.addAll(result);
					return true;
				}
			} catch (Exception e) {
				Log.e(TAG, "异步试题[" + order+ "]数据转换异常:" + e.getMessage(), e);
			}
			return false;
		}
		//创建选择题。
		private List<PaperItemTitleModel> createChoiceItem(final ItemType type, final PaperItemModel itemModel, final String myAnswers) {
			Log.d(TAG, "创建选择题...");
			//创建结果集合
			List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
			//标题
			PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
			titleModel.order = order + 1;
			//添加到数据源
			list.add(titleModel);
			//选项
			List<PaperItemOptModel>  optModels = this.createOptions(itemModel.getChildren(), type, itemModel.getAnswer(), myAnswers);
			if(optModels != null && optModels.size() > 0){
				//添加到数据源
				list.addAll(optModels);
			}
			//答案解析
			if(this.displayAnswer && optModels != null && optModels.size() > 0){
				PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(itemModel);
				analysisModel.options = optModels;
				analysisModel.myAnswers = myAnswers;
				//添加到数据源
				list.add(analysisModel);
			}
			return list;
		}
		//创建判断题。
		private List<PaperItemTitleModel> createJudgeItem(final ItemType type, final PaperItemModel itemModel, final String myAnswers) {
			Log.d(TAG, "创建判断题...");
			//创建结果集合
			List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
			//标题
			PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
			titleModel.order = order + 1;
			//添加到数据源
			list.add(titleModel);
			
			//判断选项初始化
			//1.正确答案
			PaperItemModel optRightModel = new PaperItemModel();
			optRightModel.setId(String.valueOf(PaperItemModel.ItemJudgeAnswer.Right.getValue()));
			optRightModel.setContent(PaperItemModel.ItemJudgeAnswer.Right.getName());
			
			//2.错误答案
			PaperItemModel optWrongModel = new PaperItemModel();
			optWrongModel.setId(String.valueOf(PaperItemModel.ItemJudgeAnswer.Wrong.getValue()));
			optWrongModel.setContent(PaperItemModel.ItemJudgeAnswer.Wrong.getName());
			
			//选项
			List<PaperItemOptModel> optModels = this.createOptions(Arrays.asList(new PaperItemModel[] {optRightModel, optWrongModel}), 
					type, itemModel.getAnswer(), myAnswers);
			if(optModels != null && optModels.size() > 0){
				//添加到数据源
				list.addAll(optModels);
			}
			//答案解析
			if(displayAnswer && optModels != null && optModels.size() > 0){
				PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(itemModel);
				analysisModel.options = optModels;
				analysisModel.myAnswers = myAnswers;
				//添加到数据源
				list.add(analysisModel);
			}
			return list;
		}
		//创建问答题
		private List<PaperItemTitleModel> createQandaItem(final ItemType type, final PaperItemModel itemModel) {
			Log.d(TAG, "创建问答题...");
			//创建结果集合
			List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
			
			//标题
			PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
			titleModel.order =  order + 1;
			//添加到数据源
			list.add(titleModel);
			
			//答案解析
			if(displayAnswer){
				PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(itemModel);
				analysisModel.options = null;
				analysisModel.myAnswers = null;
				//添加到数据源
				list.add(analysisModel);
			}
			return list;
		}
		//创建共享题干
		private List<PaperItemTitleModel> createShareTitle(final ItemType type, final PaperItemModel itemModel, final String myAnswers) {
			Log.d(TAG, "创建共享题干...");
			//创建结果集合
			List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
			
			//标题
			PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
			titleModel.order =  0;
			//添加到数据源
			list.add(titleModel);
			//
			int index = itemModel.getIndex();
			if(itemModel.getChildren() != null && itemModel.getChildren().size() > index){
				PaperItemModel child = itemModel.getChildren().get(index);
				if(child != null){
					//子标题
					PaperItemTitleModel subTitleModel = new PaperItemTitleModel(itemModel);
					subTitleModel.order =  order + 1;
					//添加到数据源
					list.add(subTitleModel);
					//选项
					List<PaperItemOptModel> optModels = this.createOptions(child.getChildren(), type, child.getAnswer(), myAnswers);
					if(optModels != null && optModels.size() > 0){
						//添加到数据源
						list.addAll(optModels);
					}
					//答案解析
					if(displayAnswer && optModels != null && optModels.size() > 0){
						PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(child);
						analysisModel.options = optModels;
						analysisModel.myAnswers = myAnswers;
						//添加到数据源
						list.add(analysisModel);
					}
				}
			}
			return list;
		}
		//创建共享答案题
		private List<PaperItemTitleModel> createShareAnswerItem(final ItemType type, final PaperItemModel itemModel, final String myAnswers) {
			Log.d(TAG, "创建共享题干...");
			//创建结果集合
			List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
			
			//标题
			if(StringUtils.isNotBlank(itemModel.getContent())){
				PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
				titleModel.order =  0;
				//添加到数据源
				list.add(titleModel);
			}
			//子题
			if(itemModel.getChildren() != null && itemModel.getChildren().size() > 0){
				int max = 0, index = itemModel.getIndex();
				PaperItemModel p = null;
				//构建选项，查找根
				List<PaperItemModel> optItemModels = new ArrayList<PaperItemModel>();
				for(PaperItemModel child : itemModel.getChildren()){
					if(child == null) continue;
					if(child.getOrderNo() > max){
						if(p != null){
							optItemModels.add(child);
						}
						p = child;
						max = child.getOrderNo();
					}else {
						optItemModels.add(child);
					}
				}
				//拼接试题
				if(p != null && p.getChildren() != null && p.getChildren().size() > index){
					PaperItemModel subItemModel = p.getChildren().get(index);
					if(subItemModel != null && subItemModel.getType() != null){
						//子标题
						PaperItemTitleModel titleModel = new PaperItemTitleModel(subItemModel);
						titleModel.order = order + 1;
						//添加到数据源
						list.add(titleModel);
						//选项
						List<PaperItemOptModel> optModels = this.createOptions(optItemModels, ItemType.values()[subItemModel.getType() - 1], subItemModel.getAnswer(), myAnswers);
						if(optModels != null && optModels.size() > 0){
							//添加到数据源
							list.addAll(optModels);
						}
						//答案解析
						if(displayAnswer && optItemModels != null && optItemModels.size() > 0){
							PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(subItemModel);
							analysisModel.options = optModels;
							analysisModel.myAnswers = myAnswers;
							//添加到数据源
							list.add(analysisModel);
						}
					}
				}
			}
			return list;
		}
		//创建试题选项数据集合。
		private List<PaperItemOptModel> createOptions(List<PaperItemModel> options, ItemType type, String rightAnswers, String myAnswers){
			int len = 0;
			if(options != null && (len = options.size()) > 0){
				List<PaperItemOptModel> opts = new ArrayList<PaperItemOptModel>(len);
				//选项
				for(PaperItemModel item : options){
					if(item == null) continue;
					//创建选项
					PaperItemOptModel optModel = new PaperItemOptModel(item);
					optModel.itemType = type;
					optModel.rightAnswers = rightAnswers;
					optModel.myAnswers = myAnswers;
					optModel.display = displayAnswer;
					//添加选项集合
					opts.add(optModel);
				}
				//
				return opts;
			}
			return null;
		}
		/*
		 * 前台UI处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				Log.d(TAG, "前台试题数据刷新["+order+"]...");
				//刷新数据适配器
				notifyDataSetChanged();
				//重绘
				if(listView != null){
					listView.postInvalidate();
				}
			}
		}
	}
	/**
	 * 试题标题数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月23日
	 */
	private class PaperItemTitleModel implements Serializable{
		private static final long serialVersionUID = 1L;
		/**
		 * 试题ID。
		 */
		String id;
		/**
		 * 题型。
		 */
		ItemType itemType;
		/**
		 * 内容。
		 */
		String content;
		/**
		 * 序号。
		 */
		int order;
		/**
		 * 图片URL集合。
		 */
		List<String> images;
		/**
		 * 构造函数。
		 * @param itemModel
		 */
		public PaperItemTitleModel(PaperItemModel itemModel){
			if(itemModel != null){
				this.id = itemModel.getId();
				if(itemModel.getType() != null){
					this.itemType = ItemType.values()[itemModel.getType() - 1];
				}
				this.content = itemModel.getContent();
				this.order = itemModel.getOrderNo();
				this.images = itemModel.getItemContentImgUrls();
			}
		}
	}
	/**
	 * 试题选项数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月23日
	 */
	private class PaperItemOptModel extends PaperItemTitleModel{
		private static final long serialVersionUID = 1L;
		/**
		 * 是否显示答案。
		 */
		boolean display;
		/**
		 * 我的答案。
		 */
		String myAnswers;
		/**
		 * 正确答案。
		 */
		String rightAnswers;
		/**
		 * 构造函数。
		 * @param itemModel
		 */
		public PaperItemOptModel(PaperItemModel itemModel) {
			super(itemModel);
			if(itemModel != null){
				this.rightAnswers = itemModel.getAnswer();
			}
		}
	}
	/**
	 * 试题答案解析数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月23日
	 */
	private class PaperItemAnalysisModel extends PaperItemOptModel{
		private static final long serialVersionUID = 1L;
		/**
		 * 选项集合。
		 */
		List<PaperItemOptModel> options;
		/**
		 * 构造函数。
		 * @param itemModel
		 */
		public PaperItemAnalysisModel(PaperItemModel itemModel) {
			super(itemModel);
			if(itemModel != null){
				this.content = itemModel.getAnalysis();
				this.images = itemModel.getItemAnalysisImgUrls();
			}
		}
	}
}