package com.unbank.berkeleydb;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentMutableConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

public class JeTools {

	public static JeTools jeTools;

	public static synchronized JeTools getIntence() {
		if (jeTools == null) {
			jeTools = new JeTools();
			openDatabase();
		}
		return jeTools;
	}

	private static Log logger = LogFactory.getLog(JeTools.class);
	// 数据库环境
	private static Environment myDbEnvironment = null;
	// 数据库配置
	private static DatabaseConfig dbConfig = null;
	// //数据库游标
	// private Cursor myCursor = null;
	// 数据库对象
	private static Database myDatabase = null;
	// 数据库文件名
	private static String fileName = "je_urls";
	// 数据库名称
	private static String dbName = "duplicate_urls";

	public List<String> getUrls() {

		List<String> urls = new ArrayList<String>();

		return urls;

	}

	/*
	 * 打开当前数据库
	 */
	public synchronized static void openDatabase() {
		try {
			logger.info("打开数据库: " + dbName);
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			envConfig.setTransactional(true);
			envConfig.setReadOnly(false);
			envConfig.setTxnTimeout(10000);
			envConfig.setLockTimeout(10000);
			/*
			 * 其他配置 可以进行更改
			 */
			EnvironmentMutableConfig envMutableConfig = new EnvironmentMutableConfig();
			envMutableConfig.setCachePercent(50);// 设置je的cache占用jvm 内存的百分比。
			envMutableConfig.setCacheSize(2048 * 10);// 设定缓存的大小为123456Bytes
			envMutableConfig.setTxnNoSync(false);// 设定事务提交时是否写更改的数据到磁盘，true不写磁盘。
			envMutableConfig.setTxnWriteNoSync(false);// 设定事务在提交时，是否写缓冲的log到磁盘
			// 。如果写磁盘会影响性能，不写会影响事务的安全。随机应变。
			File file = new File(fileName);
			if (!file.exists()) {
				file.mkdirs();
			}
			myDbEnvironment = new Environment(file, envConfig);
			dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			dbConfig.setTransactional(true);
			dbConfig.setReadOnly(false);
			if (myDatabase == null) {
				myDatabase = myDbEnvironment.openDatabase(null, dbName,
						dbConfig);
			}
			logger.info(dbName + "数据库中的数据个数: " + myDatabase.count());
		} catch (DatabaseException e) {
			logger.info("打开JE数据库出错", e);
		}
	}

	/*
	 * 向数据库中写入记录 传入key和value
	 */
	public boolean writeToDatabase(String key, String value, boolean isOverwrite) {
		try {
			// 设置key/value,注意DatabaseEntry内使用的是bytes数组
			DatabaseEntry theKey = new DatabaseEntry(key.trim().getBytes(
					"UTF-8"));
			DatabaseEntry theData = new DatabaseEntry(value.getBytes("UTF-8"));
			OperationStatus res = null;
			Transaction txn = null;
			try {
				TransactionConfig txConfig = new TransactionConfig();
				txConfig.setSerializableIsolation(true);
				txn = myDbEnvironment.beginTransaction(null, txConfig);
				if (isOverwrite) {
					res = myDatabase.put(txn, theKey, theData);
				} else {
					res = myDatabase.putNoOverwrite(txn, theKey, theData);
				}
				txn.commit();
				if (res == OperationStatus.SUCCESS) {
					logger.info("向数据库" + dbName + "中写入:" + key + "," + value);
					return true;
				} else if (res == OperationStatus.KEYEXIST) {
					logger.info("向数据库" + dbName + "中写入:" + key + "," + value
							+ "失败,该值已经存在");
					return false;
				} else {
					logger.info("向数据库" + dbName + "中写入:" + key + "," + value
							+ "失败");
					return false;
				}
			} catch (Exception lockConflict) {
				txn.abort();
				logger.info("向数据库" + dbName + "中写入:" + key + "," + value
						+ "出现lock异常");
				logger.info(lockConflict.getMessage());
				logger.info(lockConflict.getCause().toString());
				logger.info(lockConflict.getStackTrace().toString());
				return false;
			}
		} catch (Exception e) {
			// 错误处理
			logger.info("向数据库" + dbName + "中写入:" + key + "," + value + "出现错误");
			return false;
		}
	}

