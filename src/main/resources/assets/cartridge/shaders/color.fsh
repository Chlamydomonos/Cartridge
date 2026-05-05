#version 330

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform Globals {
    ivec3 CameraBlockPos;
    vec3 CameraOffset;
    vec2 ScreenSize;
    float GlintAlpha;
    float GameTime;
    int MenuBlurRadius;
    int UseRgss;
};

layout(std140) uniform ColorConfig {
    float TargetR;
    float TargetG;
    float TargetB;
    float MinAlpha;
    float MaxAlpha;
    float Speed;
};

out vec4 fragColor;

void main(){
    vec2 sizeRatio = OutSize / InSize;

    vec4 diffuseColor = texture(InSampler, texCoord);
    vec4 targetColor = vec4(TargetR, TargetG, TargetB, 1.0);
    float pulse = (sin(GameTime * Speed * 6.28) + 1.0) / 2.0;
    float rate = mix(MinAlpha, MaxAlpha, pulse);
    vec4 outColor = mix(diffuseColor, targetColor, rate);
    fragColor = vec4(outColor.rgb, 1.0);
}
