package net.ropelato.compactcarrace.graphics3d;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;

import net.ropelato.compactcarrace.main.Main;

import com.sun.j3d.utils.image.TextureLoader;

public class Terrain extends BranchGroup
{
	int xFields = 0;
	int zFields = 0;

	TransformGroup transformGroup = null;
	Transform3D transform3D = null;

	Point3f[] points = null;
	Shape3D shape3D = null;

	float xMove = 0f;
	float yMove = 0f;
	float zMove = 0f;

	float xScale = 1f;
	float yScale = 1f;
	float zScale = 1f;

	float xMin = 0f;
	float xMax = 0f;
	float zMin = 0f;
	float zMax = 0f;

	Color3f color = null;
	String textureFileName = null;
	float visible = 1f;

	float textureScaleX = 1f;
	float textureScaleZ = 1f;

	float liftValue = 0f;
	float liftStep = 0f;
	float liftIndex = 0f;
	float liftPosition = 0f;

	float sink = 0f;

	public Terrain(int xFields, int zFields, float xMove, float yMove, float zMove, float xScale, float yScale, float zScale)
	{
		this.xFields = xFields;
		this.zFields = zFields;

		this.xScale = xScale;
		this.yScale = yScale;
		this.zScale = zScale;

		this.xMove = xMove;
		this.yMove = yMove;
		this.zMove = zMove;

		transform3D = new Transform3D();
		setPosition(xMove, yMove, zMove);
		// transform3D.setTranslation(new Vector3f(xMove, yMove, zMove));
		transformGroup = new TransformGroup(transform3D);

		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		xMin = xMove;
		xMax = xMove + xFields * xScale;
		zMin = zMove;
		zMax = zMove + zFields * zScale;

		int numberOfPoints = (xFields + 1) * (zFields + 1);
		points = new Point3f[numberOfPoints];
		int count = 0;

		for(int z = 0; z <= zFields; z++)
		{
			for(int x = 0; x <= xFields; x++)
			{
				Point3f point = new Point3f(x * xScale, 0f, z * zScale);
				points[count] = point;
				count++;
			}
		}
	}

	public void setColor(Color3f color)
	{
		this.color = color;
	}

	public void setTexture(String textureFileName, float textureScaleX, float textureScaleZ)
	{
		this.textureFileName = textureFileName;
		this.textureScaleX = textureScaleX;
		this.textureScaleZ = textureScaleZ;
	}

	public void setVisible(float visible)
	{
		this.visible = visible;
	}

	public void setCoordinates(int x, float y, int z)
	{
		Point3f point = new Point3f(x * xScale, y * yScale, z * zScale);
		int index = x + z * (xFields + 1);
		points[index] = point;
	}

	public void generateTerrain()
	{
		int numberOfPoints = (xFields + 1) * (zFields + 1);
		int numberOfIndecies = 6 * xFields * zFields;

		IndexedTriangleArray terrain = new IndexedTriangleArray(numberOfPoints, GeometryArray.COORDINATES, numberOfIndecies);

		int count = 0;

		for(int i = 0; i < points.length; i++)
		{
			terrain.setCoordinate(i, points[i]);

		}

		count = 0;
		for(int z = 0; z < zFields; z++)
		{
			for(int x = 0; x < xFields; x++)
			{

				int indexA = x + z * (xFields + 1);
				int indexB = x + z * (xFields + 1) + 1;
				int indexC = x + (z + 1) * (xFields + 1) + 1;
				int indexD = x + (z + 1) * (xFields + 1);

				// Triangle: ABC
				terrain.setCoordinateIndex(count, indexC);
				terrain.setCoordinateIndex(count + 1, indexB);
				terrain.setCoordinateIndex(count + 2, indexA);

				// Triangle: ACD
				terrain.setCoordinateIndex(count + 3, indexD);
				terrain.setCoordinateIndex(count + 4, indexC);
				terrain.setCoordinateIndex(count + 5, indexA);

				count += 6;
			}
		}

		Appearance appearance = new Appearance();
		Material material = new Material();

		if(color != null)
		{
			material.setDiffuseColor(color);
			material.setSpecularColor(new Color3f(Color.BLACK));
			appearance.setMaterial(material);
		}
		if(textureFileName != null)
		{
			Texture texImage = new TextureLoader(textureFileName, Main.frame).getTexture();

			TexCoordGeneration tg = new TexCoordGeneration();
			tg.setGenMode(TexCoordGeneration.OBJECT_LINEAR);
			tg.setPlaneS(new Vector4f((1f / textureScaleX), 0f, 0f, 0f));
			tg.setPlaneT(new Vector4f(0f, 0f, (1f / textureScaleZ), 0f));

			appearance.setTexCoordGeneration(tg);
			appearance.setTexture(texImage);
		}

		if(visible < 1f)
		{
			TransparencyAttributes t = new TransparencyAttributes(0, 1f - visible);
			appearance.setTransparencyAttributes(t);
		}

		shape3D = new Shape3D(terrain);
		shape3D.setAppearance(appearance);
		transformGroup.addChild(shape3D);

		addChild(transformGroup);

		compile();
	}

