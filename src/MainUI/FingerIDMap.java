package MainUI;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FingerIDMap extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImageIcon finger_map;
	private int image_height;
	
	public FingerIDMap(String s) {
		finger_map = new ImageIcon(s);
		image_height = finger_map.getIconHeight();
		JLabel image = new JLabel(finger_map);
		add(image);
		setVisible(true);
	}
	
	public int getImageHeight() {
		return image_height;
	}
	
	public void toggleView() {
		if(this.isVisible()) 
			this.setVisible(false);
		else
			this.setVisible(true);
	}
}
