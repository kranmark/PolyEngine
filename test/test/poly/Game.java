package test.poly;

import kran.poly.PolyGame;
import kran.poly.graphics.*;
import kran.poly.input.BasicControl;
import kran.poly.input.Input;
import kran.poly.input.ToggleControl;
import kran.poly.math.FastMath;
import kran.poly.math.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL13;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/**
 * Created by Mark on 2015-04-15.
 */
public class Game extends PolyGame {
	
	private static final int SHADER_POSITION = 0;
	private static final int SHADER_TEX_COORD = 1;
	
	private static final int PR_MATRIX_ID = Shader.uniformNameToID("pr_matrix");
	private static final int VW_MATRIX_ID = Shader.uniformNameToID("vw_matrix");
	private static final int ML_MATRIX_ID = Shader.uniformNameToID("ml_matrix");
	private static final int DIFFUSE_ID = Shader.uniformNameToID("diffuse");
	private final BasicControl ctrlForward = new BasicControl();
	private final BasicControl ctrlBackward = new BasicControl();
	private final BasicControl ctrlLeft = new BasicControl();
	private final BasicControl ctrlRight = new BasicControl();
	private final BasicControl ctrlJump = new BasicControl();
	private final BasicControl ctrlCrouch = new BasicControl();
	private final ToggleControl ctrlDebug = new ToggleControl(false);
	private final Vector3f axis = new Vector3f();
	private final Vector3f movement = new Vector3f();
	private final Vector3f direction = new Vector3f(0, 0, -1);
	private final Vector3f up = new Vector3f(0, 1, 0);
	private final Vector3f position = new Vector3f(0, 0, 0);
	private final float sensitivityX = 0.15f;
	private final float sensitivityY = 0.15f;
	private TextureManager textureManager;
	private Shader shader;
	private VertexArray vao;
	private Mesh mesh;
	private Mesh textured;
	private Shader shader2;
	private VertexArray vao2;
	private Texture texture;
	private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	private Camera camera;
	private int indexCount;
	private float rotationX = 0f;
	private float rotationY = 0f;
	
	public static void main(String[] args) {
		new Game().start();
	}
	
	@Override
	protected void init() {
		glfwDefaultWindowHints();
		
		if (DEBUG)
			glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);
		
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		
		createWindow("Demo Game", 640, 360);
		
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		window.setPosition((GLFWvidmode.width(vidmode) - window.getWidth()) / 2,
				(GLFWvidmode.height(vidmode) - window.getHeight()) / 2);
		
		window.show();
		
		glClearColor(0.01f, 0.01f, 0.1f, 1.0f);
		
		textureManager = new TextureManager();
		texture = textureManager.createTexture("test.png");
		
		shader = new Shader();
		shader.attachShader(Shader.VERTEX_SHADER, "shaders/colored.vert");
		shader.attachShader(Shader.FRAGMENT_SHADER, "shaders/colored.frag");
		shader.bindAttribLocation("position", SHADER_POSITION);
		shader.link();
		
		shader.enable();
		
		camera = new Camera();
		camera.setFrustumPerspective(60f, (float) window.getWidth() / window.getHeight(), 0.1f, 100f);
		camera.getProjectionMatrix().fillFloatBuffer(matrixBuffer, true).flip();
		shader.setUniformMat4(PR_MATRIX_ID, false, matrixBuffer);
		camera.getViewMatrix().fillFloatBuffer(matrixBuffer, true).flip();
		shader.setUniformMat4(VW_MATRIX_ID, false, matrixBuffer);
		shader.setUniform4f(DIFFUSE_ID, 0.90f, 0.90f, 0.95f, 0.4f);
		
		vao = new VertexArray();
		vao.bind();
		
		final int POINTS = 21;
		final float SCALE = 6.0f;
		final float Y = -1f;
		
		mesh = new Mesh(new VertexFormat().addAttribute(0, SHADER_POSITION, 3, GL_FLOAT, false, 0, 0), 1);
		FloatBuffer fb = mesh.createBuffer(0, POINTS * 3 << 2).asFloatBuffer();
		float[] posArray = new float[POINTS * 3];
		
