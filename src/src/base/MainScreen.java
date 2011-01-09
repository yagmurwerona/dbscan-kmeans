package base;

import gui.AttributeSummaryPanel;
import gui.ClusterPanel;
import gui.ViewerDialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

@SuppressWarnings("serial")
public class MainScreen extends JPanel {

	public JMenuBar menuBar = new JMenuBar();
	JFileChooser fc;
	AttributeSummaryPanel m_AttSummaryPanel = new AttributeSummaryPanel();
	ClusterPanel clusterPanel = new ClusterPanel();
	static JFrame jf = new JFrame("Preprocess");
	String[] instances;
	String strAttribute="";
	int numInstances = 0;
	int numAttributes=0;

	public MainScreen() {
		// Set up the GUI layout
	    JPanel menuPanel = new JPanel();
	    menuPanel.setLayout(new BorderLayout());
	    menuPanel.add(menuBar);
		//Build Menu
		JMenu fileMenu = new JMenu("File");
		JMenu viewMenu = new JMenu("View");
		JMenu runMenu = new JMenu("Run");
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem dbscan = new JRadioButtonMenuItem("DBScan");
		dbscan.setSelected(true);
		JRadioButtonMenuItem kmeans = new JRadioButtonMenuItem("KMeans");
		group.add(dbscan);
		group.add(kmeans);
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(runMenu);
		JMenuItem openFile = new JMenuItem("Open File");
		JMenuItem exitFile = new JMenuItem("Exit");
		fileMenu.add(openFile);
		fileMenu.add(exitFile);
		JMenuItem showData= new JMenuItem("Show data");
		viewMenu.add(showData);
		setLayout(new BorderLayout());
	    add(menuPanel, BorderLayout.NORTH);
	    runMenu.add(dbscan);
	    runMenu.add(kmeans);
	    m_AttSummaryPanel.setBorder(BorderFactory.createTitledBorder("Selected Attribute"));
		JPanel attVis = new JPanel();
	    attVis.setLayout( new BorderLayout());
	    attVis.add(m_AttSummaryPanel);
	    
	    JPanel centre = new JPanel();
	    centre.setLayout(new BorderLayout());
	    
	    centre.add(clusterPanel, BorderLayout.CENTER);
	    
	    JPanel rhs = new JPanel();
	    rhs.setLayout(new BorderLayout());
	    rhs.add(attVis, BorderLayout.NORTH);
	    rhs.add(centre, BorderLayout.CENTER);
	    
	    add(rhs,BorderLayout.CENTER);
	    fc = new JFileChooser();
	    //Add Action Listener for button
	    openFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					openDialog(arg0);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	    
	    showData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				viewer();
			}
		});
	    
	    exitFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
	}
	
	// Handle open button action.
	public void openDialog(ActionEvent e) throws FileNotFoundException {
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		ArrfFileFilter filter = new ArrfFileFilter();
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);
		if ((returnVal == JFileChooser.APPROVE_OPTION)
				&& (fc.getSelectedFile().isFile())) {
			//Reset
			instances =null;
			strAttribute="";
			numInstances = 0;
			numAttributes=0;
			
			File file = fc.getSelectedFile();
			Scanner scanner = new Scanner (new FileInputStream(file));

			try {
				// dis.available() returns 0 if the file does not have more lines.
				String str = "";
				boolean isData=false;
				while (scanner.hasNextLine()) {
					str = scanner.nextLine();
					// this statement reads the line from the file and print it to the console
					if (str.indexOf("RELATION") >0){
						m_AttSummaryPanel.setRelation(str.subSequence(str.indexOf("RELATION") + 9,str.length()).toString().toUpperCase());
					}
					if (str.indexOf("ATTRIBUTE") >0){
						numAttributes ++;
						//For Categorical value
						if (str.indexOf("{") > 0)
							strAttribute += str.subSequence(str.indexOf("ATTRIBUTE") + 9,str.indexOf("{") -1) + ",";
						else //For NUMERIC value
							strAttribute += str.subSequence(str.indexOf("ATTRIBUTE") + 9,str.indexOf("NUMERIC") -1) + ",";
					}
					if (isData)
						numInstances ++;
					if (str.indexOf("DATA") >0)
						isData= true;
				}
				
				Scanner scanner_table = new Scanner (new FileInputStream(file));
				instances = new String[numInstances];
				int i =0;
				isData=false;
				while (scanner_table.hasNextLine()) {
					str= scanner_table.nextLine();
					if (isData){
						instances[i] = str;
						i ++;
					}
					if (str.indexOf("DATA") >0)
						isData= true;
				}
				m_AttSummaryPanel.setNumAttributes(numAttributes);
				m_AttSummaryPanel.setNumInstances(numInstances);
				m_AttSummaryPanel.setInfo();

				clusterPanel.setFile(file.getAbsolutePath());
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
		} else {
			System.out.println("wrong file");
		}
	}
	
	/**
	 * view the current instances object in the viewer
	 */
	public void viewer() {
		try{
			if (instances !=null){
				ViewerDialog dialog = new ViewerDialog(null);
				dialog.setTableViewer(instances, strAttribute, numAttributes,numInstances);
				dialog.setVisible(true);
			}
		}catch (NegativeArraySizeException nase){
			JOptionPane.showMessageDialog(MainScreen.this,
				    "Cannot load files from database!\n" + nase.toString(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}catch(Exception e){
			JOptionPane.showMessageDialog(MainScreen.this,
				    "Error" + e.toString(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void main(String[] args) {
		try {
			jf.getContentPane().setLayout(new BorderLayout());
			final MainScreen sp = new MainScreen();
			jf.getContentPane().add(sp, BorderLayout.CENTER);
			jf.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					jf.dispose();
					System.exit(0);
				}
			});
			jf.pack();
			jf.setSize(800, 600);
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screenSize = kit.getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;
			jf.setLocation(screenWidth / 8, screenHeight / 4);
			jf.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(ex.getMessage());
		}
	}
}
