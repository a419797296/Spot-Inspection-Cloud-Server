package com.blue.SocketService.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.Connection;

public class ChatSocket extends Thread {

	Socket socket;
	BufferedReader br;
	BufferedWriter wr;
	private byte[] readBuffer = new byte[10000];
	private String mac = "";
	private String username = "";

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public ChatSocket(Socket s) {
		this.socket = s;
	}

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

	/*********************** 回复信息 ***************************/
	public void out(byte[] out) {
//		 out[out.length]='\r';
//		 out[out.length+1]='\n';
		try {
			socket.getOutputStream().write(out);
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
		case "loginInfo":
			System.out.println("该Json串是loginInfo\r\n");
			userLogin(receivedData);
			break;
		case "regInfo":
			System.out.println("该Json串是regInfo\r\n");
			userRegist(receivedData);

			break;
		case "productInfo":
			System.out.println("该Json串是productInfo\r\n");
			uploadProductInfo(receivedData);

			break;
		case "controlInfo":
			System.out.println("该Json串是controlInfo\r\n");
			controlTargetProduct(receivedData);
			break;
		case "startRecvDataInfo":
			System.out.println("该Json串是startRecvDataInfo\r\n");
			//startRecvDataInfo(receivedData);
			break;
		case "wifiInfo":
			System.out.println("该Json串是wifiInfo\r\n");
			// new User().saveAsFileWriter("C:\\sockTest.txt",
			// this.receivedData);
			ChatManager
					.getChatManager()
					.replay(this,
							"{\"jsonType\":\"productInfo\",\"productMac\":\"aaaaaaaa\"}");
			break;
		case "productReg":
			System.out.println("该Json串是productReg\r\n");
			ChatManager.getChatManager().replay(this, "服务器已经收到产品mac地址");
			productReg(receivedData);
			// ChatManager.getChatManager().add(this);
			break;

		default:
			break;
		}
	}

