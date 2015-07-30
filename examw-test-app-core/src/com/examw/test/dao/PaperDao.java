package com.examw.test.dao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.app.AppConstant;
import com.examw.test.db.DbHelpers;
import com.examw.test.model.PaperItemModel;
import com.examw.test.model.PaperModel;
import com.examw.test.model.PaperRecordModel;
import com.examw.test.model.sync.SubjectSync;
import com.examw.test.utils.PaperUtils;
import com.google.gson.Gson;

/**
 * 试卷数据Dao
 * 
 * @author jeasonyoung
 * @since 2015年7月1日
 */
public class PaperDao {
	private static final String TAG = "PaperDao";
	private Context context;
	private DbHelpers dbHelpers;
	private static final Map<String, PaperModel> PapersCache = new HashMap<String, PaperModel>();
	/**
	 * 构造函数。
	 * @param context
	 */
	public PaperDao(Context context){
		Log.d(TAG, "初始化...");
		if(context == null){
			Log.d(TAG, "上下文为空!");
			throw  new IllegalArgumentException("context");
		}
		this.context = context;
	}
	//获取数据操作工具对象。
	private DbHelpers getDbHelpers(){
		//惰性初始化数据操作工具
		if(this.dbHelpers == null){
			Log.d(TAG, "初始化");
			this.dbHelpers = new DbHelpers(this.context);
		}
		return this.dbHelpers;
	}
	//获取试题ID。
	private String getItemId(PaperItemModel model){
		if(model != null){
			return model.getId() + "$" + model.getIndex();
		}
		return null;
	}
	/**
	 * 统计科目试卷。
	 * @return
	 */
	public List<SubjectTotalModel> totalSubjects(){
		Log.d(TAG, "统计科目试卷...");
		List<SubjectTotalModel> list = new ArrayList<PaperDao.SubjectTotalModel>();
		SQLiteDatabase db = null;
		try {
			final String sql = "SELECT a.code,a.name,COUNT(b.id) AS total FROM tbl_subjects a LEFT OUTER JOIN tbl_papers b ON b.subjectCode = a.code WHERE a.status = 1 GROUP BY a.code,a.name";
			db =  this.getDbHelpers().getWritableDatabase();
			Log.d(TAG, "exec-sql:" + sql);
			Cursor cursor =  db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				//初始化数据模型
				SubjectTotalModel data = new SubjectTotalModel();
				data.setCode(cursor.getString(0));
				data.setName(cursor.getString(1));
				data.setTotal(cursor.getInt(2));
				list.add(data);
			}
			cursor.close();
		} catch (Exception e) {
			 Log.e(TAG, "统计科目试卷发生异常:" + e.getMessage(), e);
		}finally{
			if(db != null){
				db.close();
			}
		}
		return list;
	}
	/**
	 * 加载科目下试卷类型集合。
	 * @param subjectCode
	 * 科目代码。
	 * @return
	 * 试卷类型集合。
	 */
	public List<PaperModel.PaperType> findPaperTypes(String subjectCode){
		Log.d(TAG, "加载科目["+ subjectCode +"]下试卷类型集合...");
		List<PaperModel.PaperType> list = new ArrayList<PaperModel.PaperType>();
		SQLiteDatabase db = null;
		try {
			final String sql = "select type from tbl_papers where subjectCode = ? group by type order by type desc";
			db = this.getDbHelpers().getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, new String[]{ subjectCode });
			while(cursor.moveToNext()){
				PaperModel.PaperType type = PaperModel.PaperType.values()[cursor.getInt(0) - 1];
				if(type != null){
					list.add(type);
				}
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "加载试卷类型异常:" + e.getMessage(), e);
		}finally{
			if(db != null) db.close();
		}
		return list;
	}
	/**
	 * 按科目和试卷类型分页查询试卷信息数据。
	 * @param subjectCode
	 * 科目代码。
	 * @param type
	 * 试卷类型。
	 * @param pageIndex
	 * 页码。
	 * @return
	 * 试卷信息集合。
	 */
	public List<PaperInfoModel> findPaperInfos(String subjectCode, PaperModel.PaperType type, int pageIndex){
		Log.d(TAG, "按科目代码["+ subjectCode +"]试卷类型["+ type+"]查询页码[" + pageIndex +"]查询试卷信息数据...");
		List<PaperInfoModel> list = new ArrayList<PaperDao.PaperInfoModel>();
		SQLiteDatabase db = null;
		try {
			pageIndex = Math.max(pageIndex, 0);
			//1.拼装查询字符串
			StringBuilder sqlBuilder = new StringBuilder()
			.append(" SELECT a.id, a.title, a.total, a.createTime, b.name AS subjectName FROM tbl_papers a ")
			.append(" LEFT OUTER JOIN tbl_subjects b ON b.code = a.subjectCode ")
			.append(String.format(" WHERE a.type = ? and a.subjectCode = ? order by a.createTime desc limit %1$d,%2$d;",
					pageIndex * AppConstant.PAGEOFROWS, AppConstant.PAGEOFROWS));
			//2.创建数据查询对象
			db = this.getDbHelpers().getReadableDatabase();
			//3.查询数据
			Log.d(TAG, "exec - " + sqlBuilder);
			Cursor cursor = db.rawQuery(sqlBuilder.toString(), new String[]{  String.valueOf(type.getValue()), subjectCode });
			while(cursor.moveToNext()){
				PaperInfoModel data = new PaperInfoModel();
				//1.试卷ID
				data.setId(cursor.getString(0));
				//2.试卷标题
				data.setTitle(cursor.getString(1));
				//3.试题数
				data.setTotal(cursor.getInt(2));
				//4.创建时间
				data.setCreateTime(cursor.getString(3));
				//5.所属科目名称
				data.setSubjectName(cursor.getString(4));
				//
				list.add(data);
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "查询试卷信息异常:" + e.getMessage(), e);
		}finally{
			if(db != null) db.close();
		}
		return list;
	}
	/**
	 * 加载试卷数据。
	 * @param paperId
	 * @return
	 */
	public PaperModel loadPaper(String paperId){
		Log.d(TAG, "加载试卷["+paperId+"]数据...");
		if(StringUtils.isNotBlank(paperId)){
			//从缓存中加载数据
			PaperModel model = PapersCache.get(paperId);
			//缓存中数据不存在
			if(model == null){ 
				SQLiteDatabase db = null;
				try {
					final String sql = "SELECT content FROM tbl_papers WHERE id = ? limit 0,1";
					db = this.getDbHelpers().getReadableDatabase();
					String hex = null;
					Cursor cursor = db.rawQuery(sql, new String[]{ paperId });
					while (cursor.moveToNext()) {
						hex = cursor.getString(0);
						break;
					}
					cursor.close();
					//1.检查密文试卷数据
					if(StringUtils.isNotBlank(hex)){
						//2.解密试卷数据
						String json = PaperUtils.decryptContent(hex, paperId);
						//3.JSON反序列化
						model = PaperModel.fromJSON(json);
						//4.放入缓存
						if(model != null){
							PapersCache.put(paperId, model);
						}
					}
				} catch (Exception e) {
					Log.e(TAG, "加载试卷["+paperId+"]数据异常:" + e.getMessage(), e);
				} finally{
					if(db != null) db.close();
				}
			}
			return model;
		}
		return null;
	}
	/**
	 * 加载最新的试卷记录。
	 * @param paperId
	 * 试卷ID。
	 * @return
	 * 试卷记录。
	 */
	public PaperRecordModel loadNewsRecord(String paperId){
		Log.d(TAG, "加载试卷["+paperId+"]最新的记录...");
		PaperRecordModel model = null;
		if(StringUtils.isBlank(paperId)){
			Log.d(TAG, "试卷ID为空!");
			return model;
		}
		SQLiteDatabase db = null;
		try {
			StringBuilder sqlBuilder = new StringBuilder()
			.append(" SELECT a.id,a.status,a.score,a.rights,a.useTimes,b.title ")
			.append(" FROM tbl_paperRecords a ")
			.append(" LEFT OUTER JOIN tbl_papers b  on b.id = a.paperId ")
			.append(" WHERE a.paperId = ? ORDER BY a.lastTime DESC,a.createTime DESC limit 0,1 ");
			
			db = this.getDbHelpers().getReadableDatabase();
			Cursor cursor = db.rawQuery(sqlBuilder.toString(), new String[]{ paperId });
			while(cursor.moveToNext()){
				//0.初始化
				model = new PaperRecordModel();
				//1.记录ID
				model.setId(cursor.getString(0));
				//2.状态
				model.setStatus(cursor.getInt(1) == 1);
				//3.得分
				model.setScore(cursor.getFloat(2));
				//4.做对题数
				model.setRights(cursor.getInt(3));
				//5.用时(秒)
				model.setUseTimes(cursor.getInt(4));
				//6.所属试卷名称
				model.setPaperName(cursor.getString(5));
				//7.所属试卷ID
				model.setPaperId(paperId);
				
				break;
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "加载试卷["+paperId+"]的最新记录异常:" + e.getMessage(), e);
		}finally{
			if(db != null)db.close();
		}
		return model;
	}
	/**
	 * 加载试卷记录。
	 * @param paperRecordId
	 * 试卷记录ID。
	 * @return
	 * 试卷记录。
	 */
	public PaperRecordModel loadPaperRecord(String paperRecordId){
		Log.d(TAG, "加载试卷记录..." + paperRecordId);
		PaperRecordModel model = null;
		if(StringUtils.isBlank(paperRecordId)){
			Log.d(TAG, "试卷记录ID不存在!");
			return model;
		}
		SQLiteDatabase db = null;
		try {
			//创建SQL
			StringBuilder sqlBuilder = new StringBuilder()
			.append(" SELECT a.id,a.status,a.score,a.rights,a.useTimes,a.paperId,b.title ")
			.append(" FROM tbl_paperRecords a ")
			.append(" LEFT OUTER JOIN tbl_papers b  on b.id = a.paperId ")
			.append(" WHERE a.id = ? ORDER BY a.lastTime DESC,a.createTime DESC limit 0,1 ");
			//初始化
			db = this.getDbHelpers().getReadableDatabase();
			Cursor cursor = db.rawQuery(sqlBuilder.toString(), new String[]{ paperRecordId});
			while(cursor.moveToNext()){
				//0.初始化
				model = new PaperRecordModel();
				//1.记录ID
				model.setId(cursor.getString(0));
				//2.状态
				model.setStatus(cursor.getInt(1) == 1);
				//3.得分
				model.setScore(cursor.getFloat(2));
				//4.做对题数
				model.setRights(cursor.getInt(3));
				//5.用时(秒)
				model.setUseTimes(cursor.getInt(4));
				//6.所属试卷ID
				model.setPaperId(cursor.getString(5));
				//7.所属试卷名称
				model.setPaperName(cursor.getString(6));
				break;
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "加载试卷记录异常:" + e.getMessage(), e);
		}finally{
			if(db != null)db.close();
		}
		return model;
	}
	/**
	 * 加载科目下的试卷记录集合。
	 * @param subjectCode
	 * 科目代码。
	 * @param pageIndex
	 * 页码
	 * @return
	 * 试卷记录集合。
	 */
	public List<PaperRecordModel> loadPaperRecords(String subjectCode, int pageIndex){
		Log.d(TAG, "查询科目["+ subjectCode+"]下页码["+pageIndex+"]下试卷记录...");
		List<PaperRecordModel> list = new ArrayList<PaperRecordModel>();
		SQLiteDatabase db = null;
		try {
			//初始化SQL
			StringBuilder sqlBuilder = new StringBuilder()
			.append(" SELECT a.id,a.paperId,b.title,a.status,a.score,a.rights,a.useTimes,a.lastTime ")
			.append(" FROM tbl_paperRecords a ")
			.append(" LEFT OUTER JOIN tbl_papers b ON b.id = a.paperId ")
			.append(" WHERE b.subjectCode = ? ")
			.append(String.format(" ORDER BY a.lastTime DESC LIMIT %1$d,%2$d ", 
					pageIndex * AppConstant.PAGEOFROWS, AppConstant.PAGEOFROWS));
			//初始化数据查询
			db = this.getDbHelpers().getReadableDatabase();
			Cursor cursor = db.rawQuery(sqlBuilder.toString(), new String[]{ subjectCode });
			while(cursor.moveToNext()){
				//0.初始化
				PaperRecordModel model = new PaperRecordModel();
				//1.试卷记录ID
				model.setId(cursor.getString(0));
				//2.所属试卷ID
				model.setPaperId(cursor.getString(1));
				//3.所属试卷名称
				model.setPaperName(cursor.getString(2));
				//4.状态
				model.setStatus(cursor.getInt(3) == 1);
				//5.得分
				model.setScore(cursor.getFloat(4));
				//6.做对题数
				model.setRights(cursor.getInt(5));
				//7.用时(秒)
				model.setUseTimes(cursor.getInt(6));
				//8.最后时间
				model.setLastTime(cursor.getString(7));
				//
				list.add(model);
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "查询科目["+ subjectCode+"]下页码["+pageIndex+"]下试卷记录异常:" + e.getMessage(), e);
		}finally{
			if(db != null)db.close();
		}
		return list;
	}
	/**
	 * 更新试卷记录。
	 * @param model
	 */
	public void updatePaperRecord(PaperRecordModel model){
		Log.d(TAG, "更新试卷记录...");
		if(model == null || StringUtils.isBlank(model.getPaperId())){
			Log.d(TAG, "试卷记录或试卷记录ID为空!");
			return;
		}
		SQLiteDatabase db = null;
		try {
			//初始化数据操作
			db = this.getDbHelpers().getWritableDatabase();
			//判断是否新增
			boolean isAdded = StringUtils.isBlank(model.getId());
			if(!isAdded){
				//查询试卷记录是否存在
				Cursor cursor = db.rawQuery("SELECT count(*) FROM tbl_paperRecords WHERE id = ?", new String[]{model.getId()});
				while(cursor.moveToNext()){
					isAdded = (cursor.getInt(0) == 0);
					break;
				}
				cursor.close();
			}
			final String tableName = "tbl_paperRecords";
			//新增处理
			if(isAdded){
				//新建试卷记录ID
				if(StringUtils.isBlank(model.getId())){
					model.setId(UUID.randomUUID().toString());
				}
				ContentValues values = new ContentValues();
				//1.试卷记录ID
				values.put("id", model.getId());
				//2.所属试卷ID
				values.put("paperId", model.getPaperId());
				//3.状态
				values.put("status", model.isStatus());
				//4.分数
				values.put("score", model.getScore());
				//5.做对题数
				values.put("rights", model.getRights());
				//6.用时(秒)
				values.put("useTimes", model.getUseTimes());
				//
			    long result = db.insert(tableName, null, values);
				Log.d(TAG, "新增试卷记录:" + (result > 0));
			}else {
				//更新处理
				ContentValues values = new ContentValues();
				//1.状态
				values.put("status", model.isStatus());
				//2.分数
				values.put("score", model.getScore());
				//3.做对题数
				values.put("rights", model.getRights());
				//4.用时(秒)
				values.put("useTimes", model.getUseTimes());
				//5.更新时间
				SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				values.put("lastTime", sdFormat.format(new Date()));
				//
				long result = db.update(tableName, values, "id=?", new String[]{ model.getId() });
				Log.d(TAG, "更新试卷记录:" + (result > 0));
			}
		} catch (Exception e) {
			Log.e(TAG, "更新试卷记录:" + e.getMessage(), e);
		}finally{
			if(db != null)db.close();
		}
	}
	/**
	 * 试题是否收藏。
	 * @param itemModel
	 * @return
	 */
	public boolean exitFavorite(final PaperItemModel itemModel){
		boolean result = false;
		if(itemModel != null){
			final String itemId = this.getItemId(itemModel);
			SQLiteDatabase db = null;
			try {
				final String sql = "SELECT count(*) FROM tbl_favorites WHERE itemId = ?";
				db = this.getDbHelpers().getReadableDatabase();
				Cursor cursor = db.rawQuery(sql, new String[]{ itemId });
				while(cursor.moveToNext()){
					result = cursor.getInt(0) > 0;
					break;
				}
				cursor.close();
			} catch (Exception e) {
				Log.e(TAG, "判断试题["+ itemId +"]是否收藏时异常:" + e.getMessage(), e);
			}finally{
				if(db != null)db.close();
			}
		}
		return result;
	}
	/**
	 * 试题在记录中状态。
	 * @param paperRecordId
	 * 试卷记录ID。
	 * @param itemModel
	 * 试题数据模型
	 * @return 
	 * 状态枚举。
	 */
	public ItemStatus exitRecord(String paperRecordId, PaperItemModel itemModel){
		ItemStatus status = ItemStatus.None;
		if(StringUtils.isNotBlank(paperRecordId) && itemModel != null){
			final String itemId = this.getItemId(itemModel);
			SQLiteDatabase db = null;
			try {
				final String sql = "SELECT status FROM tbl_itemRecords WHERE paperRecordId = ? and itemId = ? limit 0,1";
				db = this.getDbHelpers().getReadableDatabase();
				Cursor cursor = db.rawQuery(sql, new String[]{ paperRecordId,  itemId});
				while(cursor.moveToNext()){
					status = ItemStatus.values()[cursor.getInt(0)];
					break;
				}
				cursor.close();
			} catch (Exception e) {
				Log.e(TAG, "查询试题["+itemId+"]在记录["+paperRecordId+"]中状态异常:" + e.getMessage(), e);
			}finally{
				if(db != null) db.close();
			}
		}
		return status;
	}
	/**
	 * 加载试卷记录中最新的试题记录。
	 * @param paperRecordId
	 * 试卷记录ID。
	 * @return
	 * 试题记录ID(试题ID＋"$" +索引号)
	 */
	public String loadNewItemAndIndex(String paperRecordId){
		String itemId = null;
		if(StringUtils.isNotBlank(paperRecordId)){
			SQLiteDatabase db = null;
			try {
				final String sql = " SELECT itemId FROM tbl_itemRecords WHERE paperRecordId = ? ORDER BY lastTime desc,createTime desc limit 0,1 ";
				db = this.getDbHelpers().getReadableDatabase();
				Cursor cursor = db.rawQuery(sql, new String[]{ paperRecordId });
				while(cursor.moveToNext()){
					itemId = cursor.getString(0);
					break;
				}
				cursor.close();
			} catch (Exception e) {
				Log.e(TAG, "加载试卷记录["+paperRecordId+"]中最新的试题记录异常:" + e.getMessage(), e);
			}finally{
				if(db != null)db.close();
			}
		}
		return itemId;
	}
	/**
	 * 加载记录中的试题答案
	 * @param paperRecordId
	 * 所属试卷记录。
	 * @param itemModel
	 * 试题数据模型。
	 * @return
	 * 试题答案。
	 */
	public String loadRecodAnswers(String paperRecordId, PaperItemModel itemModel){
		String answers = null;
		if(StringUtils.isNotBlank(paperRecordId) && itemModel != null){
			final String itemId = this.getItemId(itemModel);
			SQLiteDatabase db = null;
			try {
				final String sql = "SELECT answer FROM tbl_itemRecords WHERE paperRecordId = ? and itemId = ? limit 0,1";
				db = this.dbHelpers.getReadableDatabase();
				Cursor cursor = db.rawQuery(sql, new String[]{ paperRecordId, itemId });
				while(cursor.moveToNext()){
					answers = cursor.getString(0);
					break;
				}
				cursor.close();
			} catch (Exception e) {
				Log.e(TAG, "加载记录["+paperRecordId+"]中的试题["+itemId+"]答案异常:" + e.getMessage(), e);
			}finally{
				if(db != null)db.close();
			}
		}
		return answers;
	}
	/**
	 * 删除试卷记录。
	 * @param paperRecordId
	 * 试卷记录ID。
	 */
	public void deletePaperRecord(String paperRecordId){
		Log.d(TAG, "删除试卷记录:" + paperRecordId);
		if(StringUtils.isNotBlank(paperRecordId)){
			SQLiteDatabase db = null;
			try {
				db = this.getDbHelpers().getWritableDatabase();
				//1.先删除试题记录
				db.delete("tbl_itemRecords", "paperRecordId = ?", new String[]{ paperRecordId });
				//2.删除试卷记录
				db.delete("tbl_paperRecords", "id = ?", new String[]{ paperRecordId });
			} catch (Exception e) {
				Log.e(TAG, "删除试卷记录["+paperRecordId+"]异常:" + e.getMessage(), e);
			}finally{
				if(db != null)db.close();
			}
		}
	}
	/**
	 * 添加试题记录。
	 * @param paperRecordId
	 * 所属试卷ID。
	 * @param itemModel
	 * 试题数据模型。
	 * @param answers
	 * 试题答案。
	 * @param useTimes
	 * 做题用时。
	 */
	public void addItemRecord(String paperRecordId, PaperItemModel itemModel, String answers, int useTimes){
		if(StringUtils.isNotBlank(paperRecordId) && itemModel != null && StringUtils.isNotBlank(answers)){
			final String itemId = this.getItemId(itemModel);
			SQLiteDatabase db = null;
			try {
				//初始化数据操作
				db = this.getDbHelpers().getWritableDatabase();
				//计算得分
				float score = 0f;
				ItemStatus itemStatus = ItemStatus.None;
				//判断存在标准答案
				if(StringUtils.isNotBlank(itemModel.getAnswer())){
					String[] myAnswers = answers.split(",");
					int count = 0;
					itemStatus = ItemStatus.Right;
					for(String myAnswer : myAnswers){
						if(StringUtils.isBlank(myAnswer)) continue;
						if(itemModel.getAnswer().indexOf(myAnswer) > -1){
							count += 1;
						}else {
							itemStatus = ItemStatus.Wrong;
						}
					}
					//获取得分
					if(itemStatus == ItemStatus.Right){
						score = itemModel.getStructureScore();
					}else if(count > 0){
						score = itemModel.getStructureMin();
					}
				}
				//查询是否存在记录
				String itemRecordId = null;
				final String query_sql = "SELECT id FROM tbl_itemRecords WHERE paperRecordId = ? and itemId = ? limit 0,1";
				Cursor cursor = db.rawQuery(query_sql, new String[]{ paperRecordId, itemId });
				while(cursor.moveToNext()){
					itemRecordId = cursor.getString(0);
					break;
				}
				cursor.close();
				//数据操作
				final String tableName = "tbl_itemRecords";
				if(StringUtils.isNotBlank(itemRecordId)){//更新操作
					ContentValues values = new ContentValues();
					//1.答案
					values.put("answer", answers);
					//2.状态
					values.put("status", itemStatus.getValue());
					//3.得分
					values.put("score", score);
					//4.用时
					values.put("useTimes", useTimes);
					//5.最后时间
					SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
					values.put("lastTime", sdFormat.format(new Date()));
					//6.同步状态
					values.put("sync", 0);
					//执行数据处理
					db.update(tableName, values, " id = ? ", new String[]{ itemRecordId });
				}else {//新增操作
					itemRecordId = UUID.randomUUID().toString();
					ContentValues values = new ContentValues();
					//1.试题记录ID
					values.put("id", itemRecordId);
					//2.所属试卷记录ID
					values.put("paperRecordId", paperRecordId);
					//3.所属结构ID
					values.put("structureId", itemModel.getStructureId());
					//4.试题ID
					values.put("itemId", itemId);
					//5.试题类型
					values.put("itemType", itemModel.getType());
					//6.试题内容
					values.put("content", PaperUtils.encryptContent(itemModel.toString(), itemRecordId));
					//7.答案
					values.put("answer", answers);
					//8.状态
					values.put("status", itemStatus.getValue());
					//9.得分
					values.put("score", score);
					//10.用时
					values.put("useTimes", useTimes);
					//执行数据处理
					db.insert(tableName, null, values);
				}
			} catch (Exception e) {
				Log.e(TAG, "添加试题["+itemId+"]记录["+paperRecordId+"]异常:" + e.getMessage(), e);
			}finally{
				if(db != null)db.close();
			}	
		}
	}
	//收藏/取消(收藏返回true,取消返回false)
	private boolean updateFavorite(PaperItemModel itemModel, UpdateFavoriteListener handler){
		boolean result = false;
		if(itemModel != null){
			//试题ID
			final String itemId = this.getItemId(itemModel);
			SQLiteDatabase db = null;
			try {
				String favId = null;
				boolean status = false;
				//查询是否收藏过
				final String query_sql = "SELECT id,status FROM tbl_favorites WHERE itemId = ? limit 0,1";
				db = this.getDbHelpers().getWritableDatabase();
				Cursor cursor = db.rawQuery(query_sql, new String[]{ itemId });
				while(cursor.moveToNext()){
					favId = cursor.getString(0);
					status = (cursor.getInt(1) > 0);
					break;
				}
				cursor.close();
				//
				final String tableName = "tbl_favorites";
				if(status){//已收藏（应变为未收藏）
					result = true;
					ContentValues values = new ContentValues();
					values.put("status", 0);
					//数据操作
					db.update(tableName, values, "id = ?", new String[]{ favId });
					result = false;
				}else {//未收藏(应变为已收藏)
					result = false;
					if(StringUtils.isNotBlank(favId)){//未收藏状态，更新未已收藏状态
						ContentValues values = new ContentValues();
						values.put("status", 1);
						db.update(tableName, values, "id = ?", new String[]{ favId });
					}else {//新收藏
						final String subjectCode = (handler != null ? handler.loadSubjectCode(db) : null);
						if(StringUtils.isNotBlank(subjectCode)){
							//试题收藏ID
							favId = UUID.randomUUID().toString();
							ContentValues values = new ContentValues();
							//1.试题收藏ID
							values.put("id", favId);
							//2.所属科目代码
							values.put("subjectCode", subjectCode);
							//3.试题ID
							values.put("itemId", itemId);
							//4.试题类型
							values.put("itemType", itemModel.getType());
							//5.试题内容
							values.put("content", PaperUtils.encryptContent(itemModel.toString(), favId));
							//数据操作
							db.insert(tableName, null, values);
						}else {
							Log.d(TAG, "试题["+itemId+"]所属科目为空!");
							return result;
						}
					}
					result = true;
				}
			} catch (Exception e) {
				Log.e(TAG, "收藏/取消试题["+itemId+"]异常:" + e.getMessage(), e);
			}finally{
				if(db != null) db.close();
			}
		}
		return result;
	}
	/**
	 * 收藏/取消收藏(收藏返回true,取消返回false)
	 * @param subjectCode
	 * 所属科目代码。
	 * @param itemModel
	 * 试题数据模型。
	 * @return
	 * 收藏返回true,取消返回false
	 */
	public boolean updateFavoriteWithSubject(final String subjectCode,final PaperItemModel itemModel){
		return this.updateFavorite(itemModel, new UpdateFavoriteListener() {
			/*
			 * 加载科目代码。
			 * @see com.examw.test.dao.PaperDao.UpdateFavoriteListener#loadSubjectCode(android.database.sqlite.SQLiteDatabase)
			 */
			@Override
			public String loadSubjectCode(SQLiteDatabase db) {
				return subjectCode;
			}
		});
	}
	/**
	 * 收藏/取消收藏(收藏返回true,取消返回false)
	 * @param paperId
	 * 试卷ID。
	 * @param itemModel
	 * 试题数据模型。
	 * @return
	 * 收藏返回true,取消返回false
	 */
	public Boolean updateFavoriteWithPaper(final String paperId, final PaperItemModel itemModel){
		return this.updateFavorite(itemModel, new UpdateFavoriteListener() {
			/*
			 * 加载试题所在科目代码
			 * @see com.examw.test.dao.PaperDao.UpdateFavoriteListener#loadSubjectCode(android.database.sqlite.SQLiteDatabase)
			 */
			@Override
			public String loadSubjectCode(SQLiteDatabase db) {
				final String query_subject_sql = "SELECT subjectCode FROM tbl_papers WHERE id = ?";
				String subjectCode = null;
				Cursor cursor = db.rawQuery(query_subject_sql, new String[]{ paperId });
				while(cursor.moveToNext()){
					subjectCode = cursor.getString(0);
					break;
				}
				cursor.close();
				return subjectCode;
			}
		});
	}
	/**
	 * 交卷。
	 * @param paperRecordId
	 * 试卷记录ID。
	 * @param useTimes
	 * 用时(秒)
	 */
	public void submit(String paperRecordId, int useTimes){
		Log.d(TAG, "开始交卷处理:" + paperRecordId);
		if(StringUtils.isNotBlank(paperRecordId)){
			SQLiteDatabase db = null;
			try {
				//统计得分/用时/正确
				final String total_score_sql = "SELECT SUM(score) AS score,SUM(useTimes) as useTimes,SUM(status) AS rights FROM tbl_itemRecords WHERE paperRecordId = ?";
				float totalScore = 0, totalRights = 0, totalUseTimes = 0;
				db = this.getDbHelpers().getWritableDatabase();
				Cursor cursor = db.rawQuery(total_score_sql, new String[]{paperRecordId});
				while(cursor.moveToNext()){
					//统计得分
					totalScore = cursor.getFloat(0);
					//统计用时
					totalUseTimes = cursor.getInt(1);
					//统计做对题数
					totalRights = cursor.getInt(2);
					break;
				}
				cursor.close();
				//更新试卷记录
				totalUseTimes += useTimes;
				//
				ContentValues values = new ContentValues();
				//1.试题状态
				values.put("status", 1);
				//2.得分
				values.put("score", totalScore);
				//3.用时(秒)
				values.put("useTimes", (int)totalUseTimes);
				//4.做对试题数
				values.put("rights", (int)totalRights);
				//5.最后时间
				SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				values.put("lastTime", sdFormat.format(new Date()));
				//
				db.update("tbl_paperRecords", values, " id = ? ", new String[]{ paperRecordId });
			} catch (Exception e) {
				Log.e(TAG, "交卷["+paperRecordId+"]处理异常:" + e.getMessage(), e);
			}finally{
				if(db != null)db.close();
			}
		}
	}
	/**
	 *  加载试卷记录结果。
	 * @param paperRecordId
	 * @return
	 */
	public PaperResultModel loadPaperRecordResult(String paperRecordId){
		Log.d(TAG, "加载试卷记录[" + paperRecordId + "]结果...");
		PaperResultModel resultModel = null;
		if(StringUtils.isNotBlank(paperRecordId)){
			SQLiteDatabase db = null;
			try {
				db = this.getDbHelpers().getReadableDatabase();
				//1.查询试卷记录信息
				final String query_sql = "SELECT paperId,score,rights,useTimes,createTime,lastTime FROM tbl_paperRecords WHERE id = ? limit 0,1";
				Cursor cursor = db.rawQuery(query_sql, new String[]{ paperRecordId });
				while(cursor.moveToNext()){
					//1.0.初始化
					resultModel = new PaperResultModel();
					//1.1.试卷记录ID
					resultModel.setId(paperRecordId);
					//1.2.所属试卷ID
					resultModel.setPaperId(cursor.getString(0));
					//1.3.得分
					resultModel.setScore(cursor.getFloat(1));
					//1.4.做对试题
					resultModel.setRights(cursor.getInt(2));
					//1.5.用时
					resultModel.setUseTimes(cursor.getInt(3));
					//1.6.开始时间
					resultModel.setCreateTime(cursor.getString(4));
					//1.7.最后时间
					resultModel.setLastTime(cursor.getString(5));
					break;
				}
				cursor.close();
				//加载统计数据
				if(resultModel != null){
					//2.统计错题记录
					final String error_sql = "SELECT count(*) FROM tbl_itemRecords WHERE paperRecordId = ? and status = 0";
					cursor = db.rawQuery(error_sql, new String[]{ paperRecordId });
					while(cursor.moveToNext()){
						resultModel.setErrors(cursor.getInt(0));
						break;
					}
					cursor.close();
					//3.查询总题数
					final String total_sql = "SELECT total FROM tbl_papers WHERE id = ? limit 0,1 ";
					cursor = db.rawQuery(total_sql, new String[]{ resultModel.getPaperId() });
					while(cursor.moveToNext()){
						resultModel.setTotal(cursor.getInt(0));
						break;
					}
					cursor.close();
				}
			} catch (Exception e) {
				Log.e(TAG, "加载试卷记录[" + paperRecordId + "]结果异常:" + e.getMessage(), e);
			}finally{
				if(db != null)db.close();
			}
		}
		return resultModel;
	}
	/**
	 * 按科目统计错题记录集合。
	 * @return
	 */
	public List<SubjectTotalModel> totalSubjectWrongRecords(){
		Log.d(TAG, "按科目统计错题记录集合...");
		List<SubjectTotalModel> list = new ArrayList<PaperDao.SubjectTotalModel>();
		SQLiteDatabase db = null;
		try {
			//初始化
			db = this.getDbHelpers().getReadableDatabase();
			//SQL
			final String sql = "SELECT a.code,a.name,COUNT(d.itemId) AS total FROM tbl_subjects a "
					+ " LEFT OUTER JOIN tbl_papers b ON b.subjectCode = a.code "
					+ " LEFT OUTER JOIN tbl_paperRecords c ON c.paperId = b.id "
					+ " LEFT OUTER JOIN tbl_itemRecords d ON d.paperRecordId = c.id  AND ((d.status IS NULL) OR (d.status = 0)) "
					+ " GROUP BY a.code,a.name";
			Log.d(TAG, "exec-sql:" + sql);
			Cursor cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()){
				//0.初始化
				SubjectTotalModel data = new SubjectTotalModel();
				//1.科目代码
				data.setCode(cursor.getString(0));
				//2.科目名称
				data.setName(cursor.getString(1));
				//3.错题统计
				data.setTotal(cursor.getInt(2));
				//添加到容器
				list.add(data);
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "按科目统计错题记录集合异常:" + e.getMessage(), e);
		}finally{
			if(db != null)db.close();
		}
		return list;
	}
	/**
	 * 按科目统计收藏记录集合。
	 * @return
	 */
	public List<SubjectTotalModel> totalFavoriteRecords(){
		Log.d(TAG, "按科目统计收藏记录集合...");
		List<SubjectTotalModel> list = new ArrayList<PaperDao.SubjectTotalModel>();
		SQLiteDatabase db = null;
		try {
			//初始化
			db = this.getDbHelpers().getReadableDatabase();
			//SQL
			final String sql = "SELECT a.code,a.name,COUNT(b.itemId) AS total FROM tbl_subjects a "
					+ " LEFT OUTER JOIN tbl_favorites b "
					+ " ON b.subjectCode = a.code  AND ((b.status IS NULL) OR (b.status = 1)) "
					+ " GROUP BY a.code,a.name";
			//
			Log.d(TAG, "exec-sql:" + sql);
			Cursor cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()){
				//0.初始化
				SubjectTotalModel data = new SubjectTotalModel();
				//1.科目代码
				data.setCode(cursor.getString(0));
				//2.科目名称
				data.setName(cursor.getString(1));
				//3.收藏统计
				data.setTotal(cursor.getInt(2));
				//添加到集合
				list.add(data);
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "按科目统计收藏记录集合异常:" + e.getMessage(), e);
		}finally{
			if(db != null)db.close();
		}
		return list;
	}
	/**
	 * 加载科目下的收藏试题集合。
	 * @param subjectCode
	 * 科目代码。
	 * @return
	 * 试题集合。
	 */
	public List<PaperItemModel> loadFavoriteItems(String subjectCode){
		Log.d(TAG, "加载科目["+subjectCode+"]下的收藏试题集合...");
		List<PaperItemModel> list = new ArrayList<PaperItemModel>();
		if(StringUtils.isNotBlank(subjectCode)){
			SQLiteDatabase db = null;
			try {
				db = this.getDbHelpers().getReadableDatabase();
				final String sql = "SELECT id,content FROM tbl_favorites WHERE status = 1 AND subjectCode = ? ORDER BY itemType,createTime DESC";
				Cursor cursor = db.rawQuery(sql, new String[]{ subjectCode });
				while(cursor.moveToNext()){
					//1.试题收藏ID
					final String favId = cursor.getString(0);
					//2.试题内容密文
					final String hex = cursor.getString(1);
					//3.解密
					final String json = PaperUtils.decryptContent(hex, favId);
					//4.反序列化试题
					PaperItemModel data = PaperItemModel.fromJSON(json);
					if(data != null){
						//添加到集合
						list.add(data);
					}
				}
				cursor.close();
			} catch (Exception e) {
				Log.e(TAG, "加载科目["+subjectCode+"]下的收藏试题集合异常:" + e.getMessage(), e);
			}finally{
				if(db != null)db.close();
			}
		}
		return list;
	}
	/**
	 * 根据科目加载错题集合。
	 * @param subjectCode
	 * @return
	 */
	public List<PaperItemModel> loadWrongItems(String subjectCode){
		Log.d(TAG, "根据科目["+subjectCode+"]加载错题集合...");
		List<PaperItemModel> list = new ArrayList<PaperItemModel>();
		if(StringUtils.isNotBlank(subjectCode)){
			SQLiteDatabase db = null;
			try {
				db = this.getDbHelpers().getReadableDatabase();
				final String sql = "SELECT a.id,a.paperRecordId,a.content FROM tbl_itemRecords a "
						+ "LEFT OUTER JOIN tbl_paperRecords b ON b.id = a.paperRecordId "
						+ "LEFT OUTER JOIN tbl_papers c ON c.id = b.paperId "
						+ "WHERE a.status = 0 AND c.subjectCode = ? ORDER BY a.itemType";
				Cursor cursor = db.rawQuery(sql, new String[]{ subjectCode });
				while(cursor.moveToNext()){
					//1.试题记录ID
					final String itemRecordId = cursor.getString(0);
					//2.试卷记录ID
					final String paperRecordId = cursor.getString(1);
					//3.试题内容密文
					final String hex = cursor.getString(2);
					//3.解密
					final String json = PaperUtils.decryptContent(hex, itemRecordId);
					//4.反序列化试题
					PaperItemModel data = PaperItemModel.fromJSON(json);
					if(data != null){
						data.setPaperRecordId(paperRecordId);
						//添加到集合
						list.add(data);
					}
				}
				cursor.close();
			} catch (Exception e) {
				Log.e(TAG, "根据科目["+subjectCode+"]加载错题集合异常:" + e.getMessage(), e);
			}finally{
				if(db != null) db.close();
			}
		}
		return list;
	}
	/**
	 * 按科目统计试卷记录集合。
	 * @return
	 */
	public List<SubjectTotalModel> totalPaperRecords(){
		Log.d(TAG, "按科目统计试卷记录集合...");
		List<SubjectTotalModel> list = new ArrayList<PaperDao.SubjectTotalModel>();
		SQLiteDatabase db = null;
		try {
			db = this.getDbHelpers().getReadableDatabase();
			final String sql = "SELECT a.code,a.name,COUNT(c.id) AS total FROM tbl_subjects a "
					+ "LEFT OUTER JOIN tbl_papers b ON b.subjectCode = a.code "
					+ "LEFT OUTER JOIN tbl_paperRecords c ON c.paperId = b.id "
					+ "WHERE a.status = 1 GROUP BY a.code,a.name";
			Cursor cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()){
				//0.初始化
				SubjectTotalModel data = new SubjectTotalModel();
				//1.科目代码
				data.setCode(cursor.getString(0));
				//2.科目名称
				data.setName(cursor.getString(1));
				//3.试卷记录统计
				data.setTotal(cursor.getInt(2));
				//添加到集合
				list.add(data);
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, "按科目统计试卷记录集合异常:" + e.getMessage(),	e);
		}finally{
			if(db != null)db.close();
		}
		return list;
	}
	
	/**
	 * 试卷记录结果数据模型
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月2日
	 */
    public class PaperResultModel extends PaperRecordModel{
		private static final long serialVersionUID = 1L;
		private int total,errors;
		private String createTime;
		/**
		 * 获取总题数。
		 * @return 总题数。
		 */
		public int getTotal() {
			return total;
		}
		/**
		 * 设置总题数。
		 * @param total 
		 *	  总题数。
		 */
		public void setTotal(int total) {
			this.total = total;
		}
		/**
		 * 获取做错题数。
		 * @return 做错题数。
		 */
		public int getErrors() {
			return errors;
		}
		/**
		 * 设置做错题数。
		 * @param errors 
		 *	  做错题数。
		 */
		public void setErrors(int errors) {
			this.errors = errors;
		}
		/**
		 * 获取未做题数。
		 * @return 未做题数。
		 */
		public int getNots() {
			return this.total - this.errors - this.getRights();
		}
		/**
		 * 获取开始时间。
		 * @return 开始时间。
		 */
		public String getCreateTime() {
			return createTime;
		}
		/**
		 * 设置开始时间。
		 * @param createTime 
		 *	  开始时间。
		 */
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
	}
	//更新收藏监听类
	private interface UpdateFavoriteListener{
		String loadSubjectCode(SQLiteDatabase db);
	}
	/**
	 * 试题状态枚举。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月2日
	 */
	public enum ItemStatus{
		/**
		 * 未做。
		 */
		None(0),
		/**
		 * 做对。
		 */
		Right(1),
		/**
		 * 做错。
		 */
		Wrong(2);
		private int value;
		private ItemStatus(int value){
			this.value = value;
		}
		/**
		 * 获取枚举值。
		 * @return 枚举值。
		 */
		public int getValue() {
			return value;
		}
	}
	/**
	 * 试卷信息。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月2日
	 */
	public class PaperInfoModel implements Serializable{
		private static final long serialVersionUID = 1L;
		private String id,title,subjectName,createTime;
		private int total;
		/**
		 * 获取试卷ID。
		 * @return 试卷ID。
		 */
		public String getId() {
			return id;
		}
		/**
		 * 设置试卷ID。
		 * @param id 
		 *	  试卷ID。
		 */
		public void setId(String id) {
			this.id = id;
		}
		/**
		 * 获取试卷名称。
		 * @return 试卷名称。
		 */
		public String getTitle() {
			return title;
		}
		/**
		 * 设置试卷名称。
		 * @param title 
		 *	  试卷名称。
		 */
		public void setTitle(String title) {
			this.title = title;
		}
		/**
		 * 获取试题数。
		 * @return 试题数。
		 */
		public int getTotal() {
			return total;
		}
		/**
		 * 设置试题数。
		 * @param total 
		 *	  试题数。
		 */
		public void setTotal(int total) {
			this.total = total;
		}
		/**
		 * 获取所属科目名称。
		 * @return 所属科目名称。
		 */
		public String getSubjectName() {
			return subjectName;
		}
		/**
		 * 设置所属科目名称。
		 * @param subjectName 
		 *	  所属科目名称。
		 */
		public void setSubjectName(String subjectName) {
			this.subjectName = subjectName;
		}
		/**
		 * 获取发布时间。
		 * @return 发布时间。
		 */
		public String getCreateTime() {
			return createTime;
		}
		/**
		 * 设置发布时间。
		 * @param createTime 
		 *	  发布时间。
		 */
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		/*
		 * 重载
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			 Gson gson = new Gson();
			 return gson.toJson(this);
		}
	}
	/**
	 * 科目试卷统计。
	 * @author jeasonyoung
	 * @since 2015年7月1日
	 */
	public class SubjectTotalModel extends SubjectSync{
		private static final long serialVersionUID = 1L;
		private int total;
		/**
		 * 获取试卷统计。
		 * @return 试卷统计。
		 */
		public int getTotal() {
			return total;
		}
		/**
		 * 设置试卷统计。
		 * @param total 
		 *	  试卷统计。
		 */
		public void setTotal(int total) {
			this.total = total;
		}
		/*
		 * 重载。
		 * @see com.examw.test.model.sync.SubjectSync#toString()
		 */
		@Override
		public String toString() {
			Gson gson = new Gson();
			return gson.toJson(this);
		}
	}
}