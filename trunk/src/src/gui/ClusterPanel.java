package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Kmeans.Kmeans;

@SuppressWarnings("serial")
public class ClusterPanel extends JPanel {
	
	protected JLabel m_MaxIterationsLab = new JLabel("maxIterations");
	
	protected JLabel m_MinStdDevLab = new JLabel("minStdDev");
	
	protected JLabel m_NumClustersLab = new JLabel("numClusters");
	
	protected JLabel m_SeedLab = new JLabel("seed");
	
	protected JTextField m_MaxIterationsText = new JTextField("100");
	
	protected JTextField m_MinStdDevText = new JTextField("1.0E-6");
	
	protected JTextField m_NumClustersText = new JTextField("2");
	
	protected JTextField m_SeedText = new JTextField("100");
	
	
	/** The output area for classification results */
	protected JTextArea m_OutText = new JTextArea(20, 40);

	/** Click to set test mode to test on training data */
	protected JRadioButton m_TrainBut = new JRadioButton("Use training set");

	/** Click to set test mode to a user-specified test set */
	protected JRadioButton m_TestSplitBut = new JRadioButton(
			"Supplied test set");

	/** Click to set test mode to generate a % split */
	protected JRadioButton m_PercentBut = new JRadioButton("Percentage split");

	/** Click to set test mode to classes to clusters based evaluation */
	protected JRadioButton m_ClassesToClustersBut = new JRadioButton(
			"Classes to clusters evaluation");

	/**
	 * Lets the user select the class column for classes to clusters based
	 * evaluation
	 */
	protected JComboBox m_ClassCombo = new JComboBox();

	/** Label by where the % split is entered */
	protected JLabel m_PercentLab = new JLabel("%", SwingConstants.RIGHT);

	/** The field where the % split is entered */
	protected JTextField m_PercentText = new JTextField("66");

	/** The button used to open a separate test dataset */
	protected JButton m_SetTestBut = new JButton("Set...");

	/** The frame used to show the test set selection panel */
	protected JFrame m_SetTestFrame;

	// /** The button used to popup a list for choosing attributes to ignore
	// while
	// clustering */
	// protected JButton m_ignoreBut = new JButton("Ignore attributes");

	protected DefaultListModel m_ignoreKeyModel = new DefaultListModel();
	protected JList m_ignoreKeyList = new JList(m_ignoreKeyModel);

	// protected Remove m_ignoreFilter = null;

