package octopart;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GsonPartAdapter extends TypeAdapter<Part> {
	@Override
	public Part read(JsonReader arg0) throws IOException {
		System.out.println("next: "+arg0.nextString());
		return new Part(null);
	}

	@Override
	public void write(JsonWriter arg0, Part arg1) throws IOException {
		System.out.println("write called");
	}
}