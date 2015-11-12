package com.unbank.store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.unbank.entity.Weixin;

public class WeiXinStore {

	public void saveWeixinByIndex(Map<String, Object> information) {
		String info = null;
		try {
			info = new ObjectMapper().writeValueAsString(information);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(info);
		saveTxt(info);
		// new IndexClient().index(information);
	}

	public void saveTxt(String text) {

		String path = null;
		try {
			path = WeiXinStore.class.getClassLoader().getResource("").toURI()
					.getPath();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String dateFileNameString = new SimpleDateFormat("yyyyMMdd")
				.format(new Date());
		File file = null;
		FileWriter fileWriter = null;
		try {
			file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(path + dateFileNameString + "_weixintestresult.txt");
			fileWriter = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fileWriter);
			bw.append(text);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			try {
//				fileWriter.flush();
//				fileWriter.close();
//			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
}
