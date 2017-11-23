package net.ropelato.compactcarrace.graphics3d;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Vector3d;

import net.ropelato.compactcarrace.world.World;

public class Camera
{
	TransformGroup transformGroup = null;
	Transform3D transform3D = null;

	float rotationX = 0f;
	float rotationY = 0f;
	float rotationZ = 0f;
	float positionX = 0f;
	float positionY = 0f;
	float positionZ = 0f;

	int cameraMode = 0;
	Model targetModel = null;
	float cameraDistance = 10f;
	float cameraSpeed = 0f;
	float distance = 0f;

	float cameraHeight = 3f;
	float centerAboveTarget = 2f;

	float higherThanTargetModel = 1.2f;

	public static int STATIC = 0;
	public static int THIRD_PERSON = 1;
	public static int FIRST_PERSON = 2;
	public static int FOLLOW = 3;

	private static int MAX_PERSPECTIVE_ID = 2;

	public Camera(TransformGroup transformGroup)
	{
		this.transformGroup = transformGroup;
		transform3D = new Transform3D();
	}

	public void setRotationX(float rotationX)
	{
		setRotation(rotationX, rotationY, rotationZ);
	}

	public void setRotationY(float rotationY)
	{
		setRotation(rotationX, rotationY, rotationZ);
	}

	public void setRotationZ(float rotationZ)
	{
		setRotation(rotationX, rotationY, rotationZ);
	}

	public void setRotation(float rotationX, float rotationY, float rotationZ)
	{
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;

		Transform3D transformX = new Transform3D();
		Transform3D transformY = new Transform3D();
		Transform3D transformZ = new Transform3D();

		resetRotation();

		transformX.rotX(Math.toRadians(rotationX));
		transform3D.mul(transformX);
		transformZ.rotZ(Math.toRadians(rotationZ));
		transform3D.mul(transformZ);
		transformY.rotY(Math.toRadians(rotationY));
		transform3D.mul(transformY);
	}

