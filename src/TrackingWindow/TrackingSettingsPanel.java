package TrackingWindow;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class TrackingSettingsPanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField threshold;
	private ButtonGroup group;
	private JRadioButton update;
	private JRadioButton donotupdate;
	
	public TrackingSettingsPanel() {
		super(new GridLayout(2,2));
		
		threshold = new JTextField("25");
		add(new JLabel("Threshold:"));
		add(threshold);
		
		update = new JRadioButton("Update Tracking Color");
		update.addActionListener(this);
		donotupdate = new JRadioButton("Do Not Update Tracking Color");
		donotupdate.setSelected(true);
		donotupdate.addActionListener(this);
		group = new ButtonGroup();
		group.add(donotupdate);
		group.add(update);
		
		add(donotupdate);
		add(update);
	}
	
	public boolean updateColor() {
		return update.isSelected();
	}
	
	public int getThreshold() {
		int t;
		try {
			t = Integer.parseInt(threshold.getText());
		}
		catch(NumberFormatException nfe){
			// if input error in text field, return default of 25
			t = 25;
		}
		
		return t;
	}

	public void actionPerformed(ActionEvent e) {
	}
}
