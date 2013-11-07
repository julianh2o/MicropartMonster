package octopart;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Source extends OctopartObject {
	public Source(JsonObject json) {
		super(json);
	}
	
	public String getName() {
		return json.get("name").getAsString();
	}
	
	public String getUid() {
		return json.get("uid").getAsString();
	}
}