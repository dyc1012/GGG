package com.oracle.olt.rpt;

public class Report
{
	private String buildID;
	private String jobName;
	private String genTime;
	private String rptLink;
	
	public String getBuildID()
	{
		return buildID;
	}
	public void setBuildID(String buildID)
	{
		this.buildID = buildID;
	}
	public String getJobName()
	{
		return jobName;
	}
	public Report(String buildID, String jobName, String genTime, String rptLink)
	{
		super();
		this.buildID = buildID;
		this.jobName = jobName;
		this.genTime = genTime;
		this.rptLink = rptLink;
	}
	public void setJobName(String jobName)
	{
		this.jobName = jobName;
	}
	public String getGenTime()
	{
		return genTime;
	}
	public void setGenTime(String genTime)
	{
		this.genTime = genTime;
	}
	public String getRptLink()
	{
		return rptLink;
	}
	public void setRptLink(String rptLink)
	{
		this.rptLink = rptLink;
	}
	
}
