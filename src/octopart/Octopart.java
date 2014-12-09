package octopart;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.client.utils.URIBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Octopart {
	public static String apiKey;
	private static Octopart instance = null;
	
	public static void setApiKey(String apiKey) {
		Octopart.apiKey = apiKey;
	}
	
	public static Octopart getInstance() {
		if (instance == null) instance = new Octopart();
		return instance;
	}
	
	private List<Parameter> defaultParameters = new LinkedList<Octopart.Parameter>();
	private Octopart() {
		defaultParameters.add(new Parameter("include[]","descriptions"));
		defaultParameters.add(new Parameter("include[]","specs"));
		defaultParameters.add(new Parameter("apikey",apiKey));
		//defaultParameters.put("filter[fields][offers.seller.uid][]","2c3be9310496fffc"); //digikey
	}
	
	private URL createSearchUrl(String query) {
		URL url;
		try {
			URIBuilder builder = new URIBuilder("http://octopart.com/api/v3/parts/search");
			for (Entry<String,String> entry : defaultParameters) {
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
		System.out.println("URL: "+url.toString());
//		JsonObject data = OctopartJsonService.getStoredResults();
		JsonObject data = OctopartJsonService.fetchJson(url);
		
		List<Part> parts = new ArrayList<Part>(data.getAsJsonArray("results").size());
		for (JsonElement el : data.getAsJsonArray("results")) {
			JsonObject object = el.getAsJsonObject();
			
			parts.add(new Part(object));
		}
			
		return parts;
	}
	
	private class Parameter implements Entry<String,String> {
		private String key,value;

		public Parameter(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public String setValue(String value) {
			this.value = value;
			return value;
		}
	}
}
