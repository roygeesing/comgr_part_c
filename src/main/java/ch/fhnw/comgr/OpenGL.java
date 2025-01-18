package ch.fhnw.comgr;

import ch.fhnw.comgr.matrix.Matrix3x3;
import ch.fhnw.comgr.matrix.Matrix4x4;
import ch.fhnw.comgr.obj.Obj;
import ch.fhnw.comgr.opengl.*;
import ch.fhnw.comgr.texture.ImageTexture;
import ch.fhnw.comgr.vector.Vector3;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLDebugMessageCallback;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;

public class OpenGL {
    private static long setupOpenGl() {
        // let GLFW work on the main thread (for macOS)
        // read the following if you want to create windows with awt/swing/javaFX:
        // https://stackoverflow.com/questions/47006058/lwjgl-java-awt-headlessexception-thrown-when-making-a-jframe
        System.setProperty("java.awt.headless", "true");

        // open a window
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        var hWindow = GLFW.glfwCreateWindow(720, 720, "ComGr", 0, 0);
        GLFW.glfwSetWindowSizeCallback(hWindow, (window, width, height) -> {
            var w = new int[1];
            var h = new int[1];
            GLFW.glfwGetFramebufferSize(window, w, h);
            glViewport(0, 0, w[0], h[0]);
        });
        GLFW.glfwMakeContextCurrent(hWindow);
        GLFW.glfwSwapInterval(1);
        createCapabilities();

        // set up opengl
        if (GLFW.glfwExtensionSupported("GL_KHR_debug")) {
            org.lwjgl.opengl.GL43.glDebugMessageCallback(
                    GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                        var msg = GLDebugMessageCallback.getMessage(length, message);
                        if (type == org.lwjgl.opengl.GL43.GL_DEBUG_TYPE_ERROR)
                            throw new RuntimeException(msg);
                        else
                            System.out.println(msg);
                    }), 0);
            glEnable(org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT);
            glEnable(org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);
        }
        glEnable(GL_FRAMEBUFFER_SRGB);
        glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
//        glClearColor(1f, 1f, 1f, 0.0f);
//        glClearDepth(1);
        glEnable(GL_DEPTH_TEST);
//        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);
//        glCullFace(GL_FRONT);

        return hWindow;
    }

    public static void main(String[] args) throws Exception {
        long hWindow = setupOpenGl();

        List<SceneObject> sceneObjects = List.of(
                SceneObject.create("vertex", "texture_fragment", "cube", new Vector3(.5f, 0, 0), "/obj/tree.png"),
                SceneObject.create("vertex", "color_fragment", "cube", new Vector3(-.5f, 0, 3), Vector3.BLUE)
        );

        for (SceneObject sceneObject : sceneObjects) {
            sceneObject.setUniform("lightDirection", new Vector3(-1, 1, -1).normalize());
            sceneObject.setUniform("cameraDirection", new Vector3(0, 0, 1));
        }

        Vector3 cameraPosition = new Vector3(0, 0, -4);

        sceneObjects = sceneObjects.stream()
                .sorted(Comparator.comparing(sceneObject -> -cameraPosition.subtract(sceneObject.position()).magnitude()))
                .toList();

//        SceneObject object = SceneObject.create("vertex", "fragment", "cube", "/obj/tree.png");

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
//        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(true);

        // check for errors during all previous calls
        var error = glGetError();
        if (error != GL_NO_ERROR)
            throw new Exception(Integer.toString(error));

        int[] widthArray = new int[1];
        int[] heightArray = new int[1];

        // render loop
        var startTime = System.currentTimeMillis();
        while (!GLFW.glfwWindowShouldClose(hWindow)) {
            float frameTime = (System.currentTimeMillis() - startTime) * 0.001f;

            GLFW.glfwGetWindowSize(hWindow, widthArray, heightArray);
            int width = widthArray[0];
            int height = heightArray[0];

            // clear screen and z-buffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            var vp = Matrix4x4.multiply(
                    Matrix4x4.createPerspectiveFieldOfView(
                            (float) Math.toRadians(90),
                            (float) width / height,
                            0.1f,
                            100f
                    ).transpose(),
                    Matrix4x4.createLookAt(
                            cameraPosition,
                            new Vector3(0, 0, 0),
                            new Vector3(0, 1, 0)
                    ).transpose()
            );

            for (SceneObject sceneObject : sceneObjects.subList(0, 2)) {
                var modelMatrix = Matrix4x4.multiply(
//                    Matrix4x4.createScale(30f).transpose(),
                        Matrix4x4.createTranslation(sceneObject.position()).transpose(),
                        Matrix4x4.createRotationZ(frameTime * 0.5f + 1f).transpose(),
                        Matrix4x4.createRotationY(frameTime + 1f).transpose(),
//                    Matrix4x4.createTranslation(0.025f, -0.1f, 0f).transpose()
                        Matrix4x4.createRotationX((float) -Math.PI / 2).transpose()
                );

                var mvp = Matrix4x4.multiply(
                        vp,
                        modelMatrix
                );
                Matrix3x3 normalMatrix = modelMatrix.invert().transpose().multiply(modelMatrix.getDeterminant()).to3x3();

                sceneObject.setUniform("mvpMatrix", mvp);
                sceneObject.setUniform("inTime", frameTime);
                sceneObject.setUniform("normalMatrix", normalMatrix);

                sceneObject.draw();
            }

            // display
            GLFW.glfwSwapBuffers(hWindow);
            GLFW.glfwPollEvents();

            error = glGetError();
            if (error != GL_NO_ERROR)
                throw new Exception(Integer.toString(error));
        }

        GLFW.glfwDestroyWindow(hWindow);
        GLFW.glfwTerminate();
    }
}
