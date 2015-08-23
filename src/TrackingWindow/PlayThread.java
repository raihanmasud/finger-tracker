package TrackingWindow;


public class PlayThread extends Thread {
	
	private TrackingControlPanel tcp;
	private boolean playing;
	
	public PlayThread(TrackingControlPanel controls) {
		tcp = controls;
	}
	
	public void run()  {
		//advance the slider every 33 ms
		playing = true;
		while(playing){
			//System.out.println("PLAYING");
			try{
				tcp.setSliderValue(tcp.getSliderValue() + 1);
				Thread.sleep(33);
				
				if(tcp.getSliderValue() == tcp.getSliderMax())
					playing = false;
				}
			catch(InterruptedException ie){}
		}
		
		tcp.donePlaying();
	}
	
	public void pause() throws InterruptedException {
		playing = false;
		
	}

}
