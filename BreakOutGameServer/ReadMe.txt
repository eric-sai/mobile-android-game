Read Me
******************************************
This is a description file for the programming project-subject COMP90018.

Server part.
******************************************

1. Team information:

	Team Id: 4
	Team Members: Xiaoxiao Ma, Jun Han, Kuai Yu, Yitian Zhang.

2. Introduction to the server part of the project: This java web project is for the server of breakout game.

2.1. com.unimelb.mobile.breakout.server.dao
	2.1.1. IUserDao.java - Interface to operate database.
	
2.2. com.unimelb.mobile.breakout.server.dao.impl
	2.2.1. UserDao.Impl.java - The class which implements the IUserDao interface.
	
2.3. com.unimelb.mobile.breakout.server.dbutil
	2.3.1. Config.java - database configure
	2.3.2. DBConn.java - to connect the database
	2.3.3. dbconfig.properties - variables which can be changed flexibly, such as IP address and port of database.
	
2.4. com.unimelb.mobile.breakout.server.po
	2.4.1. User.java - user object
	
2.5. com.unimelb.mobile.breakout.server.servlet
	2.5.1. checkScore.java - a servlet which returns the highest score of a target user.
	2.5.2. DownloadServlet.java - provides download interface to the client.
	2.5.3. LoginServlet.java - deal with the request of login.
	2.5.4. RegisterServlet.java - deal with the request to create a new user.
	2.5.5. TopServlet.java - a servlet which returns a user list who get top 10 highest score.
	2.5.6. UpdateScore.java - can update the newest and highest score to database.
	
2.6. com.unimelb.mobile.breakout.server.tools
	2.6.1. SerializableObj.java - serializable the object which needs to response to client.
	
2.7. db.sql
	2.7.1. SQL file to create database, table and some test data.
	
	