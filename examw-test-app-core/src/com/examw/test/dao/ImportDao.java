package com.examw.test.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.examw.test.app.AppContext;
import com.examw.test.db.NewImportDBManager;
import com.examw.test.domain.Subject;
import com.examw.test.model.KnowledgeInfo;
import com.examw.test.model.SyllabusInfo;
import com.examw.test.model.sync.ExamSync;
import com.examw.test.model.sync.PaperSync;
import com.examw.test.model.sync.SubjectSync;
import com.examw.test.utils.StringUtils;

/**
 * 导入工具
 * 
 * @author fengwei.
 * @since 2014年12月28日 下午2:37:27.
 */
public class ImportDao {
	NewImportDBManager dbManager = new NewImportDBManager();
	AppContext appContext;
	public ImportDao(AppContext appContext) {
		this.appContext = appContext;
	}
	/**
	 * 插入考试和科目
	 */
	public void insertExamSubjects(ExamSync result)
	{
//		if(result == null) return;
//		Set<SubjectSync> subjects = result.getSubjects();
//		if(subjects == null || subjects.size()==0) return;
//		//LogUtil.d("插入考试");
//		SQLiteDatabase db =  dbManager.openDatabase();
//		db.beginTransaction();
//		db.execSQL("update tbl_exams set status = 0");
//		db.execSQL("update tbl_subjects set status = 0");
//		//查询考试
//		Cursor cursor = db.rawQuery("select name from tbl_exams where code = ?",new String[]{result.getCode()});
//		String name = "";
//		if(cursor.moveToNext())
//		{
//			name = cursor.getString(0);
//		}
//		cursor.close();
//		//没有考试插入
//		if(StringUtils.isEmpty(name))
//		{
//			db.execSQL("insert into tbl_exams(code,name,abbr,status)values(?,?,?,1)", 
//					new Object[]{result.getCode(),result.getName(),result.getAbbr()});
//			//插入考试
//			for(SubjectSync s:subjects)
//			{
//				db.execSQL("insert into tbl_subjects(code,name,status,examCode)values(?,?,1,?)", new Object[]{s.getCode(),s.getName(),result.getCode()});
//			}
//		}else
//		{
//			//修改状态
//			db.execSQL("update tbl_exams set name = ?,abbr = ?,status = 1 where code = ?",
//					new Object[]{result.getCode()});
//			//修改科目
//			for(SubjectSync s:subjects)
//			{
//				Cursor cursor1 = db.rawQuery("select name from tbl_subjects where code = ?",new String[]{s.getCode()});
//				String name1 = "";
//				if(cursor.moveToNext())
//				{
//					name1 = cursor.getString(0);
//				}
//				cursor1.close();
//				if(StringUtils.isEmpty(name1))
//				{
//					db.execSQL("insert into tbl_subjects(code,name,status,examCode)values(?,?,1,?)", 
//							new Object[]{s.getCode(),s.getName(),result.getCode()});
//				}else
//					db.execSQL("update tbl_subjects set name = ?,examCode = ?,status = 1 where code = ?", 
//							new Object[]{s.getName(),result.getCode(),s.getCode()});
//			}
//		}
//		db.setTransactionSuccessful();
//		db.endTransaction();
//		db.close();
	}
	/**
	 * 插入试卷的集合
	 * @param list
	 * @return 返回更新的数量
	 */
	public int insertPaperList(ArrayList<PaperSync> list) {
		int count = 0;
		if (list != null && list.size() > 0) {
			SQLiteDatabase db = dbManager.openDatabase();
			String sql1 = "select * from tbl_papers where id = ?";
			String sql2 = "insert into tbl_papers(id,title,type,total,content,subjectCode,createTime)values(?,?,?,?,?,?,?)";
			db.beginTransaction();
			try {
				for (PaperSync paper : list) {
					Cursor cursor = db.rawQuery(sql1,
							new String[] { paper.getId() });
					if (cursor.getCount() > 0) {
						cursor.close();
						continue;
					}
					cursor.close();
					Object[] params = new Object[] { paper.getId(),
							paper.getTitle(), paper.getType(),
							paper.getTotal(),
							//加密试卷数据
							//CryptoUtils.encrypto(paper.getId(), paper.getContent()),
							paper.getSubjectCode(), paper.getCreateTime()
							};
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
	 * @return
	 */
	public void updatePaperContent(String paperId, String content) {
		//LogUtil.d( String.format("插入试卷[PaperId= %s]的内容", paperId));
		if (StringUtils.isEmpty(content) || StringUtils.isEmpty(paperId))
			return;
		SQLiteDatabase db = dbManager.openDatabase();
//		PaperPreview paper = GsonUtil.jsonToBean(content, PaperPreview.class);
		//String ruleContent = getRuleList(paper);
		//content = CyptoUtils.encodeContent(paperId, content);
		db.execSQL(
				"update tbl_papers set content = ? where paperid = ?",
				new Object[] { content, paperId });
		db.close();
	}

//	// 获取大题结构
//	private String getRuleList(PaperPreview paper) {
//		List<StructureInfo> rules = paper.getStructures();
//		if (rules == null)
//			return "";
//		clearItems(rules);
//		return GsonUtil.objectToJson(paper);
//	}

//	private void clearItems(List<StructureInfo> rules) {
//		for (StructureInfo info : rules) {
//			if (info == null)
//				continue;
//			if (info.getChildren() != null && info.getChildren().size() > 0) {
//				clearItems(info.getChildren());
//			}
//			info.setItems(null);
//		}
//	}

	public void insertSyllabusAndLoadChapters(Subject subject, String content) {
		if (StringUtils.isEmpty(content) || content.equals("[]"))
			return;
		//LogUtil.d( "插入考试大纲,并且获取章节信息");
		//SQLiteDatabase db = dbManager.openDatabase();
		SyllabusInfo syllabus = new SyllabusInfo();
		syllabus.setId(UUID.randomUUID().toString());
		syllabus.setTitle(String.format("《%s》考试大纲", subject.getName()));
		syllabus.setFullTitle(content);
		syllabus.setSubjectId(subject.getSubjectId());
		syllabus.setYear(Calendar.getInstance().get(Calendar.YEAR));
		syllabus.setOrderNo(1);
//		ArrayList<SyllabusInfo> list = GsonUtil.getGson().fromJson(content,
//				new TypeToken<ArrayList<SyllabusInfo>>() {
//				}.getType());
//		try {
//			db.beginTransaction();
//			insertSyllabus(db, syllabus);
//			//LogUtil.d( "插入考试大纲章节信息");
//			for (SyllabusInfo info : list) {
//				insertChapter(db, info, syllabus.getId());
//			}
//			db.setTransactionSuccessful();
//			db.endTransaction();
//			return;
//		} finally {
//			db.close();
//		}
	}

//	// 插入[避免重复插入]
//	private void insertSyllabus(SQLiteDatabase db, SyllabusInfo info) {
//		Cursor cursor = db.rawQuery(
//				"select * from SyllabusTab where syllabusId = ?",
//				new String[] { info.getId() });
//		if (cursor.getCount() > 0) {
//			cursor.close();
//			return;
//		}
//		cursor.close();
//		// syllabusId ,title,content ,subjectId ,year ,orderNo
//		db.execSQL(
//				"insert into SyllabusTab(syllabusId,title,content,subjectId,year,orderNo)values(?,?,?,?,?,?)",
//				new Object[] { info.getId(), info.getTitle(),
//						info.getFullTitle(), info.getSubjectId(),
//						info.getYear(), info.getOrderNo() });
//	}

//	private void insertChapter(SQLiteDatabase db, SyllabusInfo info,
//			String syllabusId) {
//		if (info.getChildren() != null && info.getChildren().size() > 0) {
//			List<SyllabusInfo> children = info.getChildren();
//			for (SyllabusInfo child : children) {
//				insertChapter(db, child, syllabusId);
//			}
//		}
//		if(info.getChildren()==null || info.getChildren().isEmpty())
//		{
//			try{
////				String content = ApiClient.loadKnowledgeContent(appContext, info.getId());
////				if(content!=null)
////				{
////					//获取知识点
////					List<KnowledgeInfo> list = GsonUtil.getGson().fromJson(content, new TypeToken<List<KnowledgeInfo>>(){}.getType());
////					//插入知识点
////					insertChapters(db,list.get(0));
////				}
////				content = ApiClient.loadSyllabusItems(appContext, info.getId());;
////				if(content!=null)
////				{
////					//获取试题
////					List<ItemInfo> items = GsonUtil.getGson().fromJson(content, new TypeToken<List<ItemInfo>>(){}.getType());
////					//插入试题
////					for(ItemInfo item :items)
////					{
////						insertSyllabusItem(db,item,syllabusId);
////					}
////				}
//						
//			}catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		db.execSQL(
//				"insert into ChapterTab(chapterId,syllabusId,chapterPid,title,orderNo)values(?,?,?,?,?)",
//				new Object[] { info.getId(), syllabusId, info.getPid(),
//						info.getTitle(), info.getOrderNo() });
//	}
	
	public void insertChapters(SQLiteDatabase db ,KnowledgeInfo info)
	{
		if(info == null) return;
		//knowledgeId text,title text,content text,chapterId text,subjectId text,orderid integer
		db.execSQL("insert into knowledgeTab(knowledgeId,title,content,chapterId)values(?,?,?,?)", 
				new Object[]{info.getId(),info.getTitle(),info.getDescription(),info.getSyllabusId()});
	}
	
//	private static void insertSyllabusItem(SQLiteDatabase db,ItemInfo item,String chapterId)
//	{
//		Cursor cursor = db.rawQuery("select itemId from ItemTab where itemId = ? ", 
//				new String[]{item.getId()});
//		String material = DataConverter.getItemMaterial(item);
//		//没有考虑 删除关联的情况
////		if(cursor.getCount()>0)
////		{
////			//itemId text,subjectId text,content text,material text,type integer,lasttime
////			db.execSQL("update ItemTab set content = ? ,material = ?, lasttime = datetime(?) where itemId = ?", 
////					new Object[]{CyptoUtils.encodeContent(item.getId(), item.getContent()),material,item.getLastTime(),item.getId()});
////		}else
////		{
////			//insert
////			db.execSQL("insert into ItemTab(itemId,subjectId,content,material,type,lasttime)values(?,?,?,?,?,datetime(?))", 
////					new Object[]{item.getId(),item.getSubjectId(),CyptoUtils.encodeContent(item.getId(), item.getContent()),material,item.getType(),item.getLastTime()});
////			db.execSQL("insert into ItemsyllabusTab(itemId,chapterId)values(?,?)",
////					new Object[]{item.getId(),chapterId});
////		}
//		cursor.close();
//	}
	
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
