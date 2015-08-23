package UnitTesting;

import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import org.junit.Test;

import TrackingWindow.TrackingWindow;


/**
 * @author Raihan
 * Testing Tracking window class
 * whether it can track correctly 
 */
public class TrackingWindowTest {
	
	//SettingsPanel sp;
	
	
	//Null Object Testing for TrackingWindow Class
	@Test
	public void TrackigWindowObjectTest () throws Exception
	{
		//SettingsPanel sp = new SettingsPanel();
		TrackingWindow tw1input = new TrackingWindow("./res/videos/1finger.avi", new Semaphore(1));
		TrackingWindow tw2input = new TrackingWindow("./res/videos/2fing50slice7fps.avi", new Semaphore(1));
		//TrackingWindow nullInput = new TrackingWindow("", sp);
		assertNotNull(tw1input);//should pass
		assertNotNull(tw2input);//should pass
		//assertNotNull(nullInput);//should fail
		
		
	}
	
	
	
	//Testing setUp TrackingWindow Class
	@Test
	public void TrackigWindowSetUpTest () throws Exception
	{
	
		//SettingsPanel sp = new SettingsPanel();
		TrackingWindow tw1input = new TrackingWindow("./res/videos/1finger.avi", new Semaphore(1));
		tw1input.setUp();
		
		
	}
	
	
	//Testing showImage Method TrackingWindow Class
	@Test
	public void TrackigWindowShowImageTest () throws Exception
	{
	
		//SettingsPanel sp = new SettingsPanel();
		TrackingWindow tw1input = new TrackingWindow("./res/videos/1finger.avi", new Semaphore(1));
	    
		tw1input.showImage(0); //illegalArgument, argument out of range // Need to look at in case frames start from 0 
	    
		//array out of bound *need to revise showImage for 100 and over
		tw1input.showImage(100); 
	    
		//array out of bound * should fail * OK
		tw1input.showImage(-1); 
	    
		tw1input.showImage(1); //pass
	    tw1input.showImage(30); //pass

        	    

	    
	}
	
	
	
}
