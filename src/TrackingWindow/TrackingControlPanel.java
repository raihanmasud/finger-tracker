package TrackingWindow;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;


public class TrackingControlPanel extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TrackingWindow tw;
	private JSlider frame_slider;
	private JProgressBar progress;
	private JButton start_tracking;
	private JButton stop;
	
	private JButton play;
	private JButton pause;
	private PlayThread player;
	
	private JPanel right_buttons_panel;
	private JPanel left_buttons_panel;
	private JPanel bars;
	private Semaphore stopper;
	
	public TrackingControlPanel(TrackingWindow tracker, Semaphore sem) {
		stopper = sem;
			
		tw = tracker;
		frame_slider = new JSlider(1, tw.getStackSize());
		frame_slider.setValue(1);
		frame_slider.addChangeListener(tw);
		progress = new JProgressBar(1, tw.getStackSize());
		bars = new JPanel();
		bars.add(progress);
		bars.add(frame_slider);
		progress.setVisible(false);
	
		start_tracking = new JButton("Go");
		stop = new JButton("Stop");
		start_tracking.addActionListener(this);
		stop.addActionListener(this);
		right_buttons_panel = new JPanel();
		right_buttons_panel.add(start_tracking);
		right_buttons_panel.add(stop);
		
		play = new JButton("Play");
		pause = new JButton("Pause");
		play.addActionListener(this);
		pause.addActionListener(this);
		left_buttons_panel = new JPanel();
		left_buttons_panel.add(play);
		left_buttons_panel.add(pause);
		
		pause.setVisible(false);
		stop.setVisible(false);
		add(left_buttons_panel, BorderLayout.WEST);
		add(bars, BorderLayout.CENTER);
		add(right_buttons_panel, BorderLayout.EAST);
	}
	
	public int getSliderValue() {
		return frame_slider.getValue();
	}
	
	public void setSliderValue(int i) {
		frame_slider.setValue(i);
	}
	
	public int getSliderMax() {
		return frame_slider.getMaximum();
	}
	
	public void setProgressValue(int i) {
		progress.setValue(i);
	}
	
	public void finishedTracking() {
		// when all done tracking, only show the frame slider
		stop.setVisible(false);
		start_tracking.setVisible(false);
		progress.setVisible(false);
		frame_slider.setVisible(true);
		this.repaint();
	}
	
	public void startTracking() {			// remove the start button and change it into the stop button
		stop.setVisible(true);
		start_tracking.setVisible(false);
		
		// show the progress bar instead of the slider
		progress.setVisible(true);
		frame_slider.setVisible(false);
		
		this.repaint();
		
		tw.startTracking();
	}
	
	public void play() {
		player.start();
	}
	
	public void donePlaying() {
		play.setVisible(true);
		pause.setVisible(false);
		this.repaint();
	}
	
	public void pause() {
		try {
			player.pause();
		} catch (InterruptedException ie) {}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == start_tracking.getActionCommand()) {			
			startTracking();
		}
		else if(e.getActionCommand() == stop.getActionCommand()) {
			if(tw != null)
				// first acquire the semaphore
				try{
					stopper.acquire();
					// once we have semaphore, then tell the tracking window that it should
					// stop now
					finishedTracking();					
					tw.stopTracking();
					stopper.release();
				}catch(InterruptedException IE){}
		}
		else if(e.getActionCommand() == play.getActionCommand()) {
			pause.setVisible(true);
			play.setVisible(false);
			this.repaint();

			player = new PlayThread(this);
			play();
		}
		else if(e.getActionCommand() == pause.getActionCommand()) {
			play.setVisible(true);
			pause.setVisible(false);
			this.repaint();
			pause();
		}
	}

}
