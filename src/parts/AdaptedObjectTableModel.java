package parts;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class AdaptedObjectTableModel extends AbstractTableModel {
	private List<List<Object>> model;
	private HashMap<Class,TableAdapter> adapters;
	private ArrayList<String> columnHeadings;
	private List<List<Object>> cellValues;
	
	public AdaptedObjectTableModel(List<List<Object>> model, List<? extends TableAdapter> adapters) {
		this.model = model;
		this.adapters = createAdapterMap(adapters);
		this.columnHeadings = calculateColumnHeadings(model);
		this.cellValues = calculateCellValues(model);
	}
	
	private HashMap<Class,TableAdapter> createAdapterMap(List<? extends TableAdapter> list) {
		HashMap<Class,TableAdapter> adapterMap = new HashMap<Class,TableAdapter>();
		for (TableAdapter a : list) {
			Type type = a.getClass().getGenericInterfaces()[0];		
			Class myClass = ((Class)((ParameterizedType)type).getActualTypeArguments()[0]);
			adapterMap.put(myClass, a);
		}
		
		return adapterMap;
	}
	
	private ArrayList<String> calculateColumnHeadings(List<List<Object>> model) {
		ArrayList<String> columnHeadings = new ArrayList<String>();
		for (Object o : model.get(0)) {
			if (this.adapters.containsKey(o.getClass())) {
				TableAdapter typeAdapter = this.adapters.get(o.getClass());
				columnHeadings.addAll(typeAdapter.getColumnHeaders());
			} else {
				//TODO figure out how to do this
				columnHeadings.add("");
			}
		}
		return columnHeadings;
	}
	
	private List<List<Object>> calculateCellValues(List<List<Object>> model) {
		List<List<Object>> calculatedValues = new ArrayList<List<Object>>();
		for (List<Object> row : model) {
			List<Object> calculatedRow = new ArrayList<Object>();
			for (Object o : row) {
				if (this.adapters.containsKey(o.getClass())) {
					TableAdapter typeAdapter = this.adapters.get(o.getClass());
					calculatedRow.addAll(typeAdapter.getValues(o));
				} else {
					calculatedRow.add(o);
				}
			}
			calculatedValues.add(calculatedRow);
		}
		return calculatedValues;
	}
	
	@Override
	public String getColumnName(int col) {
		return columnHeadings.get(col);
	}
	
	@Override
	public int getRowCount() {
		return model.size();
	}

	@Override
	public int getColumnCount() {
		return columnHeadings.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return cellValues.get(rowIndex).get(columnIndex);
	}

	public List<List<Object>> getModel() {
		return model;
	}
}
