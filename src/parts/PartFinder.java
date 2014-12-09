package parts;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import octopart.Octopart;
import octopart.Part;
import octopart.PartSpecification;

public class PartFinder extends JDialog {
	private JTextField search;
	private JTable results;
	private Part selectedPart;
	
	public PartFinder(Window win) {
		super(win, "Part Finder",Dialog.ModalityType.DOCUMENT_MODAL);
		JPanel panel = new JPanel(new MigLayout("fill"));
		panel.add(new JLabel("Search: "));
		search = new JTextField();
		panel.add(search,"growx,wrap 10");
		
		panel.add(new JLabel("Results"),"wrap,span");
		results = new JTable();
		panel.add(new JScrollPane(results),"wrap,span,grow");
		
		search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performSearch();
			}
		});
		
		results.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = results.rowAtPoint(e.getPoint());
		        int col = results.columnAtPoint(e.getPoint());
		        
		        if (e.getClickCount() >= 2) {
		        	PartFinder.this.selectedPart = ((Part)((AdaptedObjectTableModel)results.getModel()).getModel().get(row).get(0));
		        	setVisible(false);
		        }
			}
		});
		
		selectedPart = null;
		
		
		this.add(panel);
		pack();
		InterfaceWindow.loadWindowSize(this);
		addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	InterfaceWindow.saveWidowSize(PartFinder.this);
		    }
		});
	}
	
	public Part showDialog() {
		setVisible(true);
		return selectedPart;
	}
	
	private void performSearch() {
		String query = search.getText();
		List<Part> parts = Octopart.getInstance().findParts(query);
		
		List<List<Object>> data = new LinkedList<List<Object>>();
		for (Part part : parts) {
			data.add(Arrays.asList((Object)part));
		}
		PartTableAdapter adapter = new PartTableAdapter();
		for (PartSpecification spec : parts.get(0).getSpecifications()) {
			adapter.addSpecification(spec.getKey());
		}
		AdaptedObjectTableModel model = new AdaptedObjectTableModel(data, Arrays.asList(adapter));
		
		results.setModel(model);
	}
}
