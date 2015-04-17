package kran.poly.input;

import kran.poly.graphics.Window;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Mark on 2015-04-07.
 */
public class Input {
	
	private static final int MAX_KEYS = GLFW_KEY_LAST;
	private static final int MAX_BUTTONS = GLFW_MOUSE_BUTTON_LAST + 1;
	
	private final Window window;
	
 	private final boolean[] keys = new boolean[MAX_KEYS];
	private final boolean[] keyState = new boolean[MAX_KEYS];
	private final boolean[] keyTyped = new boolean[MAX_KEYS];
	private final KeyControl[] keyControls = new KeyControl[MAX_KEYS];
	
	private final boolean[] mouseButtons = new boolean[MAX_BUTTONS];
	private final boolean[] mouseState = new boolean[MAX_BUTTONS];
	private final boolean[] mouseClicked = new boolean[MAX_BUTTONS];
	private final MouseControl[] mouseControls = new MouseControl[MAX_BUTTONS];
	
	private boolean grabbed = false;
	
	public double mouseX, mouseY;
	public double mouseDX, mouseDY;
	private double lastMouseX, lastMouseY;
	
	public final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if(key >= keys.length) {
				System.err.println("key event: (" + key + ") out of bounds!");
				return;
			}
			keys[key] = action != GLFW_RELEASE;
			KeyControl control = keyControls[key];
			if (control != null) {
				control.invokeKey(key, scancode, action, mods);
			}
		}
	};
	
	public final GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
		@Override
		public void invoke(long window, double xpos, double ypos) {
			mouseX = xpos;
			mouseY = ypos;
		}
	};
	
	public final GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
		@Override
		public void invoke(long window, int button, int action, int mods) {
			if(button >= mouseButtons.length) {
				System.err.println("mouse event: (" + button + ") out of bounds!");
				return;
			}
			mouseButtons[button] = action != GLFW_RELEASE;
			MouseControl control = mouseControls[button];
			if (control != null) {
				control.invokeMouse(button, action, mods);
			}
		}
	};
	
	public Input(Window window) {
		this.window = window;
		Callbacks.glfwSetCallback(window.handle, keyCallback);
		Callbacks.glfwSetCallback(window.handle, cursorPosCallback);
		Callbacks.glfwSetCallback(window.handle, mouseButtonCallback);
	}
	
	public void addKeyControl(int keycode, KeyControl control) {
		if (control == null)
			return;
		keyControls[keycode] = control;
	}
	
	public void clearKeyControl(int keycode) {
		keyControls[keycode] = null;
	}
	
	public void addMouseControl(int button, MouseControl control) {
		if (control == null)
			return;
		mouseControls[button] = control;
	}
	
	public void clearMouseControl(int button) {
		mouseControls[button] = null;
	}
	
	public void setMouseGrabbed(boolean enable) {
		if (grabbed == enable)
			return;
		grabbed = enable;
		glfwSetInputMode(window.handle, GLFW_CURSOR, enable ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
	}
	
	public boolean isMouseGrabbed() {
		return grabbed;
	}
	
	public void setCursorPosition(double x, double y) {
		glfwSetCursorPos(window.handle, x, y);
	}
	
	public void release() {
		keyCallback.release();
		cursorPosCallback.release();
		mouseButtonCallback.release();
	}
	
	public void update() {
		for (int i = 0; i < keys.length; i++) {
			keyTyped[i] = keys[i] && !keyState[i];
		}
		
		for (int i = 0; i < mouseButtons.length; i++) {
			mouseClicked[i] = mouseButtons[i] && !mouseState[i];
		}
		
		System.arraycopy(keys, 0, keyState, 0, keys.length);
		System.arraycopy(mouseButtons, 0, mouseState, 0, mouseButtons.length);
		
		mouseDX = mouseX - lastMouseX;
		mouseDY = mouseY - lastMouseY;
		
		lastMouseX = mouseX;
		lastMouseY = mouseY;
	}
	
	public boolean isKeyPressed(int keycode) {
		if (keycode >= keys.length) {
			System.err.println("key poll: (" + keycode + ") out of bounds!");
			return false;
		}
		return keys[keycode];
	}
	
	public boolean isKeyTyped(int keycode) {
		if (keycode >= keys.length) {
			System.err.println("key poll: (" + keycode + ") out of bounds!");
			return false;
		}
		return keyTyped[keycode];
	}
	
	public boolean isMouseButtonPressed(int button) {
		if (button >= mouseButtons.length) {
			System.err.println("mouse poll: (" + button + ") out of bounds!");
			return false;
		}
		return mouseButtons[button];
	}
	
	public boolean isMouseButtonClicked(int button) {
		if (button >= mouseButtons.length) {
			System.err.println("mouse poll: (" + button + ") out of bounds!");
			return false;
		}
		return mouseClicked[button];
	}
	
}
