package com.blue.SocketService.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServiceLisnter extends Thread {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		// TODO Auto-generated method stub
		ServerSocket serverSocket=null;
		try {
			serverSocket = new ServerSocket(3333);// 1-65535
			System.out.println("服务器启动完成，监听端口在3333");
			System.out.println("正在等待客户连接.........");			
			while (true) {
				Socket socket = serverSocket.accept();
//				System.out.println("客户发来连接请求.........");
				
				// 建立连接
				//JOptionPane.showMessageDialog(null, "有客户端链接到了本机的12345端口");
				//将socket传递给新的进程
				ChatSocket cs =new ChatSocket(socket);
				cs.start();

				ChatManager.getChatManager().add(cs);
			}
			// block
//			serverSocket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				serverSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}