		for (int i = 0; i < POINTS; i++) {
			float rad = FastMath.TWO_PI * i / POINTS;
			posArray[i * 3] = SCALE * FastMath.cos(rad);
			posArray[i * 3 + 1] = Y;
			posArray[i * 3 + 2] = -SCALE * FastMath.sin(rad);
		}
		fb.put(posArray).flip();
		
		IntBuffer ib = BufferUtils.createIntBuffer(mesh.bufferCount());
		glGenBuffers(ib);
		int[] buffers = new int[ib.remaining()];
		ib.get(buffers);
		
		glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
		glBufferData(GL_ARRAY_BUFFER, mesh.getBuffer(0), GL_STATIC_DRAW);
		vao.addBufferNoBind(buffers[0], mesh.format.attributes.get(0));
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		indexCount = (POINTS - 2) * 3;
		ShortBuffer indexBuffer = mesh.createIndexBuffer(indexCount << 1).asShortBuffer();
		short[] indexArray = new short[indexCount];
		for (int i = 0; i < (POINTS - 2); i++) {
			indexArray[i * 3] = 0;
			indexArray[i * 3 + 1] = (short) (i + 1);
			indexArray[i * 3 + 2] = (short) (i + 2);
		}
		indexBuffer.put(indexArray).flip();
		int ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh.getIndexBuffer(), GL_STATIC_DRAW);
		
		vao.unbind();
		
		shader.disable();
		
