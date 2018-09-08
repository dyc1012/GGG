package com.oracle.olt.rpt;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.oracle.olt.rpt.util.DBUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * Servlet implementation class OLTRpt
 */
public class OLTRpt extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OLTRpt()
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
		String reportLink = request.getParameter("reportLink");
		String jobNameQuery = request.getParameter("jobNameQuery");
		String action = request.getParameter("action");
		String dbLocation = request.getParameter("dbLocation");

		// System.out.println(reportLink);
		// System.out.println(jobNameQuery);

		if (reportLink != null) // retrieve specific report detail page
		{
			response.getWriter().write(getReportLinkDetail(reportLink));
			return;
		}
		if (jobNameQuery != null || action != null) // query job name
		{
			response.setContentType("text/json; charset=UTF-8");
			String r1 = DBUtil.getJobNames(dbLocation, jobNameQuery, action)
					.toString();
			response.getOutputStream().write(r1.getBytes("UTF-8"));

			return;
		}
		else
		// get report list
		{
			response.setContentType("text/json; charset=UTF-8");
			String lists = getReportList(request);
			response.getOutputStream().write(lists.getBytes("UTF-8"));
		}
	}

	protected String getReportList(HttpServletRequest request)
	{
		String jobName = request.getParameter("jobName");
		String begin = request.getParameter("genFromTime");
		String end = request.getParameter("genToTime");
		String dbLocation = request.getParameter("dbLocation");

		// System.out.println(jobName);
		// System.out.println(begin);
		// System.out.println(end);

		ArrayList<Report> list = DBUtil.getReports(dbLocation, jobName, begin,
				end);

		JSONObject result = new JSONObject();
		JSONArray arr = new JSONArray();

		for (int i = 0; i < list.size(); i++)
		{
			JSONObject obj = new JSONObject();
			obj.put("buildId", list.get(i).getBuildID());
			obj.put("jobName", list.get(i).getJobName());
			obj.put("genTime", list.get(i).getGenTime());
			obj.put("rptLink", list.get(i).getRptLink());

			arr.add(obj);
		}

		result.put("total", Integer.valueOf(list.size()));
		result.put("rows", arr);

		return result.toString();
	}

	protected String getReportLinkDetail(String reportLink)
	{
		String r = null;
		HttpClient client = new DefaultHttpClient();
		HttpResponse resp = null;
		try
		{
			resp = client.execute(new HttpGet(reportLink));

			if (resp != null)
			{
				HttpEntity entity = resp.getEntity();

				if (entity != null)
				{
					r = EntityUtils.toString(entity, "utf-8");

				}
			}
		}
		catch (IOException e1)
		{
			return "<html>Get reprot detail ERROR for url: <a href='"
					+ reportLink + "' target='_blank'>" + reportLink
					+ "</a></html>";
		}

		return r;
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

		String url = "http://slcn03vmf0248.us.oracle.com:8086/reports/check_env_409_overall_report.html";

		if (url != null)
		{
			String r = null;
			HttpClient client = new DefaultHttpClient();
			HttpResponse resp = null;

			try
			{
				resp = client.execute(new HttpGet(url));
			}
			catch (Exception e1)
			{
				System.out.println(e1.getMessage());
				return;
			}

			if (resp != null)
			{
				HttpEntity entity = resp.getEntity();

				if (entity != null)
				{
					r = EntityUtils.toString(entity, "utf-8");
				}
			}

			System.out.println(r);
		}
	}

}
