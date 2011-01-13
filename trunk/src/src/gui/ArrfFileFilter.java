package gui;

import java.io.File;

//Limit extension "arff"
public class ArrfFileFilter extends javax.swing.filechooser.FileFilter {
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().toLowerCase().endsWith(".arff");
	}

	public String getDescription() {
		return ".arff files";
	}
}