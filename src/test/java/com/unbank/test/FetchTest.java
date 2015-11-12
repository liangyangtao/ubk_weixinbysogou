package com.unbank.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.unbank.fetch.Fetcher;
import com.unbank.fetch.PhantomjsFetcher;

public class FetchTest {

	public static void main(String[] args) {
		// http://mp.weixin.qq.com/mp/getmasssendmsg?__biz=MjM5NTcwOTgyNg==&uin=MjE5ODYyMzU%3D&key=2877d24f51fa538485fe0c8c43741cea79338838101cf07a22e7f2bd74eb621073f09f084d5d31bc5209167a7761b7a9&devicetype=Windows+7&version=61050016&lang=zh_CN&pass_ticket=8FNMXPu%2B4orefjzZvDFy0Jcqglldse6KkvF7L5yFoEI%3D
		// try {
		// System.out
		// .println(URLDecoder
		// .decode("http://sc.qq.com/http%3A%2F%2Fmp.weixin.qq.com%2Fmp%2Fgetmasssendmsg%3F__biz%3DMjM5ODgwODY2MA%3D%3D%23wechat_webview_type%3D1%26wechat_redirect",
		// "utf-8"));
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		String urlfirst = "http://mp.weixin.qq.com/mp/getmasssendmsg?__biz=MjM5NTcwOTgyNg==&uin=MjE5ODYyMzU%3D&key=2877d24f51fa538485fe0c8c43741cea79338838101cf07a22e7f2bd74eb621073f09f084d5d31bc5209167a7761b7a9&devicetype=Windows+7&version=61050016&lang=zh_CN&pass_ticket=8FNMXPu%2B4orefjzZvDFy0Jcqglldse6KkvF7L5yFoEI%3D";
		String html = Fetcher.getInstance().getHtmlWithCookie(urlfirst);
		System.out.println(html);
		String jsonString = StringUtils.substringBetween(html, "msgList = '",
				"seajs.use(");
		System.out.println(jsonString);
		// String url1 =
		// "http://weixin.sogou.com/websearch/art.jsp?sg=CBf80b2xkgY3Ern0XFJDmE2fD1PiPtSKG1ZV_dz5T5bVIvhtCP4FfbLrPr9-pKbVdOdImk-T8VgbYmCzh4IfzwdlaAJ4Va5GP09_ue4IS5KONMMwdG2Xhd2u_Tq6WqXEvZjWoYHBDg25WeQNLhQLNA..&amp;url=p0OVDH8R4SHyUySb8E88hkJm8GF_McJfBfynRTbN8wg1XF0sWIIS0GUNoU59NaG09R7DM-AShP_JB9wJBEnpQD8FAQFjW_foK8xcO3n674k0ENLxj_11-n9y8OwgD56PMRKuKv6hKkNYy-5x5In7jJFmExjqCxhpkyjFvwP6PuGcQ64lGQ2ZDMuqxplQrsbk"
		// ;
		// System.out.println(html);
		// System.exit(0);
		// String cookies = Fetcher.getInstance().getCookiesString();
		// long startdate = dateToTimestamp(dateToString());
		// // String SUID = "3689953D1220920A0000000055ED240B";
		// String SUID = StringUtils.substringBetween(cookies, "SUID=", "");
		// System.out.println(startdate);
		// System.out.println(SUID);
		// try {
		// new Thread().sleep(10 * 1000);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// long endDate = dateToTimestamp(dateToString());
		// System.out.println(endDate);
		// String url =
		// "http://pb.sogou.com/cl.gif?uigs_productid=webapp&uigs_uuid="
		// + startdate
		// +
		// "&uigs_version=v2.0&uigs_refer=http%3A%2F%2Fweixin.sogou.com%2F%3Fp%3D73141200%26kw%3D"
		// + "&uigs_cookie=SUID%3D"
		// + SUID
		// + "&uuid=9a0bc24f-035d-40b5-a173-c54ed50380c9"
		// + "&query=&noresult=0&type=weixin_gzh_pc&xy=1903,945&uigs_t="
		// + endDate
		// + "&uigs_st=11"
		// + "&uigs_cl=sogou_vr_11002601_title_0%26href%3D"
		// + url1
		// +
		// "&txt=%E9%9D%A0%E8%84%B8%E5%90%83%E9%A5%AD%E5%85%B6%E5%AE%9E%E4%B8%8D%E9%9A%BE";
		// Fetcher.getInstance().getImage(url);
		// // cookies = Fetcher.getInstance().getCookiesString();
		// // System.out.println(cookies);
		// html = Fetcher.getInstance().getHtmlWithCookie(url1);
		// System.out.println(html);

	}

	// public static void main(String[] args) {
	// String urlfirst =
	// "http://weixin.sogou.com/gzh?openid=oIWsFtwVSXrGAhQ3FLykigkZ1nEw";
	// String html = PhantomjsFetcher.get(urlfirst);
	// System.out.println(html);
	// /websearch/art.jsp?sg=CBf80b2xkgY3Ern0XFJDmE2fD1PiPtSKG1ZV_dz5T5ZRN3Xon8WIL07lNaCOW_Sq3osysextegWr1H0ko1eK_z70bcQudrOkji8mlLt5UrG93BOs7Qt0I5edfWkmCVNlEZlyN4PmzQO5WeQNLhQLNA..&amp;url=p0OVDH8R4SHyUySb8E88hkJm8GF_McJfBfynRTbN8wg1XF0sWIIS0GUNoU59NaG09R7DM-AShP_JB9wJBEnpQD8FAQFjW_foK8xcO3n674k0ENLxj_11-n9y8OwgD56PMRKuKv6hKkNYy-5x5In7jJFmExjqCxhpkyjFvwP6PuGcQ64lGQ2ZDMuqxplQrsbk
	// Document document = Jsoup.parse(html, urlfirst);
	// Elements elements = document.select("div#wxbox > a");
	// for (Element element : elements) {
	// System.out.println(element.absUrl("href"));
	// String url1 = element.absUrl("href");
	// html = PhantomjsFetcher.get(url1);
	// System.out.println(html);
	// }

	// PhantomjsFetcher.closeDriver();
	// }

	public static String dateToString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sd = sdf.format(new Date());
		return sd;
	}

	public static long dateToTimestamp(String user_time) {
		long times = 0;
		try {
			times = Timestamp.valueOf(user_time).getTime() * 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (times == 0) {
			System.out.println("String转10位时间戳失败");
		}
		return times;
	}
}
