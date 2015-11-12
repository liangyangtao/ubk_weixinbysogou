package com.unbank.spider.weixinbigV;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class WeixinGongZhonghaoReader {
	private static Logger logger = Logger
			.getLogger(WeixinGongZhonghaoReader.class);

	public List<String> readGongZhongHaoList() {
		String path = null;
		try {
			path = WeixinGongZhonghaoReader.class.getClassLoader()
					.getResource("").toURI().getPath();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		File file = new File(path + "weixingongzhonghao.txt");
		InputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			logger.info("读取微信公众号列表失败", e);
		}
		List<String> list = loadWords(input);
		return list;
	}

	public static List<String> loadWords(InputStream input) {
		String line;
		List<String> words = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(input,
					"UTF-8"), 1024);
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				} else if (line.startsWith("//")) {
					continue;
				}
				words.add(line);
			}
			br.close();
		} catch (IOException e) {
			logger.info("读取微信公众号列表失败!", e);
		}
		return words;
	}

}
