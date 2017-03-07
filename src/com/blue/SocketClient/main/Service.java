package com.blue.SocketClient.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.blue.SocketService.main.User;

public class Service extends Thread {

	Socket socket;
//	public Service(Socket s) {
//		this.socket = s;
//	}
	/*********************** 回复信息 ***************************/
	public void out(String out) {
		try {
			out = out + "\n\r";
			socket.getOutputStream().write(out.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*********************** 判断是哪类json数据（clothes_info,user_info OR control_info） ***************************/
	public String judgeJsonType(String jsondate) {
		String jsontype = "chatInfo";
		JSONObject root;
		try {
			root = new JSONObject(jsondate);
			jsontype = root.getString("jsonType");
			System.out.println("该字符串是Json串，其类型是" + jsontype + "\r\n");
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

		case "controlInfo":
			System.out.println("该Json串是controlInfo\r\n");
//			controlTargetProduct(receivedData);
			break;
		case "wifiInfo":
			System.out.println("该Json串是wifiInfo\r\n");
			configWifi(receivedData);
			break;

		default:
			break;
		}
	}
	
	public boolean configWifi(String receivedData) {
		JSONObject root;
		boolean isSuccess = false;
		try {
			root = new JSONObject(receivedData);
			String wifiSSID = root.getString("ssid");
			String wifiKEY = root.getString("key");
			new User().saveAsFileWriter("G:\\sockTest.txt", receivedData);
			UUID uuid = UUID.randomUUID();
			String macString="{\"jsonType\":\"productInfo\",\"productMac\":\""+uuid.toString()+"\"}";
			out(macString);		
			return isSuccess;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("无法解析json!\r\n");

			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void run() {
		ServerSocket serverSocket=null;
		try {
			serverSocket = new ServerSocket(12345);// 1-65535
			System.out.println("服务器启动完成，监听端口在12345");
			System.out.println("正在等待客户连接.........");	
			this.socket = serverSocket.accept();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			String line = null;

			while ((line = br.readLine()) != null) {
				// ChatManager.getChatManager().publish(this, line); //转发信息
				// ChatManager.getChatManager().replay(this, "已经收到您的来信");

				StringBuilder builder = new StringBuilder();
				builder.append(line);
				System.out.println("接收到的字符串为：" + line);
				String receivedData = builder.toString();
				whatTodo(receivedData);
			}

			br.close();
		} catch (UnsupportedEncodingException e) {
			System.out.println("socket通讯异常");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("socket通讯异常");
			e.printStackTrace();
		} 
	}
}

