uniform vec2 resolution;
uniform float time;

uniform float sigma;     // The sigma value for the gaussian function: higher value means more blur
// A good value for 9x9 is around 3 to 5
// A good value for 7x7 is around 2.5 to 4
// A good value for 5x5 is around 2 to 3.5
// ... play around with this based on what you need :)

uniform float blurSize;  // This should usually be equal to
// 1.0f / texture_pixel_width for a horizontal blur, and
// 1.0f / texture_pixel_height for a vertical blur.

uniform sampler2D texture;  // Texture that will be blurred by this shader

const float pi = 3.14159265f;

#define VERTICAL_BLUR_9

// The following are all mutually exclusive macros for various
// seperable blurs of varying kernel size
#if defined(VERTICAL_BLUR_9)
const float numBlurPixelsPerSide = 4.0f;
const vec2  blurMultiplyVec      = vec2(0.0f, 1.0f);
#elif defined(HORIZONTAL_BLUR_9)
const float numBlurPixelsPerSide = 4.0f;
const vec2  blurMultiplyVec      = vec2(1.0f, 0.0f);
#elif defined(VERTICAL_BLUR_7)
const float numBlurPixelsPerSide = 3.0f;
const vec2  blurMultiplyVec      = vec2(0.0f, 1.0f);
#elif defined(HORIZONTAL_BLUR_7)
const float numBlurPixelsPerSide = 3.0f;
const vec2  blurMultiplyVec      = vec2(1.0f, 0.0f);
#elif defined(VERTICAL_BLUR_5)
const float numBlurPixelsPerSide = 2.0f;
const vec2  blurMultiplyVec      = vec2(0.0f, 1.0f);
#elif defined(HORIZONTAL_BLUR_5)
const float numBlurPixelsPerSide = 2.0f;
const vec2  blurMultiplyVec      = vec2(1.0f, 0.0f);
#else
// This only exists to get this shader to compile when no macros are defined
const float numBlurPixelsPerSide = 0.0f;
const vec2  blurMultiplyVec      = vec2(0.0f, 0.0f);
#endif

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;

    // Incremental Gaussian Coefficent Calculation (See GPU Gems 3 pp. 877 - 889)
    vec3 incrementalGaussian;
    incrementalGaussian.x = 1.0f / (sqrt(2.0f * pi) * sigma);
    incrementalGaussian.y = exp(-0.5f / (sigma * sigma));
    incrementalGaussian.z = incrementalGaussian.y * incrementalGaussian.y;

    vec4 avgValue = vec4(0.0f, 0.0f, 0.0f, 0.0f);
    float coefficientSum = 0.0f;

    // Take the central sample first...
    avgValue += texture2D(texture, uv.xy) * incrementalGaussian.x;
    coefficientSum += incrementalGaussian.x;
    incrementalGaussian.xy *= incrementalGaussian.yz;

    // Go through the remaining 8 vertical samples (4 on each side of the center)
    for (float i = 1.0f; i <= numBlurPixelsPerSide; i++) {
        avgValue += texture2D(texture, uv.xy - i * blurSize *
        blurMultiplyVec) * incrementalGaussian.x;
        avgValue += texture2D(texture, uv.xy + i * blurSize *
        blurMultiplyVec) * incrementalGaussian.x;
        coefficientSum += 2 * incrementalGaussian.x;
        incrementalGaussian.xy *= incrementalGaussian.yz;
    }

    gl_FragColor = avgValue / coefficientSum;
}