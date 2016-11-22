#version 150
in vec2 inParamPos;
out vec3 vertColor;
uniform mat4 mat;

const float PI = 3.1415927;

vec3 surface(vec2 paramPos)
{

        // vytvořeno podle: http://www.math.uri.edu/~bkaskosz/flashmo/tools/parsur/

        float x = paramPos.x * 2 * PI;
        float y = paramPos.y * 2 - 1;
	return vec3(
		y*cos(x),
		y*sin(x),
		y
	);
}
		
void main() {
	vec3 position = surface(inParamPos);
	gl_Position = mat * vec4(position, 1.0);
	vertColor = vec3(inParamPos, 0);
}