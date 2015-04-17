package kran.poly.graphics;

import kran.poly.util.FileUtils;
import kran.poly.util.ResourceUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.memDecodeASCII;
import static org.lwjgl.system.MemoryUtil.memEncodeASCII;

/**
 * Created by Mark on 2015-04-10.
 */
public class Shader {
	public static final int VERTEX_SHADER = GL_VERTEX_SHADER;
	public static final int FRAGMENT_SHADER = GL_FRAGMENT_SHADER;
	public static final int NOT_DEFINED = -1;
	private static final int NOT_SET = -2;
	
	private static final Map<String, Integer> uniformIdMap = new HashMap<String, Integer>();
	private static final List<ByteBuffer> uniformNameList = new ArrayList<ByteBuffer>();
	
	public static int uniformNameToID(String name) {
		Integer idb = uniformIdMap.get(name);
		if (idb == null) {
			int id = uniformNameList.size();
			uniformIdMap.put(name, id);
			ByteBuffer nameEncoded = memEncodeASCII(name);
			uniformNameList.add(nameEncoded);
			return id;
		}
		return idb;
	}
	
	public static ByteBuffer uniformIDToName(int id) {
		return uniformNameList.get(id);
	}
	
	public static String uniformIDToNameString(int id) {
		return memDecodeASCII(uniformNameList.get(id));
	}
	
	public final int handle;
	private int[] uniformCache;
	
	public Shader() {
		handle = glCreateProgram();
		uniformCache = new int[uniformNameList.size()];
		Arrays.fill(uniformCache, NOT_SET);
	}
	
	public void attachShader(int type, String file) {
		int shader = createShader(type, file);
		glAttachShader(handle, shader);
		glDeleteShader(shader);
	}
	
	private int createShader(int type, String file) {
		int shader = glCreateShader(type);
		
		String source = ResourceUtils.loadAsString(file);
		glShaderSource(shader, source);
		
		glCompileShader(shader);
		int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
		String shaderLog = glGetShaderInfoLog(shader);
		if (!shaderLog.trim().isEmpty()) {
			System.err.println("SHADER (" + file + ") INFO LOG:\n" + shaderLog);
		}
		if (compiled == GL_FALSE) {
			throw new AssertionError("Could not compile shader: " + file);
		}
		return shader;
	}
	
	public void link() {
		glLinkProgram(handle);
		int linked = glGetProgrami(handle, GL_LINK_STATUS);
		String programLog = glGetProgramInfoLog(handle);
		if (!programLog.trim().isEmpty()) {
			System.err.println("SHADER PROGRAM (" + handle + ") INFO LOG:\n" + programLog);
		}
		if (linked == GL_FALSE) {
			throw new AssertionError("Could not link program (" + handle + ")");
		}
	}
	
	public void bindAttribLocation(String name, int index) {
		glBindAttribLocation(handle, index, name);
	}
	
	public int getAttribLocation(String name) {
		return glGetAttribLocation(handle, name);
	}
	
	public void setUniform1i(int uniformID, int v0) {
		glUniform1i(getUniformLocation(uniformID), v0);
	}
	
	public void setUniform1f(int uniformID, float v0) {
		glUniform1f(getUniformLocation(uniformID), v0);
	}
	
	public void setUniform2f(int uniformID, float v0, float v1) {
		glUniform2f(getUniformLocation(uniformID), v0, v1);
	}
	
	public void setUniform3f(int uniformID, float v0, float v1, float v2) {
		glUniform3f(getUniformLocation(uniformID), v0, v1, v2);
	}
	
	public void setUniform4f(int uniformID, float v0, float v1, float v2, float v3) {
		glUniform4f(getUniformLocation(uniformID), v0, v1, v2, v3);
	}
	
	public void setUniformMat4(int uniformID, boolean transpose, FloatBuffer value) {
		glUniformMatrix4(getUniformLocation(uniformID), transpose, value);
	}
	
	private int getUniformLocation(int uniformID) {
		if (uniformID >= uniformCache.length) {
			int length = uniformCache.length;
			uniformCache = Arrays.copyOf(uniformCache, uniformNameList.size());
			for (int i = length; i < uniformCache.length; i++) {
				uniformCache[i] = NOT_SET;
			}
		}
		
		if (uniformCache[uniformID] == NOT_SET) {
			ByteBuffer name = uniformIDToName(uniformID);
			uniformCache[uniformID] = glGetUniformLocation(handle, name);
			
			System.out.println("Uniform: " + uniformIDToNameString(uniformID) + " at location: " + uniformCache[uniformID]);
		}
		
		return uniformCache[uniformID];
	}
	
	public void enable() {
		glUseProgram(handle);
	}
	
	public void disable() {
		glUseProgram(0);
	}
	
	public void destroy() {
		glDeleteProgram(handle);
	}
	
}
