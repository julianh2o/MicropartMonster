package octopart;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Part extends OctopartObject {
	public Part(JsonObject json) {
		super(json);
	}
	
	public String getPartNumber() {
		return json.get("item").getAsJsonObject().get("mpn").getAsString();
	}
	
	public String getStringDescription() {
		return getDescriptions().get(0).getText();
	}
	
	public PartDescription getFirstDescription() {
		return getDescriptions().get(0);
	}
	
	public List<PartDescription> getDescriptions() {
		List<PartDescription> descriptions = new LinkedList<PartDescription>();
		
		JsonObject item = json.get("item").getAsJsonObject();
		if (!item.has("descriptions")) return null;
		for (JsonElement el : item.getAsJsonArray("descriptions")) {
			JsonObject obj = el.getAsJsonObject();
			descriptions.add(new PartDescription(obj));
		}
		return descriptions;
	}
	
	public List<PartSpecification> getSpecifications() {
		List<PartSpecification> specs = new LinkedList<PartSpecification>();
		
		JsonObject item = json.get("item").getAsJsonObject();
		if (!item.has("specs")) return null;
		for (Entry<String,JsonElement> entry : item.getAsJsonObject("specs").entrySet()) {
			specs.add(new PartSpecification(entry));
		}
		
		return specs;
	}

	public PartSpecification getSpecification(String key) {
		JsonObject item = json.get("item").getAsJsonObject();
		if (!item.has("specs")) return null;
		if (item.getAsJsonObject("specs").getAsJsonObject(key) == null) return null;
		return new PartSpecification(key,item.getAsJsonObject("specs").getAsJsonObject(key));
	}
}