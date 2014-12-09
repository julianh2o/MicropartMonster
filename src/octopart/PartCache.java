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
	
	public Part getPart(String mpn) {
		if (!partNumberMap.containsKey(mpn)) {
			partNumberMap.put(mpn,fetchPart(mpn));
			trySave();
		}
		return partNumberMap.get(mpn);
	}
	
	public boolean isPartCached(String mpn) {
		return partNumberMap.containsKey(mpn);
	}
	
	private Part fetchPart(String mpn) {
		List<Part> parts = Octopart.getInstance().findParts(mpn);
		System.out.println("fetching parts: "+mpn);
		for (Part p : parts) {
			System.out.println("found part: "+p.getManufacturerPartNumber());
			if (p.getManufacturerPartNumber().equals(mpn)) return p;
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