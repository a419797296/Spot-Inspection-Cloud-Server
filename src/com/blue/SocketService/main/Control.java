package com.blue.SocketService.main;

import org.json.JSONException;
import org.json.JSONObject;

public class Control {
	
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getTimeIn() {
		return timeIn;
	}
	public void setTimeIn(String timeIn) {
		this.timeIn = timeIn;
	}
	public String getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}
	public String getTrigStat() {
		return trigStat;
	}
	public void setTrigStat(String trigStat) {
		this.trigStat = trigStat;
	}
	private String userID="";
	private String timeIn="0";
	private String timeOut="0";
	private String trigStat="";
	private String productID="";
	private String productMac="";
	private String trigBy="";
	private String userName="";
	
	public String getTrigBy() {
		return trigBy;
	}
	public void setTrigBy(String trigBy) {
		this.trigBy = trigBy;
	}
	public String getProductMac() {
		return productMac;
	}
	public void setProductMac(String productMac) {
		this.productMac = productMac;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	/********************* 创建Json串 ****************************/
	public String jsonCreat() {
		JSONObject root = new JSONObject();
		try {
			root.put("userID", this.userID);
			root.put("productID", this.productID);
			root.put("timeIn", this.timeIn);
			root.put("timeOut", this.timeOut);
			root.put("trigStat", this.trigStat);
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
//			this.userID = root.getString("userID");
//			this.productID = root.getString("productID");
			this.timeIn = root.getString("timeIn");
			this.timeOut = root.getString("timeOut");
			this.trigStat = root.getString("controlCmd");
			this.userName = root.getString("userName");
			this.productMac = root.getString("productMac");
			this.trigBy = root.getString("trigBy");
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("无法解析json!\r\n");

			e.printStackTrace();
			return false;
		}
	}
	
	
	
}
