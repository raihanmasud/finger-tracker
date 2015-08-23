/* ControlFrame.java
 * CIS 423/510 VISUAL TEAM
 * 
 * Creates a simple window to write all information to.
 */
package MainUI;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Consol extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea infowin;
	
	public Consol(JTextArea info){
		infowin = info;
		infowin.setLineWrap(true);
		infowin.setWrapStyleWord(true);
		infowin.setText("Welcome!\n");
		infowin.setSize(300, 500);
		setVisible(true);
	}
	
	public void writeToWin(String s){
		infowin.append(s + "\n");
	}
	
	public void clearWin(){
		
		infowin.setText("");
	}
	
	public void toggleView() {
		if(this.isVisible()) 
			this.setVisible(false);
		else
			this.setVisible(true);
	}
}
