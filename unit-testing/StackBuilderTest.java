

package UnitTesting;

import static org.junit.Assert.*;

import org.junit.Test;

import ImageProcessing.StackBuilder;



/**
 * @author Raihan
 * Testing StackBuilder Class 
 * to test whether it can build Stack of frames correctly  
 */
public class StackBuilderTest {

	
	//Null object creation testing 
	@Test
	public void StackBuilderObjectTest () throws Exception
	{
	
		StackBuilder sb = new StackBuilder("");
		StackBuilder tw2input = new StackBuilder("./res/videos/2fing50slice7fps.avi");
		assertNotNull(sb); //fails , OK
		assertNotNull(tw2input); //pass
	
	}
	
}
