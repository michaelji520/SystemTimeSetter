package demo;

import java.awt.EventQueue;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.AWTException;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.JButton;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

public class SystemTimeSetter {

	private JFrame frame;
	private static SystemTray tray = SystemTray.getSystemTray();
	private static TrayIcon trayIcon;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SystemTimeSetter window = new SystemTimeSetter();
					window.frame.setVisible(true);
					window.frame.setTitle("System Time Setter by Michael Zhang");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SystemTimeSetter() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setFocusable(true);
		frame.setBounds(400, 200, 450, 200);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //关闭窗口
		
		InitGlobalFont(new Font("alias", Font.PLAIN, 12));  //设置全局界面字体
		try {
			//设置windows风格界面
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Instructions"));
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		String help = "Hot keys: → Set system time X seconds advance, ← Set system time X seconds behind, ↑ Synchronize network time.";
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setSize(200,100);
		StringBuilder builder = new StringBuilder("<html>");
        char[] chars = help.toCharArray();
        FontMetrics fontMetrics = lblNewLabel.getFontMetrics(lblNewLabel.getFont());
        int start = 0;
        int len = 0;
        while (start + len < help.length()) {
            while (true) {
                len++;
                if (start + len > help.length())break;
                if (fontMetrics.charsWidth(chars, start, len) 
                        > lblNewLabel.getWidth()) {
                    break;
                }
            }
            builder.append(chars, start, len-1).append("<br/>");
            start = start + len - 1;
            len = 0;
        }
        builder.append(chars, start, help.length()-start);
        builder.append("</html>");
	    panel.setLayout(new GridLayout(0, 2, 0, 0));
	    lblNewLabel.setText(builder.toString());
		panel.add(lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setSize(250, 100);
		panel_1.setBorder(BorderFactory.createTitledBorder("Set time unit (s)"));
		JLabel lblNewLabel_2 = new JLabel("Time unit:");
		lblNewLabel_2.setBounds(10, 20, 60, 30);
		JTextField text_1 = new JTextField("60");
		text_1.setLocation(70, 27);
		text_1.setSize(120, 21);
		panel_1.setLayout(null);
		panel_1.add(lblNewLabel_2);
		panel_1.add(text_1);
		panel.add(panel_1);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(BorderFactory.createTitledBorder("Synchronize network time"));
		frame.getContentPane().add(panel_2, BorderLayout.CENTER);
		

		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.addItem("http://time.windows.com");
		comboBox.addItem("http://www.baidu.com");
		comboBox.addItem("http://www.ntsc.ac.cn");
		comboBox.addItem("http://www.qq.com");
		panel_2.add(comboBox);
		
		JButton btnUpdate = new JButton("Synchronize");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				URL url;
				try {
					url = new URL(comboBox.getSelectedItem().toString());
					URLConnection uc = url.openConnection();// 生成连接对象
					uc.connect(); // 发出连接
					String str = (new SimpleDateFormat("HH:mm:ss")).format(uc.getDate());
					String cmd = "cmd /c time " + str;
					Runtime.getRuntime().exec(cmd);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		panel_2.add(btnUpdate);
		
		frame.addWindowListener(new WindowAdapter(){
			public void windowIconified(WindowEvent e) { // 窗口最小化事件
				frame.setVisible(false);
				miniTray(frame);
			}
		});
		
		GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook();
        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override 
            public void keyPressed(GlobalKeyEvent event) {
                String osName = System.getProperty("os.name");
				String cmd = "";
				try {
					if (osName.matches("^(?i)Windows.*$")) {
						if(event.getVirtualKeyCode() == GlobalKeyEvent.VK_RIGHT){
							Date date = new Date(System.currentTimeMillis() + Integer.parseInt(text_1.getText())*1000);
							String str = (new SimpleDateFormat("HH:mm:ss")).format(date);
							cmd = "cmd /c time " + str;
							Runtime.getRuntime().exec(cmd);
						}
						else if(event.getVirtualKeyCode() == GlobalKeyEvent.VK_LEFT){
							Date date = new Date(System.currentTimeMillis() - Integer.parseInt(text_1.getText())*1000);
							String str = (new SimpleDateFormat("HH:mm:ss")).format(date);
							cmd = "cmd /c time " + str;
							Runtime.getRuntime().exec(cmd);
						}
						else if(event.getVirtualKeyCode() == GlobalKeyEvent.VK_UP){
							URL url = new URL(comboBox.getSelectedItem().toString());
							URLConnection uc = url.openConnection();// 生成连接对象
							uc.connect(); // 发出连接
							String str = (new SimpleDateFormat("HH:mm:ss")).format(uc.getDate());
							cmd = "cmd /c time " + str;
							Runtime.getRuntime().exec(cmd);
						}
					} else { 
						//For linux
						// Date date = new
						// Date(System.currentTimeMillis()+60000);
						// String str = (new
						// SimpleDateFormat("HH:mm:ss")).format(date);
						// cmd = " date -s 20090326";
						// Runtime.getRuntime().exec(cmd);
						//cmd = "  date -s 12:01:01";
						//Runtime.getRuntime().exec(cmd);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
            }
            @Override 
            public void keyReleased(GlobalKeyEvent event) {
                //System.out.println(event); 
            }
        });
	}
	
	/**
	 * 统一设置字体，父界面设置之后，所有由父界面进入的子界面都不需要再次设置字体
	 */
	private static void InitGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}
	
	private static void miniTray(JFrame frame) { // 窗口最小化到任务栏托盘

		PopupMenu pop = new PopupMenu(); // 增加托盘右击菜单
		MenuItem show = new MenuItem("Open frame");
		MenuItem exit = new MenuItem("Exit program");

		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // 按下还原键
				tray.remove(trayIcon); // 移去托盘图标
				frame.setVisible(true);
				frame.setExtendedState(JFrame.NORMAL); // 还原窗口
				frame.toFront();
			}
		});

		exit.addActionListener(new ActionListener() { // 按下退出键
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		pop.add(show);
		pop.add(exit);

		ImageIcon trayImg = new ImageIcon(ClassLoader.getSystemResource("images/icon.png"));// 托盘图标
		trayIcon = new TrayIcon(trayImg.getImage(), "SystemTimeSetter", pop);
		trayIcon.setImageAutoSize(true);

		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) { // 鼠标器双击事件
				if (e.getButton() == MouseEvent.BUTTON1) {//设置左键点击还原窗口
					tray.remove(trayIcon); // 移去托盘图标
					frame.setVisible(true);
					frame.setExtendedState(JFrame.NORMAL); // 还原窗口
					frame.toFront();
				}
			}
		});

		try {
			tray.add(trayIcon);
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
