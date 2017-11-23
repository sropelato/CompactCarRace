package net.ropelato.compactcarrace.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileHandler
{
	public static void copyFile(File from, File to, boolean overwrite)
	{
		if(!from.exists())
		{
			System.out.println("could not copy file '" + from + "' to '" + to + "' (reason: file does not exist)");
			return;
		}
		if(to.exists() && !overwrite)
		{
			System.out.println("could not copy file '" + from + "' to '" + to + "' (reason: file already exists, no permission to overwrite)");
			return;
		}

		try
		{
			FileReader in = new FileReader(from);
			FileWriter out = new FileWriter(to);

			int c;
			while((c = in.read()) != -1)
			{
				out.write(c);
			}

			in.close();
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void copyDirectoryContent(File from, File to, boolean overwrite)
	{
		File[] files;
		files = from.listFiles();
		for(int i = 0; i < files.length; i++)
		{
			copyFile(files[i], to, overwrite);
		}
	}
}
