package com.examw.test.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.examw.test.app.AppConfig;
import com.examw.test.app.AppContext;
import com.examw.test.db.NewImportDBManager;
import com.examw.test.domain.Subject;
import com.examw.test.model.FrontPaperInfo;
import com.examw.test.model.FrontProductInfo;
import com.examw.test.model.ItemInfo;
import com.examw.test.model.KnowledgeInfo;
import com.examw.test.model.PaperPreview;
import com.examw.test.model.StructureInfo;
import com.examw.test.model.SyllabusInfo;
import com.examw.test.support.ApiClient;
import com.examw.test.support.DataConverter;
import com.examw.test.util.CyptoUtils;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.LogUtil;
import com.examw.test.util.StringUtils;
import com.google.gson.reflect.TypeToken;

/**
 * 导入工具
 * 
 * @author fengwei.
 * @since 2014年12月28日 下午2:37:27.
 */
public class NewImportDao {
	NewImportDBManager dbManager = new NewImportDBManager();
	AppContext appContext;
	public NewImportDao(AppContext appContext) {
		this.appContext = appContext;
	}
	public boolean hasInsert() {
		LogUtil.d( "查询产品信息是否存在");
		SQLiteDatabase db = dbManager.openDatabase();
		Cursor cursor = db.rawQuery(
				"select * from ProductTab where productid = ?",
				new String[] { AppContext.getMetaInfo("productId") });
		boolean flag = cursor.getCount() > 0;
		cursor.close();
		db.close();
		return flag;
	}

