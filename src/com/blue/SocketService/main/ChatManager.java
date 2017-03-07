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

	// -------------------------是否拥有相同的mac地址---------------------------------
	public int isSameMAC(String MAC) {

		int isSameMAC = -1; // -1表示默认不存在相同的MAC
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

	// -------------------------是否拥有相同的user---------------------------------
	public int isSameUser(String user) {

		int isSameUser = -1; // -1表示默认不存在相同的MAC
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

	// -------------------------是否拥有相同的socket链接---------------------------------
	public int isSameSocket(ChatSocket socket) {

		int isSameSocket = -1; // -1表示默认不存在相同的MAC
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

	// -------------------------增加一个链接---------------------------------

	public void add(ChatSocket cs) {
		// int targaetIdx = isSameMAC(cs.mac);
		int targaetIdx = isSameSocket(cs);
		// System.out.printf("返回下标是%d\r\n", targaetIdx);
		if (targaetIdx == -1) { // 没有重复的MAC
			vector.add(cs);
			System.out.printf("第%d个用户已成功连接,用户名为：%s\r\n", vector.size(),
					cs.getMac());
			cs.out("Connected");
		} else {
			System.out.printf("错误，存在相同链接，其下标是%d\r\n", targaetIdx);
		}
	}

	// -------------------------删除当前链接---------------------------------
	public void sub(ChatSocket cs) {

		int idx = vector.indexOf(cs);
		if (idx != -1) {
			vector.remove(idx);
			System.out.printf("第%d个用户已下线！用户名为：%s，MAC地址为：%s，当前在线人数为%d\r\n",
					idx + 1, cs.getUsername(), cs.getMac(), vector.size());
		} else {
			System.out.printf("---------------->出现异常");
		}

	}

	// -------------------------回复当前客户客户---------------------------------
	public void replay(ChatSocket cs, String out) {
		for (int i = 0; i < vector.size(); i++) {
			ChatSocket csChatSocket = vector.get(i);
			if (cs.equals(csChatSocket)) {
				csChatSocket.out(out);
				break;
			}
		}
	}

	// -------------------------转发给除自己外所有客户---------------------------------
	public void publish(Socket socket, byte[] receivedByteData) {
		for (int i = 0; i < vector.size(); i++) {
			ChatSocket csChatSocket = vector.get(i);
			if (!socket.equals(csChatSocket)) {
				csChatSocket.out(receivedByteData);
			}
		}
	}
	// -------------------------转发给除自己外所有客户---------------------------------
	public void publish(ChatSocket cs,String out) {
		for (int i = 0; i < vector.size(); i++) {
			ChatSocket csChatSocket = vector.get(i);
			if (!cs.equals(csChatSocket)) {
				csChatSocket.out(out);
			}
		}
	}

	// -------------------------转发给指定产品---------------------------------
	public void toTargetProduct(String Mac, String content) {

		int targaetIdx = isSameMAC(Mac);
		
		if (targaetIdx != -1) { // 能匹配到目标MAC
			ChatSocket csChatSocket = vector.get(targaetIdx);
			csChatSocket.out(content.getBytes());  //有getbyte的表示不发送\r\n结束标志
			System.out.printf("即将发送给%d号产品，产品mac地址是%s\r\n", targaetIdx + 1,Mac);
		}
		else {
			System.out.println(Mac+"未上线");
		}
	}
	// -------------------------转发给指定用户---------------------------------
	public void toTargetUser(String username, byte[] content) {

		int targaetIdx = isSameUser(username);
		if (targaetIdx != -1) { // 能匹配到目标用户
			ChatSocket csChatSocket = vector.get(targaetIdx);
			csChatSocket.out(content);
			System.out.println("即将把产品采集到的数据发送给"+username);
		}
		else {
			System.out.println(username+"未上线");
		}
	}
	// -------------------------转发给指定用户---------------------------------
	public void toTargetUser(String username, String content) {

		int targaetIdx = isSameUser(username);
		if (targaetIdx != -1) { // 能匹配到目标用户
			ChatSocket csChatSocket = vector.get(targaetIdx);
			csChatSocket.out(content);
			System.out.println("即将把产品采集到的数据发送给"+username);
		}
		else {
			System.out.println(username+"未上线");
		}
	}
	// -------------------------转发给指定客户---------------------------------
	public void toTargetUser(ArrayList<String> usernames, String content) {
		for (int i = 0; i < usernames.size(); i++) {
			String username = usernames.get(i);
			int targaetIdx = isSameUser(username);
			if (targaetIdx != -1) { // 能匹配到目标MAC
				ChatSocket csChatSocket = vector.get(targaetIdx);
				csChatSocket.out(content);
				System.out.println("即将把产品采集到的数据发送给"+username);
			}
			else {
				System.out.println(username+"未上线");
			}
		}

	}
}
