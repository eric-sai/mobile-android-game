package com.unimelb.mobile.breakout.server.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.unimelb.mobile.breakout.server.dao.IUserDao;
import com.unimelb.mobile.breakout.server.dao.impl.UserDaoImpl;
import com.unimelb.mobile.breakout.server.po.User;
import com.unimelb.mobile.breakout.server.tools.SerializableObj;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String username = request.getParameter("username").toString();
		String password = request.getParameter("password").toString();
		int score = 0;
		IUserDao userDao = new UserDaoImpl();
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setScore(score);
		int result  = userDao.register(user);
		SerializableObj so = new SerializableObj();
		if(result>0){
			response.getOutputStream().write(so.User2Bytes(user));
		}else{
			response.getOutputStream().write(so.User2Bytes(new User()));
		}
//		HttpSession hs = request.getSession();
//		if(user!=null){
//		hs.setAttribute("login", user.getUid());
//		hs.setAttribute("username", user.getUsername());
//		hs.setAttribute("score", user.getScore());
//		response.getWriter().write("Welcome, "+user.getUsername()+".<br>Your highest score is: "+user.getScore());  
		
//		System.out.print(user.getScore());
		
//		}else{
////		hs.setAttribute("login", 0);
//		response.getOutputStream().write(null);
//		}
	}

}
