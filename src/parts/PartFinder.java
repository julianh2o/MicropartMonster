package parts;

import java.awt.Color;
import java.awt.Dialog;
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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
		System.out.println("Spec count: "+parts.get(0).getSpecifications().size());
		for (PartSpecification spec : parts.get(0).getSpecifications()) {
			adapter.addSpecification(spec.getKey());
			//if (adapter.size() < 5) adapter.addSpecification(spec.getKey());
		}
		AdaptedObjectTableModel model = new AdaptedObjectTableModel(data, Arrays.asList(adapter));
		
		//PartFinderTableModel model = new PartFinderTableModel(parts);
		results.setModel(model);
	}
	
	private class PartFinderTableModel extends AbstractTableModel {
		private List<Part> parts;
		
		//todo figure out a better way of doing this
		String[] columnHeaders = new String[] {"Part Number","Description"};
		String[] columnGetters = new String[] {"getPartNumber","getStringDescription"};
		
		public PartFinderTableModel(List<Part> parts) {
			this.parts = parts;
		}
		
		@Override
		public String getColumnName(int col) {
			return columnHeaders[col];
		}
		
		@Override
		public int getRowCount() {
			return parts.size();
		}

		@Override
		public int getColumnCount() {
			return columnHeaders.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Method m;
			try {
				m = Part.class.getMethod(columnGetters[columnIndex]);
				return m.invoke(parts.get(rowIndex));
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
