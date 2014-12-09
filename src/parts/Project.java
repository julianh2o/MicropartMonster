package parts;

import java.awt.FileDialog;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.activation.DataHandler;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.table.TableColumn;

import octopart.Part;
import table.ColumnTable;
import table.DigikeyPartColumn;
import table.TextColumn;

public class Project extends InterfaceWindow {
	private File file;
	private ColumnTable table;
	
	public Project(File f) throws IOException {
		this();
		System.out.println("file: "+f);
		load(f);
	}
	
	public Project() throws IOException {
		super();
		file = null;
		table = new ColumnTable(false,new DigikeyPartColumn("Digikey Part"),new TextColumn("quantity"),new TextColumn("designator"), new TextColumn("notes"));
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
		    	try {
					Part p = (Part)info.getTransferable().getTransferData(Part.flavor);
					JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
					HashMap<String,Object> rowMap = new HashMap<String,Object>();
					rowMap.put("Digikey Part", p);
					table.getModel().addRow(rowMap,dl.getRow());
					updateTitle(true);
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
		
		final KeyStroke saveKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		final KeyStroke copyKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		final KeyStroke newKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				KeyStroke stroke = KeyStroke.getKeyStrokeForEvent(e);
				if (!Project.this.isActive()) return false;
				if (stroke.equals(newKeystroke)) {
					try {
						new Project();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					return true;
				} else if (stroke.equals(saveKeystroke)) {
					Project.this.save();
					return true;
				}
				
				return false;
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
	
	protected void updateTitle(boolean edited) {
		if (file == null) {
			this.setTitle("Unsaved Project");
			return;
		}
		this.setTitle(file.getName() + (edited ? "*" : ""));
	}
	
	protected void load(File f) {
		file = f;
		updateTitle(false);
		try {
			table.getModel().load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void save() {
		if (file == null) {
			FileDialog filepicker = new FileDialog(Project.this,"Save..",FileDialog.SAVE);
			filepicker.setVisible(true);
			File[] files = filepicker.getFiles();
			if (files.length == 0) return;
			file = files[0];
		}
		try {
			table.getModel().save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateTitle(false);
	}
	
	public Object getWindowState() {
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("file", this.file.getAbsolutePath());
		map.put("windowX", this.getLocation().x);
		map.put("windowY", this.getLocation().y);
		map.put("windowWidth", this.getSize().width);
		map.put("windowHeight", this.getSize().height);
		return map;
	}
	
	public void restoreWindowState(Object o) {
		HashMap<String,Object> map = (HashMap<String,Object>)o;
		load(new File((String)map.get("file")));
		this.setLocation((Integer)map.get("windowX"),(Integer)map.get("windowY"));
		this.setSize((Integer)map.get("windowWidth"),(Integer)map.get("windowHeight"));
	}
}
