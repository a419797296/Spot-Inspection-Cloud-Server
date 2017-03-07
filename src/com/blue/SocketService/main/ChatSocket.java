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

	/*********************** �ظ���Ϣ ***************************/
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

	/*********************** �ظ���Ϣ ***************************/
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

	/*********************** �ж�������json���ݣ�clothes_info,user_info OR control_info�� ***************************/
	public String judgeJsonType(String jsondate) {
		String jsontype = "chatInfo";
		JSONObject root;
		try {
			root = new JSONObject(jsondate);
			jsontype = root.getString("jsonType");
			System.out.println("���ַ�����Json������������" + jsontype + "\r\n");
			return jsontype;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("�޷�����json!\r\n");

			e.printStackTrace();
			return jsontype;
		}
	}

	/*********************** ��������Ҫ������Щ���� ***************************/
	public void whatTodo(String receivedData) {
		String jsontype = judgeJsonType(receivedData);
		switch (jsontype) {
		case "chatInfo":
			System.out.println("��Json����chatInfo\r\n");
			break;
		case "loginInfo":
			System.out.println("��Json����loginInfo\r\n");
			userLogin(receivedData);
			break;
		case "regInfo":
			System.out.println("��Json����regInfo\r\n");
			userRegist(receivedData);

			break;
		case "productInfo":
			System.out.println("��Json����productInfo\r\n");
			uploadProductInfo(receivedData);

			break;
		case "controlInfo":
			System.out.println("��Json����controlInfo\r\n");
			controlTargetProduct(receivedData);
			break;
		case "startRecvDataInfo":
			System.out.println("��Json����startRecvDataInfo\r\n");
			//startRecvDataInfo(receivedData);
			break;
		case "wifiInfo":
			System.out.println("��Json����wifiInfo\r\n");
			// new User().saveAsFileWriter("C:\\sockTest.txt",
			// this.receivedData);
			ChatManager
					.getChatManager()
					.replay(this,
							"{\"jsonType\":\"productInfo\",\"productMac\":\"aaaaaaaa\"}");
			break;
		case "productReg":
			System.out.println("��Json����productReg\r\n");
			ChatManager.getChatManager().replay(this, "�������Ѿ��յ���Ʒmac��ַ");
			productReg(receivedData);
			// ChatManager.getChatManager().add(this);
			break;

		default:
			break;
		}
	}

	/********************* ע���Ʒ���Զ���product_info,control_info�и�����һ����¼ ****************************/
	public boolean productReg(String receivedData) {
		JSONObject root;
		boolean isSuccess = false;
		try {
			root = new JSONObject(receivedData);
			String mac = root.getString("productMac");
			String userName = root.getString("userName");
			ConnectDatabase db = new ConnectDatabase();
			Connection conn = (Connection) db.ConnectMysql();
			// -------------------��product_info������һ����¼
			Product product = new Product();
			product.setProductMac(mac);

			// -------------------�жϸò�Ʒ�Ƿ��Ѿ�ע��

			String macID = db.selectMacID(mac);
			String userID = db.selectuserID(userName);
			System.out.println("�û�ID:!\r\n" + userID);
			if (macID == null) { // ûע��
				db.InsertProductToSql(product);
				// -------------------��product_info��user_info�л�ö�Ӧ��id��д��control_info��
				macID = db.selectMacID(mac);
				Control control = new Control();
				control.setProductID(macID);
				control.setUserID(userID);
				db.InsertControlToSql(control);
				System.out.println("�û��Ͳ�Ʒƥ��ɹ�!\r\n");

				isSuccess = true;
			} else {
				System.out.println("�û��Ͳ�Ʒ�Ѵ���ƥ���¼!\r\n");
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
			System.out.println("�޷�����json!\r\n");

			e.printStackTrace();
			return false;
		}
	}

	// -----------------------------------------------------�����ƶ�Ŀ��
	public void controlTargetProduct(String receivedData) {
		Vibration vibration = new Vibration();
		if (vibration.jsonResolve(receivedData)) {
			System.out.println("control_info �����ɹ�\r\n");

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

//	// -----------------------------------------------------��ʼ�ɼ�����
//	public void startRecvDataInfo(String receivedData) {
//		Vibration vibration = new Vibration();
//		if (vibration.jsonResolve(receivedData)) {
//			System.out.println("control_info �����ɹ�\r\n");
//			this.username = vibration.getName();
//			startReceiveData = true;
//		}
//
//	}

	// // -----------------------------------------------------�����ƶ�Ŀ��
	// public void controlTargetProduct(String receivedData) {
	// Control control = new Control();
	// if (control.jsonResolve(receivedData)) {
	// System.out.println("control_info �����ɹ�\r\n");
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

	/********************* ���²�Ʒ������Ϣ ****************************/
	public boolean uploadProductInfo(String receivedData) {
		Vibration vibration = new Vibration();

		if (vibration.jsonResolveProductInfo(receivedData)) {
			System.out.println("productInfo �����ɹ�\r\n");
			this.mac = vibration.getProductMac();// ��¼��ǰsocket��Ӧmac��ַ
			return true;
		}
		return false;
	}

	// /********************* ���²�Ʒ������Ϣ ****************************/
	// public boolean uploadProductInfo(String receivedData) {
	// Product product = new Product();
	//
	// if (product.jsonResolve(receivedData)) {
	// System.out.println("productInfo �����ɹ�\r\n");
	// this.mac = product.getProductMac();// ��¼��ǰsocket��Ӧmac��ַ
	//
	// // --------------------------------------���ϱ���Ϣд�����ݿ�
	// ConnectDatabase db = new ConnectDatabase();
	// Connection conn = (Connection) db.ConnectMysql();
	// if (db.selectMacID(product.getProductMac()) != null) {
	// if (db.updatProductToSql(product)) {
	// System.out.println("��Ʒ��Ϣ���³ɹ�!\r\n");
	// } else {
	// System.out.println("��Ʒ��Ϣ����ʧ��!\r\n");
	// }
	//
	// } else {
	// if (db.InsertProductToSql(product)) {
	// System.out.println("��Ʒ��Ϣ����ɹ�!\r\n");
	// } else {
	// System.out.println("��Ʒ��Ϣ����ʧ��!\r\n");
	// }
	// }
	// if (db.InsertProductToSqlLog(product)) {
	// System.out.println("�ɹ�����һ����ʷ��¼!\r\n");
	// } else {
	// System.out.println("��ʷ��¼д��ʧ��!\r\n");
	// }
	// // --------------------------------------���ϱ���Ϣд�����ݿ�
	//
	// // --------------------------------------���ϱ���Ϣת���������û�
	// ArrayList<String> userStrings = db.selectUsersFromMac(product
	// .getProductMac());
	// if (userStrings != null) {
	// ChatManager.getChatManager().toTargetUser(userStrings,
	// receivedData);
	// }
	//
	// // --------------------------------------���ϱ���Ϣת���������û�
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

	/********************* ��ȡע���û�����Ϣ ****************************/
	public boolean userRegist(String receivedData) {
		JSONObject root;
		User user = new User();
		try {
			root = new JSONObject(receivedData);
			String username = root.getString("userName");
			String password = root.getString("passWord");
			System.out.println("�յ��û�ע����Ϣ���û���" + username + "���룺" + password
					+ "\r\n");
			ConnectDatabase db = new ConnectDatabase();
			Connection conn = (Connection) db.ConnectMysql();
			user.setUsername(username);
			user.setPassword(password);
			boolean havedata = db.SelectUserFromSql(user);

			if (havedata) {
				System.out.println("���û��ѱ�ע��!\r\n");
				user.setRegStat("Fail");
				try {
					db.CutConnection(conn);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (db.InsertUserToSql(user)) {
					System.out.println("ע��ɹ�!\r\n");
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
				System.out.println("ע������Ϊ��" + regresult + "\r\n");
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("�޷�����json!\r\n");

			e.printStackTrace();
			return false;
		}
	}

	/********************* �û����� ****************************/
	public boolean userLogin(String receivedData) {
		JSONObject root;
		User user = new User();
		try {
			root = new JSONObject(receivedData);
			String username = root.getString("userName");
			String password = root.getString("passWord");
			System.out.println("�յ��û����룺�û���" + username + "���룺" + password
					+ "\r\n");
			user.setUsername(username);
			// user.setPassword(password);
			ConnectDatabase db = new ConnectDatabase();
			Connection conn = (Connection) db.ConnectMysql();
			boolean havedata = db.SelectUserFromSql(user);
			//
			// System.out.println("�Ѵ���û��������ǣ�" + user.getPassword() +
			// "���û������������"
			// + password + "\r\n");
			if (havedata) {
				if (user.getPassword().equals(password)) {
					System.out.println(user.getUsername() + "����ɹ�!\r\n");
					this.username = user.getUsername();
					user.setLoginStat("Success");
				} else {
					System.out.println("�������!\r\n");

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
				System.out.println("�û���������!\r\n");
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
			System.out.println("�޷�����json!\r\n");

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
//	 * line); //ת����Ϣ // ChatManager.getChatManager().replay(this, "�Ѿ��յ���������");
//	 * 
//	 * StringBuilder builder = new StringBuilder(); builder.append(line);
//	 * System.out.println("���յ����ַ���Ϊ��\r\n" + line); this.receivedData =
//	 * builder.toString(); whatTodo(this.receivedData); //
//	 * saveToSql(this.receivedData); //
//	 * System.out.println("userName="+user.getuserName()); //
//	 * System.out.println("passWord="+user.getpassWord()); //
//	 * System.out.println("SSID="+user.getSsid()); //
//	 * System.out.println("Key="+user.getKey()); }
//	 * 
//	 * br.close(); } catch (UnsupportedEncodingException e) {
//	 * System.out.println("socketͨѶ�쳣"); e.printStackTrace(); } catch
//	 * (IOException e) { System.out.println("socketͨѶ�쳣"); e.printStackTrace();
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
			System.out.println("socketͨѶ�쳣");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("socketͨѶ�쳣");
			e.printStackTrace();
		} finally {
			System.out.println("socketͨѶ�쳣");
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
//			System.out.println("socketͨѶ�쳣");
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.out.println("socketͨѶ�쳣");
//			e.printStackTrace();
//		} finally {
//			System.out.println("socketͨѶ�쳣");
//			ChatManager.getChatManager().sub(this);
//		}
//	}

}
