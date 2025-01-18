package ch.fhnw.comgr.opengl;

import ch.fhnw.comgr.matrix.Matrix3x3;
import ch.fhnw.comgr.matrix.Matrix4x4;
import ch.fhnw.comgr.obj.Obj;
import ch.fhnw.comgr.vector.Vector3;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public record SceneObject(Program program, Vao vao, int triangleCount, Vector3 position, Texture texture) {
    public static SceneObject create(String vertexShaderFile, String fragmentShaderFile, String file, Vector3 position, ch.fhnw.comgr.texture.Texture textureObject) {
        Texture texture = Texture.create(textureObject);

        SceneObject sceneObject = create(vertexShaderFile, fragmentShaderFile, file, position, texture);

        sceneObject.vao.bind();

        sceneObject.setUniform("imageTexture", texture);

        return sceneObject;
    }

    public static SceneObject create(String vertexShaderFile, String fragmentShaderFile, String file, Vector3 position, Vector3 color) {
        SceneObject sceneObject = create(vertexShaderFile, fragmentShaderFile, file, position, (Texture) null);

        sceneObject.vao.bind();
        sceneObject.setUniform("color", color);

        return sceneObject;
    }

    private static SceneObject create(String vertexShaderFile, String fragmentShaderFile, String file, Vector3 position, Texture texture) {
        Program program = Program.create(vertexShaderFile, fragmentShaderFile);
        program.use();

        // load model
        Obj object = Obj.parse(file);

        // set up a vao
        Vao vao = Vao.create(program.program());
        vao.bind();
        vao.setTris(object.getTriangleArray());

        // vertex positions
        Vbo posVbo = Vbo.create(object.getVertexArray());
        vao.addAttribPointer("inPos", 3, posVbo);

        // normals
        Vbo normalsVbo = Vbo.create(object.getNormalArray());
        vao.addAttribPointer("inNormal", 3, normalsVbo);

        // texture coordinates
        Vbo stVbo = Vbo.create(object.getStArray());
        vao.addAttribPointer("inSt", 2, stVbo);

        return new SceneObject(program, vao, object.tris().size(), position, texture);
    }

    public void draw() {
        program.use();
        vao.bind();

        if (texture != null) {
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
        }

        glDrawElements(GL_TRIANGLES, triangleCount * 3, GL_UNSIGNED_INT, 0);
    }

    public void setUniform(String name, Vector3 value) {
        program.setUniform(name, value);
    }

    public void setUniform(String name, Matrix3x3 value) {
        program.setUniform(name, value);
    }

    public void setUniform(String name, Matrix4x4 value) {
        program.setUniform(name, value);
    }

    public void setUniform(String name, Texture value) {
        program.setUniform(name, value);
    }

    public void setUniform(String name, float value) {
        program.setUniform(name, value);
    }
}
