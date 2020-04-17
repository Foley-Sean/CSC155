#version 430

//in vec2 tc;
//in vec3 varyingVertPos;
in vec4 varyingColor;
out vec4 fragColor;

//uniform mat4 mv_matrix;
//uniform mat4 proj_matrix;
// layout (binding=0) uniform sampler2D s;
//in vec4 varyingColor;

void main(void)
{
	fragColor = varyingColor;
}
