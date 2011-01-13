package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.Instance;

public class ViewerDialog extends JDialog implements ChangeListener {

	/** for serialization */
	private static final long serialVersionUID = 6747718484736047752L;

	/** Click to activate the current set parameters */
	protected JButton m_OkButton = new JButton("OK");

	/** Displays other stats in a table */
	protected JTable tableViewer = new JTable();

	/**
	 * initializes the dialog with the given parent
	 * 
	 * @param parent for this dialog
	 */
	public ViewerDialog(Frame parent) {
		super(parent, true);
//		createDialog();
	}

	/**
	 * creates all the elements of the dialog
	 */
	protected void createDialog() {
		JPanel panel;

		setTitle("Viewer");

		getContentPane().setLayout(new BorderLayout());

		// ArffPanel

		getContentPane().add(new JScrollPane(tableViewer), BorderLayout.CENTER);

		// Buttons
		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(panel, BorderLayout.SOUTH);
		getContentPane().add(panel, BorderLayout.SOUTH);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
			
		m_OkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		panel.add(m_OkButton);

		pack();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation(screenWidth / 8, screenHeight / 4);
		setVisible(true);
	}

	/**
	 * Invoked when the target of the listener has changed its state.
	 */
	public void stateChanged(ChangeEvent e) {
		//TO DO
	}

	/**
	 * @return the tableViewer
	 */
	public JTable getTableViewer() {
		return tableViewer;
	}

	/**
	 * @param tableViewer the tableViewer to set
	 */
	public void setTableViewer(Vector <Instance> data, Vector<String> attributeNames, int numAttribute, int numInstances) {
		//Convert Vector to Array
		Instance[] dataArray = new Instance[data.size()];
		data.toArray(dataArray);
		
		String[] attributeNamesArray= new String[attributeNames.size()];
		attributeNames.toArray(attributeNamesArray);
		
		//Process Table
		String tmp="";
		StringTokenizer tokenizer;
		Object[][] cells = new Object[numInstances][numAttribute];
		for (int row = 0; row < numInstances; row++) {
			tmp = dataArray[row].originalData.toString();
			tmp = "" + tmp.subSequence(1, tmp.length()-1); //Remove "[" and "]" from Vector
			tokenizer = new StringTokenizer(tmp, ",");
			for (int column = 0; column < numAttribute; column++) {
				cells[row][column] = tokenizer.nextToken();
			}
		}
		tableViewer = new JTable(cells, attributeNamesArray); 
		tableViewer.setEnabled(false);
		createDialog();
	}
}
