package gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;

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

import core.InputReader;
import core.Instances;

@SuppressWarnings("serial")
public class MainScreen extends JPanel {

	public JMenuBar menuBar = new JMenuBar();
	JFileChooser fc;
	AttributeSummaryPanel m_AttSummaryPanel = new AttributeSummaryPanel();
	ClusterPanel clusterPanel = new ClusterPanel();
	static JFrame jf = new JFrame("Preprocess");
	Instances inst;
	JRadioButtonMenuItem dbScan;
	JRadioButtonMenuItem kmeans;
	InputReader reader;
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
		dbScan = new JRadioButtonMenuItem("DBScan");
		kmeans = new JRadioButtonMenuItem("KMeans");
		dbScan.setSelected(true);
		group.add(dbScan);
		group.add(kmeans);
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(runMenu);
		JMenuItem openFile = new JMenuItem("Open File");
		JMenuItem exitFile = new JMenuItem("Exit");
		fileMenu.add(openFile);
		fileMenu.add(exitFile);
		JMenuItem showData= new JMenuItem("Show data");
		JMenuItem exportExcel= new JMenuItem("Export Excel");
		viewMenu.add(showData);
		viewMenu.add(exportExcel);
		setLayout(new BorderLayout());
	    add(menuPanel, BorderLayout.NORTH);
	    runMenu.add(dbScan);
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
	    
	    dbScan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clusterPanel.setType("dbscan");
				clusterPanel.updateOption(false);
			}
		});
	    
	    kmeans.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clusterPanel.setType("kmeans");
				clusterPanel.updateOption(true);
			}
		});
	    
	    showData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				viewer();
			}
		});
	    
	    exportExcel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportExcel();
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
			File fileTraining = fc.getSelectedFile();
			reader = new InputReader(fileTraining.getAbsolutePath());
			inst = reader.getData();
			m_AttSummaryPanel.setRelation(inst.getRelation());
			m_AttSummaryPanel.setNumAttributes(inst.getNumInstance());
			m_AttSummaryPanel.setNumInstances(inst.getNumAttribute());
			m_AttSummaryPanel.setInfo();
			clusterPanel.setFileTraining(fileTraining.getAbsolutePath());
			clusterPanel.setReader(reader);
			clusterPanel.m_StartBut.setEnabled(true);
			clusterPanel.setFlag(reader.getFlag());
			clusterPanel.setAttributeNames(inst.getAttributeName());
			if (dbScan.isSelected())
				clusterPanel.setType("dbscan");
			else if (kmeans.isSelected())
				clusterPanel.setType("kmeans");
		} else {
			System.out.println("wrong file");
		}
	}
	
	/**
	 * view the current instances object in the viewer
	 */
	public void viewer() {
		try{
			if (inst !=null){
				ViewerDialog dialog = new ViewerDialog(null);
				dialog.setTableViewer(inst.getData(), inst.attributeName, inst.getNumAttribute(),inst.getNumInstance());
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
	
	public void exportExcel(){
		if (inst !=null){
			JFileChooser fcchooser = new JFileChooser();
			fcchooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int returnVal = fcchooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File file = fcchooser.getSelectedFile();
				clusterPanel.exportExcel(file.getAbsolutePath());
			} else {
				System.out.println("It's not saved");
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			jf.getContentPane().setLayout(new BorderLayout());
			final MainScreen mainScreen = new MainScreen();
			jf.getContentPane().add(mainScreen, BorderLayout.CENTER);
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
