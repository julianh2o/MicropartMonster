package table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public abstract class ColumnDef {
	public JTable table;
	
	public abstract String getLabel();
	public abstract boolean isEditable();
	public Component render(Object value, JTable table, TableCellRenderer defaultRenderer, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		return c;
	}
	public boolean cellClicked(JTable table, MouseEvent e, Object value) {
		return false;
	}
	public void setupColumn(TableColumn column) {
	}
}
