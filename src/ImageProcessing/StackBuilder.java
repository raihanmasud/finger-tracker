/* StackBuilder.java
 * CIS 423/510 VISUAL TEAM
 * 
 * This uses ImageJ's AVI_Reader class to read in an uncompressed AVI
 * movie, and then creates an ImageJ ImageStack, which is bassically a 
 * list of frames */

package ImageProcessing;
import ij.ImageStack;
import ij.plugin.AVI_Reader;

public class StackBuilder {
	
	AVI_Reader avi_r;
	ImageStack is;
	String path;
	
	// makes a stack from the given AVI movie lacated at "path"
	public StackBuilder(String spath){
		path = spath;
		avi_r = new AVI_Reader();
		makeStack();
	}
	
	public void makeStack(){
		is = avi_r.makeStack(path, 1, avi_r.getSize(), true, false, false);
	}
	
	public ImageStack getStack(){
		return is;
	}
	
}

