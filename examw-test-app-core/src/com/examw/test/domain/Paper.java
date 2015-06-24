package com.examw.test.domain;

import com.examw.test.model.sync.PaperSync;

/**
 * 试卷[已经发布过的试卷]
 * @author fengwei.
 * @since 2014年11月27日 下午3:56:12.
 */
public class Paper extends PaperSync {
	private static final long serialVersionUID = 1L;
	
	public Paper(String id, String title, Integer type, Integer total, String content, String subjectCode, String createTime) {
		super(id,title,type,total,content,subjectCode,createTime);
	}
}
