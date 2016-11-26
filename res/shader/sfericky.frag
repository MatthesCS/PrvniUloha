#version 150
in vec3 vertColor;
in vec3 light;
in vec3 vertNormal;
out vec4 outColor;

void main() {
        if(light.x != 0 && light.y != 0 && light.z != 0)
        {
            float d = dot(normalize(vertNormal), normalize(light));
            outColor = vec4(vertColor, 1.0) * d;
        }        
        else
        {
            outColor = vec4(vertColor, 1.0);
        }
} 
