/* ImageStackWorker.java
 * CIS 423/510 VISUAL TEAM
 * 
 * This class is in charge of processing the frames of the video
 * and will pull out certain colors and find there centroid.
 * 
 *  This uses ImageJ classes:
 *  	ImageStack - a list of frames
 *  	ImageProcessor and ColorProcessor - classes used to get info (RGB values)
 *  											about the pixels of a frame
 */

package ImageProcessing;

import java.awt.Color;
import java.awt.Image;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Date; 

import MainUI.Consol;
import TrackingWindow.SelectedFinger;
import TrackingWindow.TrackerCommunicatior;
import XML.XmlWriter;
import ij.ImageStack;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

public class ImageStackWorker extends Thread{
	
	private ImageStack is;
	private ImageProcessor ip;
	private StackBuilder sb;
	private ColorProcessor cp;
	private Vector<Pixel> pixels;
	private XmlWriter xw; 
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private boolean stop;
	private int stop_index;
	private boolean center_found;
	private Consol cw;
	private int threshold = 25;
	private int start_frame = 1;
	private TrackerCommunicatior tcom;
	private int highest_proc_frame = 0;
	private boolean update_color;
	private int[] R;
	private int[] G;
	private int[] B;
	private Pixel [][] Tracker_centers;
	private Vector<SelectedFinger> tracking_fingers;
	
	
	// Constructor, takes a String which is the pathname of the movie
	// and a pointer to the settings pannel to know what to track
	// and how.
	public ImageStackWorker(String s, TrackerCommunicatior tc){
		// first aquire the semaphore
		tcom = tc;
		tcom.acquireSem();
		
		sb = new StackBuilder(s);
		is = sb.getStack();
		pixels = new Vector<Pixel>();
		Tracker_centers = new Pixel[is.getSize() + 1][11];
		tracking_fingers = new Vector<SelectedFinger>();
		center_found = false;
		stop = false;
		
		//create a new xml file as the name of the video and current time. 
		Date date=new Date(); 
		String str=df.format(date); 
		xw = new XmlWriter(s, str);
		
		// defualt setting is currently true for update color function
		update_color = true;
		R = new int[11];
		G = new int[11];
		B = new int[11];
		
		// inizilize the ImageProccessor and Color Processor
		ip = is.getProcessor(1);
		cp = (ColorProcessor) ip.convertToRGB();
	}
	
	public void run() {
		// first set the centers for the 
		GetCenterData();
		tcom.finishedTracking();
	}
	
	// Method to update the Pixel vector for the current frame i
	// 	1:  if a center was not found during the last frame (i-1)
	//      then all the pixels in the frame will be looked at
	//	2:  if all centers where found then speed up processing by
	//	    only looking at a window of pixels around each center
	private void findPixels(int i){
		ip = is.getProcessor(i);
		cp = (ColorProcessor) ip.convertToRGB();
		pixels = new Vector<Pixel>();
		boolean proc_all = true;
		
		
		for(SelectedFinger sf: tracking_fingers){
			if(center_found){
				processWindow(pixels, Tracker_centers, i, sf.getID());
				proc_all = false;
			}
		}

		if(proc_all){
			for(int x = 0; x < is.getWidth(); ++x){
				for(int y = 0; y < is.getHeight(); ++y){
					Pixel p = new Pixel(x, y, cp.getColor(x, y));
					pixels.add(p);
				}
			}
			//System.err.println("PROC ALL");
		}
	}
	
	// Method used to only look at pixels around the 
	// center for frame i, (window_centers[i])
	// these pixels are added to the Pixel vector Pixels
	private void processWindow(Vector<Pixel> Pixels, Pixel[][] window_centers, int i, int fingerID){
		int x_start = 0;
		int x_end = is.getWidth();
		int y_start = 0;
		int y_end = is.getHeight();
		
		int frame_width = is.getWidth();
		int frame_height = is.getHeight();
		
		int center_x;
		int center_y;
		//System.out.println("i is " + i);
		center_x = window_centers[i-1][fingerID].getX();
		center_y = window_centers[i-1][fingerID].getY();
		
		x_start = center_x - (int) (0.025 * frame_width);
		y_start = center_y - (int) (0.025 * frame_height);
		
		if(x_start < 0)
			x_start = 0;
		if(y_start <0)
			y_start = 0;

		x_end = center_x + (int) (0.025 * frame_width);
		y_end = center_y + (int) (0.025 * frame_height);
		
		if(x_end > frame_width)
			x_end = frame_width-1;
		if(y_end > frame_height)
			y_end = frame_height-1;
		
		if(x_start > x_end)
			x_start = x_end;
		if(y_start > y_end)
			y_start = y_end;
		
		
		for(int x = x_start; x < x_end; ++x){
			for(int y = y_start; y < y_end; ++y){
				Pixel p = new Pixel(x, y, cp.getColor(x, y));
				pixels.add(p);
			}
		}
		
	
	}
	
