package org.bubbles;

import java.util.ArrayList;

import org.utils.ColorPickerDialog.OnColorChangedListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

public class DrawableView extends View implements OnColorChangedListener {

	private static final int MAX_BUBBLES = 20;
	private static final int MAX_RADIUS = 250;
	private static final int DEFAULT_COLOR = 0xFF097286;
	
	public Paint myPaint;
	
	private Context mContext;
	private ArrayList<Bubble> bubList;
	private Bubble bub;  //current working bubble, TODO: should be static in the drawing method?
	private boolean change;  //boolean, we only change/collision detect every other frame
	private SoundPool snd;
	private int sndID[] = new int[4];
	private ArrayList<Note> notes;
	public ArrayList<Scale> scales;
    private Scale mCurrentScale;


	public DrawableView(Context context) {
		super (context);
		mContext = context;
		
		myPaint = new Paint();
		myPaint.setStrokeWidth(2);
		myPaint.setColor(DEFAULT_COLOR);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setAntiAlias(true);
		
		change = true;
		
		bubList = new ArrayList<Bubble>(MAX_BUBBLES);
		notes = new ArrayList<Note>();
		scales = new ArrayList<Scale>();
		allocateSoundPool();
		buildNoteArray();
		buildScaleArray();
		mCurrentScale = scales.get(0);
	
	}
	@Override
	//main drawing code, the change variable is just a flip flop so that we only move / do collision checking
	//every other frame.
	protected void onDraw(Canvas canvas) {
		for (int i = 0;i<bubList.size();i++){
			bub = bubList.get(i);
			canvas.drawCircle(bub.cx, bub.cy, bub.rad, myPaint);
			if (this.change) {
				bub.change();
				if (bub.rad > MAX_RADIUS) bubList.remove(bub);
			}
		}
		if (this.change) {
			collisionTest();
			this.change = false;
		}
		else this.change = true;
		invalidate();
	}
	
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	//bubbles are created when you release from the touch screen
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (bubList.size() >= MAX_BUBBLES)
				bubList.remove(0);  //remove the oldest bubble to make room	
			
