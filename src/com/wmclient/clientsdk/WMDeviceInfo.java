package com.wmclient.clientsdk;

import java.io.Serializable;

public class WMDeviceInfo implements Serializable
{
	private static final long serialVersionUID = 8344797106285912400L;
	
	private int devId;
    private int devType;
    private String devName;
    private int status;
    private WMChannelInfo channelArr[];

    public int getDevId() 
    {
        return devId;
    }

    public void setDevId(int devId) 
    {
        this.devId = devId;
    }
    
    public int getDevType() 
    {
        return devType;
    }

    public void setDevType(int devType) 
    {
        this.devType = devType;
    }    

    public String getDevName() 
    {
        return devName;
    }

    public void setDevName(String devName) 
    {
        this.devName = devName;
    }
    
    public int getStatus() 
    {
        return status;
    }

    public void setStatus(int status) 
    {
        this.status = status;
    }  
    
    public WMChannelInfo[] getChannelArr()
    {
    	return channelArr;
    }
    
    public void setChannelArr(WMChannelInfo[] channelArr)
    {
    	this.channelArr = channelArr;
    }
}
