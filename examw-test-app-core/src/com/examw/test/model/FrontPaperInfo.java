package com.examw.test.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 前端试卷摘要信息。
 * 
 * @author yangyong
 * @since 2014年9月26日
 */
public class FrontPaperInfo extends BasePaperInfo {
	private static final long serialVersionUID = 1L;
	private Integer total;
	private BigDecimal maxScore;	//最高得分
	private Long userTotal;		//总人数
	/**
	 * 获取试题总数。
	 * @return 试题总数。
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * 设置试题总数。
	 * @param total 
	 *	  试题总数。
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * 获取 最高得分
	 * @return maxScore
	 * 最高得分
	 */
	public BigDecimal getMaxScore() {
		return maxScore;
	}
	/**
	 * 设置 最高得分
	 * @param maxScore
	 * 最高得分
	 */
	public void setMaxScore(BigDecimal maxScore) {
		this.maxScore = maxScore;
	}
	/**
	 * 获取 总参考人数
	 * @return sumUser
	 * 总参考人数
	 */
	public Long getUserTotal() {
		return userTotal;
	}
	/**
	 * 设置 总参考人数
	 * @param sumUser
	 * 总参考人数
	 */
	public void setUserTotal(Long userTotal) {
		this.userTotal = userTotal;
	}
	
	private BigDecimal userScore;	//用户得分
	private Long usedTime;		//用时
	private Date examTime;		//考试时间
	private Integer examStatus;		//考试状态
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
	 * 获取 使用时间
	 * @return usedTime
	 * 使用时间
	 */
	public Long getUsedTime() {
		return usedTime;
	}
	/**
	 * 设置 使用时间
	 * @param usedTime
	 * 使用时间
	 */
	public void setUsedTime(Long usedTime) {
		this.usedTime = usedTime;
	}
	/**
	 * 获取 考试日期
	 * @return examTime
	 * 考试日期
	 */
	public Date getExamTime() {
		return examTime;
	}
	/**
	 * 设置 考试日期
	 * @param examTime
	 * 考试日期
	 */
	public void setExamTime(Date examTime) {
		this.examTime = examTime;
	}
	/**
	 * 获取 考试状态
	 * @return examStatus
	 * 考试状态
	 */
	public Integer getExamStatus() {
		return examStatus;
	}
	/**
	 * 设置 考试状态
	 * @param examStatus
	 * 考试状态
	 */
	public void setExamStatus(Integer examStatus) {
		this.examStatus = examStatus;
	}
	
	
}