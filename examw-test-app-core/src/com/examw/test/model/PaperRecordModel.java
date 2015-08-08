package com.examw.test.model;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

/**
 * 试卷记录数据模型。
 * 
 * @author jeasonyoung
 * @since 2015年7月2日
 */
public class PaperRecordModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id,paperId,paperName,lastTime;
	private boolean status;
	private float score;
	private int rights,useTimes;
	/**
	 * 构造函数。
	 */
	public PaperRecordModel(){
		
	}
	/**
	 * 构造函数。
	 * @param paperId
	 */
	public PaperRecordModel(String paperId){
		if(StringUtils.isNotBlank(paperId)){
			this.paperId = paperId;
			this.id = UUID.randomUUID().toString();
		}
	}
	
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
	 * 获取所属试卷名称。
	 * @return 所属试卷名称。
	 */
	public String getPaperName() {
		return paperName;
	}
	/**
	 * 设置所属试卷名称。
	 * @param paperName 
	 *	  所属试卷名称。
	 */
	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}
	/**
	 * 获取做题状态(true－已做完，false-未做完)。
	 * @return 做题状态(true－已做完，false-未做完)。
	 */
	public boolean isStatus() {
		return status;
	}
	/**
	 * 设置做题状态(true－已做完，false-未做完)。
	 * @param status 
	 *	  做题状态(true－已做完，false-未做完)。
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}
	/**
	 * 获取得分。
	 * @return 得分。
	 */
	public float getScore() {
		return score;
	}
	/**
	 * 设置得分。
	 * @param score 
	 *	  得分。
	 */
	public void setScore(float score) {
		this.score = score;
	}
	/**
	 * 获取做对题数。
	 * @return 做对题数。
	 */
	public int getRights() {
		return rights;
	}
	/**
	 * 设置做对题数。
	 * @param rights 
	 *	  做对题数。
	 */
	public void setRights(int rights) {
		this.rights = rights;
	}
	/**
	 * 获取用时(秒)。
	 * @return 用时(秒)。
	 */
	public int getUseTimes() {
		return useTimes;
	}
	/**
	 * 设置用时(秒)。
	 * @param useTimes 
	 *	  用时(秒)。
	 */
	public void setUseTimes(int useTimes) {
		this.useTimes = useTimes;
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
	/*
	 * 重载。
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}