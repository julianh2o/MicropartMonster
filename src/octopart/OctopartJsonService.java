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

public class OctopartJsonService extends JFrame {
	public static JsonObject fetchJson(URL url) {
		InputStream in;
		String data;
		try {
			in = url.openStream();
			try {
				data = IOUtils.toString( in );
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				IOUtils.closeQuietly(in);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		//TODO cache based on url
		try {
			FileUtils.write(new File("./cachedresult.json"), data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new JsonParser().parse(data).getAsJsonObject();
	}
	
	public static JsonObject getStoredResults() {
		try {
			return new JsonParser().parse(FileUtils.readFileToString(new File("./cachedresult.json"))).getAsJsonObject();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
