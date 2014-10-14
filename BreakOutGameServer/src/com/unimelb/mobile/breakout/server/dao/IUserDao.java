
package com.unimelb.mobile.breakout.server.dao;

import java.util.List;

import com.unimelb.mobile.breakout.server.po.User;


public interface IUserDao {
	public abstract User checkLogin(final String username,final String password);
	public abstract int register(final User user);
	public abstract int findSocreById(int uid);
	public abstract int updateScore(int uid, int score);
	public abstract List<User> getUsers();
}
