package kran.poly.graphics;

import kran.poly.input.Input;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by Mark on 2015-04-02.
 */
public class Window {
	
	private final GLFWFramebufferSizeCallback framebufferSizeCallback;
	public final Input input;
	
	private String title;
	private int width;
	private int height;
	private boolean resized = false;
	
	public final long handle;
	
	public Window(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
		
		handle = init();
		
		framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				Window.this.width = width;
				Window.this.height = height;
				GL11.glViewport(0, 0, width, height);
				Window.this.resized = true;
			}
		};
		
		input = new Input(this);
		
		
		glfwSetFramebufferSizeCallback(handle, framebufferSizeCallback);
	}
	
	private long init() {
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		
		long handle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (handle == NULL) {
			throw new RuntimeException("Failed to create GLFW window!");
		}
		
		glfwMakeContextCurrent(handle);
		glfwSwapInterval(0);
		
		return handle;
	}
	
	public void show() {
		glfwShowWindow(handle);
	}
	
	public Input getInput() {
		return input;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean wasResized() {
		if (resized) {
			resized = false;
			return true;
		}
		return false;
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(handle) == GL_TRUE;
	}
	
	public void swapBuffers() {
		glfwSwapBuffers(handle);
	}
	
	public void pollEvents() {
		glfwPollEvents();
	}
	
	public void destroy() {
		glfwDestroyWindow(handle);
		framebufferSizeCallback.release();
		input.release();
	}
	
	public void setPosition(int xpos, int ypos) {
		glfwSetWindowPos(handle, xpos, ypos);		
	}
	
	public void setShouldClose(boolean shouldClose) {
		glfwSetWindowShouldClose(handle, shouldClose ? GL_TRUE : GL_FALSE);
	}
}
