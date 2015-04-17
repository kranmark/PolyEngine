package kran.poly.graphics;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;

/**
 * Created by Mark on 2015-04-16.
 */
public class Mesh {
	
	public final VertexFormat format;
	private final ByteBuffer[] buffers;
	private ByteBuffer indexBuffer;
	private int indexType = GL_UNSIGNED_SHORT;
	
	public Mesh(VertexFormat format, int bufferCount) {
		this.format = format;
		this.buffers = new ByteBuffer[bufferCount];
	}
	
	public ByteBuffer createBuffer(int index, int capacity) {
		buffers[index] = BufferUtils.createByteBuffer(capacity);
		return buffers[index];
	}
	
	public int bufferCount() {
		return buffers.length;
	}
	
	public ByteBuffer getBuffer(int index) {
		return buffers[index];
	}
	
	public ByteBuffer createIndexBuffer(int capacity) {
		indexBuffer = BufferUtils.createByteBuffer(capacity);
		return indexBuffer;
	}
	
	public ByteBuffer getIndexBuffer() {
		return indexBuffer;
	}
	
	public void setIndexBuffer(ByteBuffer indexBuffer) {
		this.indexBuffer = indexBuffer;
	}
	
	public int getIndexType() {
		return indexType;
	}
	
	public void setIndexType(int indexType) {
		this.indexType = indexType;
	}
}
