package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

/**
 * se configura lectura de archivos, logs y cosas comunes a cualquier aplicacion
 * con spark framework
 * @author rodrigo
 *
 */
public class BaseMain
{
	public Properties prop = new Properties();
	public BaseMain()
	{
		//--------busco archivo de configuracion y asigno datos
		File file = new File("conf/config.properties");
		prop = new Properties();
    	InputStream stream;
    	
		try
		{
			
			stream = new FileInputStream(file.getPath());
			prop.load(stream);
			
			
		} 
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
	       try
			{
	        	  Properties props = new Properties();
				props.load(new FileInputStream(prop.getProperty("log4jPath")));
				  PropertyConfigurator.configure(props);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      
	}

}
