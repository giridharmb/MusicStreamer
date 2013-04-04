package com.media.play;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {
	
	private boolean ALT_PRESSED = false;
	private boolean SHIFT_PRESSED = false;
	private boolean N_PRESSED = false;
	private boolean P_PRESSED = false;
	private boolean R_PRESSED = false;
	private boolean S_PRESSED = false;
	private boolean V_PRESSED = false;
	
	public GlobalKeyListener() {
		
	}
	
    public void nativeKeyPressed(NativeKeyEvent e) {
    	
            System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()) );
            
            if( NativeKeyEvent.getKeyText(e.getKeyCode()) != null ) {
            	if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("Alt")) {
            		ALT_PRESSED = true;
            	}
            }
            
            if( NativeKeyEvent.getKeyText(e.getKeyCode()) != null ) {
            	if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("Shift")) {
            		SHIFT_PRESSED = true;
            	}
            }
            
            if( NativeKeyEvent.getKeyText(e.getKeyCode()) != null ) {
            	if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("N")) {
            		N_PRESSED = true;
            	}
            }
            
            if( NativeKeyEvent.getKeyText(e.getKeyCode()) != null ) {
            	if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("P")) {
            		P_PRESSED = true;
            	}
            }
            
            if( NativeKeyEvent.getKeyText(e.getKeyCode()) != null ) {
            	if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("R")) {
            		R_PRESSED = true;
            	}
            }
            
            if( NativeKeyEvent.getKeyText(e.getKeyCode()) != null ) {
            	if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("V")) {
            		V_PRESSED = true;
            	}
            }
            
            if( NativeKeyEvent.getKeyText(e.getKeyCode()) != null ) {
            	if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("S")) {
            		S_PRESSED = true;
            	}
            }
            
            if ( SHIFT_PRESSED == true && ALT_PRESSED == true && N_PRESSED == true) {
            	System.out.println("Key Combo pressed to play next track !");
            	Main.PLAYER_STATE = Main.S_NEXT;
            	ALT_PRESSED = false;
            	SHIFT_PRESSED = false;
            	N_PRESSED = false;
            }
            
            if ( SHIFT_PRESSED == true && ALT_PRESSED == true && P_PRESSED == true) {
            	System.out.println("Key Combo pressed to play track !");
            	Main.PLAYER_STATE = Main.S_PLAY;
            	ALT_PRESSED = false;
            	SHIFT_PRESSED = false;
            	P_PRESSED = false;
            }
            
            if ( SHIFT_PRESSED == true && ALT_PRESSED == true && R_PRESSED == true) {
            	System.out.println("Key Combo pressed to play random track !");
            	Main.PLAYER_STATE = Main.S_RANDOM;
            	ALT_PRESSED = false;
            	SHIFT_PRESSED = false;
            	R_PRESSED = false;
            }
            	
            if ( SHIFT_PRESSED == true && ALT_PRESSED == true && S_PRESSED == true) {
            	System.out.println("Key Combo pressed to stop track !");
            	Main.PLAYER_STATE = Main.S_STOP;
            	ALT_PRESSED = false;
            	SHIFT_PRESSED = false;
            	S_PRESSED = false;
            }
            
            if ( SHIFT_PRESSED == true && ALT_PRESSED == true && V_PRESSED == true) {
            	System.out.println("Key Combo pressed to play previous track !");
            	Main.PLAYER_STATE = Main.S_PREVIOUS;
            	ALT_PRESSED = false;
            	SHIFT_PRESSED = false;
            	V_PRESSED = false;
            }

            if (e.getKeyCode() == NativeKeyEvent.VK_ESCAPE) {
                    GlobalScreen.unregisterNativeHook();
            }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
            System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
            System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
    }

    public void setup() {
            try {
                    GlobalScreen.registerNativeHook();
            }
            catch (NativeHookException ex) {
                    System.err.println("There was a problem registering the native hook.");
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
            }

            //Construct the example object and initialze native hook.
            GlobalScreen.getInstance().addNativeKeyListener(new GlobalKeyListener());
    }
}