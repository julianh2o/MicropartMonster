package parts;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import net.miginfocom.swing.MigLayout;

import octopart.Octopart;
import octopart.Part;
import octopart.PartSpecification;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharSet;
import org.apache.http.client.utils.URIBuilder;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import au.com.bytecode.opencsv.CSVReader;

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
