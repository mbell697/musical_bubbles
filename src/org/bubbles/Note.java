package org.bubbles;


//Class defines a musical Note
public class Note implements Comparable<Note> {

	public float freq;  //frequency of the note, currently unused
	public int sndIDindex;   //soundID of the sound sample needed to produce this note 
	public float spd;   //speed offset needed to produce this note with sample
	public int rad;     //cut off radius for this note
	public String name; //name of the note, C0 or C0# for example
	
	Note(String name, int Sindex, float speed) {
		this.sndIDindex = Sindex;
		this.spd = speed;
		this.name = name;
	}
	
	Note (int rad)  {
		this.rad = rad;
	}

	public int compareTo(Note another) {
		
		if (this.rad > another.rad)
			return 1;
		else if (this.rad < another.rad)
			return -1;
		else
			return 0;
	}
	
	
	
}
