package parts;

import java.util.List;

public interface TableAdapter<T> {
	public List<Object> getValues(T obj);
	
	public List<String> getColumnHeaders();
}
