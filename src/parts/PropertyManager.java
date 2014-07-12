package parts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

public class PropertyManager extends JFrame {
	public static class Properties {
		public List<OpenWindow> inventories = new LinkedList<OpenWindow>();
		public List<OpenWindow> projects = new LinkedList<OpenWindow>();
	}
	
	public static class OpenWindow {
		public String file;
		public int windowX;
		public int windowY;
		public int windowWidth;
		public int windowHeight;
		public OpenWindow(String file, int windowX, int windowY, int windowWidth, int windowHeight) {
			super();
			this.file = file;
			this.windowX = windowX;
			this.windowY = windowY;
			this.windowWidth = windowWidth;
			this.windowHeight = windowHeight;
		}
	}
	
	Properties properties;
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
		properties = new Properties();
		loadProperties();
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public File getDefaultProperties() {
		return new File("default.yml");
	}
	
	public File getUserProperties() {
		return new File("user.yml");
	}
	
	public void loadPropertiesFile(File f) throws IOException {
		FileInputStream in = new FileInputStream(f);
		Yaml yaml = new Yaml();
		properties = yaml.loadAs(in,Properties.class);
		in.close();
	}
	
	public void saveProperties(File f) throws IOException {
		FileOutputStream out = new FileOutputStream(f);
		Yaml yaml = new Yaml();
		String ymlString = yaml.dumpAsMap(properties);
		PrintWriter pr = new PrintWriter(out);
		pr.write(ymlString);
		pr.close();
		out.close();
	}
	
	public void save() {
		try {
			saveProperties(getUserProperties());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadProperties() throws IOException {
		File userFile = getUserProperties();
		if (userFile.exists()) {
			loadPropertiesFile(userFile);
		}
	}
}
