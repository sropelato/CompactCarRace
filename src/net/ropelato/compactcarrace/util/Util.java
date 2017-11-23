package net.ropelato.compactcarrace.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.Date;

public class Util
{
	private static long fpsTime0;

	private static long fpsTime1;

	private static boolean fpsStart = false;

	private static float fps;

	private static float fpsSum;

	private static float fpsNr;

	private static long stopwatchStart;

	private static boolean indicatorState = false;

	private static boolean oldIndicatorState = false;

	private static long indicatorTime = 0;

	private static int indicatorPeriode = 500;

	public static void delay(int milliSecs)
	{
		try
		{
			Thread.sleep(milliSecs);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
	}

	public static long getMillisecs()
	{
		Date d = new Date();
		return d.getTime();
	}

	public static void startFPSCounter()
	{
		fpsTime0 = getMillisecs();
		fpsStart = true;
	}

	public static float getFPS()
	{
		if(fpsStart == true)
		{
			fpsTime1 = getMillisecs();
			fps = 1000 / (fpsTime1 - fpsTime0 + 1);
			fpsTime0 = getMillisecs();
			fpsSum += fps;
			fpsNr++;
			if(fpsNr > 50)
			{
				fpsSum = fpsSum / fpsNr;
				fpsNr = 1;
			}
			return fps;
		}
		else
		{
			System.out.println("FPSCounter not started.");
			return 0;
		}
	}

	public static float getFPSAveraage()
	{
		getFPS();
		return fpsSum / fpsNr;
	}

	public static void startStopWatch()
	{
		stopwatchStart = getMillisecs();
	}

	public static long getStopWatchTime()
	{
		return getMillisecs() - stopwatchStart;
	}

	public static boolean getIndicatorState()
	{
		if(getStopWatchTime() < indicatorTime + indicatorPeriode)
		{
			indicatorState = true;
		}
		else
		{
			if(getStopWatchTime() > indicatorTime + indicatorPeriode * 2)
			{
				indicatorState = false;
				indicatorTime = getStopWatchTime();
			}
			else
			{
				indicatorState = false;
			}
		}
		return indicatorState;
	}

	public static boolean getIndicatorImpulse()
	{
		oldIndicatorState = indicatorState;
		indicatorState = getIndicatorState();
		if(indicatorState != oldIndicatorState)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static float modifyAngle(float angle)
	{
		while(angle >= 360f)
		{
			angle -= 360f;
		}
		while(angle < 0f)
		{
			angle += 360f;
		}
		return angle;
	}

	public static Image loadImage(File file)
	{
		Image thisImage = null;
		thisImage = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
		return thisImage;
	}
}
