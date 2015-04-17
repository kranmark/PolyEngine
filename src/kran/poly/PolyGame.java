package kran.poly;

import kran.poly.graphics.Window;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.libffi.Closure;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_TRUE;

/**
 * Created by Mark on 2015-04-15.
 */
public abstract class PolyGame implements Runnable {
	
	public static final boolean DEBUG = LWJGLUtil.DEBUG;
	
	private static final int TICKS = 60;
	
	private int framesPerSecond = 0, updatePerSecond = 0;
	
	private GLFWErrorCallback errorCallback;
	private Closure debugProc;
	
	protected Thread thread;
	protected Window window;
	
	protected PolyGame() {
	}
	
	protected void createWindow(String title, int width, int height) {
		window = new Window(title, width, height);
		
		if (DEBUG)
			debugProc = GLContext.createFromCurrent().setupDebugMessageCallback(System.err);
		else
			GLContext.createFromCurrent();
	}
	
	public void start() {
		thread = new Thread(this, "Game");
		thread.start();
	}
	
	public final void run() {
		try {
			glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));
			if (glfwInit() != GL_TRUE) {
				throw new IllegalStateException("Failed to initialize GLFW!");
			}
			LWJGLUtil.initialize();
			
			init();
			loop();
		} finally {
			try {
				destroy();
				
				if (window != null)
					window.destroy();
				
				if (DEBUG && debugProc != null)
					debugProc.release();
				
				glfwTerminate();
				errorCallback.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loop() {
		long lastTime = System.nanoTime();
		long delta = 0;
		final long ns = 1000000000;
		
		long timer = lastTime;
		int updates = 0;
		int frames = 0;
		
		while (!window.shouldClose()) {
			long now = System.nanoTime();
			delta += (now - lastTime) * TICKS;
			lastTime = now;
			
			window.pollEvents();
			if (delta >= ns) {
				update();
				updates++;
				delta -= ns;
			}
			render();
			frames++;
			window.swapBuffers();
			if (now - timer >= ns) {
				timer += ns;
				updatePerSecond = updates;
				framesPerSecond = frames;
				updates = 0;
				frames = 0;
				tick();
			}
		}
	}
	
	public int getFPS() {
		return framesPerSecond;
	}
	
	public int getUPS() {
		return updatePerSecond;
	}
	
	protected abstract void init();
	
	protected abstract void update();
	
	protected abstract void render();
	
	protected abstract void tick();
	
	protected abstract void destroy();
	
}
