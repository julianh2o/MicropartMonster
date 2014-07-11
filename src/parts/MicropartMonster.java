package parts;

import java.awt.FileDialog;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
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

import org.apache.commons.lang3.StringUtils;

import octopart.Octopart;
import octopart.Part;
import octopart.PartCache;
import table.ColumnTable;
import table.DigikeyPartColumn;
import table.ExpandingColumnTableModel;
import table.TextColumn;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MicropartMonster extends InterfaceWindow {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		Octopart.setApiKey("566cc7d2");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		FileDialog filepicker = new FileDialog((java.awt.Frame) null);
		
		String[] openinventories = ((String)PropertyManager.getInstance().get("openinventories")).split(":");
		String file = openinventories[0];
		if (file == null) return;
		
		PartCache.getInstance().loadCache(new File("./mpncache.json"));
		new MicropartMonster(new File(file));
		//PartLibrary pd = new PartLibrary(new MicropartMonster(new File(file)));
		//pd.showDialog();
	}
	
	private File file;
	private ColumnTable table;
	
	public MicropartMonster(File f) throws IOException {
		super();
		file = f;
		table = new ColumnTable(new DigikeyPartColumn("Digikey Part"),new TextColumn("location"),new TextColumn("stock"));
		if (f.exists()) {
			table.getModel().load(f);
		}
		
		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				MicropartMonster.this.setTitle(file.getName()+"*");
			}
		});
		setTitle(f.getName());
		final JTable jtable = table.getJTable();
		if (f.exists()) {
	        int digikeyColumn = table.getModel().findColumn("Digikey Part");
	        for (int i=0; i<table.getModel().getRowCount(); i++) {
	        	String val = (String)table.getModel().getValueAt(i, digikeyColumn);
	        	if (!StringUtils.isBlank(val)) jtable.setValueAt(PartCache.getInstance().getPart(val),i,digikeyColumn);
	        }
		}
		jtable.setDragEnabled(true);
		jtable.setTransferHandler(new TransferHandler() {
		    public boolean canImport(TransferHandler.TransferSupport info) {
		    	return false;
		    }
		    
		    protected Transferable createTransferable(JComponent c) {
		        JTable table = (JTable) c;
		        int index = table.getSelectedRow();
		        int modelIndex = table.getColumn("Digikey Part").getModelIndex();
		        Object o = jtable.getValueAt(index,modelIndex);
		        return new DataHandler((Part)o,Part.flavor.getMimeType());
		    }
		    
		    public int getSourceActions(JComponent c) {
		        return TransferHandler.COPY;
		    }
		    
		    protected void exportDone(JComponent c, Transferable data, int action) {
		    	System.out.println("MM: export done"+data+"  "+action);
		    }
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int row = jtable.rowAtPoint(e.getPoint());
		        int col = jtable.columnAtPoint(e.getPoint());
		        Object value = jtable.getValueAt(row, col);
		        
				String columnName = jtable.getColumnName(col);
				if (columnName.equals("Digikey Part") && e.getClickCount() > 1) {
					PartFinder finder = new PartFinder(MicropartMonster.this);
					Part part = finder.showDialog();
					if (part != null) jtable.setValueAt(part.getPartNumber(),row,col);
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
					Object value = jtable.getValueAt(jtable.getSelectedRow(),jtable.getSelectedColumn());
					StringSelection stringSelection = new StringSelection(value.toString());
					Toolkit.getDefaultToolkit ().getSystemClipboard().setContents(stringSelection,null);
				}
			}
		});
		JScrollPane scroll = new JScrollPane(table);
		this.add(scroll);
		
		final KeyStroke saveKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		final KeyStroke newKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				KeyStroke s = KeyStroke.getKeyStrokeForEvent(e);
				if (s.equals(saveKeystroke)) {
					try {
						MicropartMonster.this.table.getModel().save(MicropartMonster.this.file);
						MicropartMonster.this.setTitle(file.getName());
						return true;
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MicropartMonster.this, "Failed to save CSV!");
						e1.printStackTrace();
					}
				}
				if (s.equals(newKeystroke)) {
					try {
						new Project();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return true;
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
}
