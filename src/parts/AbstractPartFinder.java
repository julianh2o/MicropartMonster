package parts;

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

public class AbstractPartFinder extends JFrame {
	public final String digikeyuid = "2c3be9310496fffc";
	public String selectPart(int row, String search) {
		URL url;
		try {
			URIBuilder builder = new URIBuilder("http://octopart.com/api/v3/parts/search");
			builder.addParameter("include[]","descriptions");
			builder.addParameter("filter[fields][offers.seller.uid][]",digikeyuid);
			builder.addParameter("apikey",apiKey);
			builder.addParameter("q",search);
			url = builder.build().toURL();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("URL: "+url.toString());
		JsonObject data;
		if (new File("./cachedresult.json").exists()) {
			data = getStoredResults();
		} else {
			data = fetchJson(url);
		}
		for (JsonElement el : data.getAsJsonArray("results")) {
			JsonObject object = el.getAsJsonObject();
			JsonObject item = object.getAsJsonObject("item");
			String mpn = item.get("mpn").getAsString();
			JsonArray offers = item.getAsJsonArray("offers");
			String description = getDescription(item);
			for (JsonElement offer : offers) {
				JsonObject offerObj = offer.getAsJsonObject();
				int qty = offerObj.get("in_stock_quantity").getAsInt();
				String sku = offerObj.get("sku").getAsString();
				String seller = offerObj.get("seller").getAsJsonObject().get("uid").getAsString();
				String prices = offerObj.get("prices").toString();
				if (qty > 0 && digikeyuid.equals(seller)) {
					System.out.println(mpn);
					System.out.println("description: "+description);
					System.out.println("prices: "+prices);
					System.out.println("sku: "+sku);
					//System.out.println("   "+offer.toString());
				}
			}
		}
			
		return search;
	}
	
	public String getDescription(JsonObject result) {
		if (!result.has("descriptions")) return null;
		for (JsonElement el : result.getAsJsonArray("descriptions")) {
			JsonObject obj = el.getAsJsonObject();
			return obj.get("value").getAsString();
		}
		return null;
	}
	
	public static final String apiKey = "566cc7d2";
	public JsonObject fetchJson(URL url) {
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
		
		try {
			FileUtils.write(new File("./cachedresult.json"), data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new JsonParser().parse(data).getAsJsonObject();
	}
	
	public JsonObject getStoredResults() {
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
