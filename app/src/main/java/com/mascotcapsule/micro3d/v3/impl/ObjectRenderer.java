package com.mascotcapsule.micro3d.v3.impl;

import android.opengl.GLES20;

public class ObjectRenderer {

	private final int program;
	private final int normalHandle;
	private final int positionHandle;
	private final int mvpMatrixHandle;

	// number of coordinates per vertex in this array
	private static final int COORDS_PER_VERTEX = 3;
	private static final int COLORS_PER_VERTEX = 4;

	public ObjectRenderer() {
		program = GLUtils.createProgram();
		positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
		normalHandle = GLES20.glGetAttribLocation(program, "vNormal");
		mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
	}

	public void draw(float[] mvpMatrix, FigureImpl figure) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(program);

		// Prepare the triangle coordinate data
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(
				positionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false,
				0, figure.triangleBuffer);

		// Prepare the vertex normal data
		GLES20.glEnableVertexAttribArray(normalHandle);
		GLES20.glVertexAttribPointer(
				normalHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false,
				0, figure.normalBuffer);

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
		GLUtils.checkGlError("glUniformMatrix4fv");

		int count = figure.indexBuffer.capacity();
		// Draw the triangle
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_SHORT, figure.indexBuffer);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(positionHandle);
		// Disable normal array
		GLES20.glDisableVertexAttribArray(normalHandle);
	}

}