	/* METHOD USED TP PICK OUT COLORS:
	 * will pick out colors depending on the values
	 * determined from the SettingsPanel sp.
	 * 
	 * Will return an array of the chosen centers
	 * currently(4/20/09) indicies are:
	 * [0] -> RED
	 * [1] -> GREEN
	 * [2] -> BLUE
	 */
	private Pixel[] trackFingers(){
		cp = (ColorProcessor) ip.convertToRGB();
		Color color;
		
		Pixel [] center = new Pixel[11];
		int x;
		int y;
		int center_x;
		int center_y;
		
		int tracking_x_total[] = new int [11];
		int tracking_y_total[] = new int [11];
		int tracking_count[] = new int [11];
		int ID = 0;
		for(int i = 0; i <=10; ++i) {
			tracking_x_total[i] = 0;
			tracking_y_total[i] = 0;
			tracking_count[i] = 0;
		}
		
		// Depending on the selection of the SettingsPanel , the following
		// will set rel_comp to a value that will be compared to the
		// color wanted to be picked out.
		//
		// If the wanted color component is higher then rel_comp,
		// then that pixel is pulled out.  This will do this for each pixel
		// that is in the Pixel Vector pixels
		center_found = true;
		for(Pixel p: pixels){
			if(stop) {
				return center;
			}
			x = p.getX();
			y = p.getY();
			color = cp.getColor(x,y);
			
			// look for the tracking finger color in pixels we are looking at
			for(SelectedFinger sf: tracking_fingers){
				ID = sf.getID();
				
				if((color.getRed() < R[ID] + threshold) && (color.getRed() > R[ID] - threshold)) {
					if((color.getGreen() < G[ID] + threshold) && (color.getGreen() > G[ID]- threshold)) {
						if((color.getBlue() < B[ID] + threshold) && (color.getBlue() > B[ID] - threshold)){
							tracking_x_total[ID] += x;
							tracking_y_total[ID] += y;
							++tracking_count[ID];
						}
					}
				}
			}
		}
		
		// find the center of the tracking value
		for(SelectedFinger sf: tracking_fingers){
			ID = sf.getID();
		
			if(tracking_count[ID] != 0){
				center_x = tracking_x_total[ID]/tracking_count[ID];
				center_y = tracking_y_total[ID]/tracking_count[ID];
				Color tempcolor = cp.getColor(center_x, center_y);
				// center[3] is currently the tracking center place, this
				// will change later after one finger picker works
				center[ID] = new Pixel(center_x, center_y, tempcolor);
				
				
				// update tracking color to use in next frame
				// NOTE updating caused fingers to drift too much
				// this might be able to be done better but for now
				// it is taken out, but some videos need it
				// ...........
				// Decided to make it a settings option
				if(update_color){
					//System.out.println("UPDATING COLOR");
					R[ID] = tempcolor.getRed();
					G[ID] = tempcolor.getGreen();
					B[ID] = tempcolor.getBlue();
				}
				
			}
			else {
				center[ID] = new Pixel(-1, -1, Color.WHITE);
				center_found = false;
			}
		}
		
		return center;	
	}
	
