package com.sf.sts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {
		"/Servlet3Tst" }, asyncSupported = true, loadOnStartup = -1, name = "s3t", displayName = "Servlet3Tst", initParams = {
				@WebInitParam(name = "username", value = "tom") })
public class Servlet3Tst extends HttpServlet {

	private static final long serialVersionUID = -7504575336656237514L; 

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println(this.getInitParameter("username") + "------------" + req.getParameter("p1"));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
