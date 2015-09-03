package com.wmclient.clientsdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.R.string;
import android.view.SurfaceHolder;

import com.google.gson.Gson;

public class WMClientSdk 
{
	private static WMClientSdk m_sdkinstance = new WMClientSdk();
	
	private Map m_streamPlayerMap = new HashMap();
	private ClientEngineer m_engineer = new ClientEngineer();
	
	private WMDeviceInfo[] m_deviceList = null;
	private WMMapNodeInfo[] m_mapNodeList = null;

	public static WMClientSdk getInstance() 
	{
        return m_sdkinstance;
    }

	public int init(int logLevel) 
	{
		return m_engineer.init(logLevel);
	}
	
	public int uninit() 
	{
		return m_engineer.uninit();
	}
	
	public int login(String userName, String password, String svrIp, int svrPort) 
	{
		return m_engineer.login(userName, password, svrIp, svrPort);
	}
	
	public int logout()
	{
		return m_engineer.logout();
	}
	public int saveSnapshot(int playerId, String fileName)
	{
		StreamPlayer streamPlayer = (StreamPlayer)m_streamPlayerMap.get(playerId);
		if(null == streamPlayer)
		{
			return Constants.fail;
		}
			
		return streamPlayer.saveSnapshot(fileName);
	}
	
	public int openSound(int playerId)
	{
		StreamPlayer streamPlayer = (StreamPlayer)m_streamPlayerMap.get(playerId);
		if(null == streamPlayer)
		{
			return Constants.fail;
		}
		
		return streamPlayer.OpenSound();
	}
	
	public int closeSound(int playerId)
	{
		StreamPlayer streamPlayer = (StreamPlayer)m_streamPlayerMap.get(playerId);
		if(null == streamPlayer)
		{
			return Constants.fail;
		}
		
		return streamPlayer.CloseSound();		
	}
	
	public int updatePassword(String oldPassword, String newPassword)
	{
		return m_engineer.UpdatePassword(oldPassword, newPassword);
	}
	
	public int getDeviceList(List<WMDeviceInfo> deviceList) 
	{
		if(m_deviceList == null)
		{
			m_deviceList = m_engineer.GetDeviceList();
		}	
		
		//add
		for(int i=0; i<m_deviceList.length; i++)
		{
			deviceList.add(m_deviceList[i]);
		}
		
		return Constants.success;
	}
	
	public int getMapNodeList(List<WMMapNodeInfo> nodeList) 
	{			
		if(m_mapNodeList == null)
		{
			m_mapNodeList = m_engineer.GetMapNodeList();
		}	
		
		//add
		for(int i=0; i<m_mapNodeList.length; i++)
		{
			nodeList.add(m_mapNodeList[i]);
		}
		
		return Constants.success;
	}
	
	public StreamPlayer CreatePlayer(int deviceType, Object showObj)
	{
		StreamPlayer streamPlayer =  new StreamPlayer(deviceType);
		streamPlayer.setShowObj(showObj);
		
		return streamPlayer;
	}

	public int DestroyPlayer(StreamPlayer player)
	{
		player.stopPlay();

		return Constants.success;
	}	
	
	public int startRealPlay(int deviceId, int devChannelId, StreamPlayer player) 
	{
		int deviceType = player.getDeviceType();		
		if(Constants.DEVICE_TYPE_XM_DEV != deviceType && Constants.DEVICE_TYPE_HK_DEV != deviceType
			&& Constants.DEVICE_TYPE_HK_PUSHDEV != deviceType)
		{
			return Constants.WMPLAYERID_INVALID;
		}
		
        //start play
		int playerId = m_engineer.StartRealPlay(deviceId, devChannelId, player);		
		if(Constants.WMPLAYERID_INVALID != playerId) 
		{	
			m_streamPlayerMap.put(playerId, player);
			//m_engineer.SetRealPlayCallBackFunc(playerId, player);
		}
		
		return playerId;
	}
	
	public int stopRealPlay(int playerId) 
	{
//		int ret = m_engineer.ResetRealPlayCallBackFunc(playerId);		
//		if(Constants.success == ret)
//		{
			m_engineer.StopRealPlay(playerId);	
			m_streamPlayerMap.remove(playerId);
//		}
				
		return Constants.success;
	}
	
	public int ptzControl(int deviceId, int devChannelId, int ptzCommand, int nStop, int nSpeed)
	{
		return m_engineer.PTZControl(deviceId, devChannelId, ptzCommand, nStop, nSpeed);		
	}
	
	public int startRecord(int playerId, String fileName)	
	{
		return m_engineer.StartRecord(playerId, fileName);
	}
	
	public int stopRecord(int playerId)	
	{
		return m_engineer.StopRecord(playerId);
	}	
	
	public int LocalFilePlayStart(String filePath, Object showObj, FileDuration fileDuration)
	{
		return Constants.success;
	}
	
	public int FilePlayStop(int playerId)
	{
		return Constants.success;
	}
}
