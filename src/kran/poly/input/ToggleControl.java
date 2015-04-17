package kran.poly.input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * Created by Mark on 2015-04-16.
 */
public class ToggleControl implements KeyControl, MouseControl {
	
	protected boolean active;
	
	public ToggleControl(boolean state) {
		active = state;
	}
	
	public boolean isActivated() {
		return active;
	}
	
	@Override
	public void invokeKey(int key, int scancode, int action, int mods) {
		if (action == GLFW_PRESS)
			active = !active;
	}
	
	@Override
	public void invokeMouse(int button, int action, int mods) {
		if (action == GLFW_PRESS)
			active = !active;
	}
	
}