package parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import octopart.Part;
import octopart.PartSpecification;

public class PartTableAdapter implements TableAdapter<Part> {
	List<String> specificationKeys;
	public PartTableAdapter() {
		specificationKeys = new LinkedList<String>();
	}
	
	public PartTableAdapter(String ...keys) {
		this();
		specificationKeys.addAll(Arrays.asList(keys));
	}
	
	public void addSpecification(String key) {
		specificationKeys.add(key);
	}
	
	public int size() {
		return specificationKeys.size();
	}
	
	@Override
	public List<Object> getValues(Part obj) {
		List<Object> values = new ArrayList<Object>(Arrays.asList( 
			obj.getManufacturerPartNumber(),
			obj.getStringDescription()
		));
		for(String key : specificationKeys) {
			PartSpecification spec = obj.getSpecification(key);
			if (spec == null) {
				values.add("");
				continue;
			}
			values.add(StringUtils.join(spec.getValues(),"\n"));
		}
		return values;
	}

	@Override
	public List<String> getColumnHeaders() {
		List<String> headers = new ArrayList(Arrays.asList("Part Number","Description"));
		headers.addAll(specificationKeys);
		return headers;
	}
}
