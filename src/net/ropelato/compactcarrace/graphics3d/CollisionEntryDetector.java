package net.ropelato.compactcarrace.graphics3d;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.WakeupOnCollisionEntry;

public class CollisionEntryDetector extends Behavior
{
	private WakeupOnCollisionEntry wEntry;

	Model model;

	public CollisionEntryDetector(Model model)
	{
		this.model = model;
	}

	public BranchGroup getModell()
	{
		return model;
	}

	public void initialize()
	{
		wEntry = new WakeupOnCollisionEntry(model, WakeupOnCollisionEntry.USE_GEOMETRY);
		wakeupOn(wEntry);
	}

	public void processStimulus(Enumeration e)
	{
		model.getCollidingObjects().add(wEntry.getTriggeringPath().getObject());
		wakeupOn(wEntry);
	}
}
