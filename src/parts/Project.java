package parts;

import java.awt.FileDialog;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.StringUtils;

import octopart.Octopart;
import octopart.Part;
import table.ColumnTable;
import table.DigikeyPartColumn;
import table.TextColumn;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class Project extends InterfaceWindow {
	private File file;
	private ColumnTable table;
	
	public Project() throws IOException {
		super();
		table = new ColumnTable(new DigikeyPartColumn("Digikey Part"),new TextColumn("quantity"),new TextColumn("designator"), new TextColumn("notes"));
		table.getJTable().setDropMode(DropMode.INSERT);
		table.getJTable().setTransferHandler(new TransferHandler() {
		    public boolean canImport(TransferHandler.TransferSupport info) {
		    	return info.isDataFlavorSupported(Part.flavor);
		    }
		    
		    protected Transferable createTransferable(JComponent c) {
		    	System.err.println("this probably shouldnt happen");
		        JTable table = (JTable) c;
		        int index = table.getSelectedColumn();
		        TableColumn column = table.getColumn(index);
		        return new DataHandler(column.getModelIndex(),Part.flavor.getMimeType());
		    }
		    
		    public int getSourceActions(JComponent c) {
		        return TransferHandler.COPY_OR_MOVE;
		    }
		    
		    public boolean importData(TransferHandler.TransferSupport info) {
		    	System.out.println("foo");
		    	try {
					Part p = (Part)info.getTransferable().getTransferData(Part.flavor);
					JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
					HashMap<String,Object> rowMap = new HashMap<String,Object>();
					rowMap.put("Digikey Part", p);
					table.getModel().addRow(rowMap);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
		    }
		    
		    protected void exportDone(JComponent c, Transferable data, int action) {
		    	System.out.println("export done"+data+"  "+action);
		    }
		});
		
		JScrollPane scroll = new JScrollPane(table);
		this.add(scroll);
		setTitle("Untitled Project");
		if (file != null) setTitle(file.getName());
		
		this.setVisible(true);
		this.pack();
		loadSize();
		addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	InterfaceWindow.saveWidowSize(Project.this);
		    }
		});
	}
	
//	private class ProjectTableModel extends AbstractTableModel {
//		List<String> columns;
//		List<Map<String,String>> data;
//		
//		private void ensureOneEmptyRow() {
//			boolean change = false;
//			if (!isEmpty(data.get(data.size()-1))) {
//				data.add(new HashMap<String,String>());
//				change = true;
//			}
//			while(isEmpty(data.get(data.size()-1)) && isEmpty(data.get(data.size()-2))) {
//				data.remove(data.size()-1);
//				change = true;
//			}
//			if (change) this.fireTableDataChanged();
//		}
//		
//		private boolean isEmpty(Map<String,String> row) {
//			for (Entry<String,String> e : row.entrySet()) {
//				if (StringUtils.isNotBlank(e.getValue())) {
//					return false;
//				}
//			}
//			return true;
//		}
//	
//		
//		public ProjectTableModel() throws IOException {
//			String[] fields = new String[] {"Digikey Part","quantity","designator","notes"};
//			columns = new LinkedList<String>(Arrays.asList(fields));
//			data = new ArrayList<Map<String,String>>();
//			data.add(new HashMap<String,String>());
//		}
//		
//		public ProjectTableModel(File csv) throws IOException {
//			CSVReader reader = new CSVReader(new FileReader(csv));
//			
//			columns = null;
//			data = new ArrayList<Map<String,String>>();
//			String[] lineData;
//		    while ((lineData = reader.readNext()) != null) {
//				List<String> fields = Arrays.asList(lineData);
//				
//				if (columns == null) {
//					columns = new LinkedList<String>(fields);
//					continue;
//				} else {
//					if (fields.size() != columns.size()) {
//						continue;
//					}
//					Map<String,String> map = new HashMap<String,String>();
//					for (int i=0; i<fields.size(); i++) {
//						map.put(columns.get(i),fields.get(i));
//					}
//					data.add(map);
//				}
//			}
//		    reader.close();
//		    
//		    if (!columns.contains("Digikey Part")) columns.add("Digikey Part");
//		}
//		
//		public void insertPart(Part p, int row) {
//		}
//		
//		public String getColumnName(int col) {
//			if (col < columns.size()) return columns.get(col);
//			return null;
//		}
//
//		public int getRowCount() {
//			return data.size();
//		}
//
//		@Override
//		public int getColumnCount() {
//			return columns.size();
//		}
//
//		@Override
//		public Object getValueAt(int rowIndex, int columnIndex) {
//			String columnName = getColumnName(columnIndex);
//			Object o = data.get(rowIndex).get(columnName);
//			return o;
//		}
//		
//		public boolean isCellEditable(int row, int col) {
//			if (getColumnName(col).equals("Digikey Part")) return false;
//			return true;
//		}
//		
//		public void setValueAt(Object value, int row, int col) {
//			String name = getColumnName(col);
//			data.get(row).put(name, (String)value);
//			fireTableCellUpdated(row, col);
//		}
//	}
	
	private void save(File f) throws IOException {
		System.out.println("implement save");
//		setTitle(f.getName());
//		CSVWriter writer;
//		writer = new CSVWriter(new FileWriter(Project.this.file));
//		TableModel model = table.getModel();
//		
//		writer.writeNext(model.columns.toArray(new String[this.model.columns.size()]));
//		for (Map<String, String> rowData : model.data) {
//			String[] columns = new String[model.columns.size()];
//			for (int i=0; i<model.columns.size(); i++) {
//				columns[i] = rowData.get(model.columns.get(i));
//			}
//			writer.writeNext(columns);
//		}
//		writer.close();
	}
}
