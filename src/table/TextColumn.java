package table;

import javax.swing.table.TableColumn;

public class TextColumn extends ColumnDef {
	private String label;
	private int min;
	private int max;
	private int preferred;
	
	public TextColumn(String label) {
		this(label,10,200,75);
	}
	public TextColumn(String label, int size) {
		this(label,size,size,size);
	}
	public TextColumn(String label, int min, int max, int preferred) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.preferred = preferred;
	}
	
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isEditable() {
		return true;
	}
	
	@Override
	public void setupColumn(TableColumn column) {
		column.setMinWidth(this.min);
		column.setMaxWidth(this.max);
		column.setPreferredWidth(this.preferred);
	}
}
