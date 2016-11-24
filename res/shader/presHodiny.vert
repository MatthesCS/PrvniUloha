#version 150
in vec2 inParamPos;
out vec3 vertColor;
uniform mat4 mat;
uniform float barva;

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