#version 150
in vec2 inParamPos;
out vec3 vertColor;
uniform mat4 mat;
uniform float barva;

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
        if(barva==0)
        {
                vertColor = vec3(0, 0, 0);
        }
        else if(barva==1)
        {
                vertColor = vec3(1, 0, 0);
        }
        else if(barva==2)
        {
                vertColor = vec3(0, 1, 0);
        }
        else if(barva==3)
        {
                vertColor = vec3(0, 0, 1);
        }
        else if(barva==4)
        {
                vertColor = vec3(1, 1, 1);
        }
        else if(barva==5)
        {
                vertColor = vec3(inParamPos, 0);
        }else if(barva==6)
        {
                vertColor = vec3(position);
        }
        else if(barva==7)
        {
                vec3 normal = vec3(1,1,1);
                vertColor = vec3(normal);
        }
}