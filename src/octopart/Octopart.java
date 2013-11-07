package octopart;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharSet;
import org.apache.http.client.utils.URIBuilder;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import au.com.bytecode.opencsv.CSVReader;

public class Octopart extends JFrame {
	public static String apiKey;
	private static Octopart instance = null;
	
	public static void setApiKey(String apiKey) {
		Octopart.apiKey = apiKey;
	}
	
	public static Octopart getInstance() {
		if (instance == null) instance = new Octopart();
		return instance;
	}
	
	private HashMap<String,String> defaultParameters = new HashMap<String, String>();
	private Octopart() {
		defaultParameters.put("include[]","descriptions");
		defaultParameters.put("filter[fields][offers.seller.uid][]","2c3be9310496fffc"); //digikey
		defaultParameters.put("apikey",apiKey);
	}
	
	private URL createSearchUrl(String query) {
		URL url;
		try {
			URIBuilder builder = new URIBuilder("http://octopart.com/api/v3/parts/search");
			for (Entry<String,String> entry : defaultParameters.entrySet()) {
				builder.addParameter(entry.getKey(), entry.getValue());
			}
			builder.addParameter("q",query);
			url = builder.build().toURL();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		return url;
	}
	
	public List<Part> findParts(String query) {
		URL url = createSearchUrl(query);
		JsonObject data = OctopartJsonService.fetchJson(url);
		
		List<Part> parts = new LinkedList<Part>();
		for (JsonElement el : data.getAsJsonArray("results")) {
			JsonObject object = el.getAsJsonObject();
			
			parts.add(new Part(object));
		}
			
		return parts;
	}
}
