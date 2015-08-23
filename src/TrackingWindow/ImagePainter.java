/* ImagePainter.java
 * CIS 423/510 VISUAL TEAM
 * 
 * Panel held by the TrackingWindow that will paint/display the frames,
 * and where the tracking centers are at.
 */

package TrackingWindow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Vector;
import javax.swing.JPanel;

import ImageProcessing.Pixel;


public class ImagePainter extends JPanel implements MouseMotionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Pixel [] centers;
	private Image cur_frame;
	private int X_location;
	private int Y_location;
	private Vector<SelectedFinger> tracking_fingers;
	private boolean [] tracking;
	private boolean selecting_mode;
	private boolean before_starting_frame;
	private int frame_width;
	private int frame_height;
	private TrackingWindow tw;
	
	public ImagePainter(TrackingWindow tracker){
		tw = tracker;
		frame_width = tw.getFrameWidth();
		frame_height = tw.getFrameHeight();
		centers = new Pixel[11];
		tracking = new boolean[11];
		addMouseMotionListener(this);
		selecting_mode = true;
		tracking_fingers = new Vector<SelectedFinger>();
	}
	
	public void setCenterPixels(Pixel [] tracking_centers){
		for(int i = 1; i <= 10; ++ i){
			centers[i] = tracking_centers[i];
		}
	}
	
	public void setImage(Image i){
		cur_frame = i;
		repaint();
	}
	
	public void setTrackingIDs(boolean [] b){
		tracking = b;
	}
	
	public void setBeforeStartingFrame(boolean b) {
		before_starting_frame = b;
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		
		// paint the frame
		int x;
		int y;
		
		g2d.drawImage(cur_frame, 0, 0, Color.WHITE, null);
		
		if(selecting_mode) {
			// draw X and Y cords of the 
			g2d.setColor(Color.GREEN);
			if(!(X_location >= frame_width) && !(Y_location >= frame_height)) {
				g2d.drawOval(X_location - 10, Y_location - 10, 20, 20);
				g2d.drawLine(X_location - 10, Y_location, X_location + 10, Y_location);
				g2d.drawLine(X_location, Y_location - 10, X_location, Y_location + 10);
				
				// draw the fingers ID number
				g2d.drawString(Integer.toString(tw.getSelectedFingerID()), X_location, Y_location - 11);
			}
			
			for(SelectedFinger sf: tracking_fingers) {
				// draw cross-hairs
				x = sf.getX();
				y = sf.getY();
				g2d.drawOval(x - 10, y - 10 , 20, 20);
				g2d.drawLine(x - 10, y, x + 10, y);
				g2d.drawLine(x, y - 10, x, y + 10);
				
				// draw the fingers ID number
				g2d.drawString(Integer.toString(sf.getID()), x, y - 11);
			}
		}
		
		for(int i = 1; i <= 10; ++i){
			if(tracking[i]){
				if(centers[i] != null){
					x = centers[i].getX();
					y = centers[i].getY();
					if(x >= 0 && y >= 0){
						g2d.setColor(Color.GREEN);
						g2d.fillOval(x - 5, y - 5 , 10, 10);
					
						// draw the fingers ID number
						g2d.drawString(Integer.toString(i), x, y - 11);
					}
				}
			}
		}
		
	}
	
	public void setSelectingMode(boolean t) {
		selecting_mode = t;
	}
	
	public void setSelectedFingers(Vector<SelectedFinger> fingers) {
		tracking_fingers = fingers;
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoved(MouseEvent e) {
		// if we are in selecting mode, save the X and Y of the mouse
		if(selecting_mode){
			X_location = e.getX();
			Y_location = e.getY();
			repaint();
			tw.refresh();
		}
	}
	
	
}
