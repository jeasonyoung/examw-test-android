package com.examw.test.model;

import java.io.Serializable;
import java.math.BigDecimal;
/**
 * 用户试题记录信息。
 * 
 * @author yangyong
 * @since 2014年10月11日
 */
public class UserItemRecordInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id,structureId,itemId,itemContent,answer;
	private Integer status,terminalCode;
	private Long usedTime;
	private BigDecimal score;
	private String createTime,lastTime;
	/**
	 * 获取记录ID。
	 * @return 记录ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置记录ID。
	 * @param id 
	 *	  记录ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取所属试卷结构ID。
	 * @return 所属试卷结构ID。
	 */
	public String getStructureId() {
		return structureId;
	}
	/**
	 * 设置所属试卷结构ID。
	 * @param structureId 
	 *	  所属试卷结构ID。
	 */
	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}
	/**
	 * 获取所属试题ID。
	 * @return 所属试题ID。
	 */
	public String getItemId() {
		return itemId;
	}
	/**
	 * 设置所属试题ID。
	 * @param itemId 
	 *	  所属试题ID。
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	/**
	 * 获取所属试题内容JSON。
	 * @return 所属试题内容JSON。
	 */
	public String getItemContent() {
		return itemContent;
	}
	/**
	 * 设置所属试题内容JSON。
	 * @param itemContent 
	 *	  所属试题内容JSON。
	 */
	public void setItemContent(String itemContent) {
		this.itemContent = itemContent;
	}
	/**
	 * 获取用户答案。
	 * @return 用户答案。
	 */
	public String getAnswer() {
		return answer;
	}
	/**
	 * 设置用户答案。
	 * @param answer 
	 *	  用户答案。
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	/**
	 * 获取用户终端类型代码。
	 * @return 用户终端类型代码。
	 */
	public Integer getTerminalCode() {
		return terminalCode;
	}
	/**
	 * 设置用户终端类型代码。
	 * @param terminalCode 
	 *	  用户终端类型代码。
	 */
	public void setTerminalCode(Integer terminalCode) {
		this.terminalCode = terminalCode;
	}
	/**
	 * 获取试题状态。
	 * @return 试题状态。
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置试题状态。
	 * @param status 
	 *	  试题状态。
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取用户做题用时（秒）。
	 * @return 用户做题用时（秒）。
	 */
	public Long getUsedTime() {
		return usedTime;
	}
	/**
	 * 设置用户做题用时（秒）。
	 * @param usedTime 
	 *	  用户做题用时（秒）。
	 */
	public void setUsedTime(Long usedTime) {
		this.usedTime = usedTime;
	}
	/**
	 * 获取用户得分。
	 * @return 用户得分。
	 */
	public BigDecimal getScore() {
		return score;
	}
	/**
	 * 设置用户得分。
	 * @param score 
	 *	  用户得分。
	 */
	public void setScore(BigDecimal score) {
		this.score = score;
	}
	/**
	 * 获取创建时间。
	 * @return 创建时间。
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * 设置创建时间。
	 * @param createTime 
	 *	  创建时间。
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取最后修改时间。
	 * @return 最后修改时间。
	 */
	public String getLastTime() {
		return lastTime;
	}
	/**
	 * 设置最后修改时间。
	 * @param lastTime 
	 *	  最后修改时间。
	 */
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
}