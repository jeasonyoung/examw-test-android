package com.examw.test.model.sync;

import java.io.Serializable;
import java.math.BigDecimal;
/**
 * 试题记录同步
 * 
 * @author yangyong
 * @since 2015年3月9日
 */
public class PaperRecordSync implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id,paperId;
	private Integer status,rights,useTimes;
	private BigDecimal score;
	private String createTime,lastTime;
	/**
	 * 获取试卷记录ID。
	 * @return 试卷记录ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置试卷记录ID。
	 * @param id 
	 *	  试卷记录ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取所属试卷ID。
	 * @return 所属试卷ID。
	 */
	public String getPaperId() {
		return paperId;
	}
	/**
	 * 设置所属试卷ID。
	 * @param paperId 
	 *	  所属试卷ID。
	 */
	public void setPaperId(String paperId) {
		this.paperId = paperId;
	}
	/**
	 * 获取做题状态。
	 * @return 做题状态(0-未完成，1-完成)。
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置做题状态。
	 * @param status 
	 *	  做题状态(0-未完成，1-完成)。
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
	 * 获取做对的试题数。
	 * @return 做对的试题数。
	 */
	public Integer getRights() {
		return rights;
	}
	/**
	 * 设置做对的试题数。
	 * @param rights 
	 *	  做对的试题数。
	 */
	public void setRights(Integer rights) {
		this.rights = rights;
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
}