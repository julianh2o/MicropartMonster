package parts;

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

import octopart.Octopart;
import octopart.Part;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MicropartMonster extends InterfaceWindow {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		Octopart.setApiKey("566cc7d2");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		FileDialog filepicker = new FileDialog((java.awt.Frame) null);
		String file = "/home/julian/Desktop/led100a_v2.csv";
		if (file == null) return;
		new MicropartMonster(new File(file));
	}
	
	private File file;
	private JTable table;
	private PartTableModel model; 
	
	public MicropartMonster(File f) throws IOException {
		super();
		file = f;
		model = new PartTableModel(f);
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				System.out.println("table edited");
				MicropartMonster.this.setTitle(file.getName()+"*");
			}
		});
		table = new JTable(model);
		setTitle(f.getName());
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int row = table.rowAtPoint(e.getPoint());
		        int col = table.columnAtPoint(e.getPoint());
		        Object value = table.getValueAt(row, col);
		        
				String columnName = table.getColumnName(col);
				if (columnName.equals("Digikey Part") && e.getClickCount() > 1) {
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
		
		KeyStroke saveKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				KeyStroke s = KeyStroke.getKeyStrokeForEvent(e);
				if (s.equals(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))) {
					try {
						MicropartMonster.this.save(MicropartMonster.this.file);
						return true;
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MicropartMonster.this, "Failed to save CSV!");
						e1.printStackTrace();
					}
				}
				return false;
			}
		});
		
		this.setVisible(true);
		this.pack();
		loadSize();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	InterfaceWindow.saveWidowSize(MicropartMonster.this);
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
			if (getColumnName(col).equals("Digikey Part")) return false;
			return true;
		}
		
		public void setValueAt(Object value, int row, int col) {
			String name = getColumnName(col);
			data.get(row).put(name, (String)value);
			fireTableCellUpdated(row, col);
		}
	}
	
	private void save(File f) throws IOException {
		setTitle(f.getName());
		CSVWriter writer;
		writer = new CSVWriter(new FileWriter(MicropartMonster.this.file));
		
		writer.writeNext(this.model.columns.toArray(new String[this.model.columns.size()]));
		for (Map<String, String> rowData : this.model.data) {
			String[] columns = new String[this.model.columns.size()];
			for (int i=0; i<this.model.columns.size(); i++) {
				columns[i] = rowData.get(this.model.columns.get(i));
			}
			writer.writeNext(columns);
		}
		writer.close();
	}
}
