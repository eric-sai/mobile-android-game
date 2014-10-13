package com.unimelb.mobile.breakout.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.unimelb.mobile.breakout.server.dao.IUserDao;
import com.unimelb.mobile.breakout.server.dao.impl.UserDaoImpl;
import com.unimelb.mobile.breakout.server.po.User;
import com.unimelb.mobile.breakout.server.tools.SerializableObj;

/**
 * Servlet implementation class TopServlet
 */
@WebServlet("/TopServlet")
public class TopServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TopServlet() {
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
		IUserDao userDao = new UserDaoImpl();
		List<User> lst = new ArrayList<User>();
		lst = userDao.getUsers();
		SerializableObj so = new SerializableObj();
		response.getOutputStream().write(so.List2Bytes(lst));
	}

}
