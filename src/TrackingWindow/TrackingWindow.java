/* TrackingWindow.java
 * CIS 423/510 VISUAL TEAM
 * 
 * The TrackingWindow processes/shows the frames of the movie.
 * It is its own thread so that it can run in paralel with the ControlFrame,
 * so the ControlFrame can stop the TrackingWindow if needed.
 * 
 * This holds:
 * 		ImagePainter imagep - to show the frame and tracked centers
 * 		ImageStackWorker isw - to process the frames and find the center data.
 */

package TrackingWindow;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ImageProcessing.ImageStackWorker;
import MainUI.Consol;



public class TrackingWindow extends Thread implements MouseListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePainter imagep;
	private ImageStackWorker isw;
	private TrackingSelectPanel tsp;
	private TrackingControlPanel tcp;
	private TrackingSettingsPanel tsettings;
	private JFrame track_win;
	private JPanel settings_and_controls;
	private int cur_frame;
	private int start_tracking_frame;
	private Vector<SelectedFinger> tracking_fingers;
	private boolean selecting_mode;
	private boolean finished_tracking;
	
	public TrackingWindow(String s, Semaphore sem){	
		track_win = new JFrame(s);
		isw = new ImageStackWorker(s,  new TrackerCommunicatior(sem, this));
		imagep = new ImagePainter(this);
		tsp = new TrackingSelectPanel(this);
		tcp = new TrackingControlPanel(this, sem);
		tsettings = new TrackingSettingsPanel();
		imagep.addMouseListener(this);
		selecting_mode = true;
		finished_tracking = false;
		tracking_fingers = new Vector<SelectedFinger>();
		cur_frame = 1;
		start_tracking_frame = 1;
	}
	
	public void run(){
		setUp();
	}
	
	// This is method that will use the ImageStackWorker to process all
	// frames of the movie, and get the tracked centers data
	public void setUp(){
		// opens first frame, and waits for user to input fingers to track.
		//track_win.setResizable(false);
		showFirstImage();
		settings_and_controls = new JPanel(new GridLayout(3,1));
		settings_and_controls.add(tcp);
		settings_and_controls.add(tsp);
		settings_and_controls.add(tsettings);
		track_win.add(imagep, BorderLayout.CENTER);
		track_win.add(settings_and_controls, BorderLayout.SOUTH);
		
		tsp.setBorder(BorderFactory.createTitledBorder("Select Fingers"));
		tsettings.setBorder(BorderFactory.createTitledBorder("Settings"));
		tcp.setBorder(BorderFactory.createTitledBorder("Controls"));
		imagep.setSize(isw.getWidth(), isw.getHeight());
		settings_and_controls.setSize(isw.getWidth(), 270);
		track_win.setBounds(650, 0, isw.getWidth() + 15, isw.getHeight()+settings_and_controls.getHeight());
		
		track_win.setVisible(true);
		track_win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public int getCurFrame(){
		return cur_frame;
	}
	
	public void setCurFrame(int i){
		cur_frame = i;
		showImage(i);
	}
	
	public int getStackSize(){
		return isw.getStackSize();
	}
	
	public Vector<SelectedFinger> getTrackingFingers() {
		return tracking_fingers;
	}
	
	// Tells the ImagePainter to paint the frame, and paint the tracked centers
	public void showImage(int i){
		imagep.setCenterPixels(isw.getTrackingCenters(i));
		
		if(i < start_tracking_frame)
			imagep.setBeforeStartingFrame(true);
		else
			imagep.setBeforeStartingFrame(false);
		
		imagep.setImage(isw.getImage(i));
	}	
	
	public void showFirstImage(){
		imagep.setImage(isw.getImage(1));
	}
	
	public void setConsolWin(Consol cw){
		isw.setConsolWin(cw);
	}
	
	public int getSelectedFingerID() {
		return tsp.getSelectedIndex() + 1;
	}
	
	public void deleteFingerID(int id) {
		boolean remove_ID = false;
		SelectedFinger victim = new SelectedFinger(0,0,0);
		for(SelectedFinger sf: tracking_fingers) {
			if(id == sf.getID()) {
				remove_ID = true;
				victim = sf;
			}
		}
		if(remove_ID)
			tracking_fingers.remove(victim);
		
		imagep.repaint();
	}
	
	public void startTracking(){
		selecting_mode = false;
		imagep.setSelectingMode(selecting_mode);
		
		// set tracking settings
		isw.setThreshold(tsettings.getThreshold());
		isw.setUpdateColor(tsettings.updateColor());
		
		isw.setTrackingColors(getCurFrame(), tracking_fingers);
		isw.start();
		showImage(cur_frame);
		start_tracking_frame = cur_frame;
	}
	
	public void stopTracking() {
		isw.stopTracking();
	}
	
	public void finishedTracking() {
		track_win.remove(settings_and_controls);
		track_win.setSize(isw.getWidth(), isw.getHeight()+100);
		settings_and_controls = new JPanel(new GridLayout(1,1));
		settings_and_controls.add(tcp);
		track_win.add(settings_and_controls, BorderLayout.SOUTH);
		imagep.setTrackingIDs(isw.getTrackingIDs());
		tcp.finishedTracking();
		finished_tracking = true;
	}
	
	public boolean notTrackedYet() {
		return !finished_tracking;
	}
	
	public void updateProgress(int i) {
		tcp.setProgressValue(i);
	}
	
	public int getFrameWidth(){
		return isw.getWidth();
	}
	
	public int getFrameHeight(){
		return isw.getHeight();
	}
	
	public void refresh() {
		track_win.repaint();
	}
	
	public TrackingControlPanel getTrackingControls(){
		return tcp;
	}

	public void mouseClicked(MouseEvent e) {
		if(selecting_mode) {
			int x = e.getX();
			int y = e.getY();
			int ID = tsp.getSelectedIndex() + 1;
			
			if(x >= isw.getWidth() || y >= isw.getHeight())
				return;
			
			tsp.setXFieldValue(Integer.toString(x));
			tsp.setYFieldValue(Integer.toString(y));
			
			// check if figner ID allready is allready being tracked,
			// if it is, deleteFingerID will remove it
			deleteFingerID(ID);
			
			tracking_fingers.add(new SelectedFinger(x,y, ID));
			
			// update the image painter
			imagep.setSelectedFingers(tracking_fingers);
			imagep.repaint();
		}
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void stateChanged(ChangeEvent e) {
		setCurFrame(tcp.getSliderValue());
	}
}
