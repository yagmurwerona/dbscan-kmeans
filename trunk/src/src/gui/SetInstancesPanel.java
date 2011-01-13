package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.InputReader;
import core.Instances;

public class SetInstancesPanel
  extends JPanel {

  /** for serialization. */
  private static final long serialVersionUID = -384804041420453735L;

  /** the text denoting "no class" in the class combobox. */
  public final static String NO_CLASS = "No class";
  
  /** Click to open instances from a file. */
  protected JButton m_OpenFileBut = new JButton("Open file...");

  /** Click to open instances from a URL. */
  protected JButton m_OpenURLBut = new JButton("Open URL...");

  /** Click to close the dialog. */
  protected JButton m_CloseBut = new JButton("Close");

  /** The instance summary component. */
  protected InstancesSummaryPanel m_Summary = new InstancesSummaryPanel();

  /** the label for the class combobox. */
  protected JLabel m_ClassLabel = new JLabel("Class");
  
  /** the class combobox. */
  protected JComboBox m_ClassComboBox = new JComboBox(new DefaultComboBoxModel(new String[]{NO_CLASS}));
  
  /** The file chooser for selecting arff files. */
//  protected ConverterFileChooser m_FileChooser
//    = new ConverterFileChooser(new File(System.getProperty("user.dir")));

  /** Stores the last URL that instances were loaded from. */
  protected String m_LastURL = "http://";

  /** The thread we do loading in. */
  protected Thread m_IOThread;

  /** the parent frame. if one is provided, the close-button is displayed */
  protected JFrame m_ParentFrame = null;

  /** the panel the Close-Button is located in. */
  protected JPanel m_CloseButPanel = null;

  /** whether to read the instances incrementally, if possible. */
  protected boolean m_readIncrementally = true;
  
  /** whether to display zero instances as unknown ("?"). */
  protected boolean m_showZeroInstancesAsUnknown = false;
  
  /** whether to display a combobox that allows the user to choose the class
   * attribute. */
  protected boolean m_showClassComboBox;
  
  private InputReader reader;
  
  /**
   * Default constructor.
   */
  public SetInstancesPanel() {
    this(false, false);
  }
  
  /**
   * Create the panel.
   * 
   * @param showZeroInstancesAsUnknown	whether to display zero instances
   * 					as unknown (e.g., when reading data
   * 					incrementally)
   * @param showClassComboBox		whether to display a combobox
   * 					allowing the user to choose the class
   * 					attribute
   */
  public SetInstancesPanel(boolean showZeroInstancesAsUnknown, boolean showClassComboBox) {
    m_showZeroInstancesAsUnknown = showZeroInstancesAsUnknown;
    m_showClassComboBox = showClassComboBox;
    
    m_OpenFileBut.setToolTipText("Open a set of instances from a file");
    m_OpenURLBut.setToolTipText("Open a set of instances from a URL");
    m_CloseBut.setToolTipText("Closes the dialog");
    m_OpenFileBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
    	  setInstancesFromFileQ();
      }
    });
    m_CloseBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeFrame();
      }
    });
    m_Summary.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelButtons.add(m_OpenFileBut);
    panelButtons.add(m_OpenURLBut);

    JPanel panelClass = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelClass.add(m_ClassLabel);
    panelClass.add(m_ClassComboBox);

    JPanel panelButtonsAndClass;
    if (m_showClassComboBox) {
      panelButtonsAndClass = new JPanel(new GridLayout(2, 1));
      panelButtonsAndClass.add(panelButtons);
      panelButtonsAndClass.add(panelClass);
    }
    else {
      panelButtonsAndClass = new JPanel(new GridLayout(1, 1));
      panelButtonsAndClass.add(panelButtons);
    }
    
    m_CloseButPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_CloseButPanel.add(m_CloseBut);
    m_CloseButPanel.setVisible(false);
    
    JPanel panelButtonsAll = new JPanel(new BorderLayout());
    panelButtonsAll.add(panelButtonsAndClass, BorderLayout.CENTER);
    panelButtonsAll.add(m_CloseButPanel, BorderLayout.SOUTH);
    
    setLayout(new BorderLayout());
    add(m_Summary, BorderLayout.CENTER);
    add(panelButtonsAll, BorderLayout.SOUTH);
  }

  /**
   * Sets the frame, this panel resides in. Used for displaying the close 
   * button, i.e., the close-button is visible if the given frame is not null.
   * @param parent        the parent frame
   */
  public void setParentFrame(JFrame parent) {
    m_ParentFrame = parent;
    m_CloseButPanel.setVisible(m_ParentFrame != null);
  }
  
  /**
   * Returns the current frame the panel knows of, that it resides in. Can be
   * null.
   * @return the current parent frame
   */
  public JFrame getParentFrame() {
    return m_ParentFrame;
  }

  /**
   * closes the frame, i.e., the visibility is set to false.
   */
  public void closeFrame() {
    if (m_ParentFrame != null)
      m_ParentFrame.setVisible(false);
  }

  /**
   * Queries the user for a file to load instances from, then loads the
   * instances in a background process. This is done in the IO
   * thread, and an error message is popped up if the IO thread is busy.
   */
	@SuppressWarnings("deprecation")
  public void setInstancesFromFileQ() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		ArrfFileFilter filter = new ArrfFileFilter();
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);
		if ((returnVal == JFileChooser.APPROVE_OPTION)
				&& (fc.getSelectedFile().isFile())) {
			File file = fc.getSelectedFile();
			InputReader reader = new InputReader(file.getAbsolutePath());
			Instances inst = reader.getData();
			m_Summary.setRelation(inst.getRelation());
			m_Summary.setNumAttributes(inst.getNumInstance());
			m_Summary.setNumInstances(inst.getNumAttribute());
			m_Summary.updateValues();
			setReader(reader);
		}
  }

  /**
   * Returns the currently selected class index.
   * 
   * @return		the class index, -1 if none selected
   */
  public int getClassIndex() {
    if (m_ClassComboBox.getSelectedIndex() <= 0)
      return -1;
    else
      return m_ClassComboBox.getSelectedIndex() - 1;
  }
  
  /**
   * Sets whether or not instances should be read incrementally
   * by the Loader. If incremental reading is used, then
   * the client should obtain the Loader object (by calling
   * getLoader()) and read the instances one at a time. If
   * batch loading is used, then SetInstancesPanel will load
   * the data into memory inside of a separate thread and notify
   * the client when the operation is complete. The client can
   * then retrieve the instances by calling getInstances().
   *
   * @param incremental true if instances are to be read incrementally
   * 
   */
  public void setReadIncrementally(boolean incremental) {
    m_readIncrementally = incremental;
  }

  /**
   * Gets whether instances are to be read incrementally or not.
   *
   * @return true if instances are to be read incrementally
   */
  public boolean getReadIncrementally() {
    return m_readIncrementally;
  }

/**
 * @return the reader
 */
public InputReader getReader() {
	return reader;
}

/**
 * @param reader the reader to set
 */
public void setReader(InputReader reader) {
	this.reader = reader;
}
}
