#version 150
in vec2 inParamPos;
out vec3 vertColor;
uniform mat4 mat;
uniform float barva;
uniform float objekt;

const float PI = 3.1415927;
const float DELTA = 0.001;

vec3 klikovaHridel(vec2 paramPos)
{
// vytvořeno podle: http://www.math.uri.edu/~bkaskosz/flashmo/tools/cylin/

        float x = paramPos.x * 2 * PI;
        float y = paramPos.y * 2 * PI - PI;
	return vec3(
		2+cos(2*y)*sin(x),
		x,
		y
	);
}

vec3 sombrero(vec2 paramPos)
{
// vytvořeno podle: http://www.math.uri.edu/~bkaskosz/flashmo/tools/cylin/

        float x = paramPos.x * 2 * PI;
        float y = paramPos.y * 2 * PI;
	return vec3(
		y,
		x,
		2*sin(y)
	);
}

vec3 kulPlocha(vec2 paramPos)
{
        float x = paramPos.x * 2 * PI;
        float y = paramPos.y * PI;
	return vec3(
		sin(y),
		x,
		cos(y)
	);
}

vec3 prepocet(vec3 paramPos)
{
    float r = paramPos.x;
    float azimut = paramPos.y;
    float v = paramPos.z;
    return vec3(
        r*cos(azimut),
        r*sin(azimut),
        v
    );
}

vec3 surface(vec2 paramPos)
{
    vec3 souradnice;

    if(objekt == 1)
    {
        souradnice = kulPlocha(paramPos);
    }
    else if(objekt == 2)
    {
        souradnice = sombrero(paramPos);
    }
    else if(objekt == 3)
    {
       souradnice = klikovaHridel(paramPos);
    }
    else if(objekt == 4)
    {
       souradnice = klikovaHridel(paramPos);
    }

    return prepocet(souradnice);
}

vec3 normal(vec2 paramPos)
{
        vec2 dx = vec2(DELTA, 0), dy = vec2(0, DELTA);
	vec3 tx = surface(paramPos + dx) - surface(paramPos - dx);
	vec3 ty = surface(paramPos + dy) - surface(paramPos - dy);
	return normalize(cross(tx, ty));
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
                c = normal(inParamPos);
        }
   return c;
}
		
void main() {
	vec3 position = surface(inParamPos);
	gl_Position = mat * vec4(position, 1.0);
        vertColor = color(barva, position);
}