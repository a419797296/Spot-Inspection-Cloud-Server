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
			System.out.println("������������ɣ������˿���3333");
			System.out.println("���ڵȴ��ͻ�����.........");			
			while (true) {
				Socket socket = serverSocket.accept();
//				System.out.println("�ͻ�������������.........");
				
				// ��������
				//JOptionPane.showMessageDialog(null, "�пͻ������ӵ��˱�����12345�˿�");
				//��socket���ݸ��µĽ���
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
