package main;

import static spark.Spark.after;
import static spark.Spark.before;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import conf.Conf;
import controller.CalculateController;
import spark.Spark;

public class Main
{
	public Properties prop = new Properties();

	public Main() throws IOException
	{

		// --------busco archivo de configuracion y asigno datos
		File file = new File("conf/config.properties");
		prop = new Properties();
		InputStream stream;

		stream = new FileInputStream(file.getPath());
		prop.load(stream);

		Properties props = new Properties();
		props.load(new FileInputStream(prop.getProperty("log4jPath")));
		PropertyConfigurator.configure(props);

		// --------- configuro el server
		int maxThreads = 8;
		int minThreads = 2;
		int timeOutMillis = 30000;
		Spark.threadPool(maxThreads, minThreads, timeOutMillis);
		Spark.port(4568);

		// ------------------------------

		Conf.spreadsheetPath = prop.getProperty("spreadsheetPath");

		/// ------------------------------------------------------------

		before((request, response) -> {

			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Request-Method", "POST");
			response.header("Access-Control-Allow-Headers", "*");

			// boolean authenticated = true;

			// if (!authenticated) {
			// halt(401, "You are not welcome here");

			// }

		});

		after((request, response) -> {
			// response.header("Content-Encoding", "gzip");
		});

		// creo el controlador de calculos
		new CalculateController();

		System.out.println("server init...");
	}

}
