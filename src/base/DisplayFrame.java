package base;

import java.awt.Container;
import java.awt.Menu;

import javax.swing.JFrame;
import javax.swing.JLabel;


public class DisplayFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DisplayFrame()

	{

	}

	// Container contentPane;

	public void changeRoot()

	{

		Container test = this.getContentPane();

//		test.add(new WijzigAccount());

		test.repaint();

		test.setVisible(true);

		System.out.println("Menu item[] was pressed.");

	}

	/**
	 * 
	 * @param args
	 *            the command line arguments
	 */

	public static void main(String[] args) {

		JFrame frame = new JFrame("School beheer");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(new JLabel("test"));

//		frame.add(new NieuwAccount());
//
//		frame.setJMenuBar(new Menu());

		frame.pack();

		frame.setSize(500, 600);

		frame.setVisible(true);

	}

}