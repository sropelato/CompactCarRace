package net.ropelato.compactcarrace.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SynchronizedRandom
{
	int numberOfEntries = 2000;

	double[] values = new double[numberOfEntries];

	int position = 0;

	public SynchronizedRandom(String fileName, int seed)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			boolean eof = false;
			int i = 0;
			while(!eof)
			{
				String str = reader.readLine();
				if(str != null)
				{
					values[i] = new Double(str).doubleValue();
					i++;
				}
				else
				{
					eof = true;
				}
			}
			reader.close();
			position = seed;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public double getValue()
	{
		double value = values[position];
		position++;
		if(position >= numberOfEntries)
		{
			position = 0;
		}
		return value;
	}

}
