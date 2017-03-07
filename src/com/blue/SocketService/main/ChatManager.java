package com.blue.SocketService.main;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {
	private ChatManager() {
	}

	private static final ChatManager cm = new ChatManager();

	public static ChatManager getChatManager() {
		return cm;
	}

	Map<String, ChatSocket> clientsMap = new ConcurrentHashMap<String, ChatSocket>();

	Vector<ChatSocket> vector = new Vector<ChatSocket>();

	// -------------------------�Ƿ�ӵ����ͬ��mac��ַ---------------------------------
	public int isSameMAC(String MAC) {

		int isSameMAC = -1; // -1��ʾĬ�ϲ�������ͬ��MAC
		ChatSocket csChatSocket;
		for (int i = 0; i < vector.size(); i++) {
			csChatSocket = vector.get(i);
			if (MAC.equals(csChatSocket.getMac())) {
				isSameMAC = i;
				break;
			}
		}
		return isSameMAC;
	}

	// -------------------------�Ƿ�ӵ����ͬ��user---------------------------------
	public int isSameUser(String user) {

		int isSameUser = -1; // -1��ʾĬ�ϲ�������ͬ��MAC
		ChatSocket csChatSocket;
		for (int i = 0; i < vector.size(); i++) {
			csChatSocket = vector.get(i);
			if (user.equals(csChatSocket.getUsername())) {
				isSameUser = i;
				break;
			}
		}
		return isSameUser;
	}

	// -------------------------�Ƿ�ӵ����ͬ��socket����---------------------------------
	public int isSameSocket(ChatSocket socket) {

		int isSameSocket = -1; // -1��ʾĬ�ϲ�������ͬ��MAC
		ChatSocket csChatSocket;
		for (int i = 0; i < vector.size(); i++) {
			csChatSocket = vector.get(i);
			if (socket.equals(csChatSocket)) {
				isSameSocket = i;
				break;
			}
		}
		return isSameSocket;
	}

	// -------------------------����һ������---------------------------------

	public void add(ChatSocket cs) {
		// int targaetIdx = isSameMAC(cs.mac);
		int targaetIdx = isSameSocket(cs);
		// System.out.printf("�����±���%d\r\n", targaetIdx);
		if (targaetIdx == -1) { // û���ظ���MAC
			vector.add(cs);
			System.out.printf("��%d���û��ѳɹ�����,�û���Ϊ��%s\r\n", vector.size(),
					cs.getMac());
			cs.out("Connected");
		} else {
			System.out.printf("���󣬴�����ͬ���ӣ����±���%d\r\n", targaetIdx);
		}
	}

	// -------------------------ɾ����ǰ����---------------------------------
	public void sub(ChatSocket cs) {

		int idx = vector.indexOf(cs);
		if (idx != -1) {
			vector.remove(idx);
			System.out.printf("��%d���û������ߣ��û���Ϊ��%s��MAC��ַΪ��%s����ǰ��������Ϊ%d\r\n",
					idx + 1, cs.getUsername(), cs.getMac(), vector.size());
		} else {
			System.out.printf("---------------->�����쳣");
		}

	}

	// -------------------------�ظ���ǰ�ͻ��ͻ�---------------------------------
	public void replay(ChatSocket cs, String out) {
		for (int i = 0; i < vector.size(); i++) {
			ChatSocket csChatSocket = vector.get(i);
			if (cs.equals(csChatSocket)) {
				csChatSocket.out(out);
				break;
			}
		}
	}

	// -------------------------ת�������Լ������пͻ�---------------------------------
	public void publish(Socket socket, byte[] receivedByteData) {
		for (int i = 0; i < vector.size(); i++) {
			ChatSocket csChatSocket = vector.get(i);
			if (!socket.equals(csChatSocket)) {
				csChatSocket.out(receivedByteData);
			}
		}
	}
	// -------------------------ת�������Լ������пͻ�---------------------------------
	public void publish(ChatSocket cs,String out) {
		for (int i = 0; i < vector.size(); i++) {
			ChatSocket csChatSocket = vector.get(i);
			if (!cs.equals(csChatSocket)) {
				csChatSocket.out(out);
			}
		}
	}

	// -------------------------ת����ָ����Ʒ---------------------------------
	public void toTargetProduct(String Mac, String content) {

		int targaetIdx = isSameMAC(Mac);
		
		if (targaetIdx != -1) { // ��ƥ�䵽Ŀ��MAC
			ChatSocket csChatSocket = vector.get(targaetIdx);
			csChatSocket.out(content.getBytes());  //��getbyte�ı�ʾ������\r\n������־
			System.out.printf("�������͸�%d�Ų�Ʒ����Ʒmac��ַ��%s\r\n", targaetIdx + 1,Mac);
		}
		else {
			System.out.println(Mac+"δ����");
		}
	}
	// -------------------------ת����ָ���û�---------------------------------
	public void toTargetUser(String username, byte[] content) {

		int targaetIdx = isSameUser(username);
		if (targaetIdx != -1) { // ��ƥ�䵽Ŀ���û�
			ChatSocket csChatSocket = vector.get(targaetIdx);
			csChatSocket.out(content);
			System.out.println("�����Ѳ�Ʒ�ɼ��������ݷ��͸�"+username);
		}
		else {
			System.out.println(username+"δ����");
		}
	}
	// -------------------------ת����ָ���û�---------------------------------
	public void toTargetUser(String username, String content) {

		int targaetIdx = isSameUser(username);
		if (targaetIdx != -1) { // ��ƥ�䵽Ŀ���û�
			ChatSocket csChatSocket = vector.get(targaetIdx);
			csChatSocket.out(content);
			System.out.println("�����Ѳ�Ʒ�ɼ��������ݷ��͸�"+username);
		}
		else {
			System.out.println(username+"δ����");
		}
	}
	// -------------------------ת����ָ���ͻ�---------------------------------
	public void toTargetUser(ArrayList<String> usernames, String content) {
		for (int i = 0; i < usernames.size(); i++) {
			String username = usernames.get(i);
			int targaetIdx = isSameUser(username);
			if (targaetIdx != -1) { // ��ƥ�䵽Ŀ��MAC
				ChatSocket csChatSocket = vector.get(targaetIdx);
				csChatSocket.out(content);
				System.out.println("�����Ѳ�Ʒ�ɼ��������ݷ��͸�"+username);
			}
			else {
				System.out.println(username+"δ����");
			}
		}

	}
}
