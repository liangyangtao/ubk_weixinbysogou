package com.unbank.store;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.Jsoup;

import com.unbank.mybatis.entity.SQLAdapter;
import com.unbank.mybatis.factory.DynamicConnectionFactory;
import com.unbank.mybatis.mapper.SQLAdapterMapper;

public class WeiXinStoreToPlantform {
	private static Log logger = LogFactory.getLog(WeiXinStoreToPlantform.class);

	// 16096
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
			sqlSession.rollback(true);
		} finally {
			sqlSession.close();
		}

	}

	private void saveWeixinText(Map<String, Object> information,
			SQLAdapterMapper sqlAdapterMapper, int id) {
		SQLAdapter sqlAdapter = new SQLAdapter();
		sqlAdapter.setSql("insert into ptf_crawl_text (crawl_id,text) values ("
				+ id + ",'" + information.get("content") + "')");
		sqlAdapterMapper.executeSQL(sqlAdapter);
	}

	public int saveWeixin(Map<String, Object> information,
			SQLAdapterMapper sqlAdapterMapper) {
		SQLAdapter sqlAdapter = new SQLAdapter();
		sqlAdapter.setSql("insert into ptf_crawl ");
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("website_id", "16096");
		maps.put("crawl_title", information.get("title"));
		String brief = Jsoup.parse((String) information.get("content")).text();
		if (brief.length() > 100) {
			brief = brief.substring(0, 100);
		} else if (brief.length() < 10) {
			brief = (String) information.get("title");
		}
		maps.put("crawl_brief", brief);
		maps.put("crawl_views", "0");
		maps.put("web_name", "微信公众号");
		maps.put("url", information.get("url"));
		maps.put("file_index", "7");
		maps.put(
				"news_time",
				new Date(Long.parseLong((String) information
						.get("lastModified")) * 1000));

		maps.put("crawl_time", new Date());
		// maps.put("account", information.get("sourcename"));
		maps.put("task", "2");
		// maps.put("openid", information.get("openid"));
		sqlAdapter.setObj(maps);
		sqlAdapterMapper.insertReturnPriKey(sqlAdapter);
		int id = sqlAdapter.getPrikey();
		return id;
	}
}
