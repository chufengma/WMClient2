package com.wmclient.clientsdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.MediaPlayer.PlayM4.Player;

import com.xm.intercom.AudioDataCallBack;
import com.xm.intercom.IntercomSdk;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

public class HKPlayer implements IPlayer, AudioDataCallBack
{
	private Player m_playerInstance = Player.getInstance();
	private int m_nPort = -1;
	private static final int Errcode_Buf_Over = 11;
	
	private IntercomSdk mIntercomSdk = null;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}		
	};	
		

	@Override
	public boolean IsPlaying()
	{
		return (m_nPort > 0) ? true : false;
	}
	
	@Override
	public int StartPlay(byte[] pStreamHead, int nSize, int streamType, Object showObj)
	{
		int nMode = m_playerInstance.STREAM_REALTIME;
		
		switch(streamType)
		{
		case Constants.WMStreamType_RealTime :
			{
				nMode = m_playerInstance.STREAM_REALTIME;
			}
			break;
			
		case Constants.WMStreamType_File :
			{
				nMode = m_playerInstance.STREAM_FILE;
			}
			break;
			
		default:
			break;
		}
		
		int nPort = m_playerInstance.getPort();
		if(nPort < 0)
		{
			return Constants.fail;
		}
		else if (!m_playerInstance.setStreamOpenMode(nPort, nMode))
		{
			m_playerInstance.freePort(nPort);

			return Constants.fail;
		}
		else if(!m_playerInstance.setSecretKey(nPort, 1, "ge_security_3477".getBytes(), 128))
		{
			m_playerInstance.freePort(nPort);

			return Constants.fail;
		}		
		else if (!m_playerInstance.openStream(nPort, pStreamHead, nSize, 600*1024))	//600K
		{
			m_playerInstance.freePort(nPort);

			return Constants.fail;
		}	
		else if(!m_playerInstance.play(nPort, (SurfaceHolder)showObj))
		{
			m_playerInstance.closeStream(m_nPort);
			m_playerInstance.freePort(nPort);
			
			return Constants.fail;
		}
		
		//set
		m_nPort = nPort;		
		
		return Constants.success;
	}
	
	@Override
	public int StopPlay()
	{
		if(m_nPort < 0)
		{
			return Constants.fail;
		}
		else if(!m_playerInstance.stop(m_nPort))
		{
			return Constants.fail;
		}
		else if(!m_playerInstance.closeStream(m_nPort))
		{
			//return Constants.fail;
		}	
		else if(m_playerInstance.freePort(m_nPort))
		{
			//return Constants;
		}
		
		//reset
		m_nPort = -1;
		
		return Constants.success;
	}

	@Override
	public int InputData(byte[] pBuf, int nSize)
	{
		if(m_nPort < 0)
		{
			return Constants.fail;
		}
		else if(!m_playerInstance.inputData(m_nPort, pBuf, nSize))
		{
			//check buffer over
			if(Errcode_Buf_Over == GetLastError())
			{
				return Constants.ErrorCode_PlayerBufOver;
			}
			
			return Constants.fail;
		}
		
		return Constants.success;
	}

	@Override
	public int PausePlay(int bPause)
	{
		if(m_nPort < 0)
		{
			return Constants.fail;
		}
		else if(!m_playerInstance.pause(m_nPort, bPause))
		{
			return Constants.fail;
		}
		
		return Constants.success;
	}

	@Override
	public int ControlFilePlay(int nControlCode, int nParam)
	{
		if(m_nPort < 0)
		{
			return Constants.fail;
		}
		
		switch(nControlCode)
		{
		case Constants.WMPlayControlCode_SpeedUp:
			{
				return (m_playerInstance.fast(m_nPort) ? Constants.success : Constants.fail);
			}

		case Constants.WMPlayControlCode_SpeedDown:
			{
				return (m_playerInstance.slow(m_nPort) ? Constants.success : Constants.fail);
			}

		default:
			break;
		}

		return Constants.success;
	}
	
	@Override
	public int GetPlaySpeed()
	{
		return Constants.success;
	}

	@Override
	public int GetPlayTime()
	{
		return Constants.success;
	}
	
	@Override
	public int SetPlayTime()
	{
		return Constants.success;
	}

	@Override
	public int OpenSound()
	{
		if(m_nPort < 0)
		{
			return Constants.fail;
		}
		else if (!m_playerInstance.playSound(m_nPort))
		{
			return Constants.fail;
		}

		return Constants.success;
	}
	
	@Override
	public int CloseSound()
	{
		if(m_nPort < 0)
		{
			return Constants.fail;
		}
		else if (!m_playerInstance.stopSound())
		{
			return Constants.fail;
		}

		return Constants.success;
	}

	@Override
	public int SetVolume(int nVolume)
	{
		return Constants.success;
	}
	
	@Override
	public int GetVolume()
	{
		return Constants.success;
	}

	@Override
	public int SaveSnapshot(String fileName, int nFormat)
	{
		if(Constants.WMSnapshotType_JPEG != nFormat)
		{
			return Constants.fail;
		}
		
		Player.MPInteger stWidth = new Player.MPInteger();
		Player.MPInteger stHeight = new Player.MPInteger();
		
	    if (!m_playerInstance.getPictureSize(m_nPort, stWidth, stHeight))
	    {
	    	return Constants.fail;
	    }
	    
	    int nSize = 2 * stWidth.value * stHeight.value;
	    byte[] picBuf = new byte[nSize];
	    Player.MPInteger stSize = new Player.MPInteger();
	    
	    //BMP picture
	    if(!m_playerInstance.getJPEG(m_nPort, picBuf, nSize, stSize))
	    {
	    	return Constants.fail;
	    }
	    
	    try
	    {
	    	File file = new File(fileName);
	    	file.createNewFile();
	    	
	    	if(!file.isFile()) 
	    	{
	    		return Constants.fail;
	    	}	    	
	    	
		    FileOutputStream fileStream = new FileOutputStream(file);
		    fileStream.write(picBuf, 0, stSize.value);
		    fileStream.close();
	    }
	    catch (Exception err)
		{
	    	return Constants.fail;
		}
	    
		return Constants.success;
	}

	@Override
	public int ResetSourceBuffer()
	{
		return Constants.success;
	}
	
	@Override
	public int GetSourceBufferSize()
	{
		return Constants.success;
	}	
/*	
	public int StartVoiceTalk()
	{
		return Constants.success;
	}	
	
	public int StopVoiceTalk()
	{
		return Constants.success;
	}		
	
	@Override
	public int GetLastError()
	{
		if(m_nPort < 0)
		{
			return Constants.fail;
		}
		
		return m_playerInstance.getLastError(m_nPort);
	}*/
	@Override
	public int StartVoiceTalk(Handler handler, Context context)
	{
		if(null == mIntercomSdk)
		{
			mIntercomSdk = new IntercomSdk(mHandler, context);
			mIntercomSdk.setAudioDataListener(this);			
		}
		
		mIntercomSdk.onStart();
		
		return Constants.success;
	}	
	
	@Override
	public int StopVoiceTalk()
	{
		mIntercomSdk.onStop();
		
		return Constants.success;
	}		
	
	@Override
	public int GetLastError()
	{
		return 0;
	}
	
	@Override
	public void onData(byte[] data, int len) {
		Log.e("clientsdk.jar", "voice data callback , size:" + len);
	}	
}
