package Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtil {
	public static JsonElement getElementAtPath(JsonElement el, String path) {
		String[] tokens = path.split("\\.");
		for (String tok : tokens) {
			el = ((JsonObject)el).get(tok);
		}
		return el;
	}

	public static List<JsonElement> getArrayAtPath(JsonElement el, String path) {
		List<JsonElement> list = new ArrayList<>();
		for(Iterator<JsonElement> it = getElementAtPath(el,path).getAsJsonArray().iterator(); it.hasNext();) {
			list.add(it.next());
		}
		return list;
	}

	public static String getStringAtPath(JsonElement el, String path) {
		return getElementAtPath(el,path).getAsString();
	}
}
