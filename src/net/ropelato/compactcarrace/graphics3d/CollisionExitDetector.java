package net.ropelato.compactcarrace.graphics3d;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;

public class CollisionExitDetector extends Behavior
{
	private WakeupOnCollisionExit wExit;

	Model model;

	public CollisionExitDetector(Model model)
	{
		this.model = model;
	}

	public BranchGroup getModel()
	{
		return model;
	}

	public void initialize()
	{
		wExit = new WakeupOnCollisionExit(model, WakeupOnCollisionEntry.USE_GEOMETRY);
		wakeupOn(wExit);
	}

	public void processStimulus(Enumeration e)
	{
		model.getCollidingObjects().remove(wExit.getTriggeringPath().getObject());
		wakeupOn(wExit);
	}
}
