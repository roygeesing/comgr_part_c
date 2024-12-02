package ch.fhnw.comgr;

import ch.fhnw.comgr.matrix.Matrix4x4;
import ch.fhnw.comgr.vector.Vector3;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLDebugMessageCallback;

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
    public static void main(String[] args) throws Exception {
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
//        glClearDepth(1);
        glEnable(GL_DEPTH_TEST);
//        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);
//        glCullFace(GL_FRONT);

        // load, compile and link shaders
        Shader vertexShader = Shader.readFile("vertex", GL_VERTEX_SHADER);
        Shader fragmentShader = Shader.readFile("fragment", GL_FRAGMENT_SHADER);

        // link shaders to a program
        var hProgram = glCreateProgram();
        vertexShader.attach(hProgram);
        fragmentShader.attach(hProgram);
        glLinkProgram(hProgram);
        if (glGetProgrami(hProgram, GL_LINK_STATUS) != GL_TRUE)
            throw new Exception(glGetProgramInfoLog(hProgram));

        // upload model vertices to a vbo
        var triangleVertices = new float[] {
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
        };
        var vboTriangleVertices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboTriangleVertices);
        glBufferData(GL_ARRAY_BUFFER, triangleVertices, GL_STATIC_DRAW);

        // upload model indices to a vbo
        var triangleIndices = new int[] {
                2, 1, 0,
                1, 2, 3,
                4, 5, 6,
                7, 6, 5,

                0, 1, 4,
                5, 4, 1,
                6, 3, 2,
                3, 6, 7,

                4, 2, 0,
                2, 4, 6,
                1, 3, 5,
                7, 5, 3,
        };
        var vboTriangleIndices = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboTriangleIndices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, triangleIndices, GL_STATIC_DRAW);

        // set up a vao
        var vaoTriangle = glGenVertexArrays();
        glBindVertexArray(vaoTriangle);
        var posAttribIndex = glGetAttribLocation(hProgram, "inPos");
        if (posAttribIndex != -1) {
            glEnableVertexAttribArray(posAttribIndex);
//            glBindBuffer(GL_ARRAY_BUFFER, vboTriangleVertices);
            glVertexAttribPointer(posAttribIndex, 3, GL_FLOAT, false, 0, 0);
        }
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboTriangleIndices);

        // color vbo
        float[] colors = new float[] {
                1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f,
                1f, 0f, 1f,
                1f, 1f, 0f,
                0f, 1f, 1f,
                1f, 1f, 1f,
                0f, 0f, 0f,
        };
        int vboColor = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboColor);
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);

        // color vao
        int colorAttrib = glGetAttribLocation(hProgram, "inColor");
        if (colorAttrib != -1) {
            glEnableVertexAttribArray(colorAttrib);
            glVertexAttribPointer(colorAttrib, 3, GL_FLOAT, false, 0, 0);
        }

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

            // switch to our shader
            glUseProgram(hProgram);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboTriangleIndices);

            var vp = Matrix4x4.multiply(
                    Matrix4x4.createPerspectiveFieldOfView(
                            (float) Math.toRadians(90),
                            (float) width / height,
                            0.1f,
                            100f
                    ).transpose(),
                    Matrix4x4.createLookAt(
                            new Vector3(0, 0, -4),
                            new Vector3(0, 0, 0),
                            new Vector3(0, 1, 0)
                    ).transpose()
            );

            var mvp0 = Matrix4x4.multiply(
                    vp,
                    Matrix4x4.createTranslation(new Vector3(2f, 0f, 0f)).transpose(),
                    Matrix4x4.createRotationX(frameTime).transpose(),
                    Matrix4x4.createRotationY(frameTime).transpose()
            );
            glUniformMatrix4fv(glGetUniformLocation(hProgram, "inMatrix"), false, mvp0.toArray());
            glUniform1f(glGetUniformLocation(hProgram, "inTime"), frameTime);
            glDrawElements(GL_TRIANGLES, triangleIndices.length, GL_UNSIGNED_INT, 0);

            var mvp1 = Matrix4x4.multiply(
                    vp,
                    Matrix4x4.createTranslation(new Vector3(0f, 0f, 0f)).transpose(),
                    Matrix4x4.createRotationX(frameTime + 1f).transpose(),
                    Matrix4x4.createRotationY(frameTime + 1f).transpose()
            );
            glUniformMatrix4fv(glGetUniformLocation(hProgram, "inMatrix"), false, mvp1.toArray());
            glUniform1f(glGetUniformLocation(hProgram, "inTime"), frameTime + 1f);
            glDrawElements(GL_TRIANGLES, triangleIndices.length, GL_UNSIGNED_INT, 0);

            var mvp2 = Matrix4x4.multiply(
                    vp,
                    Matrix4x4.createTranslation(new Vector3(-2f, 0f, 0f)).transpose(),
                    Matrix4x4.createRotationX(frameTime + 2f).transpose(),
                    Matrix4x4.createRotationY(frameTime + 2f).transpose()
            );
            glUniformMatrix4fv(glGetUniformLocation(hProgram, "inMatrix"), false, mvp2.toArray());
            glUniform1f(glGetUniformLocation(hProgram, "inTime"), frameTime + 2f);
            glDrawElements(GL_TRIANGLES, triangleIndices.length, GL_UNSIGNED_INT, 0);

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
