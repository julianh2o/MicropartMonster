package octopart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JFrame;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class OctopartJsonService extends JFrame {
	public static JsonObject fetchJson(URL url) {
		InputStream in;
		String data;
		try {
			in = url.openStream();
			try {
				data = IOUtils.toString( in );
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				IOUtils.closeQuietly(in);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		//TODO cache based on url
		try {
			FileUtils.write(new File("./cachedresult.json"), data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new JsonParser().parse(data).getAsJsonObject();
	}
	
	public static JsonObject getStoredResults() {
		try {
			return new JsonParser().parse(FileUtils.readFileToString(new File("./cachedresult.json"))).getAsJsonObject();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
