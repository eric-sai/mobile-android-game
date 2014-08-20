/**
 * Copyright     2014     Renren.com
 * @author JunHan 
 *  All rights reserved.
 */
package com.unimelb.mobile.breakout.server.dao;

import com.unimelb.mobile.breakout.server.po.User;


public interface IUserDao {
	public abstract User checkLogin(final String username,final String password);
}
