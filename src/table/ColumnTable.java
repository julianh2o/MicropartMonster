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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.StringUtils;

import octopart.Octopart;
import octopart.Part;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ColumnTable extends JPanel {
	JTable table;
	ColumnTableModel model;

	public ColumnTable(ColumnDef... columnDefinitions) throws IOException {
		model = new ExpandingColumnTableModel(columnDefinitions);
		
		this.setLayout(new MigLayout("fill"));
		table = new JTable(model);
		this.add(new JScrollPane(table),"wrap,span,grow");
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
		        int col = table.columnAtPoint(e.getPoint());
		        Object value = table.getValueAt(row, col);
		        ColumnDef def = model.columnDefinitions.get(col);
		        if (!def.cellClicked(table,e,value)) super.mouseClicked(e);
		        
//				if (SwingUtilities.isRightMouseButton(e)) {
//					StringSelection stringSelection = new StringSelection(value.toString());
//					Toolkit.getDefaultToolkit ().getSystemClipboard().setContents(stringSelection,null);
//				}
			}
		});
		
//		table.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent e) {
//				KeyStroke stroke = KeyStroke.getKeyStrokeForEvent(e);
//				KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
//				if (stroke.equals(copy)) {
//					Object value = table.getValueAt(table.getSelectedRow(),table.getSelectedColumn());
//					StringSelection stringSelection = new StringSelection(value.toString());
//					Toolkit.getDefaultToolkit ().getSystemClipboard().setContents(stringSelection,null);
//				}
//			}
//		});
	}
	
	public ColumnTableModel getModel() {
		return model;
	}
	
	public JTable getJTable() {
		return table;
	}
}