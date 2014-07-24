package table;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang3.StringUtils;

public class ExpandingColumnTableModel extends ColumnTableModel {
	public ExpandingColumnTableModel(ColumnDef... columnDefinitions) throws IOException {
		super(columnDefinitions);
		this.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				ensureOneEmptyRow();
			}
		});
		ensureOneEmptyRow();
	}

	private void ensureOneEmptyRow() {
		boolean change = false;
		if (data.size() == 0 || !isEmpty(data.get(data.size()-1))) {
			data.add(new HashMap<String,Object>());
			change = true;
		}
		if (data.size() > 1) {
			while(isEmpty(data.get(data.size()-1)) && isEmpty(data.get(data.size()-2))) {
				data.remove(data.size()-1);
				change = true;
			}
		}
		if (change) this.fireTableDataChanged();
	}
	
	private boolean isEmpty(Map<String,Object> row) {
		for (Entry<String,Object> e : row.entrySet()) {
			if (e.getValue() == null) continue;
			if (e.getValue() instanceof String && !StringUtils.isNotBlank((String)e.getValue())) continue;
			return false;
		}
		return true;
	}
}
