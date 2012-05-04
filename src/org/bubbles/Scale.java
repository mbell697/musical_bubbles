package org.bubbles;

import java.util.ArrayList;
import java.util.Collections;

public class Scale {

	public String mName;
	private ArrayList<Note> mNotes;

	public Scale (String name) {
		mNotes = new ArrayList<Note>();
		mName = name;
	}
	
	//return a Note from the scale based on a radius
	public Note getNote(int rad) {
		int ind;
		ind = Collections.binarySearch(mNotes, new Note(rad));
		if (ind < 0) {
			ind = -(ind+1);
		}
		return mNotes.get(ind);
	}
	
	//notes MUST be added in musical order
	public void addNote(Note n)
	{
		mNotes.add(n);	
	}
	
	//must be called after all notes are added
	//this fills in the mapping of a bubble radius to each note
	//the mapping is logarithmic across the range of available notes
	public void mapNotes(int max_rad)
	{
		int num_notes = mNotes.size();
		double num_notes_d = (double)num_notes;  //avoids a ton of casts
		
		double sclr = (double)max_rad / Math.log(1.0/num_notes_d);
		
		for (int i = 0; i < num_notes; i++)
		{
			mNotes.get(i).rad = (int)Math.floor(sclr * Math.log( ((double)(i+1)) / num_notes_d));
		}	
		
		Collections.sort(mNotes);  //TODO this is always worst case, it always has to flip the entire order...
	}	
}
