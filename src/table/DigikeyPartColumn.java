package table;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import octopart.Part;

import parts.MicropartMonster;
import parts.PartFinder;

public class DigikeyPartColumn extends ColumnDef {
	String label;
	public DigikeyPartColumn(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean cellClicked(JTable table, MouseEvent e, Object value) {
		if (e.getClickCount() > 1) {
			PartFinder finder = new PartFinder(SwingUtilities.getWindowAncestor(table));
			Part part = finder.showDialog();
			if (part != null) table.setValueAt(part,table.getSelectedRow(),table.getSelectedColumn());
			return true;
		}
		return false;
	}
}
