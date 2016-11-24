#version 150
in vec2 inParamPos;
out vec3 vertColor;
uniform mat4 mat;
uniform float barva;

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

vec3 color(float barva, vec3 position)
{
   vec3 c;
   if(barva==0)
        {
                c = vec3(0, 0, 0);
        }
        else if(barva==1)
        {
                c = vec3(1, 0, 0);
        }
        else if(barva==2)
        {
                c = vec3(0, 1, 0);
        }
        else if(barva==3)
        {
                c = vec3(0, 0, 1);
        }
        else if(barva==4)
        {
                c = vec3(1, 1, 1);
        }
        else if(barva==5)
        {
                c = vec3(inParamPos, 0);
        }
        else if(barva==6)
        {
                c = vec3(position);
        }
        else if(barva==7)
        {
                vec3 normal = vec3(1,1,1);
                c = vec3(normal);
        }
   return c;
}
		
void main() {
	vec3 position = surface(inParamPos);
	gl_Position = mat * vec4(position, 1.0);
	vertColor = color(barva, position);
}