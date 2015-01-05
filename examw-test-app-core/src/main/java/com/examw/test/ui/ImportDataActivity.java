package com.examw.test.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.ImportDao;
import com.examw.test.domain.Chapter;
import com.examw.test.domain.Subject;
import com.examw.test.exception.AppException;
import com.examw.test.model.FrontPaperInfo;
import com.examw.test.model.FrontProductInfo;
import com.examw.test.support.ApiClient;
import com.examw.test.support.URLs;
import com.examw.test.util.StringUtils;

/**
 * 导入数据界面(不在用户界面中显示)
 * @author fengwei
 *
 */
public class ImportDataActivity extends BaseActivity implements OnClickListener {
	private AppContext appContext;
	private ProgressDialog proDialog;
	private Handler handler;
	private ArrayList<Subject> subjects;
	private ImportDao dao;
//	private boolean productFlag,paperFlag,syllabusFlag,allFlag;
//	//SD卡中数据库压缩文件保存目录  /mnt/sdcard/kuaiji/zipfiles/
	private static final String dataDir = Environment
			.getExternalStorageDirectory().getPath()
			+ File.separator
			+ "examw" + File.separator + "image" + File.separator;
//	//SD卡中数据库文件保存目录  /mnt/sdcard/kuaiji/database/
//	private static final String dbBaseDir = Environment
//			.getExternalStorageDirectory().getPath()
//			+ File.separator
//			+ "examw" + File.separator + "database" + File.separator;
//	//SD卡中数据更新文件保存目录  /mnt/sdcard/kuaiji/updateData/
//	private static final String UPDATAPATH = Environment
//			.getExternalStorageDirectory().getPath()
//			+ File.separator+"CHAccountant"+File.separator+"kuaiji"+File.separator+"updateData"+File.separator;
//	//SD卡中数据更新XML保存目录  /mnt/sdcard/kuaiji/updateXml/
//	private static final String XMLPATH = Environment
//			.getExternalStorageDirectory().getPath()
//			+ File.separator+"CHAccountant"+File.separator+"kuaiji"+File.separator+"updateXml"+File.separator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_import_data);
		this.findViewById(R.id.cleanData).setOnClickListener(this);
		this.findViewById(R.id.importProduct).setOnClickListener(this);
		this.findViewById(R.id.importPaper).setOnClickListener(this);
		this.findViewById(R.id.importSyllabus).setOnClickListener(this);
		this.findViewById(R.id.importAll).setOnClickListener(this);
		appContext = (AppContext) getApplication();
		dao = new ImportDao(appContext);
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (proDialog != null) {
					proDialog.dismiss();
				}
				switch (msg.what) {
				case 1:
					print("清除数据成功");
					break;
				case -1:
					print("清除数据失败");
					break;
				case 2:
					print("导入产品数据成功");
					break;
				case -2:
					print("导入产品数据失败");
					break;
				case 3:
					print("导入题目数据成功");
					break;
				case -3:
					print("导入题目数据失败");
					break;
				case 4:
					print("导入大纲数据成功");
					break;
				case -4:
					print("导入大纲数据失败");
					break;
				case 5:
					print("导入全部数据成功");
					break;
				case -5:
					print("导入全部数据失败");
					break;
				}
			};
		};
	}

	private void print(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cleanData:
			cleanData();
			break;
		case R.id.importProduct:
			importProduct();
			break;
		case R.id.importPaper:
			importPaper();
			break;
		case R.id.importSyllabus:
			importSyllabus();
			break;
		case R.id.importAll:
			importAllData();
			break;
		}
	}
	private void cleanData() {
		if (proDialog == null) {
			proDialog = ProgressDialog.show(this, null, "导入数据中请稍候...", true,
					true);
			proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		} else {
			proDialog.show();
		}
		new Thread() {
			public void run() {
				try {
					dao.clear();
					//删除图片文件夹
					new File(dataDir).delete();
					handler.sendEmptyMessage(1);
				}catch(Exception e)
				{
					e.printStackTrace();
					handler.sendEmptyMessage(-1);
				}
			}
		}.start();
	}

	//导入产品数据存于内部存储中
	private void importProduct() {
		if (proDialog == null) {
			proDialog = ProgressDialog.show(this, null, "导入数据中请稍候...", true,
					true);
			proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		} else {
			proDialog.show();
		}
		new Thread() {
			public void run() {
				try {
					//导入产品数据
					if(!dao.hasInsert())
					{
						try {
							FrontProductInfo info = ApiClient.getProductInfo(appContext);
							if(info !=null)
							{
								dao.insert(info);
								//构造科目
								String[] subjectIds = info.getSubjectId();
								if(subjectIds != null && subjectIds.length > 0)
								{
									subjects = new ArrayList<Subject>();
									String[] subjectNames = info.getSubjectName();
									for(int i=0;i<subjectIds.length;i++)
									{
										subjects.add(new Subject(subjectIds[i],subjectNames[i],i));
									}
								}
							}
						} catch (AppException e) {
							e.printStackTrace();
						}
					}
					handler.sendEmptyMessage(2);
				} catch (Exception e) {
					handler.sendEmptyMessage(-2);
					e.printStackTrace();
				}
			};
		}.start();
	}
	/**
	 * 导入试卷,同时要导入图片
	 */
	private void importPaper() {
		if (proDialog == null) {
			proDialog = ProgressDialog.show(this, null, "导入数据中请稍候...", true,
					true);
			proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		} else {
			proDialog.show();
		}
		new Thread() {
			public void run() {
				try {
					ArrayList<FrontPaperInfo> list = ApiClient.getPaperList((AppContext)getApplication());
					dao.insertPaperList(list);
					if(list == null || list.size()==0)
						handler.sendEmptyMessage(3);
					else
					{
						File file = new File(dataDir);
						if(!file.exists())
						{
							file.mkdirs();
						}
						//导入试卷的数据
						for(FrontPaperInfo paper:list)
						{
							String content = ApiClient.loadPaperContent(appContext,paper.getId());
							dao.updatePaperContent(paper.getId(), content);
							//加载试卷的图片
							loadImage(content, dataDir);
						}
					}
					handler.sendEmptyMessage(3);
				}catch(Exception e)
				{
					e.printStackTrace();
					handler.sendEmptyMessage(-3);
				}
			};
		}.start();
	}
	
	private void loadImage(String content,String imagePath)throws Exception 
	{
		Log.e("导入图片","ddddddddddd");
		//查询字符串中是否包含图片
		if(StringUtils.isEmpty(content)) return;
		Pattern ps = Pattern.compile("<img[^>]+src\\s*=\\s*[\\\\][\"]([^\"]+)[\\\\][\"][^>]*>");//<img[^<>]*src=[\'\"]([0-9A-Za-z.\\/]*)[\'\"].(.*?)>");
        Matcher m = ps.matcher(content);
        while(m.find()){
        	String url = m.group(1);
        	if(!url.startsWith("http"))
        	{
        		url = URLs.HOST + url;
        	}
        	Log.e("导入图片",url);
        	ApiClient.getNetImage(url, imagePath);
        }
		
	}
	
	private void importSyllabus() {
		if (proDialog == null) {
			proDialog = ProgressDialog.show(this, null, "导入数据中请稍候...", true,
					true);
			proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		} else {
			proDialog.show();
		}
		new Thread() {
			public void run() {
				try {
					if(subjects!=null && subjects.size()>0)
					{
						for(Subject s:subjects)
						{
							String content = ApiClient.loadSyllabusContent(
									(AppContext) getApplication(), s.getSubjectId());
							if (!StringUtils.isEmpty(content)) {
								dao.insertSyllabusAndLoadChapters(
										s, content);
							}
						}
					}
					handler.sendEmptyMessage(3);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(-3);
				}
			}
		}.start();
	}


	private void inputObject(Serializable ser, String filePath,String fileName) throws Exception {
		File f = new File(filePath);
		if(!f.exists())
		{
			f.mkdirs();
		}
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(new File(filePath+fileName));
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	private Object outputObject(String file) throws Exception {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				// File data = getFileStreamPath(file);
				File data = new File(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	private void importAllData() {
		if (proDialog == null) {
			proDialog = ProgressDialog.show(this, null, "导入数据中请稍候...", true,
					true);
			proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		} else {
			proDialog.show();
		}
		new Thread() {
			public void run() {
				try {
					if(!dao.hasInsert())
					{
						try {
							FrontProductInfo info = ApiClient.getProductInfo(appContext);
							if(info !=null)
							{
								//导入产品信息
								dao.insert(info);
								//构造科目
								String[] subjectIds = info.getSubjectId();
								if(subjectIds != null && subjectIds.length > 0)
								{
									subjects = new ArrayList<Subject>();
									String[] subjectNames = info.getSubjectName();
									for(int i=0;i<subjectIds.length;i++)
									{
										subjects.add(new Subject(subjectIds[i],subjectNames[i],i));
									}
								}
								//导入试卷
								ArrayList<FrontPaperInfo> list = ApiClient.getPaperList((AppContext)getApplication());
								dao.insertPaperList(list);
								if(list == null || list.size()==0)
									handler.sendEmptyMessage(2);
								else
								{
									File file = new File(dataDir);
									if(!file.exists())
									{
										file.mkdirs();
									}
									//导入试卷的数据
									for(FrontPaperInfo paper:list)
									{
										String content = ApiClient.loadPaperContent(appContext,paper.getId());
										dao.updatePaperContent(paper.getId(), content);
										loadImage(content, dataDir);
									}
								}
								//导入大纲
								if(subjects!=null && subjects.size()>0)
								{
									for(Subject s:subjects)
									{
										String content = ApiClient.loadSyllabusContent(
												(AppContext) getApplication(), s.getSubjectId());
										if (!StringUtils.isEmpty(content)) {
											dao.insertSyllabusAndLoadChapters(
													s, content);
										}
									}
								}
							}
						} catch (AppException e) {
							e.printStackTrace();
						}
					}
					// 所有地区数据都搞定后
					handler.sendEmptyMessage(5);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(-5);
				}
			};
		}.start();
	}
}
