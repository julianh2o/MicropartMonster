package table;

import java.awt.event.MouseEvent;

import javax.swing.JTable;

public abstract class ColumnDef {
	public abstract String getLabel();
	public abstract boolean isEditable();
	public boolean cellClicked(JTable table, MouseEvent e, Object value) {
		return false;
	}
}
