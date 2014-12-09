package octopart;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PartCache {
	private static Gson GSON = new GsonBuilder().registerTypeAdapter(Part.class, new GsonPartAdapter()).create();
	private static PartCache instance = null;
	public static PartCache getInstance() {
		if (instance == null) instance = new PartCache();
		return instance;
	}
	
	Map<String,Part> partNumberMap;
	File file;
	
	public PartCache() {
		partNumberMap = new HashMap<String,Part>();
	}
	
	public Part getPart(String sku) {
		if (!partNumberMap.containsKey(sku)) {
			partNumberMap.put(sku,fetchPart(sku));
			trySave();
		}
		return partNumberMap.get(sku);
	}
	
	public boolean isPartCached(String sku) {
		return partNumberMap.containsKey(sku);
	}
	
	private Part fetchPart(String sku) {
		List<Part> parts = Octopart.getInstance().findParts(sku);
		for (Part p : parts) {
			if (p.getSkuPartNumber("Digi-Key").equals(sku)) return p;
		}
		return null;
	}
	
	public String toJson() {
		JsonObject jobj = new JsonObject();
		for (Entry<String,Part> entry : partNumberMap.entrySet()) {
			if (entry.getValue() != null) {
				jobj.add(entry.getKey(), entry.getValue().json);
			}
		}
		return jobj.toString();
	}
	
	public void loadCache(File f) {
		this.file = f;
		try {
			JsonObject jobj = new JsonParser().parse(FileUtils.readFileToString(f)).getAsJsonObject();
			partNumberMap.clear();
			for (Entry<String,JsonElement> entry : jobj.entrySet()) {
				partNumberMap.put(entry.getKey(),new Part(entry.getValue().getAsJsonObject()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void trySave() {
		try {
			saveCache();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveCache() throws IOException {
		FileUtils.writeStringToFile(file, toJson());
	}
}