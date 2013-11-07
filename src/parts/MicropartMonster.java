package parts;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

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

public class MicropartMonster extends JFrame {
	public static void main(String[] args) throws IOException {
		Octopart.setApiKey("566cc7d2");
		new MicropartMonster(new File("./led100a_v2.csv"));
	}
	
	public MicropartMonster(File f) throws IOException {
		PartTableModel model = new PartTableModel(f);
		final JTable table = new JTable(model);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int row = table.rowAtPoint(e.getPoint());
		        int col = table.columnAtPoint(e.getPoint());
		        Object value = table.getValueAt(row, col);
		        
				String columnName = table.getColumnName(col);
				if (columnName.equals("Digikey Part")) {
					PartFinder finder = new PartFinder(MicropartMonster.this);
					Part part = finder.showDialog();
					if (part != null) table.setValueAt(part.getPartNumber(),row,col);
				}
				
				if (SwingUtilities.isRightMouseButton(e)) {
					StringSelection stringSelection = new StringSelection(value.toString());
					Toolkit.getDefaultToolkit ().getSystemClipboard().setContents(stringSelection,null);
				}
			}
		});
		
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				KeyStroke stroke = KeyStroke.getKeyStrokeForEvent(e);
				KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
				if (stroke.equals(copy)) {
					Object value = table.getValueAt(table.getSelectedRow(),table.getSelectedColumn());
					StringSelection stringSelection = new StringSelection(value.toString());
					Toolkit.getDefaultToolkit ().getSystemClipboard().setContents(stringSelection,null);
				}
			}
		});
		JScrollPane scroll = new JScrollPane(table);
		this.add(scroll);
		this.setVisible(true);
		this.setSize(500,500);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	private class PartTableModel extends AbstractTableModel {
		List<String> columns;
		List<Map<String,String>> data;
		
		public PartTableModel(File csv) throws IOException {
			CSVReader reader = new CSVReader(new FileReader(csv));
			
			columns = null;
			data = new ArrayList<Map<String,String>>();
			String[] lineData;
		    while ((lineData = reader.readNext()) != null) {
				List<String> fields = Arrays.asList(lineData);
				
				if (columns == null) {
					columns = new LinkedList<String>(fields);
					continue;
				} else {
					if (fields.size() != columns.size()) {
						System.err.println("Skipping line: "+lineData);
						continue;
					}
					Map<String,String> map = new HashMap<String,String>();
					for (int i=0; i<fields.size(); i++) {
						map.put(columns.get(i),fields.get(i));
					}
					data.add(map);
				}
			}
		    reader.close();
		    
		    //if (!columns.contains("Part Number")) columns.add("Part Number");
		    if (!columns.contains("Digikey Part")) columns.add("Digikey Part");
		}
		
		public String getColumnName(int col) {
			if (col < columns.size()) return columns.get(col);
			return null;
		}

		public int getRowCount() {
			return data.size();
		}

		@Override
		public int getColumnCount() {
			return columns.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			String columnName = getColumnName(columnIndex);
			Object o = data.get(rowIndex).get(columnName);
			return o;
		}
		
		public boolean isCellEditable(int row, int col) {
			if (getColumnName(col).equals("Digikey Part")) return true;
			return false;
		}
		
		public void setValueAt(Object value, int row, int col) {
			String name = getColumnName(col);
			data.get(row).put(name, (String)value);
		}
	}
}