	/*
	 * 关闭当前数据库
	 */
	public void closeDatabase() {
		// TODO Auto-generated method stub
		if (myDatabase != null) {
			try {
				myDatabase.close();
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
		if (myDbEnvironment != null) {
			logger.info("关闭数据库: " + dbName);
			try {
				myDbEnvironment.cleanLog();
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				myDbEnvironment.close();
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * 删除数据库中的一条记录
	 */
	public boolean deleteFromDatabase(String key) {
		boolean success = false;
		long sleepMillis = 0;
		for (int i = 0; i < 3; i++) {
			if (sleepMillis != 0) {
				try {
					Thread.sleep(sleepMillis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sleepMillis = 0;
			}
			Transaction txn = null;
			try {
				TransactionConfig txConfig = new TransactionConfig();
				txConfig.setSerializableIsolation(true);
				txn = myDbEnvironment.beginTransaction(null, txConfig);
				DatabaseEntry theKey;
				theKey = new DatabaseEntry(key.trim().getBytes("UTF-8"));
				OperationStatus res = myDatabase.delete(txn, theKey);
				txn.commit();
				if (res == OperationStatus.SUCCESS) {
					logger.info("从数据库" + dbName + "中删除:" + key);
					success = true;
					return success;
				} else if (res == OperationStatus.KEYEMPTY) {
					logger.info("没有从数据库" + dbName + "中找到:" + key + "。无法删除");
				} else {
					logger.info("删除操作失败，由于" + res.toString());
				}
				return false;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return false;
			} catch (Exception lockConflict) {
				logger.info("删除操作失败，出现lockConflict异常");
				logger.info(lockConflict.getMessage());
				logger.info(lockConflict.getCause().toString());
				logger.info(lockConflict.getStackTrace().toString());
				sleepMillis = 1000;

				continue;
			} finally {
				if (!success) {
					if (txn != null) {
						try {
							txn.abort();
						} catch (DatabaseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		return false;
	}

	/*
	 * 从数据库中读出数据 传入key 返回value
	 */
	public String readFromDatabase(String key) {
		try {
			DatabaseEntry theKey = new DatabaseEntry(key.trim().getBytes(
					"UTF-8"));
			DatabaseEntry theData = new DatabaseEntry();
			Transaction txn = null;
			try {
				TransactionConfig txConfig = new TransactionConfig();
				txConfig.setSerializableIsolation(true);
				txn = myDbEnvironment.beginTransaction(null, txConfig);
				OperationStatus res = myDatabase.get(txn, theKey, theData,
						LockMode.DEFAULT);
				txn.commit();
				if (res == OperationStatus.SUCCESS) {
					byte[] retData = theData.getData();
					String foundData = new String(retData, "UTF-8");
					logger.info("从数据库" + dbName + "中读取:" + key + ","
							+ foundData);
					return foundData;
				} else {
					logger.info("No record found for key '" + key + "'.");
					return "";
				}
			} catch (Exception lockConflict) {
				try {
					txn.abort();
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logger.info("从数据库" + dbName + "中读取:" + key + "出现lock异常");
				logger.info(lockConflict.getMessage());
				logger.info(lockConflict.getCause().toString());
				logger.info(lockConflict.getStackTrace().toString());

				return "";
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/*
	 * 遍历数据库中的所有记录，返回list
	 */
	public ArrayList<String> getEveryItem() {
		Cursor myCursor = null;
		ArrayList<String> resultList = new ArrayList<String>();
		Transaction txn = null;
		try {
			txn = this.myDbEnvironment.beginTransaction(null, null);
			CursorConfig cc = new CursorConfig();
			cc.setReadCommitted(true);
			if (myCursor == null) {
				myCursor = myDatabase.openCursor(txn, cc);
			}
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();
			// 使用cursor.getPrev方法来遍历游标获取数据
			if (myCursor.getFirst(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				String theKey = new String(foundKey.getData(), "UTF-8");
				String theData = new String(foundData.getData(), "UTF-8");
				resultList.add(theData);
				logger.info("Key | Data : " + theKey + " | " + theData + "");
				while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
					theKey = new String(foundKey.getData(), "UTF-8");
					theData = new String(foundData.getData(), "UTF-8");
					resultList.add(theData);
					logger.info("Key | Data : " + theKey + " | " + theData + "");
				}
			}
			myCursor.close();
			txn.commit();
			return resultList;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			logger.info("getEveryItem处理出现异常");
			logger.info(e.getMessage().toString());
			logger.info(e.getCause().toString());

			try {
				txn.abort();
			} catch (DatabaseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (myCursor != null) {
				try {
					myCursor.close();
				} catch (DatabaseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return null;
		}
	}
}
