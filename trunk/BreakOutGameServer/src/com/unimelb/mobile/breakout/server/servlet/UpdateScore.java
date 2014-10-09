package com.unimelb.mobile.breakout.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.unimelb.mobile.breakout.server.dao.IUserDao;
import com.unimelb.mobile.breakout.server.dao.impl.UserDaoImpl;
import com.unimelb.mobile.breakout.server.po.User;

/**
 * Servlet implementation class UpdateScore
 */
@WebServlet("/UpdateScore")
public class UpdateScore extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateScore() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		int uid = Integer.parseInt( request.getParameter("uid").toString());
		int score =  Integer.parseInt( request.getParameter("score").toString());
		IUserDao userDao = new UserDaoImpl();
		int result = userDao.updateScore(uid, score);
		response.getWriter().write(result);
	}

}
