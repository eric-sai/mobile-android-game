/**
 * Copyright     2014     Renren.com
 * @author JunHan 
 *  All rights reserved.
 */
package com.unimelb.mobile.breakout.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.unimelb.mobile.breakout.server.dao.IUserDao;
import com.unimelb.mobile.breakout.server.dbutil.DBConn;
import com.unimelb.mobile.breakout.server.po.User;

public class UserDaoImpl implements IUserDao {
	private DBConn dbconn = null;

	public UserDaoImpl() {
		this.dbconn = new DBConn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unimelb.mobile.breakout.server.dao.IUserDao#checkLogin(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public User checkLogin(String username, String password) {
		// TODO Auto-generated method stub
		User user = new User();
		String strSQL = "select * from user_info where username=? and password=?";
		ResultSet rs = this.dbconn.execQuery(strSQL, new Object[]{password,username});
		try {
			rs.next();
			user.setUid(rs.getInt(1));
			user.setUsername(rs.getString(2));
			user.setPassword(rs.getString(3));
			user.setScore(rs.getInt(4));
			return user;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			user.setUid(0);
			return user;
		} finally{
			this.dbconn.closeConn();
		}
		
	}

}
