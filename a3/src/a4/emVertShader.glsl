/*#version 430

layout (location=0) in vec3 vertPos;
layout (location=1) in vec3 vertNormal;
//test texture stuff
layout (location=2) in vec2 tex_coord;
out vec2 tc;

//layout (binding=0) uniform sampler2D s;

out vec3 varyingNormal, varyingLightDir, varyingVertPos, varyingHalfVec;
out vec4 shadow_coord;

struct PositionalLight
{	vec4 ambient, diffuse, specular;
	vec3 position;
};
struct Material
{	vec4 ambient, diffuse, specular;
	float shininess;
};

uniform vec4 globalAmbient;
uniform PositionalLight light;
//uniform PositionalLight mvLight;
uniform Material material;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
uniform mat4 norm_matrix;
uniform mat4 shadowMVP;

layout (binding=0) uniform sampler2DShadow shadowTex;
layout (binding=1) uniform sampler2D s;
layout (binding=2) uniform samplerCube t;

void main(void)
{	//output the vertex position to the rasterizer for interpolation
	varyingVertPos = (mv_matrix * vec4(vertPos,1.0)).xyz;

	//get a vector from the vertex to the light and output it to the rasterizer for interpolation
	varyingLightDir = light.position - varyingVertPos;

	//get a vertex normal vector in eye space and output it to the rasterizer for interpolation
	varyingNormal = (norm_matrix * vec4(vertNormal,1.0)).xyz;

	// calculate the half vector (L+V)
	varyingHalfVec = (varyingLightDir-varyingVertPos).xyz;

	shadow_coord = shadowMVP * vec4(vertPos,1.0);

	//texture
	tc = tex_coord;
	gl_Position = proj_matrix * mv_matrix * vec4(vertPos,1.0);
}
*/
//simple version
#version 430

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
out vec3 vNormal;
out vec3 vVertPos;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
uniform mat4 norm_matrix;
layout (binding = 0) uniform samplerCube t;

void main(void)
{
	vVertPos = (mv_matrix * vec4(position,1.0)).xyz;
	vNormal = (norm_matrix * vec4(normal,1.0)).xyz;
	gl_Position = proj_matrix * mv_matrix * vec4(position,1.0);
}


