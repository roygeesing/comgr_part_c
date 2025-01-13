package ch.fhnw.comgr.opengl;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

public record Vbo(int vbo) {
    public static Vbo create(float[] data) {
        Vbo vbo = new Vbo(glGenBuffers());

        vbo.bind();
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        vbo.unbind();

        return vbo;
    }

    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
    }

    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
