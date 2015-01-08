package com.examw.test.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品信息
 * @author fengwei.
 * @since 2014年12月3日 下午3:43:15.
 */
public class FrontProductInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	/*
	 * "id": "60a4eb98-8c95-42d7-9178-d053a17f9e87",
    "name": "2015年一级建造师《综合考试》考试通（工程经济+工程项目管理+工程法规）",
    "image": "",
    "content": "",
    "categoryId": "f57d600b-6127-4e10-9386-7f0eaec1eeef",
    "categoryName": "建筑工程",
    "examId": "9bf07238-e86c-439a-8c36-6db91f13b178",
    "examName": "一级建造师",
    "statusName": "启用",
    "analysisTypeName": "没有",
    "realTypeName": "有",
    "subjectId": [
        "aaf71eb3-d5d7-45fa-a3c7-c2ed918d1340",
        "95c428e4-9048-4afc-8fcd-650dee44e229",
        "6fe52db2-f1c4-40e5-8b1f-c0a25767e637"
    ],
    "subjectName": [
        "工程法规",
        "工程项目管理",
        "工程经济"
    ],
    "orderNo": 1,
    "status": 1,
    "analysisType": 0,
    "realType": 1,
    "paperTotal": 12,
    "itemTotal": 1095,
    "price": 100,
    "discount": 0,
    "createTime": "2015-01-05 13:59:44",
    "lastTime": "2015-01-05 14:02:27",
    "paperCount": 12,
    "itemCount": 1095,
    "hasRealItem": true
	 */
	private String id,name,image,content,categoryId,categoryName,examId,examName,statusName,analysisTypeName,realTypeName,createTime,lastTime;
	private Integer orderNo,status,paperCount,itemCount,itemTotal,paperTotal,analysisType,realType;
	private String[] subjectId,subjectName;
	private BigDecimal price,discount;
	private Boolean hasRealItem;
	/**
	 * 获取 
	 * @return id
	 * 
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置 
	 * @param id
	 * 
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取 
	 * @return name
	 * 
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置 
	 * @param name
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取 
	 * @return content
	 * 
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置 
	 * @param content
	 * 
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 获取 
	 * @return categoryId
	 * 
	 */
	public String getCategoryId() {
		return categoryId;
	}
	/**
	 * 设置 
	 * @param categoryId
	 * 
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	/**
	 * 获取 
	 * @return categoryName
	 * 
	 */
	public String getCategoryName() {
		return categoryName;
	}
	/**
	 * 设置 
	 * @param categoryName
	 * 
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	/**
	 * 获取 
	 * @return examId
	 * 
	 */
	public String getExamId() {
		return examId;
	}
	/**
	 * 设置 
	 * @param examId
	 * 
	 */
	public void setExamId(String examId) {
		this.examId = examId;
	}
	/**
	 * 获取 
	 * @return examName
	 * 
	 */
	public String getExamName() {
		return examName;
	}
	/**
	 * 设置 
	 * @param examName
	 * 
	 */
	public void setExamName(String examName) {
		this.examName = examName;
	}
	/**
	 * 获取 
	 * @return statusName
	 * 
	 */
	public String getStatusName() {
		return statusName;
	}
	/**
	 * 设置 
	 * @param statusName
	 * 
	 */
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	/**
	 * 获取 
	 * @return createTime
	 * 
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * 设置 
	 * @param createTime
	 * 
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取 
	 * @return lastTime
	 * 
	 */
	public String getLastTime() {
		return lastTime;
	}
	/**
	 * 设置 
	 * @param lastTime
	 * 
	 */
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	/**
	 * 获取 
	 * @return orderNo
	 * 
	 */
	public Integer getOrderNo() {
		return orderNo;
	}
	/**
	 * 设置 
	 * @param orderNo
	 * 
	 */
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * 获取 
	 * @return status
	 * 
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置 
	 * @param status
	 * 
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取 
	 * @return paperCount
	 * 
	 */
	public Integer getPaperCount() {
		return paperCount;
	}
	/**
	 * 设置 
	 * @param paperCount
	 * 
	 */
	public void setPaperCount(Integer paperCount) {
		this.paperCount = paperCount;
	}
	/**
	 * 获取 
	 * @return itemCount
	 * 
	 */
	public Integer getItemCount() {
		return itemCount;
	}
	/**
	 * 设置 
	 * @param itemCount
	 * 
	 */
	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}
	/**
	 * 获取 
	 * @return subjectId
	 * 
	 */
	public String[] getSubjectId() {
		return subjectId;
	}
	/**
	 * 设置 
	 * @param subjectId
	 * 
	 */
	public void setSubjectId(String[] subjectId) {
		this.subjectId = subjectId;
	}
	/**
	 * 获取 
	 * @return subjectName
	 * 
	 */
	public String[] getSubjectName() {
		return subjectName;
	}
	/**
	 * 设置 
	 * @param subjectName
	 * 
	 */
	public void setSubjectName(String[] subjectName) {
		this.subjectName = subjectName;
	}
	/**
	 * 获取 
	 * @return price
	 * 
	 */
	public BigDecimal getPrice() {
		return price;
	}
	/**
	 * 设置 
	 * @param price
	 * 
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * 获取 
	 * @return discount
	 * 
	 */
	public BigDecimal getDiscount() {
		return discount;
	}
	/**
	 * 设置 
	 * @param discount
	 * 
	 */
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	/**
	 * 获取 
	 * @return hasRealItem
	 * 
	 */
	public Boolean getHasRealItem() {
		return hasRealItem;
	}
	/**
	 * 设置 
	 * @param hasRealItem
	 * 
	 */
	public void setHasRealItem(Boolean hasRealItem) {
		this.hasRealItem = hasRealItem;
	}
	
	private String info;
	/**
	 * 获取 
	 * @return info
	 * 
	 */
	public String getInfo() {
		return info;
	}
	/**
	 * 设置 
	 * @param info
	 * 
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	/**
	 * 获取 
	 * @return image
	 * 
	 */
	public String getImage() {
		return image;
	}
	/**
	 * 设置 
	 * @param image
	 * 
	 */
	public void setImage(String image) {
		this.image = image;
	}
	/**
	 * 获取 
	 * @return analysisTypeName
	 * 
	 */
	public String getAnalysisTypeName() {
		return analysisTypeName;
	}
	/**
	 * 设置 
	 * @param analysisTypeName
	 * 
	 */
	public void setAnalysisTypeName(String analysisTypeName) {
		this.analysisTypeName = analysisTypeName;
	}
	/**
	 * 获取 
	 * @return realTypeName
	 * 
	 */
	public String getRealTypeName() {
		return realTypeName;
	}
	/**
	 * 设置 
	 * @param realTypeName
	 * 
	 */
	public void setRealTypeName(String realTypeName) {
		this.realTypeName = realTypeName;
	}
	/**
	 * 获取 
	 * @return itemTotal
	 * 
	 */
	public Integer getItemTotal() {
		return itemTotal;
	}
	/**
	 * 设置 
	 * @param itemTotal
	 * 
	 */
	public void setItemTotal(Integer itemTotal) {
		this.itemTotal = itemTotal;
	}
	/**
	 * 获取 
	 * @return paperTotal
	 * 
	 */
	public Integer getPaperTotal() {
		return paperTotal;
	}
	/**
	 * 设置 
	 * @param paperTotal
	 * 
	 */
	public void setPaperTotal(Integer paperTotal) {
		this.paperTotal = paperTotal;
	}
	/**
	 * 获取 
	 * @return analysisType
	 * 
	 */
	public Integer getAnalysisType() {
		return analysisType;
	}
	/**
	 * 设置 
	 * @param analysisType
	 * 
	 */
	public void setAnalysisType(Integer analysisType) {
		this.analysisType = analysisType;
	}
	/**
	 * 获取 
	 * @return realType
	 * 
	 */
	public Integer getRealType() {
		return realType;
	}
	/**
	 * 设置 
	 * @param realType
	 * 
	 */
	public void setRealType(Integer realType) {
		this.realType = realType;
	}
	
}
