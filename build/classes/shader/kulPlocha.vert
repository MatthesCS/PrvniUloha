#version 150
in vec2 inParamPos;
out vec3 vertColor;
uniform mat4 mat;

const float PI = 3.1415927;

vec3 surface(vec2 paramPos)
{
	float azimuth = 2 * PI * paramPos.x;
	float zenith = PI * (0.5 - paramPos.y);
	return vec3(
		cos(azimuth)*cos(zenith),
		sin(azimuth)*cos(zenith),
		sin(zenith)
	);
}
		
void main() {
	vec3 position = surface(inParamPos);
	gl_Position = mat * vec4(position, 1.0);
	vertColor = vec3(inParamPos, 0);
}