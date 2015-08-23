/* ControlFrame.java
 * CIS 423/510 VISUAL TEAM
 * 
 * The MainFrame will open a movie and start a new TrackingWindow
 * for that movie. 
 * This is the main UI component which sends messages and information
 * to everything else. It contains a FingerID map to show the user
 * the finger indexing rules, and also contains a consol window to display
 * information to the user.
 */


package MainUI;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;


import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.sun.org.apache.bcel.internal.generic.IREM;


import TrackingWindow.TrackingWindow;
import XML.XmlWriter;

public class MainFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int orig_height;
	private JFileChooser jfc;
	private JFrame cf;
	private Vector<TrackingWindow> tw;
	private FingerIDMap finger_map;
	private boolean cancle;
	private boolean clear_console;
	private JMenuBar menuBar;
	private JMenu file_menu;
	private JMenu view_menu;
	private JMenu help_menu;
	private JMenuItem item_open ;
	private JMenuItem item_save_xml ; 
	private JMenuItem item_proc_all;
	private JCheckBoxMenuItem item_console ;
	private JCheckBoxMenuItem item_finger_map ;
	private JCheckBoxMenuItem item_clear_console;
	private JMenuItem item_tech_doc ;
	private JMenuItem item_user_doc ;
	private JMenu item_tutorial ;
	private JMenuItem item_tutorial_vid ;
	private JMenuItem item_tutorial_text ;
	
	private JMenuItem item_exit;
	
	
	private Consol cw;
	private JScrollPane areaScrollPane;
	
	
	public MainFrame(){
		tw = new Vector<TrackingWindow>();
		finger_map = new FingerIDMap("./res/FingerIndexsmall.jpg");
		
		item_open =  new JMenuItem("Open");
		item_open.addActionListener(this);
		
		item_proc_all = new JMenuItem("Process All");
		item_proc_all.addActionListener(this);
		
		item_exit = new JMenuItem("Exit");
		item_exit.addActionListener(this);
		
		
		
		
		item_clear_console = new JCheckBoxMenuItem("Clear Console");
		item_clear_console.addActionListener(this);
		
		item_console =  new JCheckBoxMenuItem("Console");
		item_console.setSelected(true);
		item_console.addActionListener(this);
		
		item_finger_map =  new JCheckBoxMenuItem("Finger Map");
		item_finger_map.setSelected(true);
		item_finger_map.addActionListener(this);
		
	
		item_tutorial = new JMenu("Tutorial");
		item_tutorial_vid = new JMenuItem("Video");
		item_tutorial_text = new JMenuItem("Text");
		item_tutorial_vid.addActionListener(this);
		item_tutorial_text.addActionListener(this);
		item_tutorial.add(item_tutorial_vid);
		item_tutorial.add(item_tutorial_text);
				
		item_user_doc = new JMenuItem ("User Doc");
		item_user_doc.addActionListener(this);

		
		item_tech_doc = new JMenuItem ("Tech Doc");
		item_tech_doc.addActionListener(this);
		
		file_menu=new JMenu("File");
		view_menu=new JMenu("View");
		help_menu=new JMenu("Help");
		
	    file_menu.add(item_open);
	    file_menu.add(item_proc_all);
	    file_menu.add(item_exit);
	    
	    
	    
	    view_menu.add(item_console);
	    view_menu.add(item_finger_map);
	    view_menu.add(item_clear_console);
	    
	    help_menu.add(item_tutorial);
	    help_menu.add(item_user_doc);
	    help_menu.add(item_tech_doc);
	    
	    menuBar = new JMenuBar();
	    
		menuBar.add(file_menu);
		menuBar.add(view_menu);
		menuBar.add(help_menu);
	    
		JTextArea infowin = new JTextArea();
		infowin.setEditable(false);
		areaScrollPane = new JScrollPane(infowin);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		cw = new Consol(infowin);
        
		cf = new JFrame();		
		jfc = new JFileChooser();
	}
	
	public void run(){
		setUp();
	}
	
	public void setUp(){
		cf.setTitle("Finger Tracker");
		cf.add(finger_map, BorderLayout.WEST);
		cf.add(areaScrollPane, BorderLayout.EAST);
		cf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cf.setVisible(true);
		
		jfc.setCurrentDirectory(new File("."));
		jfc.addActionListener(this);
		jfc.setMultiSelectionEnabled(true);
		cf.setLocation(100, 100);
		cf.setJMenuBar(menuBar);
		clear_console= false;
		
		
		cw.writeToWin("1. Choose Video to track Finger from \"File\" > Open\n" +
				"2. Select finger from combo box\n" +
				"3. Click on the particular Finger tip to track from Image\n" +
				"4. If you are advanced user \"Set Threshold\" otherwise keep the default settings for threshold\n" +
				"5. Hit \"Go\" for start finger tracking\n" +
				"6. Click \"Play\" to view tracked finger centroids of your video.");
		
		cf.pack();
		orig_height = cf.getHeight();
		cf.setResizable(false);
		//fixWindowSize();
	}
	
	private void refreshConsolText() {
		cw.clearWin();
		cw.writeToWin("1. Choose Video to track Finger from \"File\" > Open\n" +
				"2. Select finger from combo box\n" +
				"3. Click on the particular Finger tip to track from Image\n" +
				"4. If you are advanced user \"Set Threshold\" otherwise keep the default settings for threshold\n" +
				"5. Hit \"Go\" for start finger tracking\n" +
				"6. Click \"Play\" to view tracked finger centroids of your video.");
	}
	
	private void openWindow(){
		refreshConsolText();
		File [] movies;
		cancle= false;
		
		jfc.showOpenDialog(cf);
		movies = jfc.getSelectedFiles();
		
		for(int i = 0; i < movies.length; ++i){
				if(movies[i] == null || cancle)
					return;
			
			// start a new tracking window with the selected file
			TrackingWindow tracker_win = new TrackingWindow(movies[i].getPath(), new Semaphore(1));
			tracker_win.setConsolWin(cw);
			tracker_win.start();
			tw.add(tracker_win);
		}
	}
	
	private void fixWindowSize() {
		cf.pack();

		if(!areaScrollPane.isVisible() && !finger_map.isVisible()) {
			cf.setSize(300, 100);
		}
		else {
			cf.setSize(cf.getWidth(), orig_height);
		}
	}
	
    private static void openURL(String url) {
    	String errMsg = "Error attempting to launch web browser";
    	String osName = System.getProperty("os.name");

        try {
        	if (osName.startsWith("Mac OS")) {
        		Runtime.getRuntime().exec( "open " + url);
        	}

        	else if (osName.startsWith("Windows"))
        		Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);

        	else { //assume Unix or Linux
        		String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
        		String browser = null;


        		for (int count = 0; count < browsers.length && browser == null; count++)
        			if (Runtime.getRuntime().exec(
        					new String[] {"which", browsers[count]}).waitFor() == 0)
        						browser = browsers[count];

        			if (browser == null)
        				throw new Exception("Could not find web browser");
        			else
        				Runtime.getRuntime().exec(new String[] {browser, url});
        	}
        }
        catch (Exception e) {
        	JOptionPane.showMessageDialog(null, errMsg + ":\n" + e.getLocalizedMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand() == item_open.getActionCommand()){
                openWindow();
        }
        else if(e.getActionCommand() == item_proc_all.getActionCommand()){
           // go through all the tracking windows and tell them to all start tracking
                for(TrackingWindow tracking: tw) {
                        if(tracking.notTrackedYet())
                                tracking.getTrackingControls().startTracking();
                }
        }
        
       
        
        else if(e.getActionCommand() == item_console.getActionCommand()) {
                // toggle view of the info window
                if(areaScrollPane.isVisible())
                        areaScrollPane.setVisible(false);
                else
                        areaScrollPane.setVisible(true);
                fixWindowSize();
        }
        else if(e.getActionCommand() == item_finger_map.getActionCommand()) {
                // toggle the view of the ID map and then fix the window size
                finger_map.toggleView();
                fixWindowSize();
                
        }
        
        else if (e.getActionCommand() ==  item_clear_console.getActionCommand()) {
        
                ///toggle clear console
                if(!clear_console)
                        {
                        cw.clearWin();
                        clear_console = true;
                        }
                
                
                else{
                
                        refreshConsolText();
                        clear_console= false;
                }
                
        }
        else if (e.getActionCommand() == item_exit.getActionCommand()) {
                Runtime.getRuntime().exit(0);
        }
        else if (e.getActionCommand() == item_tutorial_vid.getActionCommand()) {
                File f = new File("./docs/VideoTutorial.swf");
                try {
                        openURL(f.toURL().toString());
                } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                }
        }
        
        else if (e.getActionCommand() == item_tutorial_text.getActionCommand()) {
            File f = new File("./docs/Tutorial.txt");
            try {
                    openURL(f.toURL().toString());
            } catch (MalformedURLException e1) {
                    e1.printStackTrace();
            }
    }
        
        else if (e.getActionCommand() == item_tech_doc.getActionCommand()) {
            File f = new File("./docs/TechDoc.html");
            try {
                    openURL(f.toURL().toString());
            } catch (MalformedURLException e1) {
                    e1.printStackTrace();
            }
    }
        
        
        else if (e.getActionCommand() == item_user_doc.getActionCommand()) {
        File f = new File("./docs/UserDoc.html");
        try {
                openURL(f.toURL().toString());
        } catch (MalformedURLException e1) {
                e1.printStackTrace();
        }}
        else if(e.getActionCommand() == JFileChooser.CANCEL_SELECTION){
                // then do not open the file
                cancle = true;
        }
    }
}
