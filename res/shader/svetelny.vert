#version 150
in vec2 inParamPos;
out vec3 vertColor;
uniform vec3 poziceSvetla;
uniform mat4 mat;

const float PI = 3.1415927;

vec3 kulPlocha(vec2 paramPos)
{
        float azimuth = 2 * PI * paramPos.x;
	float zenith = PI * (0.5 - paramPos.y);
	return vec3(
		cos(azimuth)*cos(zenith)/4,
		sin(azimuth)*cos(zenith)/4,
		sin(zenith)/4
	);
}

vec3 surface(vec2 paramPos)
{
    return kulPlocha(paramPos);
}
		
void main() {
	vec3 position = surface(inParamPos) + poziceSvetla;
	gl_Position = mat * vec4(position, 1.0);
        vertColor = vec3(1,1,0);
}