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
        if(svetlo == 0 || svetlo == 10)
        {
            outColor = vec4(vertColor, 1.0);
            if(svetlo == 10)
            {
                outColor *= texture2D(texture, textCoord);
            }
        }
        else if (svetlo == 1 || svetlo == 11)
        {
            outColor = vec4(vertColor, 1.0) * diff;
            if(svetlo == 11)
            {
                outColor *= texture2D(texture, textCoord);
            }
        }        
        else if(svetlo == 2 ||svetlo == 12)
        {
            float d = dot(normalize(vertNormal), normalize(light));
            outColor = vec4(vertColor, 1.0) * d;
            if(svetlo == 12)
            {
                outColor *= texture2D(texture, textCoord);
            }
        }
} 
