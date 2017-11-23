package net.ropelato.compactcarrace.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Fog;
import javax.swing.JFrame;

import net.ropelato.compactcarrace.cars.Car;
import net.ropelato.compactcarrace.cars.Tacho;
import net.ropelato.compactcarrace.controls.Controller;
import net.ropelato.compactcarrace.graphics3d.Camera;
import net.ropelato.compactcarrace.graphics3d.Model;
import net.ropelato.compactcarrace.graphics3d.MyPointLight;
import net.ropelato.compactcarrace.graphics3d.Terrain;
import net.ropelato.compactcarrace.util.Util;
import net.ropelato.compactcarrace.view.FrameProcessor;
import net.ropelato.compactcarrace.view.MyCanvas3D;
import net.ropelato.compactcarrace.view.View;
import net.ropelato.compactcarrace.world.World;

public class Main implements FrameProcessor
{
	View view = null;
	Car myCar = null;
	Controller controller = null;
	String args[] = null;
	World world = null;
	int delay = 5;
	int targetFPS = 80;
	public static JFrame frame = null;

	private Main(String[] args)
	{
		// save args
		this.args = args;

		// create frame
		frame = new JFrame();
		frame.setTitle("Compact Car Race");
		frame.setSize(640, 480);
		frame.setLocation(100, 100);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout(1, 1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// load invisible mouse cursor
		Cursor cursor = frame.getToolkit().createCustomCursor(Util.loadImage(new File("./img/transparent.gif")), new Point(0, 0), "Cursor");
		frame.setCursor(cursor);

		// create view
		view = new View(frame, 1440, 900, 32, false);

		// add view to frame
		frame.getContentPane().setBackground(Color.BLACK);

		view.getCanvas3D().setVisible(false);
		view.getCanvas3D().addKeyListener(new CanvasKeyListener());
		frame.getContentPane().add(view.getCanvas3D(), BorderLayout.CENTER);

		// show frame
		frame.setVisible(true);

		// create world
		String worldDescriptor = selectWorld();
		world = new World(worldDescriptor);

		Fog fog = world.getFog();
		if(fog != null)
		{
			BranchGroup fogGroup = new BranchGroup();
			fogGroup.addChild(fog);
			view.addBranchGroup(fogGroup);
		}

		ArrayList worldModels = world.getModels();
		for(int i = 0; i < worldModels.size(); i++)
		{
			Model model = (Model)worldModels.get(i);
			view.addBranchGroup(model);
		}

		BranchGroup ambientLight = world.getAmbientLightBG();
		view.addBranchGroup(ambientLight);

		ArrayList worldPointLights = world.getPointLights();
		for(int i = 0; i < worldPointLights.size(); i++)
		{
			MyPointLight pointLight = (MyPointLight)worldPointLights.get(i);
			view.addBranchGroup(pointLight);
		}

		ArrayList worldTerrains = world.getTerrains();
		for(int i = 0; i < worldTerrains.size(); i++)
		{
			Terrain terrain = (Terrain)worldTerrains.get(i);
			view.addBranchGroup(terrain);
		}

		// create cars
		myCar = new Car("./cars/minicooper.xml");

		view.addBranchGroup(myCar.getModel());
		if(myCar.getCollisionModel() != null)
		{
			view.addBranchGroup(myCar.getCollisionModel());
		}

		// setup camera
		view.getCamera().setCameraMode(Camera.THIRD_PERSON);
		view.getCamera().setHigherThanTargetModel(myCar.getCameraHeight());

		// define controls
		controller = new Controller(view.getCanvas3D());

		controller.addCommand("turnLeft", Controller.KEYBOARD, KeyEvent.VK_LEFT, 0, 1, false, 10, false);
		controller.addCommand("turnRight", Controller.KEYBOARD, KeyEvent.VK_RIGHT, 0, 1, false, 10, false);
		controller.addCommand("forward", Controller.KEYBOARD, KeyEvent.VK_UP, 0, 1, false, 0, false);
		controller.addCommand("backward", Controller.KEYBOARD, KeyEvent.VK_DOWN, 0, 1, false, 0, false);
		controller.addCommand("changeCamera", Controller.KEYBOARD, KeyEvent.VK_C, 0, 1, false, 0, true);
		controller.addCommand("escape", Controller.KEYBOARD, KeyEvent.VK_ESCAPE, 0, 1, false, 0, false);

		// show canvas
		view.getCanvas3D().setVisible(true);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setVisible(true);
		view.getCanvas3D().requestFocus();

		// prepare car
		myCar.setPosition(14, 0f, 63.8f);
		myCar.setRotation(0f, 270f, 0f);
		myCar.update();
		myCar.getModel().setCollision(false);

		// prepare tacho
		Tacho myTacho = myCar.getTacho();
		myTacho.setImageObserver(view.getCanvas3D());
		myTacho.setPositionX(view.getCanvas3D().getWidth() - 119 - 10);
		myTacho.setPositionY(view.getCanvas3D().getHeight() - 122 - 10);
		((MyCanvas3D)view.getCanvas3D()).addPaintComponent(myTacho);
		((MyCanvas3D)view.getCanvas3D()).addFrameProcessor(this);
		myCar.setTacho(myTacho);

		view.getCamera().setTargetModel(myCar.getModel());
		view.getCamera().update(world, 0f, 0f, false);

		// start FPS counter
		Util.startFPSCounter();

	}

	public void doFrame()
	{

		// control car
		if(!myCar.isCollision())
		{
			myCar.steer(myCar.getMaxTurn() * controller.getCommand("turnLeft") - myCar.getMaxTurn() * controller.getCommand("turnRight"));

			if(myCar.getSpeed() >= 0)
			{
				myCar.accelerate(myCar.getMaxAcceleration() * controller.getCommand("forward") - myCar.getMaxDeceleration() * controller.getCommand("backward"));
			}
			else
			{
				myCar.accelerate(myCar.getMaxDeceleration() * controller.getCommand("forward") - myCar.getMaxAcceleration() * controller.getCommand("backward"));
			}
		}
		else
		{
			myCar.restore();
			if(myCar.getSpeed() >= 0f)
			{
				myCar.move(-0.2f);
			}
			else
			{
				myCar.move(0.2f);
			}
			;
			myCar.setSpeed(myCar.getSpeed() * -0.5f);
		}
		myCar.adaptToTerrain(world);

		// change camera view
		if(controller.getCommand("changeCamera") == 1)
		{
			view.getCamera().changeView();
		}

		// exit
		if(controller.getCommand("escape") == 1)
		{
			end();
		}

		// update world
		world.update();

		// update objects and camera
		myCar.update();
		view.getCamera().update(world, 10, Math.abs(myCar.getSpeed()) * 5f, myCar.isReverse());

		// print current fps
		int averageFPS = (int)Util.getFPS();
		if(averageFPS > targetFPS)
		{
			delay++;
		}
		// print current fps
		if(averageFPS < targetFPS)
		{
			delay--;
		}
		if(delay < 0)
		{
			delay = 0;
		}
		Util.delay(delay);
	}

	public String selectWorld()
	{
		String worldDescriptor = "world/test1_windengine.xml";

		if(args != null && args.length > 0 && args[0] != null)
		{
			worldDescriptor = args[0];
		}

		return worldDescriptor;
	}

	public void end()
	{
		System.exit(0);
	}

	protected class CanvasKeyListener extends KeyAdapter
	{
		public void keyTyped(KeyEvent e)
		{
			// ...
		}
	}

	public static void main(String[] args)
	{
		Main m = new Main(args);
	}
}
