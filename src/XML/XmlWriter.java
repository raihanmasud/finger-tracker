/* ImageStackWorker.java
 * CIS 423/510 VISUAL TEAM
 * 
 * This class is in write the data into xml file on real time. 
 *   
 */

package XML;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.*; 

public class XmlWriter {
	private FileOutputStream fos = null;
	private String videoPath;
	private static String LineSep = System.getProperty("line.separator");
	private double timeDelay = 0;

	public XmlWriter(String videoPath, String date) {
		try {
			//create output file
			this.fos = new FileOutputStream("./data/" + getFileName(videoPath));
			this.videoPath = videoPath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + LineSep;
		String root = "<coordStream StartDateTime=\"" + date + "\">" + LineSep;
		try {
			fos.write(head.getBytes("UTF-8"));
			fos.write(root.getBytes("UTF-8"));
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//get the time delay between frames in microseconds
	public double getTimeDelay (String videoPath){
		try {
			//read the video file
			FileInputStream fis = new FileInputStream(videoPath);
			byte[] buffer = new byte[4];
			fis.skip(32);
			fis.read(buffer);
			this.timeDelay = byteToInt(buffer);
			fis.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return (timeDelay/1000);
	}
	
	public static int byteToInt(byte[] buffer) throws Exception {
		if (buffer == null || buffer.length != 4) {
			throw new Exception("The avi header is wrong!");
		}
		int result = 0;
		for (int i = 3; i >= 0; i--) {
			result = result | ((buffer[i] & 0xff) << (i * 8));
		}
		return result;
	}
 
	//get videoname from videopath
	private String getFileName(String videoPath) {
		if (videoPath == null || videoPath.length() == 0) {
			return null;
		}
		videoPath = videoPath.replace('\\', '/');
		int index1 = videoPath.lastIndexOf('/');
		int index2 = videoPath.lastIndexOf(".");
		if (index1 < 0) {
			index1 = -1;
		}
		index1++;
		if (index2 < 0) {
			index2 = videoPath.length() - 1;
		}
		return videoPath.substring(index1, index2) + ".xml";
	}

	//output </coordStream> at the end of the xml file
	public void OutputXML() {
		String info = "</coordStream>" + LineSep;
		try {
			fos.write(info.getBytes("UTF-8"));
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//add element event as <event fingerID="1" time="33.338" X="100" Y="200" />
	public void AddEvent(String FId, int frameID, int X, int Y) {
		double t = frameID*getTimeDelay(videoPath);
		BigDecimal temp = new BigDecimal(t);   
		double time =temp.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		String info = "  <event fingerID=\"" + FId + "\" time=\"" + time
						+ "\" X=\"" + X + "\" Y=\"" + Y + "\" />" + LineSep;
		try {
			fos.write(info.getBytes("UTF-8"));
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}