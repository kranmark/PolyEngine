package kran.poly.input;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Mark on 2015-04-16.
 */
public class BasicControl implements KeyControl, MouseControl {
	
	protected int pressCount = 0;
	
	public boolean isActivated() {
		return pressCount > 0;
	}
	
	@Override
	public void invokeKey(int key, int scancode, int action, int mods) {
		if (action == GLFW_PRESS) {
			pressCount++;
		} else if (action == GLFW_RELEASE ){
			pressCount--;
		}
	}
	
	@Override
	public void invokeMouse(int button, int action, int mods) {
		if (action == GLFW_PRESS) {
			pressCount++;
		} else if (action == GLFW_RELEASE){
			pressCount--;
		}
	}
}
