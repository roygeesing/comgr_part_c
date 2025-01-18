package ch.fhnw.comgr.opengl;

import ch.fhnw.comgr.Shader;
import ch.fhnw.comgr.matrix.Matrix3x3;
import ch.fhnw.comgr.matrix.Matrix4x4;
import ch.fhnw.comgr.vector.Vector3;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public record Program(int program) {
    public static Program create(String vertexShaderName, String fragmentShaderName) {
        Shader vertexShader = Shader.readFile(vertexShaderName, GL_VERTEX_SHADER);
        Shader fragmentShader = Shader.readFile(fragmentShaderName, GL_FRAGMENT_SHADER);

        Program program = new Program(glCreateProgram());

        glAttachShader(program.program, vertexShader.shader());
        glAttachShader(program.program, fragmentShader.shader());

        glLinkProgram(program.program());
        if (glGetProgrami(program.program(), GL_LINK_STATUS) != GL_TRUE)
            throw new RuntimeException(glGetProgramInfoLog(program.program()));

        return program;
    }

    public void use() {
        glUseProgram(program);
    }

    public void setUniform(String name, int value) {
        use();
        glUniform1i(glGetUniformLocation(program, name), value);
    }

    public void setUniform(String name, float value) {
        use();
        glUniform1f(glGetUniformLocation(program, name), value);
    }

    public void setUniform(String name, Vector3 vector3) {
        use();
        glUniform3fv(glGetUniformLocation(program, name), vector3.toArray());
    }

    public void setUniform(String name, Matrix3x3 matrix3x3) {
        use();
        glUniformMatrix3fv(glGetUniformLocation(program, name), false, matrix3x3.toArray());
    }

    public void setUniform(String name, Matrix4x4 matrix4x4) {
        use();
        glUniformMatrix4fv(glGetUniformLocation(program, name), false, matrix4x4.toArray());
    }

    public void setUniform(String name, Texture texture) {
        use();
        setUniform(name, 0);
    }
}
