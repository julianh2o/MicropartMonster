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
