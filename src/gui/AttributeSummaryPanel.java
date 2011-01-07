package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class AttributeSummaryPanel extends JPanel{
	
	private int numAttributes;
	private int numInstances;
	private String relation;
	
	/** Message shown when no instances have been loaded and no attribute set */
	  protected static final String NO_SOURCE = "None";
	
	/** Displays the name of the relation */
	protected JLabel lblRelation = new JLabel(NO_SOURCE);

	/** Displays the type of attribute */
	protected JLabel lblNumAttributes = new JLabel(NO_SOURCE);

	/** Displays the number of missing values */
	protected JLabel lblNumInstances = new JLabel(NO_SOURCE);

	/** Displays the number of unique values */
	protected JLabel lblSumOfWeights = new JLabel(NO_SOURCE);
	
	private String[] columnNames = { "Planet", "Radius", "Moons", "Gaseous",
	"Color" };

	private Object[][] cells = { { "Mercury", 2440.0, 0, false, Color.yellow },
			{ "Venus", 6052.0, 0, false, Color.yellow },
			{ "Earth", 6378.0, 1, false, Color.blue },
			{ "Mars", 3397.0, 2, false, Color.red },
			{ "Neptune", 24766.0, 8, true, Color.blue },
			{ "Pluto", 1137.0, 1, false, Color.black } };
	
	/** Displays other stats in a table */
	protected JTable tableViewer = new JTable(cells, columnNames) {
		/**
		 * returns always false, since it's just information for the user
		 * 
		 * @param row the row
		 * @param column the column
		 * @return always false, i.e., the whole table is not editable
		 */
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	public AttributeSummaryPanel() {
		JPanel simple = new JPanel();
	    GridBagLayout gbL = new GridBagLayout();
	    simple.setLayout(gbL);
	    JLabel lab = new JLabel("Relation:", SwingConstants.RIGHT);
	    lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
	    GridBagConstraints gbC = new GridBagConstraints();
	    gbC.anchor = GridBagConstraints.EAST;
	    gbC.fill = GridBagConstraints.HORIZONTAL;
	    gbC.gridy = 0;     gbC.gridx = 0;
	    gbL.setConstraints(lab, gbC);
	    simple.add(lab);
	    gbC = new GridBagConstraints();
	    gbC.anchor = GridBagConstraints.WEST;
	    gbC.fill = GridBagConstraints.HORIZONTAL;
	    gbC.gridy = 0;     gbC.gridx = 1;
	    gbC.weightx = 100; gbC.gridwidth = 3;
	    gbL.setConstraints(lblRelation, gbC);
	    simple.add(lblRelation);
	    lblRelation.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
	    
	    lab = new JLabel("Attributes:", SwingConstants.RIGHT);
	    lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
	    gbC = new GridBagConstraints();
	    gbC.anchor = GridBagConstraints.EAST;
	    gbC.fill = GridBagConstraints.HORIZONTAL;
	    gbC.gridy = 0;     gbC.gridx = 4;
	    gbL.setConstraints(lab, gbC);
	    simple.add(lab);
	    gbC = new GridBagConstraints();
	    gbC.anchor = GridBagConstraints.WEST;
	    gbC.fill = GridBagConstraints.HORIZONTAL;
	    gbC.gridy = 0;     gbC.gridx = 5;
	    gbC.weightx = 100;
	    gbL.setConstraints(lblNumAttributes, gbC);
	    simple.add(lblNumAttributes);
	    lblNumAttributes.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));

	    // Put into a separate panel?
	    lab = new JLabel("Instances:", SwingConstants.RIGHT);
	    lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
	    gbC = new GridBagConstraints();
	    gbC.anchor = GridBagConstraints.EAST;
	    gbC.fill = GridBagConstraints.HORIZONTAL;
	    gbC.gridy = 1;     gbC.gridx = 0;
	    gbL.setConstraints(lab, gbC);
	    simple.add(lab);
	    gbC = new GridBagConstraints();
	    gbC.anchor = GridBagConstraints.WEST;
	    gbC.fill = GridBagConstraints.HORIZONTAL;
	    gbC.gridy = 1;     gbC.gridx = 1;
	    gbC.weightx = 100;
	    gbL.setConstraints(lblNumInstances, gbC);
	    simple.add(lblNumInstances);
	    lblNumInstances.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));

	    lab = new JLabel("Sum of Weights:", SwingConstants.RIGHT);
	    lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
	    gbC = new GridBagConstraints();
	    gbC.anchor = GridBagConstraints.EAST;
	    gbC.fill = GridBagConstraints.HORIZONTAL;
	    gbC.gridy = 1;     gbC.gridx = 4;
	    gbL.setConstraints(lab, gbC);
	    simple.add(lab);
	    gbC = new GridBagConstraints();
	    gbC.anchor = GridBagConstraints.WEST;
	    gbC.fill = GridBagConstraints.HORIZONTAL;
	    gbC.gridy = 1;     gbC.gridx = 5;
	    gbC.weightx = 100;
	    gbL.setConstraints(lblSumOfWeights, gbC);
	    simple.add(lblSumOfWeights);
	    lblSumOfWeights.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
	    
	    
	    setLayout(new BorderLayout());
	    add(simple, BorderLayout.NORTH);
