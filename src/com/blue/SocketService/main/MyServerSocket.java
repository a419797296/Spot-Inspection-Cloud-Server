package com.blue.SocketService.main;

public class MyServerSocket {

	public static void main(String[] args) {
		
		Vibration vibration =new Vibration();
		System.out.println(vibration.jsonCreat());
		new ServiceLisnter().start();
//
//		Product product=new Product();
//		
//		product.setIsDark("yes");
//		product.setIsRain("yes");
//		product.setSOC(90);
//		product.setStat("in");
//		product.setTemperature(28);
//		product.setHuminity(20.1);
//		product.setBuyDate("2015");
//		product.setError("can't move in");
//		product.setLux(1000);
//		product.setProductMac("AAAAAA");
//		
//		product.setSoftwareVersion("1.0");
//		product.setHardwareVersion("1.0");
//		
//		String jsondata=product.jsonCreat();
//		System.out.println(jsondata);
//		
//		
//		String updateContext;
//		updateContext="stat=\""+product.getStat()+
//				"\",SOC="+product.getSOC()+
//				",temp="+product.getTemperature()+
//				",hum="+product.getHuminity()+
//				",lux="+product.getLux()+
//				",isdark=\""+product.getIsDark()+
//				"\",israin=\""+product.getIsRain()+
//				"\",error=\""+product.getError()+"\"";
//		String sql="update product_info set"+updateContext+"where MAC=\""+product.getProductMac()+"\"";
//		System.out.println(updateContext+"\r\n");
//		System.out.println(sql+"\r\n");
	}
}
