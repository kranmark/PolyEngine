package kran.poly.graphics;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created by Mark on 2015-04-10.
 */
public class VertexArray {
	
	public final int handle;
	
	public VertexArray() {
		handle = glGenVertexArrays();
	}
	
	public void addBuffer(int buffer, int index, int size, int type, boolean normalized, int stride, int offset) {
		glBindBuffer(GL_ARRAY_BUFFER, buffer);
		
		addBufferNoBind(buffer, index, size, type, normalized, stride, offset);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void addBufferNoBind(int buffer, int index, int size, int type, boolean normalized, int stride, int offset) {
		glEnableVertexAttribArray(index);
		glVertexAttribPointer(index, size, type, normalized, stride, offset);
	}
	
	public void addBuffer(int buffer, VertexFormat.Attribute a) {
		glBindBuffer(GL_ARRAY_BUFFER, buffer);
		
		addBufferNoBind(buffer, a);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void addBufferNoBind(int buffer, VertexFormat.Attribute a) {
		addBufferNoBind(buffer, a.index, a.size, a.type, a.normalized, a.stride, a.offset);
	}
	
	public void bind() {
		glBindVertexArray(handle);
	}
	
	public void unbind() {
		glBindVertexArray(0);
	}
	
	public void destroy() {
		glDeleteVertexArrays(handle);
	}
	
}
