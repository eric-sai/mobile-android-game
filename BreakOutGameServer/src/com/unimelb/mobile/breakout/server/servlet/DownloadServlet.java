package com.unimelb.mobile.breakout.server.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownloadServlet
 */
@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String filePath="resources"+File.separator;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void  doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		download(request,response);
	}
	public HttpServletResponse download(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException{
		String fileName = request.getParameter("fileName") +".txt";
		String realpath=  getServletContext().getRealPath(File.separator);
//		System.out.print(realpath);
		String path = realpath+ filePath+fileName;
		System.out.print(path);
		File file = new File(path);
		if (!file.exists()) {
			response.setContentType(" text/html;charset=UTF-8 ");
			response.getWriter().print(" The file is not exist. ");
//			return;
		}
//		ServletOutputStream out = response.getOutputStream();
//		response.setHeader(" Content-disposition ", " attachment; filename= "
//				+ fileName);
		try {
		    InputStream fis = new BufferedInputStream(new FileInputStream(file));

			byte[] buff = new byte[fis.available()];
			fis.read(buff);
			fis.close();
			
			response.reset();
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(fileName.getBytes(),"UTF-8"));
			response.addHeader("Content-Length", "" + file.length());
			ServletOutputStream sos = response.getOutputStream();
			 OutputStream toClient = new BufferedOutputStream(sos);
			 response.setContentType("application/octet-stream");
			 toClient.write(buff);
			 toClient.flush();
			 toClient.close();
//			int bytesRead;
//			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
//				bos.write(buff, 0, bytesRead);
			
		} catch (IOException e) {
			throw e;
		}
		return  response;
	}

}
