package net.ropelato.compactcarrace.cars;

import net.ropelato.compactcarrace.graphics2d.PaintComponent;
import net.ropelato.compactcarrace.util.Util;

import javax.media.j3d.J3DGraphics2D;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.io.File;

public class Tacho implements PaintComponent
{
	Image tacho = null;
	Image tachoPointer = null;
	AffineTransform pointerTransform = new AffineTransform();
	ImageObserver imageObserver = null;
	int positionX = 0;
	int positionY = 0;

	public Tacho(String tachoImage, String pointerImage)
	{
		tacho = Util.loadImage(new File(tachoImage));
		tachoPointer = Util.loadImage(new File(pointerImage));

		pointerTransform = new AffineTransform();
	}

	public void rotatePointer(float angle)
	{
		if(tachoPointer != null)
		{
			pointerTransform.setToTranslation(positionX, positionY);
			pointerTransform.rotate(Math.toRadians(angle), tachoPointer.getWidth(imageObserver) / 2, tachoPointer.getHeight(imageObserver) / 2);
		}
	}

	public void paint(J3DGraphics2D g)
	{
		g.drawImage(tacho, positionX, positionY, imageObserver);
		g.drawImage(tachoPointer, pointerTransform, imageObserver);
	}

	public int getPositionX()
	{
		return positionX;
	}

	public void setPositionX(int positionX)
	{
		this.positionX = positionX;
	}

	public int getPositionY()
	{
		return positionY;
	}

	public void setPositionY(int positionY)
	{
		this.positionY = positionY;
	}

	public ImageObserver getImageObserver()
	{
		return imageObserver;
	}

	public void setImageObserver(ImageObserver imageObserver)
	{
		this.imageObserver = imageObserver;
	}
}
