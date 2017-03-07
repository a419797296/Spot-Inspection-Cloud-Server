package com.blue.SocketClient.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.blue.SocketService.main.Control;
import com.blue.SocketService.main.User;

public class Client extends Thread {
	public String ip = null;// 连接服务器的IP
	public Integer port = null;// 连接服务器的端口
	private Socket socket = null;// 套节字对象
	private boolean close = false; // 关闭连接标志位，true表示关闭，false表示连接
	// private Integer sotimeout = 1 * 1 * 10;// 超时时间，以毫秒为单位

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	// ------------------------------------------------------------------------------
	public Client() {
		init();
	}

	public Client(String ip, Integer port) {
		setIp(ip);
		setPort(port);
		init();
	}

	/**
	 * 初始化socket对象
	 */
	public void init() {
		try {
			InetAddress address = InetAddress.getByName(getIp());
			socket = new Socket(address, getPort());
			socket.setKeepAlive(true);// 开启保持活动状态的套接字
			// socket.setSoTimeout(sotimeout);// 设置超时时间
			close = !Send("test");// 发送初始数据，发送成功则表示已经连接上，发送失败表示已经断开
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读数据线程
	 */
	public void run() {
		// ---------读数据---------------------------
		close = isServerClose(socket);// 判断是否断开
		if (!close) {// 没有断开，开始读数据
			try {
				
				byte[] readLen=new byte[4]; 
				int ad = 0;
	            DataInputStream netInputStream = new DataInputStream(socket.getInputStream());  
	            DataOutputStream netOutputStream = new DataOutputStream(socket.getOutputStream()); 
	            while (true) {
					netInputStream.read(readLen); 
					//readLen[0]<<=8;
					//ad = 0;
				/*	int tmp;
					tmp=readLen[0];
					ad=tmp&0xff;
					System.out.printf("接收到的字符串为%d：\r\n", ad);
					ad<<=8;
					tmp=readLen[1];
					tmp=tmp&0xff;
					ad|=tmp;*/

					ad=(int)readLen[0]&0xff;
					//System.out.printf("接收到的字符串为%d：\r\n", ad);
					ad<<=8;
					ad|=(int)readLen[1]&0xff;
					ad=(ad*5000)>>15;    //AD值转电压值
					//ad=(int)readLen[0]<<8+readLen[1];
					System.out.printf("接收到的字符串为%d  %d：\r\n", readLen[0],readLen[1]);
					System.out.printf("接收到的字符串为%x  %x：\r\n", readLen[0],readLen[1]);
					System.out.printf("接收到的字符串为: %d\r\n", ad);
				}

				
			/*	
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "UTF-8"));
				String line = null;

				while ((line = br.readLine()) != null) {
					// ChatManager.getChatManager().publish(this, line); //转发信息
					// ChatManager.getChatManager().replay(this, "已经收到您的来信");

					StringBuilder builder = new StringBuilder();
					builder.append(line);
					
					String receivedData = builder.toString();
					System.out.println("接收到的字符串为：\r\n" + receivedData);
					//whatTodo(receivedData);
				}

				br.close();*/
			} catch (UnsupportedEncodingException e) {
				System.out.println("socket通讯异常");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("socket通讯异常");
				e.printStackTrace();
			}

		}

		// ---------创建连接-------------------------
		while (close) {// 已经断开，重新建立连接
			try {
				System.out.println("重新建立连接：" + getIp() + ":" + getPort());
				InetAddress address = InetAddress.getByName(getIp());
				socket = new Socket(address, getPort());
				socket.setKeepAlive(true);
				// socket.setSoTimeout(sotimeout);
				close = !Send("2");
				System.out.println("建立连接成功：" + getIp() + ":" + getPort());
			} catch (Exception se) {
				System.out.println("创建连接失败:" + getIp() + ":" + getPort());
				close = true;
			}
		}
	}

	/**
	 * 发送数据，发送失败返回false,发送成功返回true
	 * 
	 * @param csocket
	 * @param message
	 * @return
	 */
	public Boolean Send(String message) {
		try {
			PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
			out.println(message);
			return true;
		} catch (Exception se) {
			se.printStackTrace();
			return false;
		}
	}

	/**
	 * 判断是否断开连接，断开返回true,没有返回false
	 * 
	 * @param socket
	 * @return
	 */
	public Boolean isServerClose(Socket socket) {
		try {
			socket.sendUrgentData(0);// 发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
			return false;
		} catch (Exception se) {
			return true;
		}
	}
	
	/*********************** 判断是哪类json数据（clothes_info,user_info OR control_info） ***************************/
	public String judgeJsonType(String jsondate) {
		String jsontype = "chatInfo";
		JSONObject root;
		try {
			root = new JSONObject(jsondate);
			jsontype = root.getString("jsonType");
			System.out.println("该字符串是Json串，其类型是" + jsontype);
			return jsontype;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("无法解析json!\r\n");

			e.printStackTrace();
			return jsontype;
		}
	}

	/*********************** 服务器需要处理哪些事情 ***************************/
	public void whatTodo(String receivedData) {
		String jsontype = judgeJsonType(receivedData);
		switch (jsontype) {
		case "chatInfo":
			System.out.println("该Json串是chatInfo\r\n");
			break;
		case "loginInfo":
			System.out.println("该Json串是loginInfo\r\n");
			break;
		case "regInfo":
			System.out.println("该Json串是regInfo\r\n");

			break;
		case "productInfo":
			System.out.println("该Json串是productInfo\r\n");

			break;
		case "controlInfo":
			System.out.println("该Json串是controlInfo\r\n");
			controlInfo(receivedData);
			break;
		case "wifiInfo":
			System.out.println("该Json串是wifiInfo\r\n");
			break;
		case "productReg":
			break;

		default:
			break;
		}
	}
	
	//-----------------------------------------------------控制制定目标
	public void controlInfo(String receivedData) {
		Control control = new Control();
		if (control.jsonResolve(receivedData)) {
			System.out.println("control_info 解析成功");
			String stigStat=control.getTrigStat();
			new User().saveAsFileWriter("G:\\sockTest.txt",stigStat);
			
			System.out.println(stigStat);
		}

	}
	
    // 第三种方法：设定指定任务task在指定延迟delay后进行固定频率peroid的执行。  
    // scheduleAtFixedRate(TimerTask task, long delay, long period)  
    public static void timer() {  
        Timer timer = new Timer();  
        timer.scheduleAtFixedRate(new TimerTask() {  
            public void run() {  
                System.out.println("-------设定要指定任务--------");  
            }  
        }, 1000, 2000);  
    }  
	
}
