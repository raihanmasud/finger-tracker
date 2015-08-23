package TrackingWindow;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class TrackingSelectPanel extends JPanel implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox fingerlist;
	private JTextField Xfield;
	private JTextField Yfield;
	private JButton delete;
	private TrackingWindow tw;
	private Vector<SelectedFinger> tracking_fingers;
	
	public TrackingSelectPanel(TrackingWindow trackingwin) {
		super(new GridLayout(1,4));
		Xfield = new JTextField();
		Xfield.setEditable(false);
		Yfield = new JTextField();
		Yfield.setEditable(false);
		tw = trackingwin;

		String [] items = new String [10]; 
		for(int i = 1; i <= 10; ++i){
			items[i-1] = "Finger" + i;
		}
		delete = new JButton("Delete");
		delete.addActionListener(this);
		fingerlist = new JComboBox(items);
		fingerlist.addActionListener(this);
		add(delete);
		add(fingerlist);
		add(Xfield);
		add(Yfield);
	}
	
	public void setXFieldValue(String s) {
		Xfield.setText(s);
	}
	
	public void setYFieldValue(String s) {
		Yfield.setText(s);
	}
	
	public int getSelectedIndex() {
		return fingerlist.getSelectedIndex();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == delete.getActionCommand()) {
			// if the action was delete...
			tracking_fingers = tw.getTrackingFingers();
			int id = getSelectedIndex() + 1;
			boolean found = false;
			
			for(SelectedFinger sf: tracking_fingers) {
				if(sf.getID() == id) {
					Xfield.setText("");
					Yfield.setText("");
					found = true;
				}
			}
			
			if(found) {
				// delete the figner from the trackign window
				tw.deleteFingerID(id);
			}
		}
		else {
			// ... else the action was selcting a finger
			// when an action is preformed (i.e. new selection in combo boc)
			// update the tracking fingers, and set the X and Y value of the 
			// selected index into the X and Y fields
			tracking_fingers = tw.getTrackingFingers();
			int id = getSelectedIndex() + 1;
			boolean found = false;
			
			for(SelectedFinger sf: tracking_fingers) {
				if(sf.getID() == id) {
					Xfield.setText(Integer.toString(sf.getX()));
					Yfield.setText(Integer.toString(sf.getY()));
					found = true;
				}
			}
			
			if(!found) {
				Xfield.setText("");
				Yfield.setText("");
			}
		}
	}
}
