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
	HashMap<String,Object> properties;
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
		properties = new HashMap<String, Object>();
		loadProperties();
	}
	
	public HashMap<String,Object> getProperties() {
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
		HashMap<String,Object> data = (HashMap<String, Object>) yaml.load(in);
		this.properties = data;
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
	
	public void clear() {
		properties = new HashMap<String,Object>();
		properties.put("inventories",new LinkedList<Object>());
		properties.put("projects",new LinkedList<Object>());
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
