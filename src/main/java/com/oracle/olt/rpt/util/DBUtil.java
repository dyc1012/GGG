package com.oracle.olt.rpt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.oracle.olt.rpt.Report;

public class DBUtil
{

	static Properties dbconfig = new Properties();

	static
	{
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		InputStream db = classLoader.getResourceAsStream("dbconfig.properties");

		try
		{
			dbconfig.load(db);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Connection getConnection(String dbLocation) throws Exception
	{
		try
		{
			Class.forName("oracle.jdbc.OracleDriver");
			String URL = "jdbc:oracle:thin:@" + dbLocation + ":1521:xe";
			String USERNAME = dbconfig.getProperty(dbLocation + ".user");
			String PASSWORD = dbconfig.getProperty(dbLocation + ".passd");

			// System.out.println(dbLocation + "\t" + USERNAME + "\t" +
			// PASSWORD);

			Connection conn = DriverManager.getConnection(URL, USERNAME,
					PASSWORD);
			return conn;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception();
		}
	}

	public static void insertReportItem(String dbLocation, int buildId,
			String jobName, Timestamp generatedTime, String reportLink)
			throws Exception, Exception
	{
		Connection conn = null;
		PreparedStatement pst = null;

		try
		{
			conn = getConnection(dbLocation);

			pst = conn.prepareStatement("insert into "
					+ dbconfig.getProperty(dbLocation + ".table")
					+ " values(?,?,?,?)");

			pst.setInt(1, buildId);
			pst.setString(2, jobName);
			pst.setTimestamp(3, generatedTime);
			pst.setString(4, reportLink);
			pst.execute();
		}
		finally
		{
			closeDbObject(null, pst, conn);
		}
	}

	public static JSONObject getJobNames(String dbLocation, String key,
			String action)
	{
		Connection conn = null;
		// Statement stmt = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();

		try
		{
			conn = getConnection(dbLocation);
			// stmt = conn.createStatement();

			StringBuilder sql = new StringBuilder(
					"select distinct JOB_NAME from  "
							+ dbconfig.getProperty(dbLocation + ".table")
							+ "  where 1=1");

			if (key != null && key.trim().length() > 0)
			{
				sql.append(" AND instr(JOB_NAME, ?) > 0");
			}

			sql.append(" ORDER BY JOB_NAME");

			// System.out.println(sql);
			// rs = stmt.executeQuery(sql);

			pst = conn.prepareStatement(sql.toString());

			if (key != null && key.trim().length() > 0)
			{
				pst.setString(1, key);
			}
			rs = pst.executeQuery();

			while (rs.next())
			{
				JSONObject o = new JSONObject();
				o.put("jobName", rs.getString("JOB_NAME"));
				arr.add(o);
			}

			obj.put("d", arr);
			return obj;
		}
		catch (Exception e)
		{
			return obj;
		}
		finally
		{
			closeDbObject(rs, pst, conn);
		}
	}

	public static ArrayList<Report> getReports(String dbLocation,
			String jobName, String begin, String end)
	{
		Connection conn = null;
		// Statement stmt = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int i = 1;

		try
		{
			conn = getConnection(dbLocation);
			// stmt = conn.createStatement();

			StringBuilder sql = new StringBuilder("select * from  "
					+ dbconfig.getProperty(dbLocation + ".table")
					+ "  where 1=1");

			if (jobName != null && jobName.trim().length() > 0)
			{
				sql.append(" AND JOB_NAME = ?");
			}

			if (begin != null && begin.trim().length() > 0)
			{
				sql.append(" AND GENERATE_TIME>=to_timestamp(?, 'mm/dd/yyyy hh24:mi')");
			}

			if (end != null && end.trim().length() > 0)
			{
				sql.append(" AND GENERATE_TIME<=to_timestamp(? ,'mm/dd/yyyy hh24:mi')");
			}

			sql.append(" ORDER BY GENERATE_TIME DESC");

			pst = conn.prepareStatement(sql.toString());

			if (jobName != null && jobName.trim().length() > 0)
			{
				pst.setString(i++, jobName);
			}

			if (begin != null && begin.trim().length() > 0)
			{
				pst.setString(i++, begin);
			}

			if (end != null && end.trim().length() > 0)
			{
				pst.setString(i++, end);
			}

			rs = pst.executeQuery();
			// rs = stmt.executeQuery(sql);
			ArrayList<Report> list = new ArrayList<Report>();

			String t = "";
			while (rs.next())
			{
				t = rs.getString("GENERATE_TIME");
				if (t.indexOf('.') > 0)
				{
					t = t.substring(0, t.indexOf('.'));
				}

				list.add(new Report(rs.getString("BUILD_ID"), rs
						.getString("JOB_NAME"), t, rs.getString("REPORTLINK")));
			}

			return list;
		}
		catch (Exception e)
		{
			return new ArrayList<Report>();
		}
		finally
		{
			closeDbObject(rs, pst, conn);
		}
	}

	protected static void closeDbObject(ResultSet rs, Statement stmt,
			Connection conn)
	{
		if (rs != null)
		{
			try
			{
				rs.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		if (stmt != null)
		{
			try
			{
				stmt.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		if (conn != null)
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void transferRpt2DB() throws Exception
	{
		String dbLocation = "";
		String path = "D:\\work\\autos\\jobs_160822\\jobs";
		File f = new File(path);
		Pattern pattern = Pattern
				.compile("\\[exec] (.*)  INFO ReportOltSessionHelper - Test Name (.*?) Overall Report (.*?)overall_report.html");

		Connection conn = getConnection(dbLocation);
		PreparedStatement pst = conn
				.prepareStatement("insert into HR.HISTORYREPORTS values(?,?,?,?)");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

		// FileWriter fw = new FileWriter("D:\\work\\autos\\jrpts.txt");

		try
		{
			if (!f.isDirectory())
			{
				return;
			}

			for (File f1 : f.listFiles())
			{
				// System.out.println(f1.getName());
				// System.out.println(f1.getAbsolutePath());

				String jobName = f1.getName();
				System.out.println(jobName);

				File f2 = new File(f1.getAbsolutePath() + "\\builds");

				if (!f2.exists())
				{
					continue;
				}

				for (File f3 : f2.listFiles())
				{
					if (f3.isDirectory() && !f3.getName().contains("-"))
					{
						String buildId = f3.getName();
						File f4 = new File(f3.getAbsolutePath() + "\\log");

						BufferedReader br = new BufferedReader(new FileReader(
								f4));
						StringBuffer content = new StringBuffer();
						String s = br.readLine();

						while (s != null)
						{
							content.append(s + "\r\n");
							s = br.readLine();
						}

						br.close();
						Matcher matcher = pattern.matcher(content);

						if (matcher.find() && matcher.groupCount() == 3)
						{
							String rptUrl = matcher.group(3).trim()
									+ "overall_report.html";
							String genTime = matcher.group(1).trim();

							// System.out.println(jobName + "\t" + buildId +
							// "\t" + genTime + "\t" + rptUrl);

							// fw.write(jobName + "\t" + buildId + "\t" +
							// genTime + "\t" + rptUrl + "\r\n");
							//
							// pst = conn
							// .prepareStatement("insert into HR.HISTORYREPORTS values(?,?,?,?)");
							pst.setInt(1, Integer.valueOf(buildId));
							pst.setString(2, jobName);
							pst.setTimestamp(3, new Timestamp(sdf
									.parse(genTime).getTime()));
							pst.setString(4, rptUrl);
							pst.execute();
							pst.close();

						}
					}
				}
			}
		}
		finally
		{
			// fw.flush();
			// fw.close();
			closeDbObject(null, pst, conn);
		}
	}

	public static void setDesc() throws Exception
	{
		String username = "admin";
		String password = "Oracle123";

		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(username, password));

		BasicScheme basicAuth = new BasicScheme();
		BasicHttpContext context = new BasicHttpContext();
		context.setAttribute("preemptive-auth", basicAuth);
		client.addRequestInterceptor(new PreemptiveAuth(), 0);

		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
				true);

		HttpGet get = new HttpGet("http://slcn03vmf0248.us.oracle.com/api/xml");

		HttpResponse response = client.execute(get, context);
		HttpEntity entity = response.getEntity();
		String r = EntityUtils.toString(entity, "utf-8");

		Document doc = Jsoup.parse(r);
		Elements paneList = doc.select("job");

		for (int i = 0; i < paneList.size(); i++)
		{
			String descStr = ((Element) (paneList.get(i).child(0))).ownText();
			System.out.println(i + "\t" + descStr);
		}

		// String jname = "check_nimbula_environment";

		// HttpGet get = new HttpGet("http://slcn03vmf0248.us.oracle.com/job/"
		// + jname + "/api/xml");
		//
		// HttpResponse response = client.execute(get, context);
		// HttpEntity entity = response.getEntity();
		// String r = EntityUtils.toString(entity, "utf-8");
		//
		// Document doc = Jsoup.parse(r);
		// Elements paneList = doc.select("displayName");
		//
		// String descStr = ((Element) (paneList.get(0).previousSibling()))
		// .ownText();

		// String descStr = "bb";
		//
		// String newDescStr = descStr
		// + "xxxxxxxxxxxx";
		//
		// String jsonStr = "{\"description\":\"" + newDescStr + "\"}";
		//
		// System.out.println(descStr + " --> " + newDescStr);
		//
		//
		// HttpPost httpPost = new HttpPost(
		// "http://slcn03vmf0248.us.oracle.com/job/" + jname
		// + "/submitDescription?description=hhhhhhhhhhhhhhhhhh");
		//
		// httpPost.setHeader("Content-Type",
		// "application/x-www-form-urlencoded");
		// httpPost.setHeader("Upgrade-Insecure-Requests", "1");
		//
		// List<NameValuePair> list = new ArrayList<NameValuePair>();
		// list.add(new BasicNameValuePair("Submit", "Submit"));
		// list.add(new BasicNameValuePair("description", newDescStr));
		// list.add(new BasicNameValuePair("json", jsonStr));
		//
		// UrlEncodedFormEntity uefEntity = new
		// UrlEncodedFormEntity(list,"UTF-8");
		// httpPost.setEntity(uefEntity);
		// System.out.println("POST request...." + httpPost.getURI());
		//
		// CloseableHttpResponse httpResponse = client.execute(httpPost,
		// context);
		//
		// try{
		// HttpEntity entity = httpResponse.getEntity();
		//
		// if (null != entity){
		// System.out.println("-------------------------------------------------------");
		// System.out.println(EntityUtils.toString(entity));
		// System.out.println("-------------------------------------------------------");
		// }
		// } finally{
		// httpResponse.close();
		// client.close();
		// }
	}

	static class PreemptiveAuth implements HttpRequestInterceptor
	{
		public void process(HttpRequest request, HttpContext context)
				throws HttpException, IOException
		{
			// Get the AuthState
			AuthState authState = (AuthState) context
					.getAttribute(ClientContext.TARGET_AUTH_STATE);

			// If no auth scheme available yet, try to initialize it
			// preemptively
			if (authState.getAuthScheme() == null)
			{
				AuthScheme authScheme = (AuthScheme) context
						.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context
						.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authScheme != null)
				{
					Credentials creds = credsProvider
							.getCredentials(new AuthScope(targetHost
									.getHostName(), targetHost.getPort()));
					if (creds == null)
					{
						throw new HttpException(
								"No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		setDesc();

		// transferRpt2DB();

		// for (int i = 1; i < 2; i++)
		// insertReportItem(12, "testjob006" + i, new Timestamp(new
		// java.util.Date().getTime()),
		// "http://oels:8080/reports/Job001_21_overall_report.html");

	}
}
