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

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import base.ArrfFileFilter;
/** 
 * A panel that displays an instance summary for a set of instances and
 * lets the user open a set of instances from either a file or URL.
 *
 * Instances may be obtained either in a batch or incremental fashion.
 * If incremental reading is used, then
 * the client should obtain the Loader object (by calling
 * getLoader()) and read the instances one at a time. If
 * batch loading is used, then SetInstancesPanel will load
 * the data into memory inside of a separate thread and notify
 * the client when the operation is complete. The client can
 * then retrieve the instances by calling getInstances().
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision: 6890 $
 */
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

  /**
   * Manages sending notifications to people when we change the set of
   * working instances.
   */
  protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);

  /** The current set of instances loaded. */
//  protected Instances m_Instances;

  /** The current loader used to obtain the current instances. */
//  protected weka.core.converters.Loader m_Loader;
  
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
//    m_FileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    m_OpenURLBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      }
    });
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

//    m_ClassComboBox.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//	if ((m_Instances != null) && (m_ClassComboBox.getSelectedIndex() != -1)) {
//	  if (m_Instances.numAttributes() >= m_ClassComboBox.getSelectedIndex()) {
//	    m_Instances.setClassIndex(m_ClassComboBox.getSelectedIndex() - 1);   // -1 because of NO_CLASS element
//	    m_Support.firePropertyChange("", null, null);
//	  }
//	}
//      }
//    });
    
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
		String[] instances;
		String strAttribute="";
		int numInstances = 0;
		int numAttributes=0;
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		ArrfFileFilter filter = new ArrfFileFilter();
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);
		if ((returnVal == JFileChooser.APPROVE_OPTION)
				&& (fc.getSelectedFile().isFile())) {
			File file = fc.getSelectedFile();
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			DataInputStream dis = null;

			try {
				fis = new FileInputStream(file);

				// Here BufferedInputStream is added for fast reading.
				bis = new BufferedInputStream(fis);
				dis = new DataInputStream(bis);

				// dis.available() returns 0 if the file does not have more lines.
				String str = "";
				boolean isData=false;
				while (dis.available() != 0) {
					// this statement reads the line from the file and print it to the console
					str= dis.readLine();
					if (str.indexOf("RELATION") >0){
//						System.out.println("Relation=" + str.subSequence(str.indexOf("RELATION") + 9,str.length()));
//						m_AttSummaryPanel.setRelation(str.subSequence(str.indexOf("RELATION") + 9,str.length()).toString().toUpperCase());
					}
					if (str.indexOf("ATTRIBUTE") >0){
						numAttributes ++;
//						System.out.println("INDEX=" + str.indexOf("ATTRIBUTE"));
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
				
				FileInputStream fis_table = new FileInputStream(file);
				BufferedInputStream bis_table = new BufferedInputStream(fis_table);
				DataInputStream dis_table = new DataInputStream(bis_table);
				instances = new String[numInstances];
				int i =0;
				isData=false;
				while (dis_table.available() != 0) {
					str= dis_table.readLine();
					if (isData){
						instances[i] = str;
						i ++;
					}
					if (str.indexOf("DATA") >0)
						isData= true;
				}
//				m_AttSummaryPanel.setNumAttributes(numAttributes);
//				m_AttSummaryPanel.setNumInstances(numInstances);
//				m_AttSummaryPanel.setInfo();

				// dispose all the resources after using them.
				fis.close();
				bis.close();
				dis.close();

			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException es) {
				es.printStackTrace();
			}
		} else {
			System.out.println("wrong file");
		}
    
//    if (m_IOThread == null) {
//      int returnVal = m_FileChooser.showOpenDialog(this);
//      if (returnVal == JFileChooser.APPROVE_OPTION) {
//	final File selected = m_FileChooser.getSelectedFile();
//	m_IOThread = new Thread() {
//	  public void run() {
//	    setInstancesFromFile(selected);
//	    m_IOThread = null;
//	  }
//	};
//	m_IOThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
//	m_IOThread.start();
//      }
//    } else {
//      JOptionPane.showMessageDialog(this,
//				    "Can't load at this time,\n"
//				    + "currently busy with other IO",
//				    "Load Instances",
//				    JOptionPane.WARNING_MESSAGE);
//    }
  }
  
  /**
   * Loads results from a set of instances contained in the supplied
   * file.
   *
   * @param f a value of type 'File'
   */
//  protected void setInstancesFromFile(File f) {
//    boolean incremental = m_readIncrementally;
//    
//    try {
//      m_Loader = ConverterUtils.getLoaderForFile(f);
//      if (m_Loader == null)
//	throw new Exception("No suitable FileSourcedConverter found for file!\n" + f);
//      
//      // not an incremental loader?
//      if (!(m_Loader instanceof IncrementalConverter))
//	incremental = false;
//
//      // load
//      ((FileSourcedConverter) m_Loader).setFile(f);
//      if (incremental) {
//        m_Summary.setShowZeroInstancesAsUnknown(m_showZeroInstancesAsUnknown);
//	setInstances(m_Loader.getStructure());
//      } else {
//        // If we are batch loading then we will know for sure that
//        // the data has no instances
//        m_Summary.setShowZeroInstancesAsUnknown(false);
//	setInstances(m_Loader.getDataSet());
//      }
//    } catch (Exception ex) {
//      JOptionPane.showMessageDialog(this,
//				    "Couldn't read from file:\n"
//				    + f.getName(),
//				    "Load Instances",
//				    JOptionPane.ERROR_MESSAGE);
//    }
//  }

  /**
   * Updates the set of instances that is currently held by the panel.
   *
   * @param i a value of type 'Instances'
   */
//  public void setInstances(Instances i) {
//
//    m_Instances = i;
//    m_Summary.setInstances(m_Instances);
//    
//    if (m_showClassComboBox) {
//      DefaultComboBoxModel model = (DefaultComboBoxModel) m_ClassComboBox.getModel();
//      model.removeAllElements();
//      model.addElement(NO_CLASS);
//      for (int n = 0; n < m_Instances.numAttributes(); n++) {
//	Attribute att = m_Instances.attribute(n);
//	String type = "(" + Attribute.typeToStringShort(att) + ")";
//	model.addElement(type + " " + att.name());
//      }
//      if (m_Instances.classIndex() == -1)
//	m_ClassComboBox.setSelectedIndex(m_Instances.numAttributes());
//      else
//	m_ClassComboBox.setSelectedIndex(m_Instances.classIndex() + 1);   // +1 because of NO_CLASS element
//    }
//    
//    // Fire property change event for those interested.
//    m_Support.firePropertyChange("", null, null);
//  }

  /**
   * Gets the set of instances currently held by the panel.
   *
   * @return a value of type 'Instances'
   */
//  public Instances getInstances() {
//    
//    return m_Instances;
//  }

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
   * Adds a PropertyChangeListener who will be notified of value changes.
   *
   * @param l a value of type 'PropertyChangeListener'
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    m_Support.addPropertyChangeListener(l);
  }

  /**
   * Removes a PropertyChangeListener.
   *
   * @param l a value of type 'PropertyChangeListener'
   */
  public void removePropertyChangeListener(PropertyChangeListener l) {
    m_Support.removePropertyChangeListener(l);
  }
}
