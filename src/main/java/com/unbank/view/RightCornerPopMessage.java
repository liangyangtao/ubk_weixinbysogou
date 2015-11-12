package com.unbank.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;

/**
 * 
 * @author BLingSoft
 * 
 *         右下角弹出式提示框 1.自动上升 2.停留一段时间，本例子中5秒 3.自动下降直至消失
 * 
 *         4.线程控制窗口的出现和消失，同时添加鼠标事件控制，可以提前使提示框消失 5.鼠标事件结合自己的需求实现，此处只是实现一个点击事件
 * 
 * @Time 2010-01-29
 * @JDK　VERSION 6.0
 * @Copy Right By BLingSoft
 */
public class RightCornerPopMessage extends JWindow implements Runnable,
		MouseListener {

	private static final long serialVersionUID = -3564453685861233338L;
	private Integer screenWidth; // 屏幕宽度
	private Integer screenHeight; // 屏幕高度
	private Integer windowWidth = 200; // 设置提示窗口宽度
	private Integer windowHeight = 200; // 设置提示窗口高度
	private Integer bottmToolKitHeight; // 底部任务栏高度，如果没有任务栏则为零
	private Integer stayTime = 5000; // 提示框停留时间

	private Integer x; // 窗口起始X坐标
	private Integer y; // 窗口起始Y坐标

	private String title = "内部警告";
	private String message = "一个小小的提示消息例子！";

	private JPanel mainPanel; // 主面板
	private JLabel titleLabel; // 标题栏标签
	private JPanel titlePanel; // 标题栏面板
	private JLabel messageLabel; // 内容标签
	private JPanel messagePanel; // 内容面板
	private JTextArea messageTextArea;

	public RightCornerPopMessage(String message) {
		this.message = message;
		this.init();
		Thread thread = new Thread(this);
		thread.start();
	}

	private void init() {
		bottmToolKitHeight = Toolkit.getDefaultToolkit().getScreenInsets(
				this.getGraphicsConfiguration()).bottom;
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = dimension.width;
		screenHeight = dimension.height;

		x = screenWidth - windowWidth;
		y = screenHeight;
		this.setLocation(x, y - bottmToolKitHeight - windowHeight);
		mainPanel = new JPanel(new BorderLayout());

		titleLabel = new JLabel(title);
		titleLabel.setForeground(Color.WHITE);
		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setBackground(Color.RED);
		titlePanel.add(titleLabel);

		messageTextArea = new JTextArea(message);
		messageTextArea.setBackground(Color.YELLOW);
		messageTextArea.setSize(windowWidth, windowHeight);
		messageTextArea.setFont(new Font("Courier", Font.PLAIN, 15));
		messageTextArea.setLineWrap(true);
		messagePanel = new JPanel();
		messagePanel.add(messageTextArea);
		messagePanel.setBackground(Color.YELLOW);

		mainPanel.add(titlePanel, BorderLayout.NORTH);
		mainPanel.add(messagePanel, BorderLayout.CENTER);

		this.setSize(windowWidth, windowHeight);
		this.setAlwaysOnTop(true);
		this.getContentPane().add(mainPanel);
		this.addMouseListener(this);
		Toolkit.getDefaultToolkit().beep(); // 播放系统声音，提示一下
		this.setVisible(true);
	}

	public void run() {
		Integer delay = 10;
		Integer step = 1;
		Integer end = windowHeight + bottmToolKitHeight;
		while (true) {
			try {
				step++;
				y = y - 1;
				this.setLocation(x, y);
				if (step > end) {
					// Thread.sleep(stayTime);
					break;

				}
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// step = 1;
		// while (true) {
		// try {
		// step++;
		// y = y + 1;
		// this.setLocation(x, y);
		// if (step > end) {
		// this.dispose();
		// break;
		// }
		// Thread.sleep(delay);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// System.exit(0);
	}

	public void mouseClicked(MouseEvent e) {
		int a = JOptionPane.showConfirmDialog(null, "请处理异常");
		if (a == 0) {
			this.dispose();
		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public static void main(String[] args) {
		new RightCornerPopMessage(
				"sfddddddddd:dsffffffffffffffffffffffffffffffffffffffffdsfadsrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
	}
}