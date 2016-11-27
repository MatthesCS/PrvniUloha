#version 150
in vec2 textCoord;
in vec3 vertColor;
in vec3 light;
in vec3 vertNormal;
in float diff;
in float svetlo;
out vec4 outColor;
uniform sampler2D texture;

void main() {
        if(svetlo == 0)
        {
            outColor = vec4(vertColor, 1.0) * texture2D(texture, textCoord);
        }
        else if (svetlo == 1)
        {
            outColor = vec4(vertColor, 1.0) * diff* texture2D(texture, textCoord);
        }        
        else
        {
            float d = dot(normalize(vertNormal), normalize(light));
            outColor = vec4(vertColor, 1.0) * d * texture2D(texture, textCoord);
        }
} 
