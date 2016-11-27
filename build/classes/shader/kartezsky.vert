#version 150
in vec2 inParamPos;
out vec2 textCoord;
out vec3 vertColor;
out vec3 light;
out vec3 vertNormal;
out float diff;
out float svetlo;
uniform vec3 poziceSvetla;
uniform mat4 mat;
uniform float barva;
uniform float objekt;
uniform float s;

const float PI = 3.1415927;
const float DELTA = 0.001;

vec3 sud(vec2 paramPos)
{
    float x = paramPos.x*PI*2;
        float y = paramPos.y*PI;
	return vec3(
		sin(y)*sin(y)*cos(x)*0.5,
		sin(y)*sin(y)*sin(x)*0.5,
		sin(y)*cos(y)
	);
}

vec3 kulPlocha(vec2 paramPos)
{
        float azimuth = 2 * PI * paramPos.x;
	float zenith = PI * (0.5 - paramPos.y);
	return vec3(
		cos(azimuth)*cos(zenith),
		sin(azimuth)*cos(zenith),
		sin(zenith)
	);
}

vec3 mobius(vec2 paramPos)
{
// vytvořeno podle: http://www.math.uri.edu/~bkaskosz/flashmo/tools/parsur/

        float x = paramPos.x * 2 * PI;
        float y = paramPos.y - 0.5;
	return vec3(
		2*cos(x)+y*cos(x/2),
		2*sin(x)+y*cos(x/2),
		y*sin(x/2)
	);
}

vec3 presHodiny(vec2 paramPos)
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

vec3 turbina(vec2 paramPos)
{
        float x = paramPos.x * 2 * PI;
        float y = paramPos.y * PI;
        float r = 2 + sin(7*x+5*y);
	return vec3(
		r*cos(x)*sin(y),
		r*sin(x)*sin(y),
		r*cos(y)
	);
}

vec3 surface(vec2 paramPos)
{
    if(objekt == 1)
    {
        return kulPlocha(paramPos);
    }
    else if(objekt == 2)
    {
        return presHodiny(paramPos);
    }
    else if(objekt == 3)
    {
       return sud(paramPos);
    }
    else if(objekt == 4)
    {
       return mobius(paramPos);
    }
    else if(objekt == 5)
    {
       return turbina(paramPos);
    }
}

float pocetTextur()
{
    if(objekt == 1)
    {
        return 2;
    }
    else if(objekt == 2)
    {
        return 3;
    }
    else if(objekt == 3)
    {
       return 3;
    }
    else if(objekt == 4)
    {
       return 3;
    }
    else if(objekt == 5)
    {
       return 5;
    }
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
        svetlo = s;
	vec3 position = surface(inParamPos);
        textCoord = inParamPos*pocetTextur();
	gl_Position = mat * vec4(position, 1.0);
        vertColor = color(barva, position);
        vertNormal = normalize(normal(inParamPos));
        if(svetlo == 0)
        {
            light = vec3(0,0,0);
            diff = 0;
        }
        else if(svetlo == 1)
        {
            light = position - poziceSvetla;
            diff = dot(normalize(vertNormal), normalize(light));
        }
        else
        {
            light = position - poziceSvetla;
            diff = 0;
        }
}