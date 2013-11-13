package parts;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import net.miginfocom.swing.MigLayout;

import octopart.Octopart;
import octopart.Part;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharSet;
import org.apache.http.client.utils.URIBuilder;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import au.com.bytecode.opencsv.CSVReader;

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
