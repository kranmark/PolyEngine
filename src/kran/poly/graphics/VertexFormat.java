package kran.poly.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mark on 2015-04-16.
 */
public class VertexFormat {
	
	public static class Attribute {
		public final int bufferIndex;
		public final int index;
		public final int size;
		public final int type;
		public final boolean normalized;
		public final int stride;
		public final int offset;
		
		public Attribute(int bufferIndex, int index, int size, int type, boolean normalized, int stride, int offset) {
			this.bufferIndex = bufferIndex;
			this.index = index;
			this.size = size;
			this.type = type;
			this.normalized = normalized;
			this.stride = stride;
			this.offset = offset;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Attribute)) return false;
			
			Attribute attribute = (Attribute) o;
			
			if (bufferIndex != attribute.bufferIndex) return false;
			if (index != attribute.index) return false;
			if (size != attribute.size) return false;
			if (type != attribute.type) return false;
			if (normalized != attribute.normalized) return false;
			if (stride != attribute.stride) return false;
			return offset == attribute.offset;
		}
		
		@Override
		public int hashCode() {
			int result = bufferIndex;
			result = 31 * result + index;
			result = 31 * result + size;
			result = 31 * result + type;
			result = 31 * result + (normalized ? 1 : 0);
			result = 31 * result + stride;
			result = 31 * result + offset;
			return result;
		}
	}
	
	public final List<Attribute> attributes;
	private final List<Attribute> privateAttributes = new ArrayList<Attribute>();
	
	public VertexFormat() {
		attributes = Collections.unmodifiableList(privateAttributes);
	}
	
	public VertexFormat addAttribute(int bufferIndex, int index, int size, int type, boolean normalized, int stride, int offset) {
		privateAttributes.add(new Attribute(bufferIndex, index, size, type, normalized, stride, offset));
		return this;
	}
	
	public void clearAttributes() {
		privateAttributes.clear();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof VertexFormat)) return false;
		
		VertexFormat that = (VertexFormat) o;
		
		return privateAttributes.equals(that.privateAttributes);
	}
	
	@Override
	public int hashCode() {
		return privateAttributes.hashCode();
	}
}
