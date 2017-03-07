package com.blue.SocketService.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectDatabase {

	String drivename = "com.mysql.jdbc.Driver";
	String url = "jdbc:mysql://115.28.74.121:3306/spot_inspection";
	String sqlUser = "root";
	String sqlPassword = "waitsmart";
	String insql;
	String upsql;
	String delsql;
	// String sql = "select * from user";
	String name;
	Connection conn;
	ResultSet rs = null;

	// ----------------------------------�������ݿ�
	public Connection ConnectMysql() {
		try {
			Class.forName(drivename);
			conn = (Connection) DriverManager.getConnection(url, sqlUser,
					sqlPassword);
			if (!conn.isClosed()) {
				System.out.println("Succeeded connecting to the Database!");
			} else {
				System.out.println("Falled connecting to the Database!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return conn;
	}

	// ----------------------------------�Ͽ����ݿ�
	public void CutConnection(Connection conn) throws SQLException {
		try {
			if (rs != null)
				rs.close();
			if (conn != null)
				conn.close();
			System.out.println("Succeeded close the Database!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// ----------------------------------�����Ʒ������Ϣ
	public boolean InsertProductToSql(Product product) {
		try {

			insql = "insert into product_info(productid,MAC,stat,SOC,temp,hum,lux,isdark,israin,error) values(?,?,?,?,?,?,?,?,?,?)";

			// ����ķ���������ķ��������ƣ�һ�����ǰ�ȫ�ԣ���һ�����������ˡ���
			// insql="insert into user(userid,username,password,email) values(user.getId,user.getName,user.getPassword,user.getEmail)";
			PreparedStatement ps = conn.prepareStatement(insql);
			// .preparedStatement(insql);
			// PreparedStatement ps=(PreparedStatement)
			// conn.prepareStatement(insql);
			UUID uuid = UUID.randomUUID();
			ps.setString(1, uuid.toString());
			ps.setString(2, product.getProductMac());
			ps.setString(3, product.getStat());
			ps.setDouble(4, product.getSOC());
			ps.setDouble(5, product.getTemperature());
			ps.setDouble(6, product.getHuminity());
			ps.setDouble(7, product.getLux());
			ps.setString(8, product.getIsDark());
			ps.setString(9, product.getIsRain());
			ps.setString(10, product.getError());
			int result = ps.executeUpdate();
			// ps.executeUpdate();�޷��ж��Ƿ��Ѿ�����
			if (result > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// ----------------------------------�����Ʒ����ʷ��Ϣ
	public boolean InsertProductToSqlLog(Product product) {
		try {

			insql = "insert into product_log(senslogid,stat,SOC,temp,hum,lux,isdark,israin,error,productid) values(?,?,?,?,?,?,?,?,?,?)";

			// ����ķ���������ķ��������ƣ�һ�����ǰ�ȫ�ԣ���һ�����������ˡ���
			// insql="insert into user(userid,username,password,email) values(user.getId,user.getName,user.getPassword,user.getEmail)";
			PreparedStatement ps = conn.prepareStatement(insql);
			// .preparedStatement(insql);
			// PreparedStatement ps=(PreparedStatement)
			// conn.prepareStatement(insql);
			UUID uuid = UUID.randomUUID();
			ps.setString(1, uuid.toString());
			ps.setString(2, product.getStat());
			ps.setDouble(3, product.getSOC());
			ps.setDouble(4, product.getTemperature());
			ps.setDouble(5, product.getHuminity());
			ps.setDouble(6, product.getLux());
			ps.setString(7, product.getIsDark());
			ps.setString(8, product.getIsRain());
			ps.setString(9, product.getError());
			String macIdString = selectMacID(product.getProductMac());
			ps.setString(10, macIdString);
			int result = ps.executeUpdate();
			// ps.executeUpdate();�޷��ж��Ƿ��Ѿ�����
			if (result > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// ----------------------------------���²�Ʒ������Ϣ
	public boolean updatProductToSql(Product product) {
		String updateContext;
		updateContext = "stat=\"" + product.getStat() + "\",SOC="
				+ product.getSOC() + ",temp=" + product.getTemperature()
				+ ",hum=" + product.getHuminity() + ",lux=" + product.getLux()
				+ ",isdark=\"" + product.getIsDark() + "\",israin=\""
				+ product.getIsRain() + "\",error=\"" + product.getError()
				+ "\"";
		String sql = "update product_info set " + updateContext
				+ " where MAC=\"" + product.getProductMac() + "\"";
		return UpdateSql(sql);
	}

	// ----------------------------------���¿�����Ϣ
	public boolean updatControlToSql(Control control) {
		String updateContext;
		updateContext = "timein=\"" + control.getTimeIn() + "\",timeout=\""
				+ control.getTimeOut() + "\",trigstat=\""
				+ control.getTrigStat() + "\"";
		String productID = selectMacID(control.getProductMac());
		String userID = selectuserID(control.getUserName());
		String sql = "update control_info set " + updateContext
				+ " where productid=\"" + productID + "\"" + " and userID=\""
				+ userID + "\"";
		System.out.println(sql);
		return UpdateSql(sql);
	}

	// ps.setString(2, product.getProductMac());

	// ----------------------------------�����û�����
	public boolean InsertUserToSql(User user) {
		try {

			insql = "insert into user_info(userid,username,password) values(?,?,?)";
			// ����ķ���������ķ��������ƣ�һ�����ǰ�ȫ�ԣ���һ�����������ˡ���
			// insql="insert into user(userid,username,password,email) values(user.getId,user.getName,user.getPassword,user.getEmail)";
			PreparedStatement ps = conn.prepareStatement(insql);
			// .preparedStatement(insql);
			// PreparedStatement ps=(PreparedStatement)
			// conn.prepareStatement(insql);
			UUID uuid = UUID.randomUUID();
			ps.setString(1, uuid.toString());
			ps.setString(2, user.getUsername());
			ps.setString(3, user.getPassword());
			System.out.println(user.getUsername() + "  " + user.getPassword());
			int result = ps.executeUpdate();
			// ps.executeUpdate();�޷��ж��Ƿ��Ѿ�����
			if (result > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// ----------------------------------���ݿ��ѯ����gan
	public ResultSet SelectFromSql(String sql) {
		ResultSet rs = null;
		try {
			Statement statement = conn.createStatement();
			rs = statement.executeQuery(sql);
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return rs;
		}
	}

	// ---------------------------------------------------------ѡ��mac��ַ
	public String selectMacID(String mac) {
		String sql = "select * from product_info where MAC=\"" + mac + "\"";
		ResultSet rs = SelectFromSql(sql);
		String macId = null;
		try {
			if (rs.next()) {
				macId = rs.getString("productid");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return macId;

	}

	// ---------------------------------------------------------ѡ���û�ID
	public String selectuserID(String username) { //
		String sql = "select * from user_info where username=\"" + username
				+ "\"";
		ResultSet rs = SelectFromSql(sql);
		String userID = null;
		try {
			if (rs.next()) {
				userID = rs.getString("userid");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userID;

	}

	// ---------------------------------------------------------���ݲ�Ʒmac��ַѡ����ƥ����û�
	public ArrayList<String> selectUsersFromMac(String mac) { // gen'j
		String macID = selectMacID(mac);
		String sqlFindMacID = "select * from control_info where productid=\""
				+ macID + "\"";
		ResultSet rsMacID = SelectFromSql(sqlFindMacID);
		try {
			if (rsMacID.next()) {
				String sqlFindUser = "select * from user_info where userid=\""
						+ rsMacID.getString("userid") + "\"";
				ResultSet rsUser = SelectFromSql(sqlFindUser);
				ArrayList<String> userArrayList = new ArrayList<String>();
				try {
					while (rsUser.next()) {
						userArrayList.add(rsUser.getString("username"));
						System.out.println("���mac��ַƥ����û��ǣ�"
								+ rsUser.getString("username"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return userArrayList;

			}
			else {
				System.out.println("δ�ҵ����mac��ַƥ����û���");
				return null;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}

	// -------------------------------------------------------�����û���Ʒƥ���¼
	public boolean InsertControlToSql(Control control) {
		try {

			insql = "insert into control_info(controlid,productid,userid,timein,timeout,trigstat) values(?,?,?,?,?,?)";

			// ����ķ���������ķ��������ƣ�һ�����ǰ�ȫ�ԣ���һ�����������ˡ���
			// insql="insert into user(userid,username,password,email) values(user.getId,user.getName,user.getPassword,user.getEmail)";
			PreparedStatement ps = conn.prepareStatement(insql);
			// .preparedStatement(insql);
			// PreparedStatement ps=(PreparedStatement)
			// conn.prepareStatement(insql);
			UUID uuid = UUID.randomUUID();
			ps.setString(1, uuid.toString());
			ps.setString(2, control.getProductID());
			ps.setString(3, control.getUserID());
			ps.setString(4, control.getTimeIn());
			ps.setString(5, control.getTimeOut());
			ps.setString(6, control.getTrigStat());
			int result = ps.executeUpdate();
			// ps.executeUpdate();�޷��ж��Ƿ��Ѿ�����
			if (result > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// ������������Ƚϣ���ѯ����ڲ�ѯ����Ҫһ����ѯ�������ResultSet���������ѯ���
	public boolean SelectUserFromSql(User user) {
		ResultSet rs = null;

		String sql = "select * from user_info where username=" + "\""
				+ user.getUsername() + "\"";
		rs = SelectFromSql(sql);
		boolean havedata = false;
		try {
			while (rs.next()) {

				String username = rs.getString("username");
				String password = rs.getString("password");
				user.setUsername(username);
				user.setPassword(password);
				// System.out.println(rs.getString("userid") +
				// user.getUsername()
				// + user.getPassword());
				havedata = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return havedata;

	}

	// ---------------------------�������ݿ�------------------------------
	public boolean UpdateSql(String upsql) {
		try {
			PreparedStatement ps = conn.prepareStatement(upsql);
			int result = ps.executeUpdate();// ������������0
			if (result > 0)
				return true;
		} catch (SQLException ex) {
			Logger.getLogger(ConnectDatabase.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return false;
	}

	// ---------------------------ɾ�����ݿ�---------------------------------
	public boolean DeletSql(String delsql) {

		try {
			PreparedStatement ps = conn.prepareStatement(upsql);
			int result = ps.executeUpdate(delsql);
			if (result > 0)
				return true;
		} catch (SQLException ex) {
			Logger.getLogger(ConnectDatabase.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return false;
	}

}