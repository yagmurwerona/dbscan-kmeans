package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ViewerDialog extends JDialog implements ChangeListener {

	/** for serialization */
	private static final long serialVersionUID = 6747718484736047752L;

	/** Signifies an OK property selection */
	public static final int APPROVE_OPTION = 0;

	/** Signifies a cancelled property selection */
	public static final int CANCEL_OPTION = 1;

	/** the result of the user's action, either OK or CANCEL */
	protected int m_Result = CANCEL_OPTION;

	/** Click to activate the current set parameters */
	protected JButton m_OkButton = new JButton("OK");

	/** Click to cancel the dialog */
	protected JButton m_CancelButton = new JButton("Cancel");
	
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
		m_CancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_Result = CANCEL_OPTION;
				setVisible(false);
			}
		});
		m_OkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_Result = APPROVE_OPTION;
				setVisible(false);
			}
		});
		panel.add(m_OkButton);
		panel.add(m_CancelButton);

		pack();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation(screenWidth / 8, screenHeight / 4);
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
	public void setTableViewer(String[] instances, String strAttribute, int numAttribute, int numInstances) {
		//Process Attribute -> Column
		String[] columnNames = new String[numAttribute];
		StringTokenizer tokenizer = new StringTokenizer(strAttribute, ",");
		for (int i = 0; i < numAttribute; i++) {
			columnNames[i] = tokenizer.nextToken();
		}
		
		//Process Table
		String tmp="";
		Object[][] cells = new Object[numInstances][numAttribute];
		for (int row = 0; row < numInstances; row++) {
			tmp = instances[row];
			tokenizer = new StringTokenizer(tmp, ",");
			for (int column = 0; column < numAttribute; column++) {
				cells[row][column] = tokenizer.nextToken();
//				tmp = "" + tmp.subSequence(tmp.indexOf(',')+1, tmp.length());
			}
		}
		tableViewer = new JTable(cells, columnNames); 
		tableViewer.setEnabled(false);
		createDialog();
	}
}
