package com.examw.test.model;
import java.math.BigDecimal;
import java.util.List;
/**
 * 试卷预览信息。
 * 
 * @author yangyong
 * @since 2014年8月15日
 */
public class PaperPreview extends BasePaperInfo {
	private static final long serialVersionUID = 1L;
	private Integer total;
	private String paperRecordId;	//试卷记录Id
	private Long leftTime;	//剩余时间
	private List<StructureInfo> structures;
	/**
	 * 获取试卷结构集合。
	 * @return 试卷结构集合。
	 */
	public List<StructureInfo> getStructures() {
		return structures;
	}
	/**
	 * 设置试卷结构集合。
	 * @param structures 
	 *	  试卷结构集合。
	 */
	public void setStructures(List<StructureInfo> structures) {
		this.structures = structures;
	}
	/**
	 * 获取试卷题目总数。
	 * @return 试卷题目总数。
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * 设置试卷题目总数。
	 * @param total 
	 *	  试卷题目总数。
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * 获取 试卷记录ID
	 * @return paperRecordId
	 * 试卷记录ID
	 */
	public String getPaperRecordId() {
		return paperRecordId;
	}
	/**
	 * 设置 试卷记录ID
	 * @param paperRecordId
	 * 试卷记录ID
	 */
	public void setPaperRecordId(String paperRecordId) {
		this.paperRecordId = paperRecordId;
	}
	private BigDecimal userScore; //用户得分
	/**
	 * 获取 用户得分
	 * @return userScore
	 * 用户得分
	 */
	public BigDecimal getUserScore() {
		return userScore;
	}
	/**
	 * 设置 用户得分
	 * @param userScore
	 * 用户得分
	 */
	public void setUserScore(BigDecimal userScore) {
		this.userScore = userScore;
	}
	/**
	 * 设置 剩余时间
	 * @param leftTime
	 * 剩余时间
	 */
	public void setLeftTime(Long leftTime) {
		this.leftTime = leftTime;
	}
	/**
	 * 获取 剩余时间
	 * @return leftTime
	 * 剩余时间
	 */
	public Long getLeftTime() {
		return leftTime;
	}
	
}