	public void resetRotation()
	{
		transform3D.set(new AxisAngle4f(0, 0, 0, 0f));
		setPosition(positionX, positionY, positionZ);
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

	public void setTargetModel(Model targetModel)
	{
		this.targetModel = targetModel;
	}

	public void setCameraMode(int cameraMode)
	{
		this.cameraMode = cameraMode;
	}

	public void update(World world, float smooth, float additionalDistance, boolean reverse)
	{
		updateCameraPosition(world, smooth, additionalDistance, reverse);
		transformGroup.setTransform(transform3D);
	}

	private void updateCameraPosition(World world, float smooth, float additionalDistance, boolean reverse)
	{
		if(targetModel != null)
		{
			float turnAngle = 0f;

			if(reverse)
			{
				turnAngle = 180f;
			}

			if(cameraMode == FOLLOW)
			{
				float distanceX = (targetModel.getPositionX() - positionX);
				float distanceZ = (targetModel.getPositionZ() - positionZ);
				float atan = 0f;
				float targetRotationY = 0f;

				if(distanceZ != 0)
				{
					atan = distanceX / distanceZ;
				}
				else
				{
					atan = Float.MAX_VALUE;
				}

				targetRotationY = (float)Math.toDegrees(Math.atan(atan));

				if(distanceZ >= 0)
				{
					targetRotationY += 180;
				}

				setRotation(0f, targetRotationY, 0f);

				distance = (float)Math.sqrt((distanceX * distanceX) + (distanceZ * distanceZ));
				cameraSpeed = (distance - cameraDistance) / 10f;

				positionX -= (float)Math.sin(Math.toRadians(rotationY)) * cameraSpeed;
				positionZ -= (float)Math.cos(Math.toRadians(rotationY)) * cameraSpeed;
				positionY = targetModel.getPositionY() + cameraHeight;

				setPosition(positionX, positionY, positionZ);
			}
			if(cameraMode == THIRD_PERSON)
			{
				float targetPositionX = targetModel.getPositionX();
				float targetPositionZ = targetModel.getPositionZ();

				boolean correction = true;
				while(correction)
				{
					correction = false;
					if(targetModel.getRotationY() + turnAngle > rotationY && targetModel.getRotationY() + turnAngle > rotationY + 180)
					{
						rotationY += 360;
						correction = true;
					}
					if(targetModel.getRotationY() + turnAngle < rotationY && targetModel.getRotationY() + turnAngle < rotationY - 180)
					{
						rotationY -= 360;
						correction = true;
					}
				}

				distance = (float)Math.sqrt(((targetModel.getPositionX() - positionX) * (targetModel.getPositionX() - positionX)) + ((targetModel.getPositionZ() - positionZ) * (targetModel.getPositionZ() - positionZ)));

				targetPositionX += (float)Math.sin(Math.toRadians(rotationY)) * (cameraDistance + additionalDistance);
				targetPositionZ += (float)Math.cos(Math.toRadians(rotationY)) * (cameraDistance + additionalDistance);
				float targetPositionY = targetModel.getPositionY() + cameraHeight;

				if(world != null && world.getActiveTerrain(positionX, positionZ) != null)
				{
					targetPositionY = world.getActiveTerrain(positionX, positionZ).getPositionY(positionX, positionZ) + cameraHeight;
				}

				setPosition(targetPositionX, (getPositionY() * smooth + targetPositionY) / (smooth + 1), targetPositionZ);

				float targetRotationX = (float)Math.cos(Math.toRadians(getRotationY())) * (float)Math.toDegrees(Math.atan((getPositionY() - targetModel.getPositionY() - centerAboveTarget) / (cameraDistance + additionalDistance))) * -1;
				float targetRotationZ = (float)Math.sin(Math.toRadians(getRotationY())) * (float)Math.toDegrees(Math.atan((getPositionY() - targetModel.getPositionY() - centerAboveTarget) / (cameraDistance + additionalDistance)));

				setRotation(targetRotationX, (getRotationY() * smooth + targetModel.getRotationY() + turnAngle) / (smooth + 1), targetRotationZ);
			}
			if(cameraMode == FIRST_PERSON)
			{
				float targetRotationX = targetModel.getRotationX();
				float targetRotationZ = targetModel.getRotationZ();

				while(targetRotationX > 180)
				{
					targetRotationX -= 360;
				}
				while(targetRotationZ > 180)
				{
					targetRotationZ -= 360;
				}

				setRotation((getRotationX() * smooth + targetRotationX) / (smooth + 1), targetModel.getRotationY(), (getRotationZ() * smooth + targetRotationZ) / (smooth + 1));

				positionX = targetModel.getPositionX();
				positionZ = targetModel.getPositionZ();
				positionY = targetModel.getPositionY() + higherThanTargetModel;

				setPosition(positionX, positionY, positionZ);
			}
			if(cameraMode == STATIC)
			{
				distance = (float)Math.sqrt(((targetModel.getPositionX() - positionX) * (targetModel.getPositionX() - positionX)) + ((targetModel.getPositionZ() - positionZ) * (targetModel.getPositionZ() - positionZ)));

				float targetRotationX = (float)Math.cos(Math.toRadians(getRotationY())) * (float)Math.toDegrees(Math.atan((getPositionY() - targetModel.getPositionY()) / (distance))) * -1;
				float targetRotationZ = (float)Math.sin(Math.toRadians(getRotationY())) * (float)Math.toDegrees(Math.atan((getPositionY() - targetModel.getPositionY()) / (distance)));

				if(targetModel.getPositionZ() - getPositionZ() != 0f)
				{
					if(targetModel.getPositionZ() >= positionZ)
					{
						setRotation(targetRotationX, (float)Math.toDegrees(Math.atan((((targetModel.getPositionX() - getPositionX())) / ((targetModel.getPositionZ() - getPositionZ()))))) + 180, targetRotationZ);
					}
					else
					{
						setRotation(targetRotationX, (float)Math.toDegrees(Math.atan((((targetModel.getPositionX() - getPositionX())) / ((targetModel.getPositionZ() - getPositionZ()))))), targetRotationZ);
					}
				}
				setPosition(positionX, positionY, positionZ);
			}
		}
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

	public TransformGroup getTransformGroup()
	{
		return transformGroup;
	}

	public Transform3D getTransform3D()
	{
		return transform3D;
	}

	public void changeView()
	{
		cameraMode++;
		if(cameraMode > MAX_PERSPECTIVE_ID)
		{
			cameraMode = 0;
		}

		if(cameraMode == STATIC)
		{
			setPosition(positionX, positionY + cameraHeight, positionZ);
		}
	}

	public float getHigherThanTargetModel()
	{
		return higherThanTargetModel;
	}

	public void setHigherThanTargetModel(float higherThanTargetModel)
	{
		this.higherThanTargetModel = higherThanTargetModel;
	}
}
