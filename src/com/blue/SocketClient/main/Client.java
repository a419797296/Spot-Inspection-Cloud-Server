package com.blue.SocketClient.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.blue.SocketService.main.Control;
import com.blue.SocketService.main.User;

public class Client extends Thread {
	public String ip = null;// ���ӷ�������IP
	public Integer port = null;// ���ӷ������Ķ˿�
	private Socket socket = null;// �׽��ֶ���
	private boolean close = false; // �ر����ӱ�־λ��true��ʾ�رգ�false��ʾ����
	// private Integer sotimeout = 1 * 1 * 10;// ��ʱʱ�䣬�Ժ���Ϊ��λ

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	// ------------------------------------------------------------------------------
	public Client() {
		init();
	}

	public Client(String ip, Integer port) {
		setIp(ip);
		setPort(port);
		init();
	}

	/**
	 * ��ʼ��socket����
	 */
	public void init() {
		try {
			InetAddress address = InetAddress.getByName(getIp());
			socket = new Socket(address, getPort());
			socket.setKeepAlive(true);// �������ֻ״̬���׽���
			// socket.setSoTimeout(sotimeout);// ���ó�ʱʱ��
			close = !Send("test");// ���ͳ�ʼ���ݣ����ͳɹ����ʾ�Ѿ������ϣ�����ʧ�ܱ�ʾ�Ѿ��Ͽ�
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �������߳�
	 */
	public void run() {
		// ---------������---------------------------
		close = isServerClose(socket);// �ж��Ƿ�Ͽ�
		if (!close) {// û�жϿ�����ʼ������
			try {
				
				byte[] readLen=new byte[4]; 
				int ad = 0;
	            DataInputStream netInputStream = new DataInputStream(socket.getInputStream());  
	            DataOutputStream netOutputStream = new DataOutputStream(socket.getOutputStream()); 
	            while (true) {
					netInputStream.read(readLen); 
					//readLen[0]<<=8;
					//ad = 0;
				/*	int tmp;
					tmp=readLen[0];
					ad=tmp&0xff;
					System.out.printf("���յ����ַ���Ϊ%d��\r\n", ad);
					ad<<=8;
					tmp=readLen[1];
					tmp=tmp&0xff;
					ad|=tmp;*/

					ad=(int)readLen[0]&0xff;
					//System.out.printf("���յ����ַ���Ϊ%d��\r\n", ad);
					ad<<=8;
					ad|=(int)readLen[1]&0xff;
					ad=(ad*5000)>>15;    //ADֵת��ѹֵ
					//ad=(int)readLen[0]<<8+readLen[1];
					System.out.printf("���յ����ַ���Ϊ%d  %d��\r\n", readLen[0],readLen[1]);
					System.out.printf("���յ����ַ���Ϊ%x  %x��\r\n", readLen[0],readLen[1]);
					System.out.printf("���յ����ַ���Ϊ: %d\r\n", ad);
				}

				
			/*	
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "UTF-8"));
				String line = null;

				while ((line = br.readLine()) != null) {
					// ChatManager.getChatManager().publish(this, line); //ת����Ϣ
					// ChatManager.getChatManager().replay(this, "�Ѿ��յ���������");

					StringBuilder builder = new StringBuilder();
					builder.append(line);
					
					String receivedData = builder.toString();
					System.out.println("���յ����ַ���Ϊ��\r\n" + receivedData);
					//whatTodo(receivedData);
				}

				br.close();*/
			} catch (UnsupportedEncodingException e) {
				System.out.println("socketͨѶ�쳣");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("socketͨѶ�쳣");
				e.printStackTrace();
			}

		}

		// ---------��������-------------------------
		while (close) {// �Ѿ��Ͽ������½�������
			try {
				System.out.println("���½������ӣ�" + getIp() + ":" + getPort());
				InetAddress address = InetAddress.getByName(getIp());
				socket = new Socket(address, getPort());
				socket.setKeepAlive(true);
				// socket.setSoTimeout(sotimeout);
				close = !Send("2");
				System.out.println("�������ӳɹ���" + getIp() + ":" + getPort());
			} catch (Exception se) {
				System.out.println("��������ʧ��:" + getIp() + ":" + getPort());
				close = true;
			}
		}
	}

	/**
	 * �������ݣ�����ʧ�ܷ���false,���ͳɹ�����true
	 * 
	 * @param csocket
	 * @param message
	 * @return
	 */
	public Boolean Send(String message) {
		try {
			PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
			out.println(message);
			return true;
		} catch (Exception se) {
			se.printStackTrace();
			return false;
		}
	}

	/**
	 * �ж��Ƿ�Ͽ����ӣ��Ͽ�����true,û�з���false
	 * 
	 * @param socket
	 * @return
	 */
	public Boolean isServerClose(Socket socket) {
		try {
			socket.sendUrgentData(0);// ����1���ֽڵĽ������ݣ�Ĭ������£���������û�п����������ݴ�����Ӱ������ͨ��
			return false;
		} catch (Exception se) {
			return true;
		}
	}
	
	/*********************** �ж�������json���ݣ�clothes_info,user_info OR control_info�� ***************************/
	public String judgeJsonType(String jsondate) {
		String jsontype = "chatInfo";
		JSONObject root;
		try {
			root = new JSONObject(jsondate);
			jsontype = root.getString("jsonType");
			System.out.println("���ַ�����Json������������" + jsontype);
			return jsontype;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("�޷�����json!\r\n");

			e.printStackTrace();
			return jsontype;
		}
	}

	/*********************** ��������Ҫ������Щ���� ***************************/
	public void whatTodo(String receivedData) {
		String jsontype = judgeJsonType(receivedData);
		switch (jsontype) {
		case "chatInfo":
			System.out.println("��Json����chatInfo\r\n");
			break;
		case "loginInfo":
			System.out.println("��Json����loginInfo\r\n");
			break;
		case "regInfo":
			System.out.println("��Json����regInfo\r\n");

			break;
		case "productInfo":
			System.out.println("��Json����productInfo\r\n");

			break;
		case "controlInfo":
			System.out.println("��Json����controlInfo\r\n");
			controlInfo(receivedData);
			break;
		case "wifiInfo":
			System.out.println("��Json����wifiInfo\r\n");
			break;
		case "productReg":
			break;

		default:
			break;
		}
	}
	
	//-----------------------------------------------------�����ƶ�Ŀ��
	public void controlInfo(String receivedData) {
		Control control = new Control();
		if (control.jsonResolve(receivedData)) {
			System.out.println("control_info �����ɹ�");
			String stigStat=control.getTrigStat();
			new User().saveAsFileWriter("G:\\sockTest.txt",stigStat);
			
			System.out.println(stigStat);
		}

	}
	
    // �����ַ������趨ָ������task��ָ���ӳ�delay����й̶�Ƶ��peroid��ִ�С�  
    // scheduleAtFixedRate(TimerTask task, long delay, long period)  
    public static void timer() {  
        Timer timer = new Timer();  
        timer.scheduleAtFixedRate(new TimerTask() {  
            public void run() {  
                System.out.println("-------�趨Ҫָ������--------");  
            }  
        }, 1000, 2000);  
    }  
	
}
