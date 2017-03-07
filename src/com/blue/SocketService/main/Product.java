package com.blue.SocketService.main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Product {
	public String getProductMac() {
		return productMac;
	}
	public void setProductMac(String productMac) {
		this.productMac = productMac;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public String getIsDark() {
		return isDark;
	}
	public void setIsDark(String isDark) {
		this.isDark = isDark;
	}
	public String getIsRain() {
		return isRain;
	}
	public void setIsRain(String isRain) {
		this.isRain = isRain;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(String buyDate) {
		this.buyDate = buyDate;
	}	
	public String getHardwareVersion() {
		return hardwareVersion;
	}
	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}
	public String getSoftwareVersion() {
		return softwareVersion;
	}
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	public double getSOC() {
		return SOC;
	}
	public void setSOC(double sOC) {
		SOC = sOC;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public double getHuminity() {
		return huminity;
	}
	public void setHuminity(double huminity) {
		this.huminity = huminity;
	}
	public double getLux() {
		return lux;
	}
	public void setLux(double lux) {
		this.lux = lux;
	}

	private String productMac="";
	private String stat="";
	private String error="";
	private double SOC=0;
	
	private double temperature=0;
	private double huminity=0;
	private double lux=0;
	private String isDark="";
	private String isRain="";
	
	private String buyDate="";
	
	private String hardwareVersion="";
	private String softwareVersion="";

	/********************* 创建Json串 ****************************/
	public String jsonCreat() {
		JSONObject root = new JSONObject();
		try {
			
			root.put("buyDate", this.buyDate);
			root.put("jsonType", "productInfo");
			
			JSONObject product = new JSONObject();
			
			
			product.put("productMac", this.productMac);
			product.put("stat", this.stat);
			product.put("SOC", this.SOC);
			product.put("error", this.error);
			
			JSONObject environment = new JSONObject();
			environment.put("temperature", this.temperature);
			environment.put("huminity", this.huminity);
			environment.put("lux", this.lux);
			environment.put("isDark", this.isDark);
			environment.put("isRain", this.isRain);

			
			JSONObject version = new JSONObject();
			version.put("hardwareVersion", this.hardwareVersion);
			version.put("softwareVersion", this.softwareVersion);

			
			JSONArray product_array = new JSONArray();
			JSONArray environment_array = new JSONArray();
			JSONArray version_array = new JSONArray();
			
			product_array=product_array.put(product);
			environment_array=environment_array.put(environment);
			version_array=version_array.put(version);
			
			root.put("product", product_array);
			root.put("environment", environment_array);
			root.put("version", version_array);
			
//			root.put("SSID", this.ssid);
//			wifi.put("KEY", this.key);
//			
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
			this.buyDate = root.getString("buyDate");

			JSONArray product_array = root.getJSONArray("product");
			JSONArray environment_array = root.getJSONArray("environment");
			JSONArray version_array = root.getJSONArray("version");
			
			
			
			JSONObject product = product_array.getJSONObject(0);
			this.error=product.getString("error");
			this.SOC=product.getDouble("SOC");
			this.productMac=product.getString("productMac");
			this.stat=product.getString("stat");
			
			JSONObject environment = environment_array.getJSONObject(0);
			this.isDark=environment.getString("isDark");
			this.huminity=environment.getDouble("huminity");
			this.isRain=environment.getString("isRain");
			this.lux=environment.getDouble("lux");
			this.temperature=environment.getDouble("temperature");

			JSONObject version = version_array.getJSONObject(0);
			this.softwareVersion=version.getString("softwareVersion");
			this.hardwareVersion=version.getString("hardwareVersion");
			
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("无法解析json!\r\n");

			e.printStackTrace();
			return false;
		}
	}
	
}
