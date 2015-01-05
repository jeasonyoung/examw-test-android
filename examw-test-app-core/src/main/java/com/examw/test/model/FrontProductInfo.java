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
	 * {
    "id": "615c758f-424f-4808-9528-67630125687e",
    "name": "2015年中级经济师《建筑经济》考试通（含基础）",
    "content": "含经济基础知识和建筑经济专业",
    "categoryId": "baefde17-f56b-4792-bda1-e14460730bd7",
    "categoryName": "财会经济",
    "examId": "18e59b72-eda4-44b5-8691-5a230fedb0d9",
    "examName": "中级经济师",
    "statusName": "启用",
    "subjectId": [
        "4454a43c-2c3b-4c88-a7fe-a99f85b8093d",
        "2ed19a82-204a-40a7-9cd4-a563770a1db3"
    ],
    "subjectName": [
        "建筑经济",
        "经济基础知识"
    ],
    "orderNo": 1,
    "status": 1,
    "price": 100,
    "discount": 0,
    "createTime": "2014-10-20 09:56:58",
    "lastTime": "2014-11-17 16:30:28",
    "paperCount": 14,
    "itemCount": 1430,
    "hasRealItem": true
	}
	 */
	private String id,name,content,categoryId,categoryName,examId,examName,statusName,createTime,lastTime;
	private Integer orderNo,status,paperCount,itemCount;
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
	
}
