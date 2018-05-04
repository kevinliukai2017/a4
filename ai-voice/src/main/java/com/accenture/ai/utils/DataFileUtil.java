package com.accenture.ai.utils;

import java.io.File;
import java.io.FileWriter;

public class DataFileUtil {

	public static void writeFile(final String filePath, final String fileName, final String errorStr)
	{
		final File folder = new File(filePath);
		if (!folder.exists())
		{
			folder.mkdirs();
		}
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(filePath + fileName);
			fw.write(errorStr);
			fw.close();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				fw.close();
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
