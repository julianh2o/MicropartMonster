package octopart;

import com.google.gson.JsonObject;

public class OctopartObject {
	JsonObject json;
	
	public OctopartObject(JsonObject json) {
		this.json = json;
	}
	
	public String getOctopartClass() {
		return json.get("__class__").getAsString();
	}
}