package table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

public class ColumnTable extends JPanel {
	JTable table;
	ColumnTableModel model;

	public ColumnTable(boolean expanding, ColumnDef... columnDefinitions) throws IOException {
		if (expanding) {
			model = new ExpandingColumnTableModel(columnDefinitions);
		} else {
			model = new ColumnTableModel(columnDefinitions);
		}
		
		this.setLayout(new MigLayout("fill"));
		table = new JTable(model);
		table.setFillsViewportHeight(true);
		this.add(new JScrollPane(table),"wrap,span,grow");
		
		int index = 0;
		for (ColumnDef def : columnDefinitions) {
			def.table = table;
			TableColumn column = table.getColumnModel().getColumn(index++);
			def.setupColumn(column);
		}
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
		        int col = table.columnAtPoint(e.getPoint());
		        if (row == -1) return;
		        Object value = table.getValueAt(row, col);
		        ColumnDef def = model.columnDefinitions.get(col);
		        if (!def.cellClicked(table,e,value)) super.mouseClicked(e);
		        
//				if (SwingUtilities.isRightMouseButton(e)) {
//					StringSelection stringSelection = new StringSelection(value.toString());
//					Toolkit.getDefaultToolkit ().getSystemClipboard().setContents(stringSelection,null);
//				}
			}
		});
		
		final TableCellRenderer defaultRenderer = table.getDefaultRenderer(Object.class);
		table.setDefaultRenderer(Object.class, new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				ColumnDef columnDef = columnDefinitions[column];
				return columnDef.render(value, table, defaultRenderer, isSelected, hasFocus, row, column);
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