	/********************* 注册产品，自动在product_info,control_info中各生成一条记录 ****************************/
	public boolean productReg(String receivedData) {
		JSONObject root;
		boolean isSuccess = false;
		try {
			root = new JSONObject(receivedData);
			String mac = root.getString("productMac");
			String userName = root.getString("userName");
			ConnectDatabase db = new ConnectDatabase();
			Connection conn = (Connection) db.ConnectMysql();
			// -------------------在product_info中生成一条记录
			Product product = new Product();
			product.setProductMac(mac);

			// -------------------判断该产品是否已经注册

			String macID = db.selectMacID(mac);
			String userID = db.selectuserID(userName);
			System.out.println("用户ID:!\r\n" + userID);
			if (macID == null) { // 没注册
				db.InsertProductToSql(product);
				// -------------------在product_info和user_info中获得对应的id并写入control_info中
				macID = db.selectMacID(mac);
				Control control = new Control();
				control.setProductID(macID);
				control.setUserID(userID);
				db.InsertControlToSql(control);
				System.out.println("用户和产品匹配成功!\r\n");

				isSuccess = true;
			} else {
				System.out.println("用户和产品已存在匹配记录!\r\n");
			}
			try {
				db.CutConnection(conn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return isSuccess;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("无法解析json!\r\n");

			e.printStackTrace();
			return false;
		}
	}

	// -----------------------------------------------------控制制定目标
	public void controlTargetProduct(String receivedData) {
		Vibration vibration = new Vibration();
		if (vibration.jsonResolve(receivedData)) {
			System.out.println("control_info 解析成功\r\n");

			ChatManager.getChatManager().toTargetProduct(
					vibration.getProductMac(), receivedData);

			// ConnectDatabase db = new ConnectDatabase();
			// Connection conn = (Connection) db.ConnectMysql();
			// db.updatControlToSql(control);
			// try {
			// db.CutConnection(conn);
			// } catch (SQLException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		}

	}

//	// -----------------------------------------------------开始采集数据
//	public void startRecvDataInfo(String receivedData) {
//		Vibration vibration = new Vibration();
//		if (vibration.jsonResolve(receivedData)) {
//			System.out.println("control_info 解析成功\r\n");
//			this.username = vibration.getName();
//			startReceiveData = true;
//		}
//
//	}

	// // -----------------------------------------------------控制制定目标
	// public void controlTargetProduct(String receivedData) {
	// Control control = new Control();
	// if (control.jsonResolve(receivedData)) {
	// System.out.println("control_info 解析成功\r\n");
	//
	// ChatManager.getChatManager().toTargetProduct(
	// control.getProductMac(), receivedData);
	//
	// ConnectDatabase db = new ConnectDatabase();
	// Connection conn = (Connection) db.ConnectMysql();
	// db.updatControlToSql(control);
	// try {
	// db.CutConnection(conn);
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// }

	/********************* 更新产品反馈信息 ****************************/
	public boolean uploadProductInfo(String receivedData) {
		Vibration vibration = new Vibration();

		if (vibration.jsonResolveProductInfo(receivedData)) {
			System.out.println("productInfo 解析成功\r\n");
			this.mac = vibration.getProductMac();// 记录当前socket对应mac地址
			return true;
		}
		return false;
	}

	// /********************* 更新产品反馈信息 ****************************/
	// public boolean uploadProductInfo(String receivedData) {
	// Product product = new Product();
	//
	// if (product.jsonResolve(receivedData)) {
	// System.out.println("productInfo 解析成功\r\n");
	// this.mac = product.getProductMac();// 记录当前socket对应mac地址
	//
	// // --------------------------------------将上报信息写入数据库
	// ConnectDatabase db = new ConnectDatabase();
	// Connection conn = (Connection) db.ConnectMysql();
	// if (db.selectMacID(product.getProductMac()) != null) {
	// if (db.updatProductToSql(product)) {
	// System.out.println("产品信息更新成功!\r\n");
	// } else {
	// System.out.println("产品信息更新失败!\r\n");
	// }
	//
	// } else {
	// if (db.InsertProductToSql(product)) {
	// System.out.println("产品信息插入成功!\r\n");
	// } else {
	// System.out.println("产品信息插入失败!\r\n");
	// }
	// }
	// if (db.InsertProductToSqlLog(product)) {
	// System.out.println("成功生成一条历史纪录!\r\n");
	// } else {
	// System.out.println("历史纪录写入失败!\r\n");
	// }
	// // --------------------------------------将上报信息写入数据库
	//
	// // --------------------------------------将上报信息转发给各个用户
	// ArrayList<String> userStrings = db.selectUsersFromMac(product
	// .getProductMac());
	// if (userStrings != null) {
	// ChatManager.getChatManager().toTargetUser(userStrings,
	// receivedData);
	// }
	//
	// // --------------------------------------将上报信息转发给各个用户
	// try {
	// db.CutConnection(conn);
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return true;
	// } else {
	// return false;
	// }
	//
	//
	// }

	/********************* 获取注册用户的信息 ****************************/
	public boolean userRegist(String receivedData) {
		JSONObject root;
		User user = new User();
		try {
			root = new JSONObject(receivedData);
			String username = root.getString("userName");
			String password = root.getString("passWord");
			System.out.println("收到用户注册信息：用户名" + username + "密码：" + password
					+ "\r\n");
			ConnectDatabase db = new ConnectDatabase();
			Connection conn = (Connection) db.ConnectMysql();
			user.setUsername(username);
			user.setPassword(password);
			boolean havedata = db.SelectUserFromSql(user);

			if (havedata) {
				System.out.println("该用户已被注册!\r\n");
				user.setRegStat("Fail");
				try {
					db.CutConnection(conn);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (db.InsertUserToSql(user)) {
					System.out.println("注册成功!\r\n");
					user.setRegStat("Success");
					this.username = user.getUsername();
				}
				try {
					db.CutConnection(conn);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (user.getRegStat().equals("Success")) {
				user.setJsontype("regResult");
				String regresult = user.jsonCreat();
				ChatManager.getChatManager().replay(this, regresult);
				System.out.println("注册内容为：" + regresult + "\r\n");
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("无法解析json!\r\n");

			e.printStackTrace();
			return false;
		}
	}

	/********************* 用户登入 ****************************/
	public boolean userLogin(String receivedData) {
		JSONObject root;
		User user = new User();
		try {
			root = new JSONObject(receivedData);
			String username = root.getString("userName");
			String password = root.getString("passWord");
			System.out.println("收到用户登入：用户名" + username + "密码：" + password
					+ "\r\n");
			user.setUsername(username);
			// user.setPassword(password);
			ConnectDatabase db = new ConnectDatabase();
			Connection conn = (Connection) db.ConnectMysql();
			boolean havedata = db.SelectUserFromSql(user);
			//
			// System.out.println("已存的用户名密码是：" + user.getPassword() +
			// "，用户登入的密码是"
			// + password + "\r\n");
			if (havedata) {
				if (user.getPassword().equals(password)) {
					System.out.println(user.getUsername() + "登入成功!\r\n");
					this.username = user.getUsername();
					user.setLoginStat("Success");
				} else {
					System.out.println("密码错误!\r\n");

					user.setLoginStat("Fail");
				}
				user.setRegStat("Fail");
				try {
					db.CutConnection(conn);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("用户名不存在!\r\n");
				user.setLoginStat("Fail");
				try {
					db.CutConnection(conn);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			user.setJsontype("loginResult");
			String regresult = user.jsonCreat();
			ChatManager.getChatManager().replay(this, regresult);

			if (user.getLoginStat().equals("Success")) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("无法解析json!\r\n");

			e.printStackTrace();
			return false;
		}
	}

//	/*
//	 * * @Override public void run() {
//	 * 
//	 * try { BufferedReader br = new BufferedReader(new InputStreamReader(
//	 * socket.getInputStream(), "UTF-8")); String line = null; while ((line =
//	 * br.readLine()) != null) { // ChatManager.getChatManager().publish(this,
//	 * line); //转发信息 // ChatManager.getChatManager().replay(this, "已经收到您的来信");
//	 * 
//	 * StringBuilder builder = new StringBuilder(); builder.append(line);
//	 * System.out.println("接收到的字符串为：\r\n" + line); this.receivedData =
//	 * builder.toString(); whatTodo(this.receivedData); //
//	 * saveToSql(this.receivedData); //
//	 * System.out.println("userName="+user.getuserName()); //
//	 * System.out.println("passWord="+user.getpassWord()); //
//	 * System.out.println("SSID="+user.getSsid()); //
//	 * System.out.println("Key="+user.getKey()); }
//	 * 
//	 * br.close(); } catch (UnsupportedEncodingException e) {
//	 * System.out.println("socket通讯异常"); e.printStackTrace(); } catch
//	 * (IOException e) { System.out.println("socket通讯异常"); e.printStackTrace();
//	 * } finally { ChatManager.getChatManager().sub(this); }
//	 * 
//	 * }
//	 */
	static final String spliterString="WILLTECH_BLUE";
	@Override
	public void run() {
		try {
			String[] dataStrings;
			String jsonString;
			String dataString;
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")); 
			String line = null; 
			while ((line =br.readLine()) != null)
		    {

				dataStrings = line.split(spliterString);
				if (dataStrings.length <= 1) {
					jsonString=dataStrings[0];
					whatTodo(jsonString);
				} else {
					jsonString = dataStrings[0];
					dataString = dataStrings[1];
					
					System.out.println(jsonString);
					System.out.println(dataString);
					
					Vibration vibration = new Vibration();
					vibration.jsonResolve(jsonString);
					System.out.println("the user name is"+vibration.getUserName());
					
					vibration.dealWithSensData(dataString);
					ChatManager.getChatManager().toTargetUser(vibration.getUserName(),
							line);
					line="";
					dataStrings=null;
					dataString="";
					jsonString="";
				}
			}

		} catch (UnsupportedEncodingException e) {
			System.out.println("socket通讯异常");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("socket通讯异常");
			e.printStackTrace();
		} finally {
			System.out.println("socket通讯异常");
			ChatManager.getChatManager().sub(this);
		}
	}


//	@Override
//	public void run() {
//		try {
////			String name = ManagementFactory.getRuntimeMXBean().getName();
//			// String pid = name.split("@")[0];
//			// System.out.println(pid);
//			String[] dataStrings;
//			String jsonString;
//			String dataString;
//			int nbytes;
////			int i=0;
//			
//			while ((nbytes = socket.getInputStream().read(readBuffer))!=0) {
//
//				String line = new String(readBuffer, "UTF-8");
//				line=line.substring(0, nbytes);
//                String valuedLine=line.substring(0, nbytes);
//                dataStrings = valuedLine.split("666");
////                out("connect"+String.valueOf(i++));
//				if (dataStrings.length <= 1) {
//					jsonString=dataStrings[0];
//					whatTodo(jsonString);
//				} else {
//					jsonString = dataStrings[0];
//					dataString = dataStrings[1];
//					
//					System.out.println(jsonString);
//					System.out.println(dataString);
//					
//					Vibration vibration = new Vibration();
//					vibration.jsonResolve(jsonString);
//					System.out.println("the user name is"+vibration.getUserName());
//					
//					vibration.dealWithSensData(dataString.getBytes());
//					ChatManager.getChatManager().toTargetUser(vibration.getUserName(),
//							readBuffer);
//				}
//			}
//
//		} catch (UnsupportedEncodingException e) {
//			System.out.println("socket通讯异常");
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.out.println("socket通讯异常");
//			e.printStackTrace();
//		} finally {
//			System.out.println("socket通讯异常");
//			ChatManager.getChatManager().sub(this);
//		}
//	}

}
