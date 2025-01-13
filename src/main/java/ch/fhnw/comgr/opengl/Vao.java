package ch.fhnw.comgr.opengl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public record Vao(int program, int vao) {
    public static Vao create(int program) {
        return new Vao(program, glGenVertexArrays());
    }

    public void bind() {
        glBindVertexArray(vao);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void setTris(int[] tris) {
        bind();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, glGenBuffers());
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, tris, GL_STATIC_DRAW);

        unbind();
    }

    public void addAttribPointer(String name, int size, Vbo vbo) {
        bind();
        vbo.bind();

        var attribIndex = glGetAttribLocation(program, name);
        if (attribIndex != -1) {
            glEnableVertexAttribArray(attribIndex);
            glVertexAttribPointer(attribIndex, size, GL_FLOAT, false, 0, 0);
        }

        vbo.unbind();
        unbind();
    }
}
