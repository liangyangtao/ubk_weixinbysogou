package com.unbank.view;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import com.sun.awt.AWTUtilities;

/**
 * 屏幕右下角出现渐隐渐显的提示框 使用到了JDK1.6中新特性的透明窗体，所以必须要使用JDK1.6或以上版本的JDK 功能如下： 1.窗体出现时逐渐清晰
 * 2.停留一会儿时间之后会自动逐渐模糊直至消失 3.点击关闭按钮后逐渐模糊直至消失 4.提示内容支持html标签
 * 
 */
public class TranslucentFrame implements Runnable {

	JFrame frame;
	JLabel label1;
	JEditorPane editorPane1;

	private int width;// 窗体宽度
	private int height;// 窗体高度
	private int stayTime;// 休眠时间
	private String title, message;// 消息标题,内容
	private int style;// 窗体样式
	static {
		try {
			UIManager
					.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 渐隐渐显的提示框
	 * 
	 * @param width
	 *            提示框宽度
	 * @param height
	 *            提示框高度
	 * @param stayTime
	 *            提示框停留时间
	 * @param style
	 *            提示框的样式 以下为样式可选值： 0 NONE 无装饰（即去掉标题栏） 1 FRAME 普通窗口风格 2
	 *            PLAIN_DIALOG 简单对话框风格 3 INFORMATION_DIALOG 信息对话框风格 4
	 *            ERROR_DIALOG 错误对话框风格 5 COLOR_CHOOSER_DIALOG 拾色器对话框风格 6
	 *            FILE_CHOOSER_DIALOG 文件选择对话框风格 7 QUESTION_DIALOG 问题对话框风格 8
	 *            WARNING_DIALOG 警告对话框风格
	 * @param title
	 *            提示框标题
	 * @param message
	 *            提示框内容（支持html标签）
	 */
	public TranslucentFrame(int width, int height, int stayTime, int style,
			String title, String message) {
		this.width = width;
		this.height = height;
		this.stayTime = stayTime;
		this.style = style;
		this.title = title;
		this.message = message;
	}

	/**
	 * 渐隐渐显的提示框
	 * 
	 * @param style
	 *            提示框样式同上
	 * @param title
	 *            提示框标题
	 * @param message
	 *            提示框内容
	 */
	public TranslucentFrame(int style, String title, String message) {
		this.width = 250;
		this.height = 180;
		this.stayTime = 5;
		this.style = style;
		this.title = title;
		this.message = message;
	}

	public static void main(String[] args) {
		String title = "友情提示！";
		String message = "<strong>JDK1.6新特性测试</strong><br>《透明窗体》<br>www.oschina.net<br>开源中国";
		// Runnable translucent=new
		// TranslucentFrame(250,180,10,4,title,message);
		Runnable translucent = new TranslucentFrame(2, title, message);
		Thread thread = new Thread(translucent);
		thread.start();
	}

	public void print() {
		frame = new JFrame();
		editorPane1 = new JEditorPane();
		editorPane1.setEditable(false);// 不可编辑
		editorPane1.setContentType("text/html");// 将编辑框设置为支持html的编辑格式
		editorPane1.setText(message);
		frame.add(editorPane1);
		frame.setTitle(title);
		// 设置窗体的位置及大小
		int x = Toolkit.getDefaultToolkit().getScreenSize().width
				- Toolkit.getDefaultToolkit().getScreenInsets(
						frame.getGraphicsConfiguration()).right - width - 5;
		int y = Toolkit.getDefaultToolkit().getScreenSize().height
				- Toolkit.getDefaultToolkit().getScreenInsets(
						frame.getGraphicsConfiguration()).bottom - height - 5;
		frame.setBounds(x, y, width, height);
		frame.setUndecorated(true); // 去掉窗口的装饰
		frame.getRootPane().setWindowDecorationStyle(style); // 窗体样式
		// frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG
		// ); //窗体样式
		AWTUtilities.setWindowOpacity(frame, 0.01f);// 初始化透明度
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);// 窗体置顶
		// 添加关闭窗口的监听
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				hide();
			}
		});
	}

	/**
	 * 窗体逐渐变清晰
	 * 
	 */
	public void show() {
		for (int i = 0; i < 50; i++) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}
			AWTUtilities.setWindowOpacity(frame, i * 0.02f);
		}
	}

	/**
	 * 窗体逐渐变淡直至消失
	 * 
	 */
	public void hide() {
		float opacity = 100;
		while (true) {
			if (opacity < 2) {
				System.out.println();
				break;
			}
			opacity = opacity - 2;
			AWTUtilities.setWindowOpacity(frame, opacity / 100);
			try {
				Thread.sleep(20);
			} catch (Exception e1) {
			}
		}
		// frame.hide();
//		System.dispose();
	}

	public void run() {
		print();
		show();
		try {
			Thread.sleep(stayTime * 1000);
		} catch (Exception e) {
		}
		hide();
	}
}