	public void insert(FrontProductInfo product) {
		if (product == null)
			return;
		LogUtil.d( "插入产品信息");
		SQLiteDatabase db = dbManager.openDatabase();
		db.beginTransaction();
		// 插产品
		db.execSQL(
				"insert into ProductTab(productid,name,examName,info)values(?,?,?,?)",
				new Object[] { product.getId(), product.getName(), product.getExamName(),
						product.getInfo() });
		// 插科目
		db.execSQL("delete from SubjectTab");
		String[] subjectIds = product.getSubjectId();
		if (subjectIds != null && subjectIds.length > 0) {
			LogUtil.d( "插入科目信息");
			String[] subjectNames = product.getSubjectName();
			for (int i = 0; i < subjectIds.length; i++) {
				db.execSQL(
						"insert into SubjectTab(subjectId,name,orderno)values(?,?,?)",
						new Object[] { subjectIds[i], subjectNames[i], i });
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	/**
	 * 插入试卷的集合
	 * 
	 * @param list
	 * @return 返回更新的数量
	 */
	public int insertPaperList(ArrayList<FrontPaperInfo> list) {
		int count = 0;
		if (list != null && list.size() > 0) {
			SQLiteDatabase db = dbManager.openDatabase();
			String sql1 = "select * from PaperTab where paperid = ?";
			String sql2 = "insert into PaperTab(id,title,type,total,content,createTime,subjectCode)values(?,?,?,?,?,?,?)";
			db.beginTransaction();
			try {
				for (FrontPaperInfo paper : list) {
					Cursor cursor = db.rawQuery(sql1,
							new String[] { paper.getId() });
					if (cursor.getCount() > 0) {
						cursor.close();
						continue;
					}
					cursor.close();
					Object[] params = new Object[] { paper.getId(),
							paper.getName(), paper.getDescription(), null,
							paper.getExamId(), paper.getSubjectId(),
							paper.getSourceName(), paper.getAreaName(),
							paper.getType(), paper.getPrice(), paper.getTime(),
							paper.getYear(), paper.getTotal(),
							paper.getUserTotal(), paper.getScore(),
							paper.getPublishTime() };
					db.execSQL(sql2, params);
					count++;
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
				db.close();
			}
		}
		return count;
	}

	/**
	 * 插入试卷的内容
	 * 
	 * @return
	 */
	public void updatePaperContent(String paperId, String content) {
		LogUtil.d( String.format("插入试卷[PaperId= %s]的内容", paperId));
		if (StringUtils.isEmpty(content) || StringUtils.isEmpty(paperId))
			return;
		SQLiteDatabase db = dbManager.openDatabase();
		PaperPreview paper = GsonUtil.jsonToBean(content, PaperPreview.class);
		String ruleContent = getRuleList(paper);
		content = CyptoUtils.encodeContent(paperId, content);
		db.execSQL(
				"update PaperTab set content = ?,structures = ? where paperid = ?",
				new Object[] { content, ruleContent, paperId });
		db.close();
	}

	// 获取大题结构
	private String getRuleList(PaperPreview paper) {
		List<StructureInfo> rules = paper.getStructures();
		if (rules == null)
			return "";
		clearItems(rules);
		return GsonUtil.objectToJson(paper);
	}

	private void clearItems(List<StructureInfo> rules) {
		for (StructureInfo info : rules) {
			if (info == null)
				continue;
			if (info.getChildren() != null && info.getChildren().size() > 0) {
				clearItems(info.getChildren());
			}
			info.setItems(null);
		}
	}

	public void insertSyllabusAndLoadChapters(Subject subject, String content) {
		if (StringUtils.isEmpty(content) || content.equals("[]"))
			return;
		LogUtil.d( "插入考试大纲,并且获取章节信息");
		SQLiteDatabase db = dbManager.openDatabase();
		SyllabusInfo syllabus = new SyllabusInfo();
		syllabus.setId(UUID.randomUUID().toString());
		syllabus.setTitle(String.format("《%s》考试大纲", subject.getName()));
		syllabus.setFullTitle(content);
		syllabus.setSubjectId(subject.getSubjectId());
		syllabus.setYear(Calendar.getInstance().get(Calendar.YEAR));
		syllabus.setOrderNo(1);
		ArrayList<SyllabusInfo> list = GsonUtil.getGson().fromJson(content,
				new TypeToken<ArrayList<SyllabusInfo>>() {
				}.getType());
		try {
			db.beginTransaction();
			insertSyllabus(db, syllabus);
			LogUtil.d( "插入考试大纲章节信息");
			for (SyllabusInfo info : list) {
				insertChapter(db, info, syllabus.getId());
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			return;
		} finally {
			db.close();
		}
	}

	// 插入[避免重复插入]
	private void insertSyllabus(SQLiteDatabase db, SyllabusInfo info) {
		Cursor cursor = db.rawQuery(
				"select * from SyllabusTab where syllabusId = ?",
				new String[] { info.getId() });
		if (cursor.getCount() > 0) {
			cursor.close();
			return;
		}
		cursor.close();
		// syllabusId ,title,content ,subjectId ,year ,orderNo
		db.execSQL(
				"insert into SyllabusTab(syllabusId,title,content,subjectId,year,orderNo)values(?,?,?,?,?,?)",
				new Object[] { info.getId(), info.getTitle(),
						info.getFullTitle(), info.getSubjectId(),
						info.getYear(), info.getOrderNo() });
	}

	private void insertChapter(SQLiteDatabase db, SyllabusInfo info,
			String syllabusId) {
		if (info.getChildren() != null && info.getChildren().size() > 0) {
			List<SyllabusInfo> children = info.getChildren();
			for (SyllabusInfo child : children) {
				insertChapter(db, child, syllabusId);
			}
		}
		if(info.getChildren()==null || info.getChildren().isEmpty())
		{
			try{
				String content = ApiClient.loadKnowledgeContent(appContext, info.getId());
				if(content!=null)
				{
					//获取知识点
					List<KnowledgeInfo> list = GsonUtil.getGson().fromJson(content, new TypeToken<List<KnowledgeInfo>>(){}.getType());
					//插入知识点
					insertChapters(db,list.get(0));
				}
				content = ApiClient.loadSyllabusItems(appContext, info.getId());;
				if(content!=null)
				{
					//获取试题
					List<ItemInfo> items = GsonUtil.getGson().fromJson(content, new TypeToken<List<ItemInfo>>(){}.getType());
					//插入试题
					for(ItemInfo item :items)
					{
						insertSyllabusItem(db,item,syllabusId);
					}
				}
						
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		db.execSQL(
				"insert into ChapterTab(chapterId,syllabusId,chapterPid,title,orderNo)values(?,?,?,?,?)",
				new Object[] { info.getId(), syllabusId, info.getPid(),
						info.getTitle(), info.getOrderNo() });
	}
	
	public void insertChapters(SQLiteDatabase db ,KnowledgeInfo info)
	{
		if(info == null) return;
		//knowledgeId text,title text,content text,chapterId text,subjectId text,orderid integer
		db.execSQL("insert into knowledgeTab(knowledgeId,title,content,chapterId)values(?,?,?,?)", 
				new Object[]{info.getId(),info.getTitle(),info.getDescription(),info.getSyllabusId()});
	}
	
	private static void insertSyllabusItem(SQLiteDatabase db,ItemInfo item,String chapterId)
	{
		Cursor cursor = db.rawQuery("select itemId from ItemTab where itemId = ? ", 
				new String[]{item.getId()});
		String material = DataConverter.getItemMaterial(item);
		//没有考虑 删除关联的情况
		if(cursor.getCount()>0)
		{
			//itemId text,subjectId text,content text,material text,type integer,lasttime
			db.execSQL("update ItemTab set content = ? ,material = ?, lasttime = datetime(?) where itemId = ?", 
					new Object[]{CyptoUtils.encodeContent(item.getId(), item.getContent()),material,item.getLastTime(),item.getId()});
		}else
		{
			//insert
			db.execSQL("insert into ItemTab(itemId,subjectId,content,material,type,lasttime)values(?,?,?,?,?,datetime(?))", 
					new Object[]{item.getId(),item.getSubjectId(),CyptoUtils.encodeContent(item.getId(), item.getContent()),material,item.getType(),item.getLastTime()});
			db.execSQL("insert into ItemsyllabusTab(itemId,chapterId)values(?,?)",
					new Object[]{item.getId(),chapterId});
		}
		cursor.close();
	}
	
	public void clear() {
		SQLiteDatabase db =dbManager.openDatabase();
		db.execSQL("delete from ProductTab");
		db.execSQL("delete from SubjectTab");
		db.execSQL("delete from PaperTab");
		db.execSQL("delete from SyllabusTab");
		db.execSQL("delete from ChapterTab");
		db.execSQL("delete from KnowledgeTab");
		db.execSQL("delete from ItemTab");
		db.execSQL("delete from ItemsyllabusTab");
		db.close();
	}
}
