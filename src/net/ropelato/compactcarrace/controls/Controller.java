package net.ropelato.compactcarrace.controls;

import javax.media.j3d.Canvas3D;

public class Controller
{
	Canvas3D canvas3D = null;
	int indexMax = 0;

	String[] commandName = new String[1000];
	int[] inputType = new int[1000];
	int[] inputSource = new int[1000];
	float[] inputShift = new float[1000];
	float[] inputScale = new float[1000];
	boolean[] inputInverse = new boolean[1000];
	boolean[] inputLocked = new boolean[1000];
	boolean[] oneHit = new boolean[1000];
	float[] smooth = new float[1000];
	float[] oldValue = new float[1000];

	KeyboardListener keyboardListener = null;

	public static int KEYBOARD = 0;
	public static int JOYSTICK_BUTTON = 1;
	public static int JOYSTICK_AXIS = 2;
	public static int JOYSTICK_POV = 3;

	public static int X_AXIS = 0;
	public static int Y_AXIS = 1;
	public static int Z_AXIS = 2;
	public static int R_AXIS = 3;
	public static int U_AXIS = 4;
	public static int V_AXIS = 5;

	public Controller(Canvas3D canvas3D)
	{
		this.canvas3D = canvas3D;
		keyboardListener = new KeyboardListener();
		canvas3D.addKeyListener(keyboardListener);

		for(int i = 0; i < 1000; i++)
		{
			commandName[i] = "";
			inputType[i] = 0;
			inputSource[i] = 0;
			inputInverse[i] = false;
			inputLocked[i] = false;
			oneHit[i] = false;
		}
	}

	public void addCommand(String name, int inputType, int inputSource, float shift, float scale, boolean inverse, float smooth, boolean oneHit)
	{
		this.commandName[indexMax] = name;
		this.inputType[indexMax] = inputType;
		this.inputSource[indexMax] = inputSource;
		this.inputShift[indexMax] = shift;
		this.inputScale[indexMax] = scale;
		this.inputInverse[indexMax] = inverse;
		this.smooth[indexMax] = smooth;
		this.oldValue[indexMax] = 0f;
		this.oneHit[indexMax] = oneHit;
		indexMax++;
	}

	public float getCommand(String name)
	{
		float value = 0f;
		float newValue = 0f;

		for(int i = 0; i < indexMax; i++)
		{
			if(commandName[i].equals(name))
			{
				if(inputType[i] == KEYBOARD)
				{
					if(!oneHit[i])
					{
						if(keyboardListener.getKeyDown(inputSource[i]) == true)
						{
							newValue = 1f;
						}
						else
						{
							newValue = 0f;
						}
					}
					else
					{
						if(inputLocked[i])
						{
							newValue = 0f;
							if(keyboardListener.getKeyDown(inputSource[i]) == false)
							{
								inputLocked[i] = false;
							}
						}
						else
						{
							if(keyboardListener.getKeyDown(inputSource[i]) == true)
							{
								newValue = 1f;
								inputLocked[i] = true;
							}
						}
					}
				}

				value = (oldValue[i] * smooth[i] + newValue) / (1 + smooth[i]);
				oldValue[i] = value;
			}
		}

		return value;
	}
}
