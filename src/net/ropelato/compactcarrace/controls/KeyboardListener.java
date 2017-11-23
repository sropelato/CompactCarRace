package net.ropelato.compactcarrace.controls;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardListener extends KeyAdapter
{
	boolean[] keyDown = new boolean[1000];

	public void keyPressed(KeyEvent event)
	{
		keyDown[event.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent event)
	{
		keyDown[event.getKeyCode()] = false;
	}

	public boolean getKeyDown(int keyCode)
	{
		return keyDown[keyCode];
	}
}
