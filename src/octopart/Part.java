package octopart;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Part extends OctopartObject {
	public Part(JsonObject json) {
		super(json);
	}
	
	public String getPartNumber() {
		return json.get("mpn").getAsString();
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
}