package com.oracle.olt.msg;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * Servlet implementation class OLTRpt
 */
public class Stat extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Stat()
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
		JSONObject jsonResult = null;

		if ("reset".equalsIgnoreCase(request.getParameter("action")))
		{
			synchronized (Stat.class)
			{

				MessageListener.clearWork(this.getServletContext());
				MessageListener.initWork(this.getServletContext());

				// System.out.println(this.getServletContext()
				// .getAttribute("baseTimeStr").toString());

				response.getWriter().write(
						"Clean historical statistics complete!");

				response.getWriter().flush();
				return;

			}

		}
		else if ("getBaseTime".equalsIgnoreCase(request.getParameter("action")))
		{
			response.getWriter().write(
					this.getServletContext().getAttribute("baseTimeStr")
							.toString());

			response.getWriter().flush();

			return;
		}
		else if ("search".equalsIgnoreCase(request.getParameter("action")))
		{
			try
			{
				long beginTime = MessageListener.sdf1.parse(
						request.getParameter("timeFrom").trim()).getTime();

				long endTime = MessageListener.sdf1.parse(
						request.getParameter("timeTo").trim()).getTime();

				int sampleCount = Integer.valueOf(request.getParameter(
						"sampleCount").trim());

				jsonResult = generateRangeChartData(beginTime, endTime,
						sampleCount);

			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else
		{
			int peak = MessageListener.totalTimesList
					.get(MessageListener.totalTimesList.size() - 1);

			int clientPeak = Integer.valueOf(request.getParameter("peakStep"));

			if (clientPeak == peak)
			{
				// System.out.println(clientPeak + " ^^^ " + peak);

				jsonResult = new JSONObject();
				jsonResult.put("peakStep", peak);
			}
			else
			{
				// System.out.println(clientPeak + " --- " + peak);

				if (MessageListener.dataCache[0] != null
						&& (System.currentTimeMillis() - Long
								.valueOf(MessageListener.dataCache[0]
										.toString())) <= 2000)
				{
					// System.out.println("hit cache^^^^^^^^^^^^^^^^^^^^");

					jsonResult = (JSONObject) MessageListener.dataCache[1];
				}
				else
				{
					// System.out.println("NOT hit cache======================");

					jsonResult = generateLatestChartData(15);
					MessageListener.updateCache(jsonResult);

				}
			}

		}

		response.setContentType("application/json");

		response.getWriter().write(jsonResult.toJSONString());
		response.getWriter().flush();
	}

	private JSONObject generateRangeChartData(long beginTime, long endTime,
			int sampleCount)
	{
		try
		{
			int currentMaxStepNum = MessageListener.totalTimesList
					.get(MessageListener.totalTimesList.size() - 1);

			int beginStep = (int) (Math
					.floor((beginTime - MessageListener.baseTime)
							/ MessageListener.interval)) + 1;

			if (beginStep == 0)
			{
				beginStep = 1;
			}

			int endStep = (int) (Math.ceil((endTime - MessageListener.baseTime)
					/ MessageListener.interval)) + 1;

			if (endStep > currentMaxStepNum)
			{
				endStep = currentMaxStepNum;
			}

			LinkedHashMap<Integer, Long> map = MessageListener.rangeResults(
					MessageListener.totalTimesList,
					MessageListener.totalCountList, beginStep, endStep,
					sampleCount);

			long startCount = 0;
			long endCount = 0;

			JSONObject totalCountsResult = new JSONObject();
			JSONObject intervalCountsResult = new JSONObject();
			JSONArray totalCountsCols = new JSONArray();
			JSONArray totalCountsRows = new JSONArray();
			JSONArray intervalCountsCols = new JSONArray();
			JSONArray intervalCountsRows = new JSONArray();

			JSONObject obj = new JSONObject();
			obj.put("id", "");
			obj.put("label", "");
			obj.put("pattern", "");
			obj.put("type", "datetime");
			totalCountsCols.add(obj);

			obj = new JSONObject();
			obj.put("id", "");
			obj.put("label", "Amount");
			obj.put("pattern", "");
			obj.put("type", "number");
			totalCountsCols.add(obj);

			obj = new JSONObject();
			obj.put("id", "");
			obj.put("label", "");
			obj.put("pattern", "");
			obj.put("type", "string");
			intervalCountsCols.add(obj);

			obj = new JSONObject();
			obj.put("id", "");
			obj.put("label", "Increment");
			obj.put("pattern", "");
			obj.put("type", "number");
			intervalCountsCols.add(obj);

			JSONArray arr;

			Set<Entry<Integer, Long>> set = map.entrySet();

			long lastTotalCount = 0;
			int i = 1;
			Calendar c = Calendar.getInstance();

			for (Entry<Integer, Long> e : set)
			{

				int stepNum = e.getKey();
				long count = e.getValue();

				// System.out.println(stepNum + " @@@@@@@@@@@ " + count);

				if (i == 1)
				{
					startCount = count;
				}

				if (i == set.size())
				{
					endCount = count;
				}

				i++;

				arr = new JSONArray();
				obj = new JSONObject();

				// System.out.println(MyServletContextListener.baseTime
				// + (stepNum - 1) * MyServletContextListener.interval);

				Date date = new Date(MessageListener.baseTime + (stepNum - 1)
						* MessageListener.interval);

				c.setTime(date);

				String t1 = "Date(" + c.get(Calendar.YEAR) + ", "
						+ (c.get(Calendar.MONTH)) + ", "
						+ c.get(Calendar.DAY_OF_MONTH) + ", "
						+ c.get(Calendar.HOUR_OF_DAY) + ", "
						+ c.get(Calendar.MINUTE) + ", "
						+ c.get(Calendar.SECOND) + ")";

				// System.out.println(t1);

				String t2 = MessageListener.sdf.format(date);

				// System.out.println(t2);

				obj.put("v", t1);
				obj.put("f", null);
				arr.add(obj);

				obj = new JSONObject();
				obj.put("v", count);
				obj.put("f", null);
				arr.add(obj);

				obj = new JSONObject();
				obj.put("c", arr);
				totalCountsRows.add(obj);

				arr = new JSONArray();
				obj = new JSONObject();
				obj.put("v", t2);
				obj.put("f", null);
				arr.add(obj);

				obj = new JSONObject();
				obj.put("v", count - lastTotalCount);
				obj.put("f", null);
				arr.add(obj);

				obj = new JSONObject();
				obj.put("c", arr);

				intervalCountsRows.add(obj);

				lastTotalCount = count;

				totalCountsResult.put("cols", totalCountsCols);
				totalCountsResult.put("rows", totalCountsRows);

				intervalCountsResult.put("cols", intervalCountsCols);
				intervalCountsResult.put("rows", intervalCountsRows);
			}

			obj = new JSONObject();
			obj.put("totalCounts", totalCountsResult);
			obj.put("intervalCounts", intervalCountsResult);

			obj.put("realSampleCount",
					(endStep - beginStep + 1) >= sampleCount ? sampleCount
							: ((endStep - beginStep + 1)));

			obj.put("maxSampleCount", (endStep - beginStep + 1) >= 100 ? 100
					: ((endStep - beginStep + 1)));

			long durationMs = (endStep - beginStep) * MessageListener.interval;

			obj.put("duration",
					MessageListener.getDurationBreakdown(durationMs));

			BigDecimal bd = new BigDecimal(endCount - startCount);

			obj.put("increaseCount",
					MessageListener.formatter.format(bd.longValue()));

			// jsonStr = obj.toString();

			return obj;

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private JSONObject generateLatestChartData(int latestStepsNum)
	{
		JSONObject totalCountsResult = new JSONObject();
		JSONObject intervalCountsResult = new JSONObject();
		JSONArray totalCountsCols = new JSONArray();
		JSONArray totalCountsRows = new JSONArray();
		JSONArray intervalCountsCols = new JSONArray();
		JSONArray intervalCountsRows = new JSONArray();

		JSONObject obj = new JSONObject();
		obj.put("id", "");
		obj.put("label", "");
		obj.put("pattern", "");
		obj.put("type", "string");
		totalCountsCols.add(obj);

		obj = new JSONObject();
		obj.put("id", "");
		obj.put("label", "Amount");
		obj.put("pattern", "");
		obj.put("type", "number");
		totalCountsCols.add(obj);

		obj = new JSONObject();
		obj.put("id", "");
		obj.put("label", "");
		obj.put("pattern", "");
		obj.put("type", "string");
		intervalCountsCols.add(obj);

		obj = new JSONObject();
		obj.put("id", "");
		obj.put("label", "Increment");
		obj.put("pattern", "");
		obj.put("type", "number");
		intervalCountsCols.add(obj);

		JSONArray arr;

		Object scopeObj = MessageListener.getLatestInfo(latestStepsNum);

		int peak = MessageListener.totalTimesList
				.get(MessageListener.totalTimesList.size() - 1);

		int scopeInfo[];

		if (scopeObj == null)
		{
			return null;
		}
		else
		{
			scopeInfo = (int[]) scopeObj;
		}

		// System.out.println(scopeInfo[0] + "\t" + scopeInfo[1] + "\t"
		// + scopeInfo[2]);

		long currentTotalCount = 0;
		long lastTotalCount = 0;

		if ((scopeInfo[2] - latestStepsNum) > 0)
		{
			int priorStep = scopeInfo[2] - latestStepsNum;

			if (scopeInfo[0] > 0
					&& MessageListener.totalTimesList.get(scopeInfo[0] - 1) == priorStep)
			{
				lastTotalCount = MessageListener.totalCountList
						.get(scopeInfo[0] - 1);
			}
			else
			{
				lastTotalCount = MessageListener.totalCountList
						.get(scopeInfo[0]);
			}
		}

		for (int i = (scopeInfo[2] - latestStepsNum) >= 0 ? (scopeInfo[2]
				- latestStepsNum + 1) : 1; i <= scopeInfo[2]; i++)
		{
			currentTotalCount = 0;

			if (scopeInfo[0] + 1 <= scopeInfo[1])
			{
				if (i <= MessageListener.totalTimesList.get(scopeInfo[0]))
				{
					currentTotalCount = MessageListener.totalCountList
							.get(scopeInfo[0]);
				}
				else
				{
					currentTotalCount = MessageListener.totalCountList
							.get(scopeInfo[0] + 1);

					scopeInfo[0] = scopeInfo[0] + 1;
				}
			}
			else
			{
				currentTotalCount = MessageListener.totalCountList
						.get(scopeInfo[1]);
			}

			arr = new JSONArray();
			obj = new JSONObject();

			String t = MessageListener.sdf.format(new Date(
					MessageListener.baseTime + (i - 1)
							* MessageListener.interval));

			obj.put("v", t);
			obj.put("f", null);
			arr.add(obj);

			obj = new JSONObject();
			obj.put("v", currentTotalCount);
			obj.put("f", null);
			arr.add(obj);

			obj = new JSONObject();
			obj.put("c", arr);
			totalCountsRows.add(obj);

			arr = new JSONArray();
			obj = new JSONObject();
			obj.put("v", t.split("\r\n")[1].trim());
			obj.put("f", null);
			arr.add(obj);

			obj = new JSONObject();
			obj.put("v", currentTotalCount - lastTotalCount);
			obj.put("f", null);
			arr.add(obj);

			obj = new JSONObject();
			obj.put("c", arr);

			intervalCountsRows.add(obj);

			lastTotalCount = currentTotalCount;
		}

		totalCountsResult.put("cols", totalCountsCols);
		totalCountsResult.put("rows", totalCountsRows);
		// System.out.println(rows);

		intervalCountsResult.put("cols", intervalCountsCols);
		intervalCountsResult.put("rows", intervalCountsRows);

		obj = new JSONObject();
		// arr = new JSONArray();
		// arr.add(totalCountsResult);
		// arr.add(intervalCountsResult);
		obj.put("totalCounts", totalCountsResult);
		obj.put("intervalCounts", intervalCountsResult);

		obj.put("peakStep", peak);

		// System.out.println("1111111111111111111  " + totalCountsResult);
		// System.out.println("2222222222222222222  " +
		// intervalCountsResult);
		// System.out.println("0000000000000000000000000  " + arr);

		// jsonStr = obj.toString();

		return obj;

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

	}

}
