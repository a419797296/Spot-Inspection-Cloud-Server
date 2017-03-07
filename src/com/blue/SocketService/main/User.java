package com.blue.SocketService.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username="";
	private String password="";
	private String ssid="";
	private String key="";
	private String filepath="";
	private String regStat="";
	private String loginStat="";
	private String jsontype="";
	private String userID="";
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getJsontype() {
		return jsontype;
	}

	public void setJsontype(String jsontype) {
		this.jsontype = jsontype;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getRegStat() {
		return regStat;
	}

	public void setRegStat(String regStat) {
		this.regStat = regStat;
	}

	public String getLoginStat() {
		return loginStat;
	}

	public void setLoginStat(String loginStat) {
		this.loginStat = loginStat;
	}

	/********************* 创建Json串 ****************************/
	public String jsonCreat() {
		JSONObject root = new JSONObject();
		try {
			root.put("jsonType", this.jsontype);
			root.put("userName", this.username);
			root.put("passWord", this.password);
			root.put("loginStat", this.loginStat);
			root.put("regStat", this.regStat);
//			root.put("SSID", this.ssid);
//			wifi.put("KEY", this.key);
//			JSONObject wifi = new JSONObject();
//			wifi.put("SSID", this.ssid);
//			wifi.put("KEY", this.key);
//
////			JSONArray array = new JSONArray();
//			array.put(wifi);
//
//			root.put("wifi", array);
			// System.out.println(root.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return root.toString();
	}

	/********************* 解析Json串 ****************************/
	public boolean jsonResolve(String jsondata) {
		JSONObject root;
		try {
			root = new JSONObject(jsondata);
			this.username = root.getString("username");
			this.password = root.getString("password");

			JSONArray array = root.getJSONArray("wifi");
			JSONObject wifi = array.getJSONObject(0);
			this.ssid = wifi.getString("SSID");
			this.key = wifi.getString("KEY");
			System.out.println("密码是" + this.key);
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("无法解析json!\r\n");

			e.printStackTrace();
			return false;
		}
	}

	/********************* 保存json数据串 ****************************/
	public void saveAsFileWriter(String path, String content) {

		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(path);
			fwriter.write(content);
			System.out.println("已经成功将内容写入" + path);
		} catch (IOException ex) {
			System.out.println("文件写入失败");
			ex.printStackTrace();
		} finally {
			try {
				fwriter.flush();
				fwriter.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
