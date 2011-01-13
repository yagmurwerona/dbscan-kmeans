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
public class AttributeSummaryPanel extends JPanel {

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
//	protected JLabel lblSumOfWeights = new JLabel(NO_SOURCE);

	public AttributeSummaryPanel() {
		JPanel simple = new JPanel();
		GridBagLayout gbL = new GridBagLayout();
		simple.setLayout(gbL);
		JLabel lab = new JLabel("Relation:", SwingConstants.RIGHT);
		lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		GridBagConstraints gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.EAST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 0;
		gbC.gridx = 0;
		gbL.setConstraints(lab, gbC);
		simple.add(lab);
		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 0;
		gbC.gridx = 1;
		gbC.weightx = 100;
		gbC.gridwidth = 3;
		gbL.setConstraints(lblRelation, gbC);
		simple.add(lblRelation);
		lblRelation.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));

		lab = new JLabel("Attributes:", SwingConstants.RIGHT);
		lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.EAST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 0;
		gbC.gridx = 4;
		gbL.setConstraints(lab, gbC);
		simple.add(lab);
		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 0;
		gbC.gridx = 5;
		gbC.weightx = 100;
		gbL.setConstraints(lblNumAttributes, gbC);
		simple.add(lblNumAttributes);
		lblNumAttributes
				.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));

		// Put into a separate panel?
		lab = new JLabel("Instances:", SwingConstants.RIGHT);
		lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.EAST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 1;
		gbC.gridx = 0;
		gbL.setConstraints(lab, gbC);
		simple.add(lab);
		gbC = new GridBagConstraints();
		gbC.anchor = GridBagConstraints.WEST;
		gbC.fill = GridBagConstraints.HORIZONTAL;
		gbC.gridy = 1;
		gbC.gridx = 1;
		gbC.weightx = 100;
		gbL.setConstraints(lblNumInstances, gbC);
		simple.add(lblNumInstances);
		lblNumInstances.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));

//		lab = new JLabel("Sum of Weights:", SwingConstants.RIGHT);
//		lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
//		gbC = new GridBagConstraints();
//		gbC.anchor = GridBagConstraints.EAST;
//		gbC.fill = GridBagConstraints.HORIZONTAL;
//		gbC.gridy = 1;
//		gbC.gridx = 4;
//		gbL.setConstraints(lab, gbC);
//		simple.add(lab);
		
//		gbC = new GridBagConstraints();
//		gbC.anchor = GridBagConstraints.WEST;
//		gbC.fill = GridBagConstraints.HORIZONTAL;
//		gbC.gridy = 1;
//		gbC.gridx = 5;
//		gbC.weightx = 100;
//		gbL.setConstraints(lblSumOfWeights, gbC);
//		simple.add(lblSumOfWeights);
//		lblSumOfWeights.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));

		setLayout(new BorderLayout());
		add(simple, BorderLayout.NORTH);
	}

	public void setInfo() {
		lblRelation.setText(getRelation());
		lblNumAttributes.setText("" + getNumAttributes());
		lblNumInstances.setText("" + getNumInstances());
	}

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