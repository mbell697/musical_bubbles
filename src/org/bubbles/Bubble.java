package org.bubbles;

//Storage for the the state of a bubble
public class Bubble {

	private final int BUBBLE_GROWTH_FACTOR = 1;
	private final int BUBBLE_MINIMUM_SIZE = 1;
	
	public int cx,cy,rad;
	public boolean isGrowing;
	public float vol_left,vol_right;
	
	public Bubble (int x,int y,int r,int scr_width) {
		this.cx = x;
		this.cy = y;
		this.rad = r;
		this.isGrowing = true;
		
		float ctr = scr_width/2;
		
		//calculate a left/right volume based on the Bubble's center.
		if (x > ctr) {
			vol_right = 1.0f;
			vol_left = 1.0f - ((float)cx - ctr) / ctr;
		}
		else {
			vol_left = 1.0f;
			vol_right = (float)cx / ctr;
		}
		
	}

	//called when the bubble needs to grow or shrink
	public void change()
	{
		if (isGrowing) { this.rad += BUBBLE_GROWTH_FACTOR;}
		else { this.rad -= BUBBLE_GROWTH_FACTOR;}
		
		if (this.rad < BUBBLE_MINIMUM_SIZE) {
			this.rad = BUBBLE_MINIMUM_SIZE;
			this.isGrowing = true;
		}		
	}
}
