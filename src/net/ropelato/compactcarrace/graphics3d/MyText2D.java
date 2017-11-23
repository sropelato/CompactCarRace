package net.ropelato.compactcarrace.graphics3d;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import net.ropelato.compactcarrace.util.Util;

import com.sun.j3d.utils.geometry.Text2D;

public class MyText2D extends BranchGroup
{
	TransformGroup transformGroup;
	Transform3D transform3D = new Transform3D();
	PointLight pointLight;
	BoundingSphere bounds;

	float rotationX = 0f;
	float rotationY = 0f;
	float rotationZ = 0f;
	float positionX = 0f;
	float positionY = 0f;
	float positionZ = 0f;

	float scale = 1f;

	public MyText2D(String text, Color color, String font, int style, int size)
	{
		Appearance appearance = new Appearance();
		Material material = new Material();
		material.setEmissiveColor(new Color3f(color));
		appearance.setMaterial(material);

		Text2D text2D = new Text2D(text, new Color3f(color), font, size, style);

		transformGroup = new TransformGroup(transform3D);
		transformGroup.addChild(text2D);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		addChild(transformGroup);
	}

	public void setRotationX(float rotationX)
	{
		Transform3D transformX = new Transform3D();
		transformX.rotX(Math.toRadians(rotationX - this.rotationX));
		transform3D.mul(transformX);

		this.rotationX = rotationX;
	}

	public void setRotationY(float rotationY)
	{
		Transform3D transformY = new Transform3D();
		transformY.rotY(Math.toRadians(rotationY - this.rotationY));
		transform3D.mul(transformY);

		this.rotationY = rotationY;
	}

	public void setRotationZ(float rotationZ)
	{
		Transform3D transformZ = new Transform3D();
		transformZ.rotZ(Math.toRadians(rotationZ - this.rotationZ));
		transform3D.mul(transformZ);

		this.rotationZ = rotationZ;
	}

	public void setRotation(float rotationX, float rotationY, float rotationZ)
	{
		Transform3D transformX = new Transform3D();
		Transform3D transformY = new Transform3D();
		Transform3D transformZ = new Transform3D();

		transformX.rotX(Math.toRadians(rotationX - this.rotationX));
		transform3D.mul(transformX);
		transformY.rotY(Math.toRadians(rotationY - this.rotationY));
		transform3D.mul(transformY);
		transformZ.rotZ(Math.toRadians(rotationZ - this.rotationZ));
		transform3D.mul(transformZ);

		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;
	}

	public void setPositionX(float positionX)
	{
		this.positionX = positionX;
		transform3D.setTranslation(new Vector3d(positionX, positionY, positionZ));
	}

	public void setPositionY(float positionY)
	{
		this.positionY = positionY;
		transform3D.setTranslation(new Vector3d(positionX, positionY, positionZ));
	}

	public void setPositionZ(float positionZ)
	{
		this.positionZ = positionZ;
		transform3D.setTranslation(new Vector3d(positionX, positionY, positionZ));
	}

	public void setPosition(float positionX, float positionY, float positionZ)
	{
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		transform3D.setTranslation(new Vector3d(positionX, positionY, positionZ));
	}

	public void setScale(float scale)
	{
		this.scale = scale;
		transform3D.setScale(scale);
	}

	public void restore()
	{
		setScale(1f);
		setRotation(0f, 0f, 0f);
		setPosition(0f, 0f, 0f);
	}

	public float getPositionX()
	{
		return positionX;
	}

	public float getPositionY()
	{
		return positionY;
	}

	public float getPositionZ()
	{
		return positionZ;
	}

	public float getRotationX()
	{
		return rotationX;
	}

	public float getRotationY()
	{
		return rotationY;
	}

	public float getRotationZ()
	{
		return rotationZ;
	}

	public float getScale()
	{
		return scale;
	}

	public void update()
	{
		rotationX = Util.modifyAngle(rotationX);
		rotationY = Util.modifyAngle(rotationY);
		rotationZ = Util.modifyAngle(rotationZ);
		transformGroup.setTransform(transform3D);
	}

}