	// Method to find the center data for all frames
	// this will go through all frames in the ImageStack and 
	// first call findPixels(i) to find the pixels to look at in
	// the frame i.  Then calls trackFingers() to find the centers 
	// in the frame, and updates the global center arrays with
	// that frames centers values.
	public void GetCenterData(){
		int x = 0;
		int y = 0;
		Pixel [] centers;
		
		// write 0,0 to XML incase tracking was started after the first frame
		int ID;
		for(int i = 1; i < start_frame; ++i) {
			for(SelectedFinger sf: tracking_fingers){
				ID = sf.getID();
				
				Tracker_centers[i][ID] = new Pixel(-1,-1,Color.WHITE);
				x = -1;
				y = -1;
				cw.writeToWin("Finger " + Integer.toString(ID) + " is at: (" + x + " , " + y + ") in frame  " + i);
				//write in xml file on real time. 
				xw.AddEvent(Integer.toString(ID),i, x, y);
			}
		}

		for(int i = start_frame; i <= is.getSize(); ++i){
			//System.out.println("Tracking with frame.... " + i);
			highest_proc_frame = i;
			tcom.updateProgress(i);
			
			// first release the semaphore, to see if the control
			// panel is trying to butt in and tell the proccessing to stop
			tcom.releaseSem();
			tcom.acquireSem();

			// if stop was called, then we want to stop 
			if(stop){
				stop_index = i;
				//write final </coordStream> into file.
				xw.OutputXML();
				tcom.releaseSem();
				return;
			}
			
			findPixels(i);
			centers = trackFingers();
			
			for(SelectedFinger sf: tracking_fingers){
				ID = sf.getID();
				
				Tracker_centers[i][ID] = centers[sf.getID()];
				x = centers[ID].getX();
				y = centers[ID].getY();
				cw.writeToWin("Finger " + Integer.toString(ID) + " is at: (" + x + " , " + y + ") in frame  " + i);
				//write in xml file on real time. 
				xw.AddEvent(Integer.toString(ID),i, x, y);
			}
		}
		
		//write final </coordStream> into file.
		xw.OutputXML();	
		
		tcom.releaseSem();
	}
	
	// returns center for frame i
	public Pixel[] getTrackingCenters(int i){
		if(!stop || i < stop_index)
			return Tracker_centers[i];
		else {
			Pixel [] temp = new Pixel[11];
			for(int k = 1; k <= 10; ++k){
				temp[k] = new Pixel(0, 0, Color.BLUE);
			}
			
			return temp;
		}
	}
	
	public Vector<Pixel> getPixels(){
		return pixels;
	}
	
	public boolean [] getTrackingIDs(){
		boolean [] tracking = new boolean [11];
		
		for(int i = 0; i <= 10; ++i){
			tracking[i] = false;
		}
		
		for(SelectedFinger sf: tracking_fingers){
			tracking[sf.getID()] = true;
		}
		
		return tracking;
	}
	
	public Image getImage(int i){
		ip = is.getProcessor(i);
		cp = (ColorProcessor) ip.convertToRGB();
		return cp.createImage();
	}
	
	// called by tracking window to tell the stack worker what color to track
	public void setTrackingColors(int i, Vector<SelectedFinger> fingers) {
		ip = is.getProcessor(i);
		cp = (ColorProcessor) ip.convertToRGB();
		tracking_fingers = fingers;
		
		Color color;
		int ID;
		for(SelectedFinger sf: tracking_fingers){
			ID = sf.getID();
			color = cp.getColor(sf.getX(), sf.getY());
			R[ID] = color.getRed();
			G[ID] = color.getGreen();
			B[ID] = color.getBlue();
			
			Tracker_centers[i-1][ID] = new Pixel(sf.getX(), sf.getY(), color);
			center_found = true;
		}
		
		start_frame = i;
		
		//System.out.println(" x,y and RGB...." + x + "," + y + " : " + R + " " + G + " " + B);
		// for prototype.... do not click start...
		// NOTE: add start button latter.....
		
	}
	
	public void setThreshold(int i) {
		threshold = i;
	}
	
	public void setUpdateColor(boolean b) {
		update_color = b;
	}
	
	public void stopTracking() {
		stop = true;
		stop_index = highest_proc_frame;
	}
	
	public int getWidth(){
		return is.getWidth();
	}
	
	public int getHeight(){
		return is.getHeight();
	}
	
	public int getStackSize(){
		// if stop was called, return the number
		return is.getSize();
	}
	
	public void setConsolWin(Consol cw){
		this.cw = cw;
	}
}
