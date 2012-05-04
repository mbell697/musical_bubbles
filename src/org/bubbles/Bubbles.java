package org.bubbles;

import org.utils.ColorPickerDialog;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public class Bubbles extends Activity {
    
	private final int MENU_CLEAR=100;
    private final int MENU_COLOR=101;
    private final int MENU_QUIT=102;
    private final int MENU_STYLE=103;
    
    
	private DrawableView v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Debug.startMethodTracing("bubbles");
        v = new DrawableView(this);
        setContentView(v);  
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	v.releaseSoundPool();
    	    	
    }
    
    public void onStart() {
    	super.onStart();
    	v.allocateSoundPool();	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0,MENU_COLOR,0,"Bubble Color").setIcon(R.drawable.ic_menu_edit);
    	SubMenu styleMenu = menu.addSubMenu(0,MENU_STYLE,0,"Style");
    	
    	menu.add(0,MENU_CLEAR, 0, "Clear").setIcon(R.drawable.ic_menu_delete);
    	menu.add(0,MENU_QUIT,0,"Quit").setIcon(R.drawable.ic_menu_close_clear_cancel);
    	
    	for (int i = 0; i < v.scales.size();i++ ) {
    		styleMenu.add(1, i, 0, v.scales.get(i).mName);
    	}
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getGroupId() == 1)  {  //Style Sub Menu
    		v.setCurrentScale(v.scales.get(item.getItemId()));
    		return true;
    	}
    
    	switch (item.getItemId()) {	
    		case MENU_CLEAR:
    			this.v.clear();
    			return true;
    		case MENU_COLOR:
    			new ColorPickerDialog(this,v,v.myPaint.getColor()).show();
    			return true;
    		case MENU_QUIT:
    			this.setResult(RESULT_OK);
    			this.finish();
    			return true;
    		}
    	return false; 	
    }
    
    
    
    
    
    
}