	/**
	 * Alters the enabled/disabled status of elements associated with each radio
	 * button
	 */
	ActionListener m_RadioListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			updateRadioLinks();
		}
	};

	/** Click to start running the clusterer */
	protected JButton m_StartBut = new JButton("Start");

	/** Stop the class combo from taking up to much space */
	private Dimension COMBO_SIZE = new Dimension(250,
			m_StartBut.getPreferredSize().height);

	/** Click to stop a running clusterer */
	protected JButton m_StopBut = new JButton("Stop");

	/**
	 * Check to save the predictions in the results list for visualizing later
	 * on
	 */
	protected JCheckBox m_StorePredictionsBut = new JCheckBox(
			"Store clusters for visualization");

	/** A thread that clustering runs in */
	protected Thread m_RunThread;

	/** The file chooser for selecting model files */
	protected JFileChooser m_FileChooser = new JFileChooser(new File(
			System.getProperty("user.dir")));

	private String file;

	public ClusterPanel() {
		// Connect / configure the components
		m_OutText.setEditable(false);
		m_OutText.setFont(new Font("Monospaced", Font.PLAIN, 12));
		m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		m_OutText.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != InputEvent.BUTTON1_MASK) {
					m_OutText.selectAll();
				}
			}
		});

		m_TrainBut.setToolTipText("Cluster the same set that the clusterer"
				+ " is trained on");
		m_PercentBut.setToolTipText("Train on a percentage of the data and"
				+ " cluster the remainder");
		m_TestSplitBut.setToolTipText("Cluster a user-specified dataset");
		m_ClassesToClustersBut
				.setToolTipText("Evaluate clusters with respect to a"
						+ " class");
		m_ClassCombo
				.setToolTipText("Select the class attribute for class based"
						+ " evaluation");
		m_StartBut.setToolTipText("Starts the clustering");
		m_StopBut.setToolTipText("Stops a running clusterer");
		m_StorePredictionsBut
				.setToolTipText("Store predictions in the result list for later "
						+ "visualization");
		// m_ignoreBut.setToolTipText("Ignore attributes during clustering");

		// m_FileChooser.setFileFilter(m_ModelFilter);
		m_FileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		m_ClassCombo.setPreferredSize(COMBO_SIZE);
		m_ClassCombo.setMaximumSize(COMBO_SIZE);
		m_ClassCombo.setMinimumSize(COMBO_SIZE);
		m_ClassCombo.setEnabled(false);

		m_TestSplitBut.setSelected(true);
		updateRadioLinks();
		ButtonGroup bg = new ButtonGroup();
		bg.add(m_TrainBut);
		bg.add(m_PercentBut);
		bg.add(m_TestSplitBut);
		bg.add(m_ClassesToClustersBut);
		
		m_TrainBut.addActionListener(m_RadioListener);
		m_PercentBut.addActionListener(m_RadioListener);
		m_TestSplitBut.addActionListener(m_RadioListener);
		m_ClassesToClustersBut.addActionListener(m_RadioListener);
		m_SetTestBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTestSet();
			}
		});
		
		m_StartBut.setEnabled(true);
		m_StopBut.setEnabled(false);

		// m_ignoreBut.setEnabled(false);
		m_StartBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startClusterer();
			}
		});
		m_StopBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopClusterer();
			}
		});

		// m_ignoreBut.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// // setIgnoreColumns();
		// }
		// });

		// m_History.setHandleRightClicks(false);
		// see if we can popup a menu for the selected result
		// m_History.getList().addMouseListener(new MouseAdapter() {
		// public void mouseClicked(MouseEvent e) {
		// if (((e.getModifiers() & InputEvent.BUTTON1_MASK) !=
		// InputEvent.BUTTON1_MASK)
		// || e.isAltDown()) {
		// // int index = m_History.getList().locationToIndex(
		// // e.getPoint());
		// if (index != -1) {
		// // String name = m_History.getNameAtIndex(index);
		// // visualizeClusterer(name, e.getX(), e.getY());
		// } else {
		// // visualizeClusterer(null, e.getX(), e.getY());
		// }
		// }
		// }
		// });

		m_ClassCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// updateCapabilitiesFilter(m_ClustererEditor
				// .getCapabilitiesFilter());
			}
		});

		// Layout the GUI
		JPanel p1 = new JPanel();
		p1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Clusterer"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		p1.setLayout(new BorderLayout());
		// p1.add(m_CLPanel, BorderLayout.NORTH);

		JPanel p2 = new JPanel();
		GridBagLayout gbL = new GridBagLayout();
		p2.setLayout(gbL);
		p2.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Cluster mode"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		GridBagConstraints gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.gridy = 0;
		gbC.gridx = 0;
		gbL.setConstraints(m_TrainBut, gbC);
		p2.add(m_TrainBut);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.gridy = 1;
		gbC.gridx = 0;
		gbL.setConstraints(m_TestSplitBut, gbC);
		p2.add(m_TestSplitBut);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.EAST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 1;
		gbC.gridx = 1;
		gbC.gridwidth = 2;
		gbC.insets = new Insets(2, 10, 2, 0);
		gbL.setConstraints(m_SetTestBut, gbC);
		p2.add(m_SetTestBut);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.gridy = 2;
		gbC.gridx = 0;
		gbL.setConstraints(m_PercentBut, gbC);
		p2.add(m_PercentBut);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.EAST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 2;
		gbC.gridx = 1;
		gbC.insets = new Insets(2, 10, 2, 10);
		gbL.setConstraints(m_PercentLab, gbC);
		p2.add(m_PercentLab);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.EAST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 2;
		gbC.gridx = 2;
		gbC.weightx = 100;
		gbC.ipadx = 20;
		gbL.setConstraints(m_PercentText, gbC);
		p2.add(m_PercentText);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.gridy = 3;
		gbC.gridx = 0;
		gbC.gridwidth = 2;
		gbL.setConstraints(m_ClassesToClustersBut, gbC);
		p2.add(m_ClassesToClustersBut);

		m_ClassCombo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.gridy = 4;
		gbC.gridx = 0;
		gbC.gridwidth = 2;
		gbL.setConstraints(m_ClassCombo, gbC);
		p2.add(m_ClassCombo);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.gridy = 5;
		gbC.gridx = 0;
		gbC.gridwidth = 2;
		gbL.setConstraints(m_StorePredictionsBut, gbC);
		p2.add(m_StorePredictionsBut);
		
		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.gridy = 6;
		gbC.gridx = 0;
		gbL.setConstraints(m_MaxIterationsLab, gbC);
		p2.add(m_MaxIterationsLab);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.EAST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 6;
		gbC.gridx = 1;
		gbC.insets = new Insets(2, 10, 2, 10);
		gbL.setConstraints(m_MaxIterationsText, gbC);
		p2.add(m_MaxIterationsText);
		
		
		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.gridy = 7;
		gbC.gridx = 0;
		gbL.setConstraints(m_MinStdDevLab, gbC);
		p2.add(m_MinStdDevLab);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.EAST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 7;
		gbC.gridx = 1;
		gbC.insets = new Insets(2, 10, 2, 10);
		gbL.setConstraints(m_MinStdDevText, gbC);
		p2.add(m_MinStdDevText);
		
		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.gridy = 8;
		gbC.gridx = 0;
		gbL.setConstraints(m_NumClustersLab, gbC);
		p2.add(m_NumClustersLab);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.EAST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 8;
		gbC.gridx = 1;
		gbC.insets = new Insets(2, 10, 2, 10);
		gbL.setConstraints(m_NumClustersText, gbC);
		p2.add(m_NumClustersText);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.gridy = 9;
		gbC.gridx = 0;
		gbL.setConstraints(m_SeedLab, gbC);
		p2.add(m_SeedLab);

		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.EAST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 9;
		gbC.gridx = 1;
		gbC.insets = new Insets(2, 10, 2, 10);
		gbL.setConstraints(m_SeedText, gbC);
		p2.add(m_SeedText);		

		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(2, 1));
		JPanel ssButs = new JPanel();
		ssButs.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		ssButs.setLayout(new GridLayout(1, 2, 5, 5));
		ssButs.add(m_StartBut);
		ssButs.add(m_StopBut);

		JPanel ib = new JPanel();
		ib.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		ib.setLayout(new GridLayout(1, 1, 5, 5));
		// ib.add(m_ignoreBut);
		buttons.add(ib);
		buttons.add(ssButs);

		JPanel p3 = new JPanel();
		p3.setBorder(BorderFactory.createTitledBorder("Clusterer output"));
		p3.setLayout(new BorderLayout());
		final JScrollPane js = new JScrollPane(m_OutText);
		p3.add(js, BorderLayout.CENTER);
		js.getViewport().addChangeListener(new ChangeListener() {
			private int lastHeight;

			public void stateChanged(ChangeEvent e) {
				JViewport vp = (JViewport) e.getSource();
				int h = vp.getViewSize().height;
				if (h != lastHeight) { // i.e. an addition not just a user
										// scrolling
					lastHeight = h;
					int x = h - vp.getExtentSize().height;
					vp.setViewPosition(new Point(0, x));
				}
			}
		});

		JPanel mondo = new JPanel();
		gbL = new GridBagLayout();
		mondo.setLayout(gbL);
		gbC = new GridBagConstraints();
		// gbC.anchor = GridBagConstraints.WEST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 0;
		gbC.gridx = 0;
		gbL.setConstraints(p2, gbC);
		mondo.add(p2);
		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.NORTH;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 1;
		gbC.gridx = 0;
		gbL.setConstraints(buttons, gbC);
		mondo.add(buttons);
		gbC = new GridBagConstraints();
		// gbC.anchor = GridBagConstraints.NORTH;
		gbC.fill = GridBagConstraints.BOTH;
		gbC.gridy = 2;
		gbC.gridx = 0;
		gbC.weightx = 0;
		// gbL.setConstraints(m_History, gbC);
		// mondo.add(m_History);
		gbC = new GridBagConstraints();
		gbC.fill = GridBagConstraints.BOTH;
		gbC.gridy = 0;
		gbC.gridx = 1;
		gbC.gridheight = 3;
		gbC.weightx = 100;
		gbC.weighty = 100;
		gbL.setConstraints(p3, gbC);
		mondo.add(p3);

		setLayout(new BorderLayout());
		add(p1, BorderLayout.NORTH);
		add(mondo, BorderLayout.CENTER);
	}

	/**
	 * Sets the user test set. Information about the current test set is
	 * displayed in an InstanceSummaryPanel and the user is given the ability to
	 * load another set from a file or url.
	 * 
	 */
	protected void setTestSet() {
		if (m_SetTestFrame == null) {
			final SetInstancesPanel sp = new SetInstancesPanel();
			m_SetTestFrame = new JFrame("Test Instances");
			m_SetTestFrame.getContentPane().setLayout(new BorderLayout());
			m_SetTestFrame.getContentPane().add(sp, BorderLayout.CENTER);
			m_SetTestFrame.pack();
		}
		m_SetTestFrame.setVisible(true);
	}

	/**
	 * Starts running the currently configured clusterer with the current
	 * settings. This is run in a separate thread, and will only start if there
	 * is no clusterer already running. The clusterer output is sent to the
	 * results history panel.
	 */
	protected void startClusterer() {
		if (m_RunThread == null && file != null) {
			m_StartBut.setEnabled(false);
			m_StopBut.setEnabled(true);
			m_RunThread = new Thread() {
				public void run() {
					try {
						int testMode = 0;
						int percent = 66;
						if (m_PercentBut.isSelected()) {
							testMode = 2;
							percent = Integer.parseInt(m_PercentText.getText());
							if ((percent <= 0) || (percent >= 100)) {
								throw new Exception(
										"Percentage must be between 0 and 100");
							}
						} else if (m_TrainBut.isSelected()) {
							testMode = 3;
						} else if (m_TestSplitBut.isSelected()) {
							testMode = 4;
							// Check the test instance compatibility
							// Code...
						} else if (m_ClassesToClustersBut.isSelected()) {
							testMode = 5;
						} else {
							try {
								throw new Exception("Unknown test mode");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(ClusterPanel.this,
										"Problem evaluating clusterer:\n"+ ex.getMessage(),
										"Evaluate clusterer",
										JOptionPane.ERROR_MESSAGE);
					} finally {
						m_RunThread = null;
						m_StartBut.setEnabled(true);
						m_StopBut.setEnabled(false);
					}
					
					Kmeans km = new Kmeans();
					km.setInput(file);
					km.setDistanceAlgorithm("Mahattan");
					km.setCluster(Integer.parseInt(m_NumClustersText.getText()));
					km.setExperimentType("class");
					km.run();
					m_OutText.setText(km.getOutput().getContent());
				}
			};
			m_RunThread.setPriority(Thread.MIN_PRIORITY);
			m_RunThread.start();
		}
	}

	/**
	 * Stops the currently running clusterer (if any).
	 */
	protected void stopClusterer() {

		if (m_RunThread != null) {
			m_RunThread.interrupt();

			// This is deprecated (and theoretically the interrupt should do).
			m_RunThread.stop();

		}
	}

	/**
	 * Updates the enabled status of the input fields and labels.
	 */
	protected void updateRadioLinks() {

		m_SetTestBut.setEnabled(m_TestSplitBut.isSelected());
		if ((m_SetTestFrame != null) && (!m_TestSplitBut.isSelected())) {
			m_SetTestFrame.setVisible(false);
		}
		m_PercentText.setEnabled(m_PercentBut.isSelected());
		m_PercentLab.setEnabled(m_PercentBut.isSelected());
		m_ClassCombo.setEnabled(m_ClassesToClustersBut.isSelected());
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}
}