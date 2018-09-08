package com.oracle.olt.msg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * Servlet implementation class OLTRpt
 */
public class Msg extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Msg()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{

		// System.out.println(request.getRemoteHost());
		// System.out.println(request.getContextPath());
		// System.out.println(request.getQueryString());
		// System.out.println(request.getMethod());
		// System.out.println(request.getContentLength());
		// System.out.println(request.getHeaderNames().);

		// for (Entry e : request.getParameterMap().entrySet())
		// {
		// System.out.println(e.getKey() + " --> " + e.getValue());
		// }
		//
		// Enumeration s = request.getHeaderNames();
		//
		// while (s.hasMoreElements())
		// {
		// System.out.println(s.nextElement());
		// }

		String bodyStr = getBody(request).trim();
		JSONArray jsonObj;
		long c;

		try
		{
			
			jsonObj = (JSONArray) JSONValue.parse(bodyStr);
			
			//System.out.println(bodyStr + "\t|\tcount: " + jsonObj.size());
			
		}
		catch (Exception e)
		{
			//System.out.println("nnnnnnnnnnnn");
			return;
		}

		synchronized (this)
		{
			c = Long.valueOf(request.getServletContext().getAttribute("count")
					.toString())
					+ jsonObj.size();

			request.getServletContext().setAttribute("count", c);
		}

		// String d = new Date() + "--" + System.currentTimeMillis();
		//
		// System.out.println(d + "__" + bodyStr);
		//
		// System.out.println(d + "__ContentLength:\t"
		// + request.getContentLength());
		//
		// System.out.println(d + "__MessageCount:\t" + jsonObj.size());
		// System.out.println(d + "__TotalCount:\t" + c);
		//
		// System.out.println();

	}

	public static String getBody(HttpServletRequest request) throws IOException
	{

		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try
		{
			InputStream inputStream = request.getInputStream();
			if (inputStream != null)
			{
				bufferedReader = new BufferedReader(new InputStreamReader(
						inputStream));
				char[] charBuffer = new char[100 * 1024]; // 100k
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0)
				{
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			}
			else
			{
				stringBuilder.append("");
			}
		}
		catch (IOException ex)
		{
			throw ex;
		}
		finally
		{
			if (bufferedReader != null)
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException ex)
				{
					throw ex;
				}
			}
		}

		body = stringBuilder.toString();
		return body;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public static void main(String[] args) throws Exception, IOException
	{
		JSONArray jsonObj;

		try
		{
			jsonObj = (JSONArray) JSONValue.parse("[{},{},{}]");
		}
		catch (Exception e)
		{
			return;
		}

		synchronized (Msg.class)
		{
			// c =
			// Long.valueOf(request.getServletContext().getAttribute("count")
			// .toString())
			// + jsonObj.size();
			//
			// request.getServletContext().setAttribute("count", c);

			System.out.println(jsonObj.size());
		}

	}

}
