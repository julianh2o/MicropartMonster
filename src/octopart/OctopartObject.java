package octopart;

import com.google.gson.JsonObject;

public abstract class OctopartObject {
	JsonObject json;
	
	public OctopartObject(JsonObject json) {
		this.json = json;
		String jsonClass = json.get("__class__").getAsString();
		if (!getOctopartClass().equals(jsonClass)) {
			throw new RuntimeException("Invalid Class!");
		}
	}
	
	public String getOctopartClass() {
		return json.get("__class__").getAsString();
	}
}