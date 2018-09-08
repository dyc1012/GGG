package com.oracle.olt.msg;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.minidev.json.JSONObject;

/**
 * Web应用监听器
 */
public class MessageListener implements ServletContextListener
{
	private static MyThread myThread;
	private static MyThread1 myThread1;
	public static long baseTime;
	public static int interval;
	private static Random r = new Random();
	public static List<Long> totalCountList;
	public static List<Integer> totalTimesList;
	public static ServletContext SC;
	public static NumberFormat formatter = NumberFormat.getInstance(new Locale(
			"en_US"));

	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"MM/dd/yyyy\r\nHH:mm:ss");

	public static SimpleDateFormat sdf1 = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss");

	public static Object[] dataCache;

	public static boolean simulateGen = false;

	public static synchronized void updateCache(JSONObject json)
	{
		// if ((System.currentTimeMillis()
		// - (dataCache[0] == null ? 0 : Long.valueOf(dataCache[0]
		// .toString())) > 2000))
		{
			dataCache[0] = System.currentTimeMillis();
			dataCache[1] = json;
		}

	}

	public void contextInitialized(ServletContextEvent event)
	{
		initWork(event.getServletContext());

		System.out.println("servlet context initialized...");
	}

	public static void initWork(ServletContext sc)
	{
		dataCache = new Object[2];
		baseTime = System.currentTimeMillis();

		totalCountList = new ArrayList<Long>();
		totalTimesList = new ArrayList<Integer>();

		interval = 5000; // ms
		System.out.println("Interval span: " + interval + " ms");

		SC = sc;
		sc.setAttribute("count", "0");
		sc.setAttribute("baseTime", baseTime);

		String baseTimeStr = sdf.format(new Date(baseTime))
				.replace("\r\n", " ");

		sc.setAttribute("baseTimeStr", baseTimeStr);
		System.out.println("Base time: " + baseTimeStr);
		System.out.println("Initial total count: " + SC.getAttribute("count"));

		if (myThread == null)
		{
			myThread = new MyThread();
			myThread.setName("MsgCollector");
			myThread.start();
		}

		if (myThread1 == null && simulateGen)
		{
			myThread1 = new MyThread1();
			myThread1.setName("MsgGenerator");
			myThread1.start();
		}

		System.out.println("初始化工作完成...");

	}

	public void contextDestroyed(ServletContextEvent event)
	{

		clearWork(event.getServletContext());

		System.out.println("servlet context destroyed...");
	}

	public static void clearWork(ServletContext sc)
	{
		if (myThread != null && !myThread.isInterrupted())
		{
			System.out.println("中断线程...");
			myThread.interrupt();
			// myThread.stop();
			myThread = null;
		}

		if (myThread1 != null && !myThread1.isInterrupted())
		{
			System.out.println("中断线程1...");
			myThread1.interrupt();
			// myThread1.stop();
			myThread1 = null;
		}

		totalCountList = null;
		totalTimesList = null;

		System.out.println("销毁工作完成...");

	}

	public static int binarySearch(List<Integer> list, int des)
	{
		int low = 0;
		int high = list.size() - 1;

		while (low <= high)
		{
			int middle = low + (high - low) / 2;

			if (middle < list.size())
			{
				if (middle > 0 && des > list.get(middle - 1)
						&& des <= list.get(middle))
				{
					return middle;
				}
				else if (middle == 0)
				{
					if (list.size() > 1 && des > list.get(0)
							&& des <= list.get(1))
					{
						return 1;
					}
					else
					{
						return 0;
					}

				}
				else if (middle == list.size() - 1)
				{
					return list.size() - 1;
				}
				else if (list.get(middle) > des)
				{
					high = middle - 1;
				}
				else
				{
					low = middle + 1;
				}
			}
			else
			{
				// System.out
				// .println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				// System.out.println(des);
				// System.out.println(totalTimesList);
				// System.out
				// .println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

				return -1;
			}

		}

		// System.out
		// .println("kkkkkkkkkkkkkkk~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		// System.out.println(des);
		// System.out.println(totalTimesList);
		// System.out
		// .println("kkkkkkkkkkkkkkkkk~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		return -1;
	}

	public static String getDurationBreakdown(long millis)
	{
		if (millis < 0)
		{
			throw new IllegalArgumentException(
					"Duration must be greater than zero!");
		}

		// long days = TimeUnit.MILLISECONDS.toDays(millis);
		// millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		StringBuilder sb = new StringBuilder(64);
		// sb.append(days);
		// sb.append(" Days ");
		sb.append(hours);
		sb.append(":");
		sb.append(minutes);
		sb.append(":");
		sb.append(seconds);
		// sb.append(" Seconds");

		return sb.toString();
	}

	public static void main(String[] args) throws Exception
	{

		final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		final Calendar c = Calendar.getInstance();
		try
		{
			c.setTime(new Date(1492841940872L));
			System.out.println("Year = " + c.get(Calendar.YEAR));
			System.out.println("Month = " + (c.get(Calendar.MONTH) + 1));
			System.out.println("Day = " + c.get(Calendar.DAY_OF_MONTH));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println(new Date(1492841940872L).getYear());
		System.out.println(sdf.format(new Date(1492841940872L)));

		BigDecimal bd = new BigDecimal(30123000123123120L);

		NumberFormat formatter = NumberFormat.getInstance(new Locale("en_US"));

		System.out.println(formatter.format(bd.longValue()));

		System.out
				.println(getDurationBreakdown((12 * 3600 + 35 * 60 + 100) * 1000));

		System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date())
				+ "_abcdefg");

		List list = new ArrayList();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		list.add(6);
		list.add(7);
		list.add(8);
		list.add(9);
		list.add(10); // No. 10
		list.add(11);
		list.add(12);
		list.add(13);

		System.out.println(binarySearch(list, 2) + "\txx");
		System.out.println(binarySearch(list, 3));
		System.out.println(binarySearch(list, 11));
		System.out.println(binarySearch(list, 13));
		System.out.println(binarySearch(list, 14));
		System.out.println(binarySearch(list, 108));
		System.out.println(binarySearch(list, 109));
		System.out.println(binarySearch(list, 110));
		System.out.println(binarySearch(list, 300000));

		System.out
				.println("--------------------------------------------------------");

		LinkedHashMap map = new LinkedHashMap();
		map.put(19999, 19999);
		map.put(22, 222);
		map.put(345, 345);
		map.put(26, 26);
		map.put(12323, 12323);

		Set<Entry> s = map.entrySet();

		for (Entry e : s)
		{
			Object k = e.getKey();
			Object v = e.getValue();

			System.out.println(k + " --> " + v);
		}
	}

	// Integer: step number in totalTimesList
	// Long: total count value of that step in totalCountList
	public static LinkedHashMap<Integer, Long> rangeResults(
			List<Integer> totalTimesList, List<Long> totalCountList,
			int fromStepNum, int toStepNum, int sampleCount)
	{

		int intervalSteps = (int) Math
				.floor((toStepNum - fromStepNum) <= sampleCount ? 1.0
						: ((toStepNum - fromStepNum) / (double) sampleCount));

		LinkedHashMap<Integer, Long> resultMap = new LinkedHashMap<Integer, Long>();

		int realSampleCount = (toStepNum - fromStepNum + 1) >= sampleCount ? sampleCount
				: ((toStepNum - fromStepNum + 1));

		// System.out.println(fromStepNum + " ------------------ " + toStepNum);

		for (int i = 0; i < realSampleCount; i++)
		{
			int stepNum = fromStepNum + intervalSteps * i;

			if (i == realSampleCount - 1)
			{
				stepNum = toStepNum;
			}

			int index = binarySearch(totalTimesList, stepNum);
			long count = totalCountList.get(index);

			// System.out.println(stepNum + " -- " + count + "   index:" +
			// index);
			// System.out.println("------>" + totalTimesList);

			resultMap.put(stepNum, count);
		}

		return resultMap;
	}

	public static int[] getLatestInfo(int latestStepsNum)
	{
		int size = totalTimesList.size();

		if (size == 0)
		{
			return null;
		}

		int maxTimes = totalTimesList.get(size - 1);

		if (size == 1)
		{
			return new int[]
			{ 0, 0, totalTimesList.get(0) };
		}

		for (int i = size - 1; i >= 0; i--)
		{
			if (i - 1 >= 0)
			{
				if ((maxTimes - totalTimesList.get(i - 1)) >= latestStepsNum)
				{
					return new int[]
					{ i, (size - 1), maxTimes };
				}
			}
		}

		return new int[]
		{ 0, (size - 1), maxTimes };
	}

	static class MyThread extends Thread
	{

		public void run()
		{
			while (!this.isInterrupted())
			{
				try
				{
					// System.out.print(System.currentTimeMillis() + "\t");

					long c = (long) Long.valueOf(SC.getAttribute("count")
							.toString());

					int maxIndex = totalTimesList.size() - 1;

					long delta = 0;

					if (maxIndex >= 0)
					{
						int maxStep = totalTimesList.get(maxIndex);
						long maxCount = totalCountList.get(maxIndex);

						if (c == maxCount)
						{
							totalTimesList.set(maxIndex, maxStep + 1);
						}
						else if (c > maxCount)
						{
							totalCountList.add(c);
							totalTimesList.add(maxStep + 1);
						}
						else
						{
							throw new RuntimeException(
									"Why total count decreased!!");
						}

						delta = System.currentTimeMillis()
								- (baseTime + maxStep * interval);

						// System.out.println(System.currentTimeMillis() +
						// " - ("
						// + baseTime + " + " + maxStep + " * " + interval
						// + ") = " + delta);

						// if (delta >= interval)
						// {
						// System.out
						// .println(sdf1.format(new Date())
						// + "\tAmount | ListSize | Steps | delta --> "
						// + c
						// + " | "
						// + totalTimesList.size()
						// + " | "
						// + totalTimesList.get(totalTimesList
						// .size() - 1) + " | "
						// + delta + "\r\n");
						//
						// System.out
						// .println("delta reaches to threshold, add one more time number!");

						// System.out
						// .println("maxIndex: "
						// + (totalTimesList.size() - 1)
						// + " | val: "
						// + totalTimesList
						// .get((totalTimesList.size() - 1)));

						// totalTimesList
						// .set(totalTimesList.size() - 1,
						// totalTimesList.get((totalTimesList
						// .size() - 1)) + 1);

						// System.out
						// .println("maxIndex: "
						// + (totalTimesList.size() - 1)
						// + " | val: "
						// + totalTimesList
						// .get((totalTimesList.size() - 1)));
						//
						// System.out
						// .println((totalTimesList.size() - 1)
						// + " 111--- "
						// + totalTimesList.get(totalTimesList
						// .size() - 1));

						// delta = delta - interval;
						// }
					}
					else
					{
						totalCountList.add(c);
						totalTimesList.add(1);
					}

					// System.out.println("totalCountList_"
					// + totalCountList.size() + ":\t" + totalCountList);

					// System.out.println("totalTimesList_"
					// + totalTimesList.size() + ":\t" + totalTimesList);

					// if (delta >= 10 * 1000)
					// {
					// System.out
					// .println("sleep no time to catch up real time >>>>>>>>>>>> "
					// + delta + " ms");
					//
					// Thread.sleep(0);
					// }
					// else
					// {

					// System.out.println(interval + " --- sleep");

					long realDelay = interval - delta;

					// System.out
					// .println(sdf1.format(new Date())
					// + "\tAmount | ListSize | Steps | delta | realDelay --> "
					// + c
					// + " | "
					// + totalTimesList.size()
					// + " | "
					// + totalTimesList.get(totalTimesList.size() - 1)
					// + " | " + delta + " | " + realDelay
					// + "\r\n");

					Thread.sleep(realDelay);

					// }

				}
				catch (InterruptedException e)
				{
					System.out.println("Thread of " + this.getName()
							+ " run complete! ");

					return;
				}

			}

		}
	}

	static class MyThread1 extends Thread
	{
		public void run()
		{
			while (!this.isInterrupted())
			{
				try
				{
					Thread.sleep(2000);
					if (r.nextInt(100) > 60)
					{

						int increase = 800 + r.nextInt(700);
						// System.out.println("++++++++++Increase count: "
						// + increase);

						SC.setAttribute(
								"count",
								Long.valueOf(SC.getAttribute("count")
										.toString()) + increase);
					}
					else
					{
						// System.out.println("++++++++++Increase count: 0");
					}

				}
				catch (InterruptedException e)
				{
					System.out.println("Thread of " + this.getName()
							+ " run complete! ");

					return;
				}
			}

		}

	}

}
