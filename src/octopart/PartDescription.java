package octopart;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PartDescription extends OctopartObject {
	public PartDescription(JsonObject json) {
		super(json);
	}
	
	public Source getFirstSource() {
		try {
			return new Source(json.getAsJsonObject("attribution").getAsJsonArray("sources").get(0).getAsJsonObject());
		} catch (Exception e) {
			return null; //catch any nulls and just give up
		}
	}
}