package parts;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import octopart.Part;
import octopart.PartSpecification;

public class PartTable extends JPanel {
	JTable table;
	List<Part> parts;
	
	public PartTable(List<Part> parts) {
		this.parts = parts;
		this.setLayout(new MigLayout("fill"));
		table = new JTable();
		this.add(new JScrollPane(table),"wrap,span,grow");
		populate();
	}
	
	public void setParts(List<Part> parts) {
		this.parts = parts;
		populate();
	}
	
	public JTable getJTable() {
		return table;
	}
	
	private void populate() {
		if (parts.size() == 0) return;
		List<List<Object>> data = new LinkedList<List<Object>>();
		for (Part part : parts) {
			data.add(Arrays.asList((Object)part));
		}
		PartTableAdapter adapter = new PartTableAdapter();
		for (PartSpecification spec : parts.get(0).getSpecifications()) {
			adapter.addSpecification(spec.getKey());
		}
		AdaptedObjectTableModel model = new AdaptedObjectTableModel(data, Arrays.asList(adapter));
		
		table.setModel(model);
	}
}
