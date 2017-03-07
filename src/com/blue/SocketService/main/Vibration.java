package com.blue.SocketService.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

public class Vibration {
	private String userID = "";
	private String productID = "";
	private String productMac = "";
	private String userName = "blue";
	private String filePath = "";
	private String fileName = "";
	private String Name = "";
	private int freq = 100;
	private int sampleNum = 5;
	private int[] sensData=new int[5000];	
	
	
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}





	public int[] getSensData() {
		return sensData;
	}

	public void setSensData(int[] sensData) {
		this.sensData = sensData;
	}

	final public String absoluatePath = "E:\\";
//	final public String absoluatePath = "/test/";
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

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

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public int getSampleNum() {
		return sampleNum;
	}

	public void setSampleNum(int sampleNum) {
		this.sampleNum = sampleNum;
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

	// ---------------------------------得到绝对路径

	public String getFilePath(String fileName) {
		SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String datetimes = formatter.format(curDate);
		// file save
		String tempdata = datetimes;
		tempdata = tempdata.replace(":", "");
		tempdata = tempdata.replace("-", "");
		tempdata = tempdata.replace(" ", "_");
		// tempdata=tempdata.substring(2);
		this.filePath = absoluatePath + fileName + "_" + tempdata + ".txt";
		return this.filePath;
	}
	
    // ---------------------------------得到传感器数据

    public static int[] bytesToAdvalue(String dataString) {
        byte[] readBuffer=dataString.getBytes();
        int[] sensData=new int[(readBuffer.length>>1)-1];
        for (int i = 0; i < (readBuffer.length>>1)-1; i++) {
            sensData[i] = (int) readBuffer[i + i] & 0xff;
            sensData[i] <<= 8;
            sensData[i] |= (int) readBuffer[i + i + 1] & 0xff;
            sensData[i] = (sensData[i] * 5000) >> 15; // AD值转电压值
            System.out.printf("采集到的AD值为: %d\r\n", sensData[i]);
        }
        return sensData;
    }

	// ---------------------------------保存成文件

	public void saveDateToFile(String filePath, int[] sensData) {
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			fileOutputStream = new FileOutputStream(filePath, true);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream,
					"utf-8");
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			bufferedWriter.write("Frequency"+"\t" + this.freq + "\r\n");
			bufferedWriter.write("sampleNum"+"\t" + this.sampleNum + "\r\n");
			for (int i = 0; i < this.sampleNum ; i++) {
				bufferedWriter.write(String.valueOf(sensData[i])+"\r\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (bufferedWriter!=null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/********************* 创建Json串 ****************************/
	public String jsonCreat() {
		JSONObject root = new JSONObject();
		try {
			root.put("userName", this.userName);
			root.put("freq", this.freq);
			root.put("sampleNum", this.sampleNum);
			root.put("jsonType", "controlInfo");
			root.put("productMac", this.productMac);
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
			// this.userID = root.getString("userID");
			// this.productID = root.getString("productID");
			this.freq = root.getInt("freq");
			this.sampleNum = root.getInt("sampleNum");
			this.userName = root.getString("userName");
			this.productMac = root.getString("productMac");
			this.Name = root.getString("fileName");
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("无法解析json!\r\n");

			e.printStackTrace();
			return false;
		}
	}
	/********************* 解析Json串 ****************************/
	public boolean jsonResolveProductInfo(String jsondata) {
		JSONObject root;
		try {
			root = new JSONObject(jsondata);
			this.productMac = root.getString("productMac");
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("无法解析json!\r\n");

			e.printStackTrace();
			return false;
		}
	}
	
	// -----------------------------------------------------控制制定目标
	public void dealWithSensData(String readBuffer) {
		String filePath = getFilePath(this.Name);
		saveDateToFile(filePath, bytesToAdvalue(readBuffer));

	}
}
