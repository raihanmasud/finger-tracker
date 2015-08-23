package TrackingWindow;

public class SelectedFinger {
	private int X;
	private int Y;
	private int ID;
	
	public SelectedFinger(int x, int y, int id) {
		X = x;
		Y = y;
		ID = id;
	}
	
	public int getX() {
		return X;
	}
	
	public int getY() {
		return Y;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setXY(int x, int y){
		X = x;
		Y = y;
	}

}