	public float getPositionY(float xPosition, float zPosition)
	{
		float y = 0f;

		int xField = (int)((xPosition - xMove) / xScale);
		int zField = (int)((zPosition - zMove) / zScale);

		int indexA = xField + zField * (xFields + 1);
		int indexB = xField + zField * (xFields + 1) + 1;
		int indexC = xField + (zField + 1) * (xFields + 1) + 1;
		int indexD = xField + (zField + 1) * (xFields + 1);

		if(indexA >= 0 && indexA < points.length && indexB >= 0 && indexB < points.length && indexC >= 0 && indexC < points.length && indexD >= 0 && indexD < points.length)
		{

			Point3f pointA = points[indexA];
			Point3f pointB = points[indexB];
			Point3f pointC = points[indexC];
			Point3f pointD = points[indexD];

			float x = xPosition - (pointA.x + xMove);
			float z = zPosition - (pointA.z + zMove);

			if(x >= 0 && z >= 0)
			{

				if(x == 0)
					x = Float.MIN_VALUE;

				if((z / x) <= (pointC.z - pointA.z) / (pointC.x - pointA.x))
				{
					y = (((pointC.y - pointA.y) / (pointC.x - pointA.x) * x + pointA.y) - ((pointB.y - pointA.y) / (pointB.x - pointA.x) * x + pointA.y)) / ((pointC.z - pointA.z) / (pointC.x - pointA.x) * x) * z + (pointB.y - pointA.y) / (pointB.x - pointA.x) * x + pointA.y;
				}
				else
				{
					y = (((pointC.y - pointA.y) / (pointC.x - pointA.x) * x + pointA.y) - ((pointD.y - pointC.y) / (pointD.x - pointC.x) * x + pointA.y)) / ((pointC.z - pointA.z) / (pointC.x - pointA.x) * x) * z + (pointD.y - pointC.y) / (pointD.x - pointC.x) * x + pointA.y;
				}
			}
		}

		y += yMove;
		y += liftPosition;
		y -= sink;
		return y;
	}

	public void setPosition(float positionX, float positionY, float positionZ)
	{
		transform3D.setTranslation(new Vector3d(positionX, positionY, positionZ));
	}

	public void update()
	{
		if(liftValue != 0f)
		{
			liftIndex += liftStep;
			liftPosition = (float)(Math.sin(Math.toDegrees(liftIndex)) + 0.5f) * liftValue;
			setPosition(xMove, yMove + liftPosition, zMove);
		}

		transformGroup.setTransform(transform3D);
	}

	public int getXFields()
	{
		return xFields;
	}

	public int getZFields()
	{
		return zFields;
	}

	public float getXMin()
	{
		return xMin;
	}

	public void setXMin(float min)
	{
		xMin = min;
	}

	public float getXMax()
	{
		return xMax;
	}

	public void setXMax(float max)
	{
		xMax = max;
	}

	public float getZMin()
	{
		return zMin;
	}

	public void setZMin(float min)
	{
		zMin = min;
	}

	public float getZMax()
	{
		return zMax;
	}

	public void setZMax(float max)
	{
		zMax = max;
	}

	public float getLiftValue()
	{
		return liftValue;
	}

	public void setLiftValue(float liftValue)
	{
		this.liftValue = liftValue;
	}

	public float getLiftStep()
	{
		return liftStep;
	}

	public void setLiftStep(float liftStep)
	{
		this.liftStep = liftStep;
	}

	public float getSink()
	{
		return sink;
	}

	public void setSink(float sink)
	{
		this.sink = sink;
	}
}
