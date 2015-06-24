package com.examw.test.model.sync;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 做题记录同步
 * 
 * @author yangyong
 * @since 2015年3月9日
 */
public class PaperItemRecordSync implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id,paperRecordId,structureId,itemId,content,answer;
	private Integer status,useTimes;
	private BigDecimal score;
	private String createTime,lastTime;
	/**
	 * 获取做题记录ID。
	 * @return 做题记录ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置做题记录ID。
	 * @param id 
	 *	  做题记录ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取所属试卷记录ID。
	 * @return 所属试卷记录ID。
	 */
	public String getPaperRecordId() {
		return paperRecordId;
	}
	/**
	 * 设置所属试卷记录ID。
	 * @param paperRecordId 
	 *	  所属试卷记录ID。
	 */
	public void setPaperRecordId(String paperRecordId) {
		this.paperRecordId = paperRecordId;
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
	 * 获取试题内容JSON。
	 * @return 试题内容JSON。
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置试题内容JSON。
	 * @param content 
	 *	  试题内容JSON。
	 */
	public void setContent(String content) {
		this.content = content;
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
	 * 获取做题状态(0-做错，1-做对)。
	 * @return 做题状态(0-做错，1-做对)。
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置做题状态(0-做错，1-做对)。
	 * @param status 
	 *	  做题状态(0-做错，1-做对)。
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取得分。
	 * @return 得分。
	 */
	public BigDecimal getScore() {
		return score;
	}
	/**
	 * 设置得分。
	 * @param score 
	 *	  得分。
	 */
	public void setScore(BigDecimal score) {
		this.score = score;
	}
	/**
	 * 获取做题用时。
	 * @return 做题用时。
	 */
	public Integer getUseTimes() {
		return useTimes;
	}
	/**
	 * 设置做题用时。
	 * @param useTimes 
	 *	  做题用时。
	 */
	public void setUseTimes(Integer useTimes) {
		this.useTimes = useTimes;
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
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(); 
		builder.append("id").append(":").append(this.getId())
				   .append("paperRecordId").append(":").append(this.getPaperRecordId())
				   .append("structureId").append(":").append(this.getStructureId())
				   .append("itemId").append(":").append(this.getItemId())
				   .append("content").append(":").append(this.getContent())
				   .append("answer").append(":").append(this.getAnswer())
				   .append("status").append(":").append(this.getStatus())
				   .append("useTimes").append(":").append(this.getUseTimes())
				   .append("score").append(":").append(this.getScore())
				   .append("createTime").append(":").append(this.getCreateTime())
				   .append("lastTime").append(":").append(this.lastTime);
		return builder.toString();
	}
}