//	    add(new JScrollPane(tableViewer), BorderLayout.CENTER);
//	    tableViewer.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public void setInfo(){
		lblRelation.setText(getRelation());
		lblNumAttributes.setText("" + getNumAttributes());
		lblNumInstances.setText("" +getNumInstances());
	}
	
//	public void setTable(AttributeStats as, int index) {
//		if (as.nominalCounts != null) {
//			Attribute att = m_Instances.attribute(index);
//			Object[] colNames = { "No.", "Label", "Count", "Weight" };
//			Object[][] data = new Object[as.nominalCounts.length][4];
//			for (int i = 0; i < as.nominalCounts.length; i++) {
//				data[i][0] = new Integer(i + 1);
//				data[i][1] = att.value(i);
//				data[i][2] = new Integer(as.nominalCounts[i]);
//				data[i][3] = new Double(Utils.doubleToString(
//						as.nominalWeights[i], 3));
//			}
//			m_StatsTable.setModel(new DefaultTableModel(data, colNames));
//			m_StatsTable.getColumnModel().getColumn(0).setMaxWidth(60);
//			DefaultTableCellRenderer tempR = new DefaultTableCellRenderer();
//			tempR.setHorizontalAlignment(JLabel.RIGHT);
//			m_StatsTable.getColumnModel().getColumn(0).setCellRenderer(tempR);
//		} else if (as.numericStats != null) {
//			Object[] colNames = { "Statistic", "Value" };
//			Object[][] data = new Object[4][2];
//			data[0][0] = "Minimum";
//			data[0][1] = Utils.doubleToString(as.numericStats.min, 3);
//			data[1][0] = "Maximum";
//			data[1][1] = Utils.doubleToString(as.numericStats.max, 3);
//			data[2][0] = "Mean" + ((!m_allEqualWeights) ? " (weighted)" : "");
//			data[2][1] = Utils.doubleToString(as.numericStats.mean, 3);
//			data[3][0] = "StdDev" + ((!m_allEqualWeights) ? " (weighted)" : "");
//			data[3][1] = Utils.doubleToString(as.numericStats.stdDev, 3);
//			m_StatsTable.setModel(new DefaultTableModel(data, colNames));
//		} else {
//			m_StatsTable.setModel(new DefaultTableModel());
//		}
//		m_StatsTable.getColumnModel().setColumnMargin(4);
//	}
	
	/**
	 * @return the numAttributes
	 */
	public int getNumAttributes() {
		return numAttributes;
	}

	/**
	 * @param numAttributes the numAttributes to set
	 */
	public void setNumAttributes(int numAttributes) {
		this.numAttributes = numAttributes;
	}

	/**
	 * @return the numInstances
	 */
	public int getNumInstances() {
		return numInstances;
	}

	/**
	 * @param numInstances the numInstances to set
	 */
	public void setNumInstances(int numInstances) {
		this.numInstances = numInstances;
	}

	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}

	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}
}