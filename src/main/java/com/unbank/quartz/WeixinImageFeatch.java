package com.unbank.quartz;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.unbank.fetch.Fetcher;
import com.unbank.tools.MD5;

public class WeixinImageFeatch {
	private static Log logger = LogFactory.getLog(WeixinImageFeatch.class);

	public String fetchImage(String imgSrc) {
		InputStream is = null;
		String imageName = null;
		BufferedInputStream bis = null;
		ImageInputStream iis = null;
		String imageFormatName = "";
		try {
			is = Fetcher.getInstance().getImage(imgSrc);
			bis = new BufferedInputStream(is);
			Date today = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			String todayStr = simpleDateFormat.format(today);
			// 存储图片的路径
			String imagePath = "//10.0.2.35/images/" + todayStr + "/";

			File imageDir = new File(imagePath);
			if (!imageDir.exists()) {
				imageDir.mkdirs();
			}
			iis = ImageIO.createImageInputStream(is);

			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (iter.hasNext()) {
				ImageReader reader = iter.next();
				reader.setInput(iis);
				imageFormatName = reader.getFormatName().toLowerCase();
				imageName = imagePath
						+ MD5.GetMD5Code(imgSrc + new Date().getTime()) + "."
						+ imageFormatName;
				ImageReadParam irp = reader.getDefaultReadParam();
				BufferedImage bi = reader.read(0, irp);
				ImageIO.write(bi, imageFormatName, new File(imageName));
			}
		} catch (IOException e) {
			logger.info("保存图片失败", e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.info("关闭输入流失败", e);
				}
			}

			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					logger.info("关闭数据流失败", e);
				}
			}

			if (iis != null) {
				try {
					iis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (imageName != null) {
			imageName = "http://10.0.2.35:8080/unbankImage/"
					+ imageName.substring(12);
		}
		return imageName;

	}

}
