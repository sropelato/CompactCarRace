package net.ropelato.compactcarrace.view;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;

import net.ropelato.compactcarrace.graphics3d.Camera;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class View
{
    Canvas3D canvas3D = null;
    GraphicsConfiguration graphicsConfiguration = null;
    SimpleUniverse universe = null;
    Camera camera = null;
    Frame frame = null;

    public View(Frame frame, int resolutionX, int resolutionY, int colorDepth, boolean fullscreen)
    {
        this.frame = frame;

        // *** set up graphics configuration ***
        if (fullscreen)
        {
            frame.setUndecorated(true);

            if (GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported())
            {
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
            }
            
            if (GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isDisplayChangeSupported())
            {
                DisplayMode displayMode = new DisplayMode(resolutionX, resolutionY, colorDepth, DisplayMode.REFRESH_RATE_UNKNOWN);
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setDisplayMode(displayMode);
            }
        }
        graphicsConfiguration = SimpleUniverse.getPreferredConfiguration();
        canvas3D = new MyCanvas3D(graphicsConfiguration);
        canvas3D.setDoubleBufferEnable(true);
        canvas3D.getGraphicsContext3D().setBufferOverride(false);

        // *** create universe ***
        universe = new SimpleUniverse(canvas3D);
        universe.getViewer().getView().setSceneAntialiasingEnable(true);
        canvas3D.getView().setFrontClipDistance(0.1f);
        canvas3D.getView().setBackClipDistance(50000f);
        // universe.setJ3DThreadPriority(Thread.MIN_PRIORITY);

        // *** create camera ***
        TransformGroup cameraTransformGroup = universe.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0);
        camera = new Camera(cameraTransformGroup);

        canvas3D.startRenderer();
    }

    public void addBranchGroup(BranchGroup branchGroup)
    {
        universe.addBranchGraph(branchGroup);
    }

    public Canvas3D getCanvas3D()
    {
        return canvas3D;
    }

    public GraphicsConfiguration getGraphicsConfiguration()
    {
        return graphicsConfiguration;
    }

    public SimpleUniverse getUniverse()
    {
        return universe;
    }

    public Camera getCamera()
    {
        return camera;
    }

}
