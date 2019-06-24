package com.mascotcapsule.micro3d.v3.impl;

import android.opengl.GLES20;
import android.util.Log;

public class GLUtils {

	private static final String TAG = GLUtils.class.getSimpleName();

	private static final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" +
					"attribute vec3 vNormal;" +
					"attribute vec4 vPosition;" +
					"varying vec3 fragColor;" +
					"void main() {" +
					"  vec3 vertexColor = vec3(1.0, 0.0, 0.0);" +
					"  fragColor = vec3(0.0);" +
					"  vec3 transformedVertexNormal = normalize((uMVPMatrix * vec4(vNormal, 0.0)).xyz);" +
					"  vec3 inverseLightDirection = normalize(vec3(0.0, 1.0, 1.0));" +
					/* Calculate the diffuse component. */
					"  vec3 diffuseLightIntensity = vec3(1.0, 1.0, 1.0);" +
					"  vec3 vertexDiffuseReflectionConstant = vertexColor;" +
					"  float normalDotLight = max(0.0, dot(transformedVertexNormal, inverseLightDirection));" +
					"  fragColor += normalDotLight * vertexDiffuseReflectionConstant * diffuseLightIntensity;" +
					/* Calculate the ambient component. */
					"  vec3 ambientLightIntensity = vec3(0.1, 0.1, 0.1);" +
					"  vec3 vertexAmbientReflectionConstant = vertexColor;" +
					"  fragColor += vertexAmbientReflectionConstant * ambientLightIntensity;" +
					/* Make sure the fragment colour is between 0 and 1. */
					"  clamp(fragColor, 0.0, 1.0);" +
					"  gl_Position = uMVPMatrix * vPosition;" +
					"}";

	private static final String fragmentShaderCode =
			"precision mediump float;" +
					"varying vec3 fragColor;" +
					"void main() {" +
					"  gl_FragColor = vec4(fragColor, 1.0);" +
					"}";

	public static int createProgram() {
		// prepare shaders and OpenGL program
		int vertexShader = loadShader(
				GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = loadShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		int mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program

		GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
		return mProgram;
	}

	/**
	 * Utility method for compiling a OpenGL shader.
	 *
	 * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
	 * method to debug shader coding errors.</p>
	 *
	 * @param type       - Vertex or fragment shader type.
	 * @param shaderCode - String containing the shader code.
	 * @return - Returns an id for the shader.
	 */
	private static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	/**
	 * Utility method for debugging OpenGL calls.
	 * <p>
	 * If the operation is not successful, the check throws an error.
	 *
	 * @param glOperation - Name of the OpenGL call to check.
	 */
	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

}
