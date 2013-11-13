package octopart;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PartSpecification extends OctopartObject {
	private String key;

	public PartSpecification(Entry<String,JsonElement> entry) {
		super(entry.getValue().getAsJsonObject());
		this.key = entry.getKey();
	}
	
	public PartSpecification(String key, JsonObject json) {
		super(json);
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getName() {
		return json.getAsJsonObject("metadata").get("name").getAsString();
	}
	
	public String getType() {
		return json.getAsJsonObject("metadata").get("type").getAsString();
	}
	
	public String getUnit() {
		return json.getAsJsonObject("metadata").get("unit").getAsString();
	}
	
	public List<String> getValues() {
		JsonArray arr = json.getAsJsonArray("value");
		LinkedList<String> values = new LinkedList<String>();
		for (JsonElement el : arr) {
			values.add(el.getAsString());
		}
		return values;
	}
}
