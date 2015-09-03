package com.wmclient.clientsdk;

//请在这里添加您的包名
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.widget.EditText;

public class LineEditText extends EditText {

	private Paint mPaint;
	/**
	 * @param context
	 * @param attrs
	 */
	public LineEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();
		
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.BLACK);
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		int width=getWidth();
	
		int count=width/15;
	
//		画底线
		for(int i=0;i<count;i++)
		{
			if(i%2==0)
			canvas.drawLine(i*15,this.getHeight()-1,  (i+1)*15, this.getHeight()-1, mPaint);	
			
		}
		
	}
}