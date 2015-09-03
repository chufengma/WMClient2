package com.onefengma.wmclient2;

import java.io.File;
import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageViewActivity extends Activity {
	private TextView imageText;
	private ImageView imageView;	
	private String imagePath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imgview_activity);		
		
		imageText = (TextView)findViewById(R.id.imageName);
		imageView = (ImageView)findViewById(R.id.imageView);
		
		imagePath = getIntent().getStringExtra("imagePath");				
		imageText.setText("图片路径：" + imagePath);

		//check
		File file = new File(imagePath);
		try {
			if(!file.exists()) {
				Thread.currentThread().sleep(1000);
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		//show image
		if(file.exists()) {
	       Bitmap bm = BitmapFactory.decodeFile(imagePath);
	       imageView.setImageBitmap(bm);
		}
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }		
	
	@Override
	protected void onStart() { 		
		super.onStart(); 			
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish(); 
		}

		return super.onKeyDown(keyCode, event);
	}	
}