			bubList.add(new Bubble((int)event.getX(), (int)event.getY(), 1, this.getWidth()));
		}	
		return true;
	}
	
	private void collisionTest() {
		Bubble test;  //bubble under test
		Bubble cmp;   //bubble compared to test
		int d;        //distance between bubble centers
		Note n1;      //notes to be played when a collision occurs
		Note n2;
		
		for (int i=0;i < bubList.size();i++){
			test = bubList.get(i);	
			for (int j=i+1;j< bubList.size();j++){
				cmp = bubList.get(j);
				d = distToBubble(test, cmp); 
				if (test.rad + cmp.rad > d) {  //check if we're in collision range
					if (test.isGrowing && cmp.isGrowing) {  //only growing bubbles can collide
						if (test.rad < d && cmp.rad < d) {  //make sure we're not inside the other bubble, get too much ringing without this
							//collision occurred
							
							//send the bubbles in the other direction
							test.isGrowing = false;
							cmp.isGrowing = false;
							
							//search for a note based on each bubbles radius
							//TODO: Support multiple scales
							n1 = mCurrentScale.getNote(test.rad);
							n2 = mCurrentScale.getNote(cmp.rad);
							
							//play the notes
							snd.play(sndID[n1.sndIDindex], test.vol_right, test.vol_left, 0, 0, n1.spd);	
							snd.play(sndID[n2.sndIDindex], cmp.vol_right, cmp.vol_left, 0, 0, n2.spd);
						}
					}
				}
			}
		}	
	}
	
	//TODO this only really needs to be done on new bubbles, the result should be cached somewhere
	private static int distToBubble (Bubble a, Bubble b) {
		return (int)FloatMath.sqrt( (b.cx-a.cx)*(b.cx-a.cx) + (b.cy-a.cy)*(b.cy-a.cy)  );   	
	}

	public void clear() {
		bubList = new ArrayList<Bubble>(20);
	}
	
	public void releaseSoundPool() {
		if (snd != null) {
			snd.release();
			snd = null;
		}
	}
	
	public void allocateSoundPool() {
		if (snd == null) {
			snd = new SoundPool(MAX_BUBBLES,AudioManager.STREAM_MUSIC,0);
		
			sndID[0] = snd.load(mContext, R.raw.hz110_echo,0);
			sndID[1] = snd.load(mContext, R.raw.hz440_echo,0);
			sndID[2] = snd.load(mContext, R.raw.hz1760_echo,0);
			sndID[3] = snd.load(mContext, R.raw.hz7040_echo,0);
		}
	}

    //handles the callback from the color picker dialog
    public void colorChanged(int color) {
        myPaint.setColor(color);
    }

    public void setCurrentScale (Scale s) {
        this.mCurrentScale = s;
    }

    //contents of this function and the buildScaleArray function are dumped out of a python script
    //based on a pre-calculated spreadsheet
    private void buildNoteArray() {
        notes.add(new Note("A1",0,0.5f));
        notes.add(new Note("A#1",0,0.529732f));
        notes.add(new Note("B1",0,0.561231f));
        notes.add(new Note("C2",0,0.594604f));
        notes.add(new Note("C#2",0,0.629961f));
        notes.add(new Note("D2",0,0.66742f));
        notes.add(new Note("D#2",0,0.707107f));
        notes.add(new Note("E2",0,0.749154f));
        notes.add(new Note("F2",0,0.793701f));
        notes.add(new Note("F#2",0,0.840896f));
        notes.add(new Note("G2",0,0.890899f));
        notes.add(new Note("G#2",0,0.943874f));
        notes.add(new Note("A2",0,1f));
        notes.add(new Note("A#2",0,1.059463f));
        notes.add(new Note("B2",0,1.122462f));
        notes.add(new Note("C3",0,1.189207f));
        notes.add(new Note("C#3",0,1.259921f));
        notes.add(new Note("D3",0,1.33484f));
        notes.add(new Note("D#3",0,1.414214f));
        notes.add(new Note("E3",0,1.498307f));
        notes.add(new Note("F3",0,1.587401f));
        notes.add(new Note("F#3",0,1.681793f));
        notes.add(new Note("G3",0,1.781797f));
        notes.add(new Note("G#3",0,1.887749f));
        notes.add(new Note("A3",1,0.5f));
        notes.add(new Note("A#3",1,0.529732f));
        notes.add(new Note("B3",1,0.561231f));
        notes.add(new Note("C4",1,0.594604f));
        notes.add(new Note("C#4",1,0.629961f));
        notes.add(new Note("D4",1,0.66742f));
        notes.add(new Note("D#4",1,0.707107f));
        notes.add(new Note("E4",1,0.749154f));
        notes.add(new Note("F4",1,0.793701f));
        notes.add(new Note("F#4",1,0.840896f));
        notes.add(new Note("G4",1,0.890899f));
        notes.add(new Note("G#4",1,0.943874f));
        notes.add(new Note("A4",1,1f));
        notes.add(new Note("A#4",1,1.059463f));
        notes.add(new Note("B4",1,1.122462f));
        notes.add(new Note("C5",1,1.189207f));
        notes.add(new Note("C#5",1,1.259921f));
        notes.add(new Note("D5",1,1.33484f));
        notes.add(new Note("D#5",1,1.414214f));
        notes.add(new Note("E5",1,1.498307f));
        notes.add(new Note("F5",1,1.587401f));
        notes.add(new Note("F#5",1,1.681793f));
        notes.add(new Note("G5",1,1.781797f));
        notes.add(new Note("G#5",1,1.887749f));
        notes.add(new Note("A5",2,0.5f));
        notes.add(new Note("A#5",2,0.529732f));
        notes.add(new Note("B5",2,0.561231f));
        notes.add(new Note("C6",2,0.594604f));
        notes.add(new Note("C#6",2,0.629961f));
        notes.add(new Note("D6",2,0.66742f));
        notes.add(new Note("D#6",2,0.707107f));
        notes.add(new Note("E6",2,0.749154f));
        notes.add(new Note("F6",2,0.793701f));
        notes.add(new Note("F#6",2,0.840896f));
        notes.add(new Note("G6",2,0.890899f));
        notes.add(new Note("G#6",2,0.943874f));
        notes.add(new Note("A6",2,1f));
        notes.add(new Note("A#6",2,1.059463f));
        notes.add(new Note("B6",2,1.122462f));
        notes.add(new Note("C7",2,1.189207f));
        notes.add(new Note("C#7",2,1.259921f));
        notes.add(new Note("D7",2,1.33484f));
        notes.add(new Note("D#7",2,1.414214f));
        notes.add(new Note("E7",2,1.498307f));
        notes.add(new Note("F7",2,1.587401f));
        notes.add(new Note("F#7",2,1.681793f));
        notes.add(new Note("G7",2,1.781797f));
        notes.add(new Note("G#7",2,1.887749f));
        notes.add(new Note("A7",3,0.5f));
        notes.add(new Note("A#7",3,0.529732f));
        notes.add(new Note("B7",3,0.561231f));
        notes.add(new Note("C8",3,0.594604f));
        notes.add(new Note("C#8",3,0.629961f));
        notes.add(new Note("D8",3,0.66742f));
        notes.add(new Note("D#8",3,0.707107f));
        notes.add(new Note("E8",3,0.749154f));
        notes.add(new Note("F8",3,0.793701f));
        notes.add(new Note("F#8",3,0.840896f));
        notes.add(new Note("G8",3,0.890899f));
        notes.add(new Note("G#8",3,0.943874f));
        notes.add(new Note("A8",3,1f));
        notes.add(new Note("A#8",3,1.059463f));
        notes.add(new Note("B8",3,1.122462f));
        notes.add(new Note("C9",3,1.189207f));
        notes.add(new Note("C#9",3,1.259921f));
        notes.add(new Note("D9",3,1.33484f));
        notes.add(new Note("D#9",3,1.414214f));
        notes.add(new Note("E9",3,1.498307f));
        notes.add(new Note("F9",3,1.587401f));
        notes.add(new Note("F#9",3,1.681793f));
        notes.add(new Note("G9",3,1.781797f));
        notes.add(new Note("G#9",3,1.887749f));
        notes.add(new Note("A9",3,2f));
    }

    private void buildScaleArray() {
		Scale CMpentatonic = new Scale("C-Major Pentatonic");  
		CMpentatonic.addNote(notes.get(0));
		CMpentatonic.addNote(notes.get(3));
		CMpentatonic.addNote(notes.get(5));
		CMpentatonic.addNote(notes.get(7));
		CMpentatonic.addNote(notes.get(10));
		CMpentatonic.addNote(notes.get(12));
		CMpentatonic.addNote(notes.get(15));
		CMpentatonic.addNote(notes.get(17));
		CMpentatonic.addNote(notes.get(19));
		CMpentatonic.addNote(notes.get(22));
		CMpentatonic.addNote(notes.get(24));
		CMpentatonic.addNote(notes.get(27));
		CMpentatonic.addNote(notes.get(29));
		CMpentatonic.addNote(notes.get(31));
		CMpentatonic.addNote(notes.get(34));
		CMpentatonic.addNote(notes.get(36));
		CMpentatonic.addNote(notes.get(39));
		CMpentatonic.addNote(notes.get(41));
		CMpentatonic.addNote(notes.get(43));
		CMpentatonic.addNote(notes.get(46));
		CMpentatonic.addNote(notes.get(48));
		CMpentatonic.addNote(notes.get(51));
		CMpentatonic.addNote(notes.get(53));
		CMpentatonic.addNote(notes.get(55));
		CMpentatonic.addNote(notes.get(58));
		CMpentatonic.addNote(notes.get(60));
		CMpentatonic.addNote(notes.get(63));
		CMpentatonic.addNote(notes.get(65));
		CMpentatonic.addNote(notes.get(67));
		CMpentatonic.addNote(notes.get(70));
		CMpentatonic.addNote(notes.get(72));
		CMpentatonic.addNote(notes.get(75));
		CMpentatonic.addNote(notes.get(77));
		CMpentatonic.addNote(notes.get(79));
		CMpentatonic.addNote(notes.get(82));
		CMpentatonic.addNote(notes.get(84));
		CMpentatonic.addNote(notes.get(87));
		CMpentatonic.addNote(notes.get(89));
		CMpentatonic.addNote(notes.get(91));
		CMpentatonic.addNote(notes.get(94));
		CMpentatonic.addNote(notes.get(96));	
		CMpentatonic.mapNotes(MAX_RADIUS);		
		scales.add(CMpentatonic);
		
		Scale pentatonic = new Scale("Egyptian Pentatonic");  
		pentatonic.addNote(notes.get(0));
		pentatonic.addNote(notes.get(2));
		pentatonic.addNote(notes.get(5));
		pentatonic.addNote(notes.get(7));
		pentatonic.addNote(notes.get(10));
		pentatonic.addNote(notes.get(12));
		pentatonic.addNote(notes.get(14));
		pentatonic.addNote(notes.get(17));	
		pentatonic.addNote(notes.get(19));
		pentatonic.addNote(notes.get(22));
		pentatonic.addNote(notes.get(26));
		pentatonic.addNote(notes.get(29));
		pentatonic.addNote(notes.get(31));
		pentatonic.addNote(notes.get(34));
		pentatonic.addNote(notes.get(36));
		pentatonic.addNote(notes.get(38));
		pentatonic.addNote(notes.get(41));
		pentatonic.addNote(notes.get(43));
		pentatonic.addNote(notes.get(46));
		pentatonic.addNote(notes.get(48));	
		pentatonic.addNote(notes.get(50));
		pentatonic.addNote(notes.get(53));
		pentatonic.addNote(notes.get(55));
		pentatonic.addNote(notes.get(58));
		pentatonic.addNote(notes.get(60));
		pentatonic.addNote(notes.get(62));
		pentatonic.addNote(notes.get(65));
		pentatonic.addNote(notes.get(67));
		pentatonic.addNote(notes.get(70));
		pentatonic.addNote(notes.get(72));
		pentatonic.addNote(notes.get(74));
		pentatonic.addNote(notes.get(77));	
		pentatonic.addNote(notes.get(79));
		pentatonic.addNote(notes.get(82));
		pentatonic.addNote(notes.get(84));
		pentatonic.addNote(notes.get(86));
		pentatonic.addNote(notes.get(89));
		pentatonic.addNote(notes.get(91));
		pentatonic.addNote(notes.get(94));
		pentatonic.addNote(notes.get(96));
		pentatonic.mapNotes(MAX_RADIUS);		
		scales.add(pentatonic);		
		
		Scale minorPentatonic = new Scale("Minor Pentatonic");  
		minorPentatonic.addNote(notes.get(1));
		minorPentatonic.addNote(notes.get(3));
		minorPentatonic.addNote(notes.get(6));
		minorPentatonic.addNote(notes.get(8));
		minorPentatonic.addNote(notes.get(10));
		minorPentatonic.addNote(notes.get(13));
		minorPentatonic.addNote(notes.get(15));
		minorPentatonic.addNote(notes.get(18));
		minorPentatonic.addNote(notes.get(20));
		minorPentatonic.addNote(notes.get(22));
		minorPentatonic.addNote(notes.get(25));
		minorPentatonic.addNote(notes.get(27));
		minorPentatonic.addNote(notes.get(30));
		minorPentatonic.addNote(notes.get(32));
		minorPentatonic.addNote(notes.get(34));
		minorPentatonic.addNote(notes.get(37));
		minorPentatonic.addNote(notes.get(39));
		minorPentatonic.addNote(notes.get(42));
		minorPentatonic.addNote(notes.get(44));
		minorPentatonic.addNote(notes.get(46));
		minorPentatonic.addNote(notes.get(49));
		minorPentatonic.addNote(notes.get(51));
		minorPentatonic.addNote(notes.get(54));
		minorPentatonic.addNote(notes.get(56));
		minorPentatonic.addNote(notes.get(58));
		minorPentatonic.addNote(notes.get(61));
		minorPentatonic.addNote(notes.get(63));
		minorPentatonic.addNote(notes.get(66));
		minorPentatonic.addNote(notes.get(68));
		minorPentatonic.addNote(notes.get(70));
		minorPentatonic.addNote(notes.get(73));
		minorPentatonic.addNote(notes.get(75));
		minorPentatonic.addNote(notes.get(78));
		minorPentatonic.addNote(notes.get(80));
		minorPentatonic.addNote(notes.get(82));
		minorPentatonic.addNote(notes.get(85));
		minorPentatonic.addNote(notes.get(87));
		minorPentatonic.addNote(notes.get(90));
		minorPentatonic.addNote(notes.get(92));
		minorPentatonic.addNote(notes.get(94));
		minorPentatonic.mapNotes(MAX_RADIUS);		
		scales.add(minorPentatonic);		
			
		Scale wholeTone = new Scale("Whole Tone");  //Whole C0 Whole Tone
		wholeTone.addNote(notes.get(1));
		wholeTone.addNote(notes.get(3));
		wholeTone.addNote(notes.get(5));
		wholeTone.addNote(notes.get(7));
		wholeTone.addNote(notes.get(9));
		wholeTone.addNote(notes.get(11));
		wholeTone.addNote(notes.get(13));
		wholeTone.addNote(notes.get(15));
		wholeTone.addNote(notes.get(17));
		wholeTone.addNote(notes.get(19));
		wholeTone.addNote(notes.get(21));
		wholeTone.addNote(notes.get(23));
		wholeTone.addNote(notes.get(25));
		wholeTone.addNote(notes.get(27));
		wholeTone.addNote(notes.get(29));
		wholeTone.addNote(notes.get(31));
		wholeTone.addNote(notes.get(33));
		wholeTone.addNote(notes.get(35));
		wholeTone.addNote(notes.get(37));
		wholeTone.addNote(notes.get(39));
		wholeTone.addNote(notes.get(41));
		wholeTone.addNote(notes.get(43));
		wholeTone.addNote(notes.get(45));
		wholeTone.addNote(notes.get(47));
		wholeTone.addNote(notes.get(49));
		wholeTone.addNote(notes.get(51));
		wholeTone.addNote(notes.get(53));
		wholeTone.addNote(notes.get(55));
		wholeTone.addNote(notes.get(57));
		wholeTone.addNote(notes.get(59));
		wholeTone.addNote(notes.get(61));
		wholeTone.addNote(notes.get(63));
		wholeTone.addNote(notes.get(65));
		wholeTone.addNote(notes.get(67));
		wholeTone.addNote(notes.get(69));
		wholeTone.addNote(notes.get(71));
		wholeTone.addNote(notes.get(73));
		wholeTone.addNote(notes.get(75));
		wholeTone.addNote(notes.get(77));
		wholeTone.addNote(notes.get(79));
		wholeTone.addNote(notes.get(81));
		wholeTone.addNote(notes.get(83));
		wholeTone.addNote(notes.get(85));
		wholeTone.addNote(notes.get(87));
		wholeTone.addNote(notes.get(89));
		wholeTone.addNote(notes.get(91));
		wholeTone.addNote(notes.get(93));
		wholeTone.addNote(notes.get(95));	
		wholeTone.mapNotes(MAX_RADIUS);		
		scales.add(wholeTone);
	}
}
