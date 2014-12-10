package parts;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import net.miginfocom.swing.MigLayout;
import octopart.Part;
import octopart.PartSpecification;

public class PartInfoDialog extends JDialog {
	Part part;
	public PartInfoDialog(Part part) {
		this.part = part;
		
		setLayout(new MigLayout("insets 10, wrap 2"));
		add(new JLabel("Sku"));
		add(new JLabel(part.getSkuPartNumber("Digi-Key")));
		add(new JLabel("Description"));
		add(new JLabel(part.getFirstDescription().getText()),"w 500!");
//		add(new JLabel("<html>"+part.getFirstDescription().getText()+"<html>"),"w 500!");
//		JTextArea textArea = new JTextArea(part.getFirstDescription().getText());
//		textArea.setWrapStyleWord(true);
//		textArea.setEditable(false);
//		textArea.setSize(400, 40);
//		add(textArea,"w 500!");
		for (PartSpecification spec : part.getSpecifications()) {
			add(new JLabel(spec.getName()));
			add(new JLabel(StringUtils.join(spec.getValues(),", ")));
		}
	}
	
	public void showDialog() {
		setVisible(true);
		this.pack();
	}
}
