package kran.poly.graphics;

import de.matthiasmann.twl.utils.PNGDecoder;
import kran.poly.util.ResourceUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Mark on 2015-04-17.
 */
public class TextureManager {
	
	public void TextureManager() {
		
	}
	
	public Texture createTexture(String resource) {
		Texture texture = null;
		InputStream in = ResourceUtils.getResourceAsStream(resource);
		try {
			PNGDecoder decoder = new PNGDecoder(in);
			texture = new Texture(decoder.getWidth(), decoder.getHeight());
			ByteBuffer buf = BufferUtils.createByteBuffer(texture.width * texture.height << 2);
			decoder.decode(buf, texture.width << 2, PNGDecoder.Format.RGBA);
			buf.flip();
			texture.bind();
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.width, texture.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
			texture.unbind();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return texture;
	}
	
}
