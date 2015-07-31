package com.examw.test.dao;

import java.io.Serializable;
import java.util.List;

import android.util.SparseArray;

import com.examw.test.dao.PaperDao.ItemStatus;
import com.examw.test.model.PaperItemModel;

/**
 * 试题数据委托接口。
 * 
 * @author jeasonyoung
 * @since 2015年7月31日
 */
public interface IPaperItemDataDelegate extends Serializable {
	/**
	 * 加载数据源(异步线程调用)。
	 * @return
	 * 试题集合。
	 */
	List<PaperItemModel> dataSourceOfPaperViews() throws Exception;
	/**
	 * 获取考试时长(分钟)。
	 * @return
	 * 考试时长(分钟)。
	 */
	int timeOfPaperView() throws Exception;
	/**
	 *加载当前试题题序。
	 * @return
	 * 题序。
	 */
	int currentOrderOfPaperView() throws Exception;
	/**
	 * 加载试题答案(异步线程调用)。
	 * @param itemModel
	 * 试题。
	 * @return 记录的答案
	 */
    String loadMyAnswer(PaperItemModel itemModel) throws Exception;
    /**
	 * 加载答题卡数据(异步线程调用)
	 * @param cardSections
	 * @param cardSectionItems
	 * @throws Exception
	 */
     void loadAnswerCardData(final List<AnswerCardSectionModel> cardSections, final SparseArray<AnswerCardItemModel[]> cardSectionItems) throws Exception;
     /**
 	 * 更新做题记录到SQL(异步线程中调用)
 	 * @param itemModel
 	 * 试题。
 	 * @param myAnswers
 	 * 答案。
 	 * @param useTimes
 	 * 用时。
 	 */
    void updateRecordAnswer(PaperItemModel itemModel, String myAnswers, int useTimes) throws Exception;
    /**
	 * 更新收藏记录(异步线程中被调用)。
	 * @param itemModel
	 * 试题。
	 * @return
	 * true - 已收藏, false - 未收藏。
	 */
    boolean updateFavorite(PaperItemModel itemModel) throws Exception;
    /**
	 * 交卷处理。
	 * @param useTimes
	 * 用时。
	 * @param handler
	 * 交卷结果处理。
	 */
    void submitPaper(int useTimes, SubmitResultHandler handler) throws Exception;
	/**
	 * 交卷结果处理。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月20日
	 */
	public interface SubmitResultHandler{
		/**
		 * 处理。
		 * @param paperRecordId
		 * 试卷记录ID。
		 */
		void hanlder(String paperRecordId);
	}
	/**
	 * 答题卡分组数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月21日
	 */
	public class AnswerCardSectionModel implements Serializable{
		private static final long serialVersionUID = 1L;
		private String title,desc;
		/**
		 * 构造函数。
		 * @param title
		 * 结构名称。
		 * @param desc
		 * 结构描述。
		 */
		public AnswerCardSectionModel(String title,String desc){
			this.title = title;
			this.desc = desc;
		}
		/**
		 * 获取结构名称。
		 * @return 结构名称。
		 */
		public String getTitle() {
			return title;
		}
		/**
		 * 获取结构描述。
		 * @return 结构描述。
		 */
		public String getDesc() {
			return desc;
		}
	}
	/**
	 * 答案卡试题数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月21日
	 */
	public final class AnswerCardItemModel implements Serializable{
		private static final long serialVersionUID = 1L;
		private int order;
		/**
		 * 构造函数。
		 * @param order
		 * 题序。
		 * @param status
		 * 状态。
		 */
		public AnswerCardItemModel(int order, ItemStatus status){
			this.order = order;
			this.status = status;
		}
		/**
		 * 是否显示答案。
		 */
		public boolean displayAnswer;
		/**
		 * 试题状态。
		 */
		public ItemStatus status;
		/**
		 * 获取题序。
		 * @return 题序。
		 */
		public int getOrder() {
			return order;
		}
	}
}