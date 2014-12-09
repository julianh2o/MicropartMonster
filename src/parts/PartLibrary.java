package parts;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import octopart.Part;

public class PartLibrary extends JDialog {
	PartTable partTable;
	JButton addButton;
	JButton saveButton;
	List<Part> parts;
	File file;
	
	public PartLibrary(Window win) {
		this(win,null);
	}
	
	public PartLibrary(Window win, File file) {
		super(win, file == null ? "Untitled Library" : file.getName(),Dialog.ModalityType.DOCUMENT_MODAL);
		this.file = file;
		
		parts = new LinkedList<Part>();
		
		JPanel panel = new JPanel(new MigLayout("fill"));
		addButton = new JButton("Add part");
		panel.add(addButton,"growx,shrinky");
		
		saveButton = new JButton("Save Library");
		panel.add(saveButton,"growx,shrinky,wrap");
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PartFinder finder = new PartFinder(PartLibrary.this);
				Part part = finder.showDialog();
				parts.add(part);
				partTable.setParts(parts);
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					PartLibrary.this.save();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		partTable = new PartTable(parts);
		panel.add(partTable,"growx,span");
		
		this.add(panel);
		pack();
		InterfaceWindow.loadWindowSize(this);
		addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	InterfaceWindow.saveWidowSize(PartLibrary.this);
		    }
		});
	}
	
	public void showDialog() {
		setVisible(true);
	}
	
	public void save() throws IOException {
		if (file == null) {
			FileDialog filepicker = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
			filepicker.setVisible(true);
			File[] files = filepicker.getFiles();
			if (files.length == 0) throw new RuntimeException("files null!");
			file = files[0];
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
		for (Part p : parts) {
			bw.append(p.getJson());
			bw.append("\n");
		}
		bw.close();
	}
}
