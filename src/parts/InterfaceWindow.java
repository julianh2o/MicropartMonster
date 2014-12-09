package parts;

import java.awt.Window;

import javax.swing.JFrame;

public class InterfaceWindow extends JFrame {
	public static String getWindowPropertyKey(Window w, String name) {
		return w.getClass().getSimpleName()+".window."+name;
	}
	
	public static void loadWindowSize(Window w) {
		try {
//			int width = PropertyManager.getInstance().getInt(getWindowPropertyKey(w,"width"));
//			int height = PropertyManager.getInstance().getInt(getWindowPropertyKey(w,"height"));
//			w.setSize(width,height);
		} catch (Exception e) {
			//dont care
		}
	}
	
	public static void saveWidowSize(Window w) {
//		Dimension dim = w.getSize();
//		PropertyManager.getInstance().set(getWindowPropertyKey(w,"width"),dim.width);
//		PropertyManager.getInstance().set(getWindowPropertyKey(w,"height"),dim.height);
	}
	
	public void loadSize() {
		loadWindowSize(this);
	}
}
