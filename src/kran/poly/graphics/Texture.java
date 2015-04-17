package kran.poly.graphics;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Mark on 2015-04-17.
 */
public class Texture {
	
	public final int handle;
	public final int width;
	public final int height;
	
	public Texture(int width, int height) {
		handle = glGenTextures();
		this.width = width;
		this.height = height;
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, handle);
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void destroy() {
		glDeleteTextures(handle);
	}
	
}
