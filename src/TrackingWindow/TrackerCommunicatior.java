package TrackingWindow;

import java.util.concurrent.Semaphore;


public class TrackerCommunicatior {
	
	private Semaphore stopper;
	private TrackingWindow tw;
	
	public TrackerCommunicatior(Semaphore sem, TrackingWindow tracker) {
		stopper = sem;
		tw = tracker;
	}
	
	public synchronized void acquireSem() {
		try{
			stopper.acquire();
		}
		catch(InterruptedException ie){}
	}
	
	public synchronized void releaseSem() {
			stopper.release();
	}
	
	public void updateProgress(int i) {
		tw.updateProgress(i);
	}
	
	// called by imageStackWorker to tell the tracking
	// window that it is finished
	public void finishedTracking() {
		tw.finishedTracking();
	}

}
