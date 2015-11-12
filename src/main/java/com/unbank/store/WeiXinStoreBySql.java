package com.unbank.store;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import com.unbank.mybatis.entity.SQLAdapter;
import com.unbank.mybatis.factory.DynamicConnectionFactory;
import com.unbank.mybatis.mapper.SQLAdapterMapper;

public class WeiXinStoreBySql {
	private static Log logger = LogFactory.getLog(WeiXinStoreBySql.class);
//	16096
	public void saveWeixinBySql(Map<String, Object> information) {
		SqlSession sqlSession = DynamicConnectionFactory
				.getInstanceSessionFactory("development").openSession();
		try {
			SQLAdapterMapper sqlAdapterMapper = sqlSession
					.getMapper(SQLAdapterMapper.class);
			int id = saveWeixin(information, sqlAdapterMapper);
			saveWeixinText(information, sqlAdapterMapper, id);
			sqlSession.commit();
		} catch (Exception e) {
			logger.info("保存微信到数据库失败", e);
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}

	}

	private void saveWeixinText(Map<String, Object> information,
			SQLAdapterMapper sqlAdapterMapper, int id) {
		SQLAdapter sqlAdapter = new SQLAdapter();
		sqlAdapter.setSql("insert into weixintext (id,text) values (" + id
				+ ",'" + information.get("content") + "')");
		sqlAdapterMapper.executeSQL(sqlAdapter);
	}

	public int saveWeixin(Map<String, Object> information,
			SQLAdapterMapper sqlAdapterMapper) {
		SQLAdapter sqlAdapter = new SQLAdapter();
		sqlAdapter.setSql("insert into weixin ");
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("title", information.get("title"));
		maps.put(
				"time",
				new Date(Long.parseLong((String) information
						.get("lastModified")) * 1000));
		maps.put("url", information.get("url"));
		maps.put("crawltime", new Date());
		maps.put("account", information.get("sourcename"));
		maps.put("task", "1");
		maps.put("openid", information.get("openid"));
		sqlAdapter.setObj(maps);
		sqlAdapterMapper.insertReturnPriKey(sqlAdapter);
		int id = sqlAdapter.getPrikey();
		return id;
	}
}
