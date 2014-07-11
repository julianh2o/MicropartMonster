package table;

public class TextColumn extends ColumnDef {
	String label;
	public TextColumn(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isEditable() {
		return true;
	}
}
