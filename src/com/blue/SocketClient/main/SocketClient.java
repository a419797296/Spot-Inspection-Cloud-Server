package com.blue.SocketClient.main;

import com.blue.SocketService.main.Product;

public class SocketClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String modelString = "sta";
		if (modelString.equals("ap")) {
			new Service().start();
		} else {
			Client chatSocket = new Client("192.168.31.159", 3333);
			chatSocket.start();
			//chatSocket.Send("789");
			//chatSocket.Send(creatProductData());
		}

		// ChatSocket.timer();
	}

	public static String creatProductData() {
		Product product = new Product();
		product.setIsDark("yes");
		product.setIsRain("yes");
		product.setSOC(90);
		product.setStat("in");
		product.setTemperature(28);
		product.setHuminity(20.1);
		product.setBuyDate("2015");
		product.setError("can't move in");
		product.setLux(1000);
		product.setProductMac("aaaaaaaa");

		product.setSoftwareVersion("1.0");
		product.setHardwareVersion("1.0");

		String jsondata = product.jsonCreat();
		System.out.println(jsondata);
		return jsondata;
	}
}
