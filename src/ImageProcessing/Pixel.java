/* Pixel.java
 * CIS 423/510 VISUAL TEAM
 * 
 * A pixel located at (x, y), and is color c */

package ImageProcessing;
import java.awt.Color;

public class Pixel {

	private int x;
	private int y;
	Color color;
	
	public Pixel(int x_coord, int y_coord, Color c){
		x = x_coord;
		y = y_coord;
		color = c;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	
	public Color getColor(){
		return color;
	}
	
}
