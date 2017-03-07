package java.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class JavaClient {
	static Socket sc = null;// Ì×½Ú×Ö¶ÔÏó
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			sc = new Socket("192.168.31.118", 3333);
            DataInputStream netInputStream = new DataInputStream(sc.getInputStream());  
            DataOutputStream netOutputStream = new DataOutputStream(sc.getOutputStream());
            netOutputStream.writeByte(999);
            while (true) {
            	byte[] b = null;
				netInputStream.read(b, 0, 4);
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
