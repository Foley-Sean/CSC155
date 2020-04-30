#version 430

layout (location = 0) in vec3 position;
//in vec3 vertPos;
//layout (location = 1) in vec2 tex_coord;
out vec4 varyingColor;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
//layout (binding=0) uniform sampler2D s;
const vec4 vertices[12] = vec4[12]
(
		//x axis
		vec4(0.0, 0.0, 0.0, 1.0),
		vec4(3.0f, 0.0, 0.0, 1.0),
		vec4(0.0, 0.0, 0.0, 1.0),
		vec4(-3.0,0.0, 0.0, 1.0),
		//y axis
		vec4(0.0, 0.0, 0.0, 1.0),
		vec4(0.0, 3.0, 0.0, 1.0),
		vec4(0.0, 0.0, 0.0, 1.0),
		vec4(0.0, -3.0, 0.0, 1.0),
		//z axis
		vec4(0.0, 0.0, 0.0, 1.0),
		vec4(0.0, 0.0, 3.0, 1.0),
		vec4(0.0, 0.0, 0.0, 1.0),
		vec4(0.0, 0.0, -3.0, 1.0));



void main(void){
	//gl_Position = vertices[gl_VertexID];
	gl_Position = proj_matrix * mv_matrix * vec4(position,1.0)* vertices[gl_VertexID];
	//varyingVertPos = (mv_matrix * vec4(vertPos,1.0)).xyz;
	//gl_Position = proj_matrix * mv_matrix * vertices[gl_VertexID];
	if(gl_VertexID < 4){
		varyingColor = vec4(1.0, 0.0, 0.0, 1.0);
	}

	else if(gl_VertexID < 8){
		varyingColor = vec4(0.0, 1.0, 0.0, 1.0);
	}

	else if(gl_VertexID > 8){
		varyingColor = vec4(0.0, 0.0, 1.0, 1.0);
	}

}
