package parts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;

public class PropertyManager extends JFrame {
	Properties defaultProperties;
	Properties userProperties;
	
	
	private static PropertyManager instance;
	public static PropertyManager getInstance() {
		try {
			if (instance == null) instance = new PropertyManager();
		} catch (IOException e) {
			return null;
		}
		return instance;
	}
	
	private PropertyManager() throws IOException {
		loadProperties();
	}
	
	public File getDefaultProperties() {
		return new File("default.properties");
	}
	
	public File getUserProperties() {
		return new File("user.properties");
	}
	
	public static Properties loadPropertiesFile(File f) throws IOException {
		Properties props = new Properties();
		FileInputStream in = new FileInputStream(f);
		props.load(in);
		in.close();
		return props;
	}
	
	public static void saveProperties(Properties props, File f) throws IOException {
		FileOutputStream out = new FileOutputStream(f);
		props.store(out,"");
		out.close();
	}
	
	public void save() {
		try {
			saveProperties(this.userProperties,getUserProperties());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadProperties() throws IOException {
		File defaultFile = getDefaultProperties();
		if (defaultFile.exists()) {
			this.defaultProperties = loadPropertiesFile(defaultFile);
		} else {
			this.userProperties = new Properties();
		}
		
		File userFile = getUserProperties();
		if (userFile.exists()) {
			this.userProperties = loadPropertiesFile(userFile);
		} else {
			this.userProperties = new Properties();
		}
	}
	
	public Object get(String key) {
		if (userProperties.containsKey(key)) return userProperties.get(key);
		
		if (defaultProperties.containsKey(key)) return defaultProperties.get(key);
		
		return null;
	}
	
	public String getString(String key) {
		return (String)get(key);
	}
	
	public int getInt(String key) {
		return Integer.parseInt(getString(key));
	}
	
	public void set(String key, String value) {
		this.userProperties.setProperty(key, value);
		save();
	}
	
	public void set(String key, int value) {
		this.userProperties.setProperty(key, ""+value);
		save();
	}
}
