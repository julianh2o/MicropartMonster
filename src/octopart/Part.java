package octopart;

import java.awt.datatransfer.DataFlavor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import Util.JsonUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Part extends OctopartObject { // implements Transferable {
	public static final DataFlavor flavor = new DataFlavor(Part.class,"MicropartMonster Part Object");
	public Part(JsonObject json) {
		super(json);
	}
	
	public String toString() {
		return getSkuPartNumber("Digi-Key");
	}
	
	public String getManufacturerPartNumber() {
		return json.get("item").getAsJsonObject().get("mpn").getAsString();
	}
	
	public String getSkuPartNumber(String seller) {
		for (JsonElement el : JsonUtil.getArrayAtPath(json,"item.offers")) {
			if (JsonUtil.getStringAtPath(el, "seller.name").equals(seller)) {
				return JsonUtil.getStringAtPath(el,"sku");
			}
		}
		System.err.println("error: seller sku not found");
		return null;
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

//	@Override
//	public DataFlavor[] getTransferDataFlavors() {
//		return new DataFlavor[] { Part.flavor };
//	}
//
//	@Override
//	public boolean isDataFlavorSupported(DataFlavor flavor) {
//		if (flavor.equals(Part.flavor) || flavor.equals(DataFlavor.stringFlavor)) return true;
//		return false;
//	}
//
//	@Override
//	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
//		if (flavor.equals(Part.flavor)) {
//			return this;
//		} else if (flavor.equals(DataFlavor.stringFlavor)) {
//			return this.toString();
//		} else {
//			throw new UnsupportedFlavorException(flavor);
//		}
//	}
}