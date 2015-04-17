#version 330 core

in vec3 position;
//in vec4 color;

uniform mat4 pr_matrix;
uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);

//out DATA {
//	vec4 color;
//} vs_out;

void main() {
	gl_Position = pr_matrix * vw_matrix * ml_matrix * vec4(position, 1.0);
//	vs_out.color = color;
}