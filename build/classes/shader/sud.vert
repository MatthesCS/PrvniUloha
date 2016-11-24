#version 150
in vec2 inParamPos;
out vec3 vertColor;
uniform mat4 mat;

const float PI = 3.1415927;

vec3 surface(vec2 paramPos)
{
        float x = paramPos.x*PI*2;
        float y = paramPos.y*PI;
	return vec3(
		sin(y)*sin(y)*cos(x)*0.5,
		sin(y)*sin(y)*sin(x)*0.5,
		sin(y)*cos(y)
	);
}
		
void main() {
	vec3 position = surface(inParamPos);
	gl_Position = mat * vec4(position, 1.0);
	vertColor = vec3(inParamPos, 0);
}