/**
 * Copyright     2014     Renren.com
 * @author JunHan 
 *  All rights reserved.
 */
package com.unimelb.mobile.breakout.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

	/* (non-Javadoc)
	 * @see com.unimelb.mobile.breakout.server.dao.IUserDao#register(com.unimelb.mobile.breakout.server.po.User)
	 */
	@Override
	public int register(User user) {
		String username = user.getUsername();
		String password = user.getPassword();
		int score = user.getScore();
		String strSQL = "INSERT INTO `user_info` (`uid`,`username`,`password`,`score`) VALUES (null,?,?,?)";
		int result  = this.dbconn.execOther(strSQL,new Object[]{password,username,score});
		this.dbconn.closeConn();
		return result;
	}

	/* (non-Javadoc)
	 * @see com.unimelb.mobile.breakout.server.dao.IUserDao#findSocreById(int)
	 */
	@Override
	public int findSocreById(int uid) {
		String strSQL = "select score from user_info where uid =?";
		ResultSet rs = this.dbconn.execQuery(strSQL, new Object[]{uid});
		int score=0;
		try {
			rs.next();
			score = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			this.dbconn.closeConn();
		}
		return score;
	}

	/* (non-Javadoc)
	 * @see com.unimelb.mobile.breakout.server.dao.IUserDao#updateUser(com.unimelb.mobile.breakout.server.po.User)
	 */
	@Override
	public int updateScore(int uid, int score) {
		String strSQL = "update user_info set score=? where uid=?";
		int result  = this.dbconn.execOther(strSQL,new Object[]{score,uid});
		this.dbconn.closeConn();
		return result;
	}

	/* (non-Javadoc)
	 * @see com.unimelb.mobile.breakout.server.dao.IUserDao#getUsers()
	 */
	@Override
	public List<User> getUsers() {
		String strSQL="SELECT * FROM breakoutgame_mysql.user_info order by score desc limit 10";
		List<User> lst  = new ArrayList<User>();
		ResultSet rs = this.dbconn.execQuery(strSQL, new Object[]{});
		try {
			while(rs.next()){
				User user = new User();
				user.setUid(rs.getInt(1));
				user.setUsername(rs.getString(2));
				user.setPassword(rs.getString(3));
				user.setScore(rs.getInt(4));
				lst.add(user);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbconn.closeConn();
		}
		return lst;
	}

}
