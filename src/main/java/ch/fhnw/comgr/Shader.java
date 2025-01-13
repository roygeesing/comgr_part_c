package ch.fhnw.comgr;

import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public record Shader(int shader) {
    public static Shader readFile(String name, int type) {
        try (
                InputStream in = Shader.class.getResourceAsStream("/shader/" + name + ".glsl");
                InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(in));
                BufferedReader reader = new BufferedReader(Objects.requireNonNull(inputStreamReader))
        ) {
            String content = reader.lines().collect(Collectors.joining("\n"));

            int shader = GL20.glCreateShader(type);
            GL20.glShaderSource(shader, content);
            GL20.glCompileShader(shader);

            if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) != GL20.GL_TRUE) {
                throw new RuntimeException("Shader compilation failed");
            }

            return new Shader(shader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