//		vao2 = new VertexArray();
//		vao2.bind();
//		
//		shader2 = new Shader();
//		shader2.attachShader(Shader.VERTEX_SHADER, "shaders/textured.vert");
//		shader2.attachShader(Shader.FRAGMENT_SHADER, "shaders/textured.frag");
//		shader2.bindAttribLocation("position", SHADER_POSITION);
//		shader2.bindAttribLocation("texCoord", SHADER_TEX_COORD);
//		shader2.link();
//		
//		shader2.enable();
//		camera.getProjectionMatrix().fillFloatBuffer(matrixBuffer, true).flip();
//		shader2.setUniformMat4(PR_MATRIX_ID, false, matrixBuffer);
//		camera.getViewMatrix().fillFloatBuffer(matrixBuffer, true).flip();
//		shader2.setUniformMat4(VW_MATRIX_ID, false, matrixBuffer);
//		shader2.setUniform1i(DIFFUSE_ID, 0);
//		
//		textured = new Mesh(new VertexFormat()
//				.addAttribute(0, SHADER_POSITION, 3, GL_FLOAT, false, (3 + 2) * 4, 0)
//				.addAttribute(0, SHADER_TEX_COORD, 2, GL_FLOAT, false, (3 + 2) * 4, 3 * 4), 1);
//		fb = textured.createBuffer(0, ((3 + 2) * 4) << 2).asFloatBuffer();
//		float[] fa = {
//				-0.5f, -0.5f, -2f, 0f, 0f, //
//				 0.5f, -0.5f, -2f, 1f, 0f, //
//				 0.5f,  0.5f, -2f, 1f, 1f, //
//				-0.5f,  0.5f, -2f, 0f, 1f, //
//		};
//		fb.put(fa).flip();
//		int vbo = glGenBuffers();
//		glBindBuffer(GL_ARRAY_BUFFER, vbo);
//		glBufferData(GL_ARRAY_BUFFER, textured.getBuffer(0), GL_STATIC_DRAW);
//		vao2.addBufferNoBind(vbo, textured.format.attributes.get(0));
//		vao2.addBufferNoBind(vbo, textured.format.attributes.get(1));
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
//		
//		ByteBuffer ibb = textured.createIndexBuffer(6);
//		byte[] bb = {
//				0, 1, 2,
//				2, 3, 0
//		};
//		ibb.put(bb).flip();
//		int ibo2 = glGenBuffers();
//		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo2);
//		glBufferData(GL_ELEMENT_ARRAY_BUFFER, textured.getBuffer(0), GL_STATIC_DRAW);
//		textured.setIndexType(GL_UNSIGNED_BYTE);
//		
//		vao2.unbind();
//		shader2.disable();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_CULL_FACE);
		
		Input input = window.input;
		input.setMouseGrabbed(true);
		input.addKeyControl(GLFW_KEY_W, ctrlForward);
		input.addKeyControl(GLFW_KEY_S, ctrlBackward);
		input.addKeyControl(GLFW_KEY_A, ctrlLeft);
		input.addKeyControl(GLFW_KEY_D, ctrlRight);
		input.addKeyControl(GLFW_KEY_SPACE, ctrlJump);
		input.addKeyControl(GLFW_KEY_LEFT_SHIFT, ctrlCrouch);
		input.addKeyControl(GLFW_KEY_F3, ctrlDebug);
	}

	@Override
	protected void update() {
		processInput();
		
	}
	
	private void processInput() {
		Input input = window.input;
		input.update();
		
		if (input.isKeyTyped(GLFW_KEY_F10))
			input.setMouseGrabbed(false);
		
		if (input.isMouseButtonClicked(GLFW_MOUSE_BUTTON_LEFT))
			input.setMouseGrabbed(true);
		
		if (input.isKeyTyped(GLFW_KEY_ESCAPE))
			window.setShouldClose(true);
		
		rotationX += (float) -input.mouseDX * FastMath.DEG_TO_RAD * sensitivityX;
		rotationY += (float) -input.mouseDY * FastMath.DEG_TO_RAD * sensitivityY;
		rotationY = FastMath.clamp(rotationY, -FastMath.HALF_PI, FastMath.HALF_PI);
		
		float sinX = FastMath.sin(rotationX);
		float cosX = FastMath.cos(rotationX);
		float sinY = FastMath.sin(rotationY);
		float cosY = FastMath.cos(rotationY);
		direction.set(sinX * cosY, sinY, cosX * cosY);
		up.set(-sinY * sinX, cosY, -sinY * cosX);
		
		axis.zero();
		if (ctrlForward.isActivated()) {
			axis.z = 1;
		}
		if (ctrlBackward.isActivated()) {
			axis.z = -1;
		}
		if (ctrlLeft.isActivated()) {
			axis.x = -1;
		}
		if (ctrlRight.isActivated()) {
			axis.x = 1;
		}
		axis.normalizeLocal();
		if (ctrlJump.isActivated()) {
			axis.y = 1;
		}
		if (ctrlCrouch.isActivated()) {
			axis.y = -1;
		}
		
		if (!axis.isZero()) {
			axis.multLocal(0.1f);
			
			movement.set(axis.z * sinX, axis.y, axis.z * cosX);
			movement.addLocal(axis.x * -cosX, 0, axis.x * sinX);
			
			position.addLocal(movement);
			
			camera.setPosition(position);
		}
		camera.setDirection(direction);
		camera.setUp(up);
	}
	
	@Override
	protected void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		
		shader.enable();
		vao.bind();
		if (camera.isRefreshViewMatrix()) {
			camera.getViewMatrix().fillFloatBuffer(matrixBuffer, true).flip();
			shader.setUniformMat4(VW_MATRIX_ID, false, matrixBuffer);
		}
		glDrawElements(GL_TRIANGLES, indexCount, mesh.getIndexType(), 0);
		vao.unbind();
		shader.disable();
		
		
//		shader2.enable();
//		vao2.bind();
//		GL13.glActiveTexture(GL13.GL_TEXTURE0);
//		texture.bind();
//		if (camera.isRefreshViewMatrix()) {
//			camera.getViewMatrix().fillFloatBuffer(matrixBuffer, true).flip();
//			shader2.setUniformMat4(VW_MATRIX_ID, false, matrixBuffer);
//		}
//		glDrawElements(GL_TRIANGLES, 6, textured.getIndexType(), 0);
//		texture.unbind();
//		vao2.unbind();
//		shader2.disable();
	}
	
	@Override
	protected void tick() {
		if (ctrlDebug.isActivated())
			System.out.println(getUPS() + " ups, " + getFPS() + " fps");
	}
	
	@Override
	protected void destroy() {
		shader.destroy();
		vao.destroy();
	}
}
