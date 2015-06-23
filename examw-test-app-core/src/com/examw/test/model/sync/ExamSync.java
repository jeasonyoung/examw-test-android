package com.examw.test.model.sync;

import java.io.Serializable;
import java.util.Set;

/**
 * 考试同步数据。
 * 
 * @author yangyong
 * @since 2015年2月27日
 */
public class ExamSync implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code,name,abbr;
	private Set<SubjectSync> subjects;
	/**
	 * 获取考试代码。
	 * @return 考试代码。
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 设置考试代码。
	 * @param code 
	 *	  考试代码。
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 获取考试名称。
	 * @return 考试名称。
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置考试名称。
	 * @param name 
	 *	  考试名称。
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取考试简称
	 * @return 考试简称
	 */
	public String getAbbr() {
		return abbr;
	}
	/**
	 * 设置考试简称
	 * @param abbr 
	 *	  考试简称
	 */
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	/**
	 * 获取所属科目集合。
	 * @return 所属科目集合。
	 */
	public Set<SubjectSync> getSubjects() {
		return subjects;
	}
	/**
	 * 设置所属科目集合。
	 * @param subjects 
	 *	  所属科目集合。
	 */
	public void setSubjects(Set<SubjectSync> subjects) {
		this.subjects = subjects;
	}
}