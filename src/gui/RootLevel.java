package gui;

import java.io.Console;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.awt.GraphicsEnvironment;

public class RootLevel {
	private static final Logger LOGGER = Logger.getGlobal();
	
	public static void main(String[] args) {
		// debug purposes
		/*
		Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()){
            String filename = RootLevel.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            try {
				Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + filename + "\""});
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		FileHandler fh;
		try {
	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("MyLogFile.log");  
	        LOGGER.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

	        // the following statement is used to log any messages  
	        LOGGER.info("Application First Log");  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }
	    */
		System.setProperty("file.encoding", "UTF-8");
		new LoginFrame();
	}
}
