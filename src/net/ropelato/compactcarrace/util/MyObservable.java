package net.ropelato.compactcarrace.util;

import java.util.Observable;

public class MyObservable extends Observable
{
	public void update()
	{
		setChanged();
		notifyObservers();
	}

	public void update(Object object)
	{
		setChanged();
		notifyObservers(object);
	}
}
