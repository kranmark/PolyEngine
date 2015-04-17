#version 330 core

out vec4 color;

uniform sampler2D diffuse;

in DATA {
	vec2 texCoord;
} fs_in;

void main() {
	//color = texture(diffuse, fs_in.texCoord);
	color = vec4(1.0, 0.0, 1.0, 1.0);
}