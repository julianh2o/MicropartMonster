package table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import octopart.Part;
import parts.PartFinder;
import parts.PartInfoDialog;

public class DigikeyPartColumn extends ColumnDef {
	String label;
	
	JLabel partNumber;
	JLabel description;
	JPanel renderComponent;
	
	JPopupMenu popupMenu;
	public DigikeyPartColumn(String label) {
		this.label = label;
		
		renderComponent = new JPanel(new MigLayout("insets 0"));
		partNumber = new JLabel();
		renderComponent.add(partNumber,"w 200!");
		
		description = new JLabel();
		description.setFont(new Font("Serif", Font.PLAIN, 12));
		description.setForeground(new Color(70,70,70));
		renderComponent.add(description,"gapleft 10");
	}
	
	public void attachRightClickMenu(JPopupMenu menu) {
		this.popupMenu = menu;
	}
	
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isEditable() {
		return false;
	}
	
	public void editSelectedValue() {
		PartFinder finder = new PartFinder(SwingUtilities.getWindowAncestor(table));
		Part part = finder.showDialog();
		if (part != null) table.setValueAt(part,table.getSelectedRow(),table.getSelectedColumn());
	}

	@Override
	public boolean cellClicked(JTable table, MouseEvent e, Object value) {
		if (SwingUtilities.isRightMouseButton(e)) {
			JTable source = (JTable)e.getSource();
            int row = source.rowAtPoint( e.getPoint() );
            int column = source.columnAtPoint( e.getPoint() );

            if (! source.isRowSelected(row)) source.changeSelection(row, column, false, false);
            if (popupMenu != null) popupMenu.show(e.getComponent(), e.getX(), e.getY());
		} else if (e.getClickCount() > 1) {
			if (value instanceof Part) {
				PartInfoDialog dialog = new PartInfoDialog((Part)value);
				dialog.showDialog();
			} else {
				editSelectedValue();
			}
		}
		return false;
	}
	
	
	@Override
	public Component render(Object value, JTable table, TableCellRenderer defaultRenderer, boolean isSelected, boolean hasFocus, int row, int column) {
		Component defaultRender = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (!(value instanceof Part)) return defaultRender;
		
		Part part = ((Part)value);
		partNumber.setText(part.getSkuPartNumber("Digi-Key"));
		description.setText(part.getFirstDescription().getText());
	
		renderComponent.setBackground(defaultRender.getBackground());
		partNumber.setForeground(defaultRender.getForeground());
		description.setForeground(defaultRender.getForeground());
		return renderComponent;
	}
}
