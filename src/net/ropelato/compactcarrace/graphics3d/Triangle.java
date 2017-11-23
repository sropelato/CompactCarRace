package net.ropelato.compactcarrace.graphics3d;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;

public class Triangle
{
    Shape3D shape3D = null;

    Point3f[] points = new Point3f[3];

    public Triangle(Point3f[] points)
    {
        setPoints(points);
    }

    private void createTriangle()
    {
        int[] stripCount = { 3 };

        GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gi.setCoordinates(points);
        gi.setStripCounts(stripCount);

        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(gi);
        gi.recomputeIndices();

        Stripifier st = new Stripifier();
        st.stripify(gi);
        gi.recomputeIndices();

        Appearance appearance = new Appearance();
        Material material = new Material();
        material.setAmbientColor(new Color3f(Color.GREEN));
        appearance.setMaterial(material);

        shape3D = new Shape3D();
        shape3D.setGeometry(gi.getGeometryArray());
        shape3D.setAppearance(appearance);
    }

    public Point3f[] getPoints()
    {
        return points;
    }

    public void setPoints(Point3f[] points)
    {
        this.points = points;
        createTriangle();
    }

    public Shape3D getShape3D()
    {
        return shape3D;
    }

}
