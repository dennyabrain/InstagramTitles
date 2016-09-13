package util;

import dennymades.space.StitchAndroid.Triangle;

/**
 * Created by abrain on 9/13/16.
 */
public class TriangleHelper {
    private Triangle mTriangle;

    public TriangleHelper(){
        mTriangle = new Triangle(new float[]{(float)Math.random(),(float)Math.random(),(float)Math.random()},
                new float[]{(float)Math.random(),(float)Math.random(),(float)Math.random()});
    }

    public void drawTriangle(){
        mTriangle.draw();
    }
}
