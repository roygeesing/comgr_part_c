/*
 * Copyright (c) 2013 - 2024 Simon Felix
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.comgr.matrix;

import ch.fhnw.comgr.vector.Vector3;

public record Matrix3x3(
		float m11, float m12, float m13,
		float m21, float m22, float m23,
		float m31, float m32, float m33
) {

//	public static Matrix3x4 multiply(Matrix3x4 a, float b) {
//		return new Matrix3x4(a.m11 * b, a.m12 * b, a.m21 * b, a.m22 * b);
//	}

	public static Vector3 multiply(Matrix3x3 a, Vector3 b) {
		return new Vector3(
				a.m11 * b.x() + a.m12 * b.y() + a.m13 * b.z(),
				a.m21 * b.x() + a.m22 * b.y() + a.m23 * b.z(),
				a.m31 * b.x() + a.m32 * b.y() + a.m33 * b.z()
		);
	}

//	public Matrix3x3 multiply(float s) {
//		return multiply(this, s);
//	}

	public Vector3 multiply(Vector3 v) {
		return multiply(this, v);
	}

	public static Matrix3x3 multiply(Matrix3x3 a, Matrix3x3 b) {
		return new Matrix3x3(
				a.m11 * b.m11 + a.m12 * b.m21 + a.m13 * b.m31,
				a.m11 * b.m12 + a.m12 * b.m22 + a.m13 * b.m32,
				a.m11 * b.m13 + a.m12 * b.m23 + a.m13 * b.m33,

				a.m21 * b.m11 + a.m22 * b.m21 + a.m23 * b.m31,
				a.m21 * b.m12 + a.m22 * b.m22 + a.m23 * b.m32,
				a.m21 * b.m13 + a.m22 * b.m23 + a.m23 * b.m33,

				a.m31 * b.m11 + a.m32 * b.m21 + a.m33 * b.m31,
				a.m31 * b.m12 + a.m32 * b.m22 + a.m33 * b.m32,
				a.m31 * b.m13 + a.m32 * b.m23 + a.m33 * b.m33
		);
	}

	public Matrix3x3 multiply(Matrix3x3 other) {
		return multiply(this, other);
	}

	public float[] toArray() {
		return new float[] { m11, m12, m13, m21, m22, m23, m31, m32, m33 };
	}
}
