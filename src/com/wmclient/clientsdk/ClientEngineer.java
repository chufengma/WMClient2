package com.wmclient.clientsdk;

public class ClientEngineer 
{
	public native int init(int logLevel);
	public native int uninit();
	
	public native int login(String userName, String password, String svrIp, int svrPort);
	public native int logout();
	
	public native int UpdatePassword(String oldPassword, String newPassword);
	
	public native WMDeviceInfo[] GetDeviceList();	
	public native WMMapNodeInfo[] GetMapNodeList();
	
	public native int[] GetOnlineDeviceIds();
	
    public native int StartRealPlay(int deviceId, int devChannelId, Object callback);
    public native int StopRealPlay(int playerId);
    
    //public native int SetRealPlayCallBackFunc(int playerId, Object callback);
    //public native int ResetRealPlayCallBackFunc(int playerId);
    
    public native int PTZControl(int deviceId, int devChannelId, int ptzCommand, int nStop, int nSpeed);
    
    public native int StartRecord(int playerId, String filePath);
    public native int StopRecord(int playerId);
    
	static 
	{  
        System.loadLibrary("wmclientsdk");
    }    
}