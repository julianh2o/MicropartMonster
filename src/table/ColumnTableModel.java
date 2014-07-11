package table;

import java.awt.FileDialog;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.StringUtils;

import octopart.Octopart;
import octopart.Part;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ColumnTableModel extends AbstractTableModel {
	List<ColumnDef> columnDefinitions;
	List<Map<String,Object>> data;

	public ColumnTableModel(ColumnDef... columnDefinitions) throws IOException {
		this.columnDefinitions = Arrays.asList(columnDefinitions);
		data = new ArrayList<Map<String,Object>>();
		data.add(new HashMap<String,Object>());
	}
	
	//TODO separate load/save out into a separate service class
	public void load(File csv) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(csv));
		
		List<String> columns = null;
		data = new ArrayList<Map<String,Object>>();
		String[] lineData;
	    while ((lineData = reader.readNext()) != null) {
			List<String> fields = Arrays.asList(lineData);
			
			if (columns == null) {
				columns = new LinkedList<String>(fields);
				continue;
			} else {
				if (fields.size() != columns.size()) {
					continue;
				}
				Map<String,Object> map = new HashMap<String,Object>();
				for (int i=0; i<fields.size(); i++) {
					map.put(columns.get(i),fields.get(i));
				}
				data.add(map);
			}
		}
	    reader.close();
	}
	
	public void save(File f) throws IOException {
		CSVWriter writer;
		writer = new CSVWriter(new FileWriter(f));
		
		String[] columnHeadings = new String[this.columnDefinitions.size()];
		for (int i=0; i<this.columnDefinitions.size(); i++) {
			columnHeadings[i] = this.columnDefinitions.get(i).getLabel();
		}
		writer.writeNext(columnHeadings);
		
		for (Map<String, Object> rowData : this.data) {
			String[] columns = new String[this.columnDefinitions.size()];
			for (int i=0; i<this.columnDefinitions.size(); i++) {
				Object o = rowData.get(this.columnDefinitions.get(i).getLabel());
				String s = null;
				if (o instanceof String) s = (String)o;
				if (o != null) s = o.toString();
				columns[i] = s;
			}
			writer.writeNext(columns);
		}
		writer.close();
	}
	
	public String getColumnName(int col) {
		if (col < columnDefinitions.size()) return columnDefinitions.get(col).getLabel();
		return null;
	}

	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return columnDefinitions.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String columnName = getColumnName(columnIndex);
		Object o = data.get(rowIndex).get(columnName);
		return o;
	}
	
	public boolean isCellEditable(int row, int col) {
		if (col < columnDefinitions.size()) return columnDefinitions.get(col).isEditable();
		return false;
	}
	
	public void setValueAt(Object value, int row, int col) {
		String name = getColumnName(col);
		data.get(row).put(name, value);
		fireTableCellUpdated(row, col);
	}
}