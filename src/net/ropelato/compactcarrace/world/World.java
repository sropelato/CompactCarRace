package net.ropelato.compactcarrace.world;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Fog;
import javax.media.j3d.LinearFog;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import net.ropelato.compactcarrace.graphics3d.Model;
import net.ropelato.compactcarrace.graphics3d.MyPointLight;
import net.ropelato.compactcarrace.graphics3d.Terrain;
import net.ropelato.compactcarrace.util.Consts;
import net.ropelato.compactcarrace.util.SynchronizedRandom;

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class World
{
	private ArrayList models = new ArrayList();
	private AmbientLight ambientLight = new AmbientLight();
	private BranchGroup ambientLightBG = new BranchGroup();
	private ArrayList pointLights = new ArrayList();
	private ArrayList terrains = new ArrayList();
	private Fog fog = null;

	public static BoundingSphere INFINITE_BOUNDINGSPHERE = new BoundingSphere(new Point3d(0d, 0d, 0d), Double.MAX_VALUE);

	public World(String fileName)
	{
		try
		{
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(fileName));

			Element root = doc.getRootElement();
			Iterator it = root.getDescendants();
			while(it.hasNext())
			{
				Object obj = it.next();
				if(obj instanceof Element)
				{
					Element element = (Element)obj;

					if(element.getName().equals("ambientlight"))
					{
						parseAmbientLight(element);
					}
					if(element.getName().equals("pointlight"))
					{
						parsePointLight(element);
					}
					if(element.getName().equals("fog"))
					{
						parseFog(element);
					}
					if(element.getName().equals("texture"))
					{
						parseTexture(element);
					}
					if(element.getName().equals("terrain"))
					{
						parseTerrain(element);
					}
					if(element.getName().equals("model"))
					{
						parseModel(element);
					}
				}
			}
			addObjects();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void addObjects()
	{
		// add ambient light
		ambientLightBG = new BranchGroup();
		ambientLightBG.addChild(ambientLight);
		ambientLightBG.compile();

		// add point lights
		for(int i = 0; i < pointLights.size(); i++)
		{
			MyPointLight pointLight = (MyPointLight)pointLights.get(i);
			pointLight.compile();
		}

		// add models
		for(int i = 0; i < models.size(); i++)
		{
			Model model = (Model)models.get(i);
			model.compile();
		}
	}

	private void parseAmbientLight(Element element)
	{
		Element color = element.getChild("color");
		Element bounds = element.getChild("bounds");

		try
		{
			int colorRed = color.getAttribute("red").getIntValue();
			int colorGreen = color.getAttribute("green").getIntValue();
			int colorBlue = color.getAttribute("blue").getIntValue();

			double boundsX = bounds.getAttribute("x").getDoubleValue();
			double boundsY = bounds.getAttribute("y").getDoubleValue();
			double boundsZ = bounds.getAttribute("z").getDoubleValue();
			float boundsRadius = bounds.getAttribute("radius").getFloatValue();

			BoundingSphere ambientLightBounds = new BoundingSphere(new Point3d(boundsX, boundsY, boundsZ), boundsRadius);
			Color3f ambientLightColor = new Color3f(new Color(colorRed, colorGreen, colorBlue));
			ambientLight = new AmbientLight(ambientLightColor);
			ambientLight.setInfluencingBounds(ambientLightBounds);
		}
		catch(ClassCastException e)
		{
			e.printStackTrace();
		}
		catch(DataConversionException e)
		{
			e.printStackTrace();
		}
	}

	private void parsePointLight(Element element)
	{
		Element color = element.getChild("color");
		Element position = element.getChild("position");
		Element bounds = element.getChild("bounds");

		try
		{
			int colorRed = color.getAttribute("red").getIntValue();
			int colorGreen = color.getAttribute("green").getIntValue();
			int colorBlue = color.getAttribute("blue").getIntValue();

			float positionX = position.getAttribute("x").getFloatValue();
			float positionY = position.getAttribute("y").getFloatValue();
			float positionZ = position.getAttribute("z").getFloatValue();

			double boundsX = bounds.getAttribute("x").getDoubleValue();
			double boundsY = bounds.getAttribute("y").getDoubleValue();
			double boundsZ = bounds.getAttribute("z").getDoubleValue();
			float boundsRadius = bounds.getAttribute("radius").getFloatValue();

			BoundingSphere pointLightBounds = new BoundingSphere(new Point3d(boundsX, boundsY, boundsZ), boundsRadius);
			Color3f pointLightColor = new Color3f(new Color(colorRed, colorGreen, colorBlue));
			MyPointLight pointLight = new MyPointLight(pointLightColor, pointLightBounds);
			pointLight.setPosition(positionX, positionY, positionZ);
			pointLights.add(pointLight);
		}
		catch(ClassCastException e)
		{
			e.printStackTrace();
		}
		catch(DataConversionException e)
		{
			e.printStackTrace();
		}
	}

	private void parseFog(Element element)
	{
		Element color = element.getChild("color");
		Element distance = element.getChild("distance");

		try
		{
			int colorRed = color.getAttribute("red").getIntValue();
			int colorGreen = color.getAttribute("green").getIntValue();
			int colorBlue = color.getAttribute("blue").getIntValue();

			float frontDistance = distance.getAttribute("front").getIntValue();
			float backDistance = distance.getAttribute("back").getIntValue();

			LinearFog linearFog = new LinearFog();

			Color3f fogColor = new Color3f(new Color(colorRed, colorGreen, colorBlue));
			linearFog.setColor(fogColor);
			linearFog.setFrontDistance(frontDistance);
			linearFog.setBackDistance(backDistance);

			linearFog.setCapability(Fog.ALLOW_COLOR_WRITE);
			linearFog.setCapability(LinearFog.ALLOW_DISTANCE_WRITE);
			linearFog.setInfluencingBounds(INFINITE_BOUNDINGSPHERE);

			fog = linearFog;

		}
		catch(DataConversionException e)
		{
			e.printStackTrace();
		}
	}

	private void parseTexture(Element element)
	{
		String fileName = element.getAttribute("src").getValue();
	}

	private void parseModel(Element element)
	{
		Element position = element.getChild("position");
		Element scale = element.getChild("scale");
		Element rotation = element.getChild("rotation");

		try
		{
			String fileName = element.getAttribute("src").getValue();
			boolean collidable = true;
			if(element.getAttribute("collidable") != null)
				collidable = element.getAttribute("collidable").getBooleanValue();

			float positionX = position.getAttribute("x").getFloatValue();
			float positionZ = position.getAttribute("z").getFloatValue();
			float positionY = 0f;

			String yString = position.getAttribute("y").getValue();
			yString = yString.replace(" ", "");
			if(yString != null && yString.contains("terrain"))
			{
				float yDifference = 0f;

				if(yString.contains("+"))
				{
					int plusPosition = yString.indexOf("+");
					yDifference = new Float(yString.substring(plusPosition + 1, yString.length())).floatValue();
				}
				else if(yString.contains("-"))
				{
					int minusPosition = yString.indexOf("-");
					yDifference = new Float(yString.substring(minusPosition + 1, yString.length())).floatValue() * -1;
				}

				Terrain activeTerrain = getActiveTerrain(positionX, positionZ);
				if(activeTerrain != null)
				{
					positionY = activeTerrain.getPositionY(positionX, positionZ) + yDifference;
				}
			}
			else
			{
				positionY = position.getAttribute("y").getFloatValue();
			}

			float scaleFactor = scale.getAttribute("factor").getFloatValue();

			float rotationX = rotation.getAttribute("x").getFloatValue();
			float rotationY = rotation.getAttribute("y").getFloatValue();
			float rotationZ = rotation.getAttribute("z").getFloatValue();

			Model model = null;

            /*for (int i = 0; i < models.size() && model == null; i++)
			{
                Model oldModel = (Model) models.get(i);

                if (oldModel.getFileName().equals(fileName))
                {
                    model = (Model) oldModel.clone();
                    model.restore();
                    model.update();
                }
            }*/

			model = new Model(fileName);
			model.setScale(scaleFactor);
			model.setPosition(positionX, positionY, positionZ);
			model.setRotation(rotationX, rotationY, rotationZ);
			model.setCollidable(collidable);
			model.update();
			models.add(model);

		}
		catch(ClassCastException e)
		{
			e.printStackTrace();
		}
		catch(DataConversionException e)
		{
			e.printStackTrace();
		}
	}

	private void parseTerrain(Element element)
	{

		Element fields = element.getChild("fields");
		Element position = element.getChild("position");
		Element scale = element.getChild("scale");
		Element color = element.getChild("color");
		Element texture = element.getChild("texture");
		Element transparency = element.getChild("transparency");
		Element lift = element.getChild("lift");
		Element sink = element.getChild("sink");

		try
		{
			boolean collidable = false;
			if(element.getAttribute("collidable") != null)
			{
				collidable = element.getAttribute("collidable").getBooleanValue();
			}

			int fieldsX = fields.getAttribute("x").getIntValue();
			int fieldsZ = fields.getAttribute("z").getIntValue();

			float positionX = position.getAttribute("x").getFloatValue();
			float positionY = position.getAttribute("y").getFloatValue();
			float positionZ = position.getAttribute("z").getFloatValue();

			float scaleX = scale.getAttribute("x").getFloatValue();
			float scaleY = scale.getAttribute("y").getFloatValue();
			float scaleZ = scale.getAttribute("z").getFloatValue();

			int colorRed = -1;
			int colorGreen = -1;
			int colorBlue = -1;

			float visible = transparency.getAttribute("visible").getFloatValue();

			if(color != null)
			{
				colorRed = color.getAttribute("red").getIntValue();
				colorGreen = color.getAttribute("green").getIntValue();
				colorBlue = color.getAttribute("blue").getIntValue();
			}

			String textureFileName = null;
			float textureScaleX = 1f;
			float textureScaleZ = 1f;

			if(texture != null)
			{
				textureFileName = texture.getAttribute("src").getValue();
				textureScaleX = texture.getAttribute("x").getFloatValue();
				textureScaleZ = texture.getAttribute("z").getFloatValue();
			}

			Terrain terrain = new Terrain(fieldsX, fieldsZ, positionX, positionY, positionZ, scaleX, scaleY, scaleZ);
			terrain.setCollidable(collidable);

			Iterator it = element.getChildren("randomize").iterator();
			while(it.hasNext())
			{
				Element setElement = (Element)it.next();
				String xString = setElement.getAttribute("x").getValue();
				String yString = setElement.getAttribute("height").getValue();
				String zString = setElement.getAttribute("z").getValue();
				int seed = setElement.getAttribute("seed").getIntValue();

				xString = xString.replace(" ", "");
				zString = zString.replace(" ", "");

				int xStart = 0;
				int xEnd = 0;
				float yStart = 0;
				float yEnd = 0;
				int zStart = 0;
				int zEnd = 0;

				if(xString.contains("-"))
				{
					int minusPosition = xString.indexOf("-");
					xStart = new Integer(xString.substring(0, minusPosition)).intValue();
					xEnd = new Integer(xString.substring(minusPosition + 1, xString.length())).intValue();
				}
				else
				{
					xStart = xEnd = new Integer(xString).intValue();
				}

				if(yString.contains("-"))
				{
					int minusPosition = yString.indexOf("-");
					if(minusPosition == 0)
						minusPosition = yString.indexOf("-", minusPosition + 1);

					yStart = new Float(yString.substring(0, minusPosition)).floatValue();
					yEnd = new Float(yString.substring(minusPosition + 1, yString.length())).floatValue();
				}
				else
				{
					yStart = yEnd = new Float(yString).floatValue();
				}

				if(zString.contains("-"))
				{
					int minusPosition = zString.indexOf("-");
					zStart = new Integer(zString.substring(0, minusPosition)).intValue();
					zEnd = new Integer(zString.substring(minusPosition + 1, zString.length())).intValue();
				}
				else
				{
					zStart = zEnd = new Integer(zString).intValue();
				}

				SynchronizedRandom rand = new SynchronizedRandom(Consts.RANDOM_CONFIG, seed);
				for(int x = xStart; x <= xEnd; x++)
				{
					for(int z = zStart; z <= zEnd; z++)
					{
						float y = (float)(rand.getValue() * (yEnd - yStart) + yStart);
						terrain.setCoordinates(x, y, z);
					}
				}
			}

			Iterator it2 = element.getChildren("set").iterator();
			while(it2.hasNext())
			{
				Element setElement = (Element)it2.next();
				String xString = setElement.getAttribute("x").getValue();
				String zString = setElement.getAttribute("z").getValue();
				float y = setElement.getAttribute("height").getFloatValue();

				xString = xString.replace(" ", "");
				zString = zString.replace(" ", "");

				int xStart = 0;
				int xEnd = 0;
				int zStart = 0;
				int zEnd = 0;

				if(xString.contains("-"))
				{
					int minusPosition = xString.indexOf("-");
					xStart = new Integer(xString.substring(0, minusPosition)).intValue();
					xEnd = new Integer(xString.substring(minusPosition + 1, xString.length())).intValue();
				}
				else
				{
					xStart = xEnd = new Integer(xString).intValue();
				}

				if(zString.contains("-"))
				{
					int minusPosition = zString.indexOf("-");
					zStart = new Integer(zString.substring(0, minusPosition)).intValue();
					zEnd = new Integer(zString.substring(minusPosition + 1, zString.length())).intValue();
				}
				else
				{
					zStart = zEnd = new Integer(zString).intValue();
				}

				for(int x = xStart; x <= xEnd; x++)
				{
					for(int z = zStart; z <= zEnd; z++)
					{
						terrain.setCoordinates(x, y, z);
					}
				}
			}

			if(lift != null)
			{
				float value = lift.getAttribute("value").getFloatValue();
				float step = lift.getAttribute("step").getFloatValue();
				terrain.setLiftValue(value);
				terrain.setLiftStep(step);
			}

			if(sink != null)
			{
				float value = lift.getAttribute("value").getFloatValue();
				terrain.setSink(value);
			}

			if(colorRed >= 0 && colorGreen >= 0 && colorBlue >= 0)
			{
				Color3f terrainColor = new Color3f(new Color(colorRed, colorGreen, colorBlue));
				terrain.setColor(terrainColor);
			}

			if(textureFileName != null)
				terrain.setTexture(textureFileName, textureScaleX, textureScaleZ);

			terrain.setVisible(visible);

			terrain.generateTerrain();
			terrains.add(terrain);

		}
		catch(ClassCastException e)
		{
			e.printStackTrace();
		}
		catch(DataConversionException e)
		{
			e.printStackTrace();
		}
	}

	public ArrayList getModels()
	{
		return models;
	}

	public BranchGroup getAmbientLightBG()
	{
		return ambientLightBG;
	}

	public ArrayList getPointLights()
	{
		return pointLights;
	}

	public ArrayList getTerrains()
	{
		return terrains;
	}

	public Fog getFog()
	{
		return fog;
	}

	public void setFog(Fog fog)
	{
		this.fog = fog;
	}

	public Terrain getActiveTerrain(float xPosition, float zPosition)
	{
		Terrain activeTerrain = null;
		float maxPositionY = Float.MAX_VALUE * -1;
		for(int i = 0; i < terrains.size(); i++)
		{
			Terrain terrain = (Terrain)terrains.get(i);
			if(xPosition >= terrain.getXMin() && xPosition <= terrain.getXMax() && zPosition >= terrain.getZMin() && zPosition <= terrain.getZMax())
			{
				if(activeTerrain == null)
				{
					activeTerrain = terrain;
					maxPositionY = activeTerrain.getPositionY(xPosition, zPosition);
				}
				else
				{
					if(terrain.getPositionY(xPosition, zPosition) > maxPositionY)
					{
						activeTerrain = terrain;
						maxPositionY = activeTerrain.getPositionY(xPosition, zPosition);
					}
				}
			}
		}
		return activeTerrain;
	}

	public void update()
	{
		for(int i = 0; i < terrains.size(); i++)
		{
			Terrain terrain = (Terrain)terrains.get(i);
			terrain.update();
		}
	}

}
