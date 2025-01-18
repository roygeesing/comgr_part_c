package ch.fhnw.comgr.opengl;

import ch.fhnw.comgr.texture.ImageTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.GL_SRGB8;
import static org.lwjgl.opengl.GL21.GL_SRGB8_ALPHA8;

public record Texture(int texture) {

    public static Texture create(ch.fhnw.comgr.texture.Texture textureObject) {
        Texture texture = new Texture(glGenTextures());

        texture.bind();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA8,
                textureObject.getWidth(),
                textureObject.getHeight(),
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                textureObject.getPixels()
        );

        GL30.glGenerateMipmap(GL_TEXTURE_2D);

        texture.unbind();

        return texture;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
