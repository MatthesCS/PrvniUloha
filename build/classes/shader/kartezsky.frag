#version 150
in vec3 vertColor;
in vec3 light;
in vec3 vertNormal;
in float diff;
in float svetlo;
out vec4 outColor;

void main() {
        if(svetlo == 0)
        {
            outColor = vec4(vertColor, 1.0);
        }
        else if (svetlo == 1)
        {
            outColor = vec4(vertColor, 1.0) * diff;
        }        
        else
        {
            float d = dot(normalize(vertNormal), normalize(light));
            outColor = vec4(vertColor, 1.0) * d;
        }
} 
