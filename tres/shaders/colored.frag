#version 330 core

out vec4 color;

uniform vec4 diffuse = vec4(1.0, 0.0, 1.0, 1.0);

//in DATA {
//	vec4 color;
//} fs_in;

void main() {
//	color = fs_in.color;
	color = diffuse;
}