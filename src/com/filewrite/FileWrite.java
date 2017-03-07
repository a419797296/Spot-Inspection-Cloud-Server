package com.filewrite;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;


public class FileWrite {

	public static void main(String[] args) throws UnsupportedEncodingException {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
//		Date time = new Date(System.currentTimeMillis());// 获取当前时间
		
		Date time= new java.sql.Date(new java.util.Date().getTime());
		String datetimes = formatter.format(time);
		System.out.println(time.toString());
		System.out.println(time.toLocaleString());
		System.out.println(time.toGMTString());
		System.out.println(datetimes);

		
	}
	
	
	

}
