package ch.fhnw.comgr;

import ch.fhnw.comgr.matrix.Matrix4x4;
import ch.fhnw.comgr.obj.Obj;
import ch.fhnw.comgr.texture.ImageTexture;
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

        // set up a vao
        var vaoTriangle = glGenVertexArrays();
        glBindVertexArray(vaoTriangle);

        // load model
        Obj teapot = Obj.parse("axe");

        // upload model vertices to a vbo
        var triangleVertices = teapot.getVertexArray();
        var vboTriangleVertices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboTriangleVertices);
        glBufferData(GL_ARRAY_BUFFER, triangleVertices, GL_STATIC_DRAW);

        // upload model indices to a vbo
        var triangleIndices = teapot.getTriangleArray();
        var vboTriangleIndices = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboTriangleIndices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, triangleIndices, GL_STATIC_DRAW);

        // vertex position vao
        var posAttribIndex = glGetAttribLocation(hProgram, "inPos");
        if (posAttribIndex != -1) {
            glEnableVertexAttribArray(posAttribIndex);
            glVertexAttribPointer(posAttribIndex, 3, GL_FLOAT, false, 0, 0);
        }

        // normals vbo
        var normals = teapot.getNormalArray();
        var vboNormals = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboNormals);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);

        // normals vao
        var normalAttribIndex = glGetAttribLocation(hProgram, "inNormal");
        if (normalAttribIndex != -1) {
            glEnableVertexAttribArray(normalAttribIndex);
            glVertexAttribPointer(normalAttribIndex, 3, GL_FLOAT, false, 0, 0);
        }

        // texture coordinates vbo
        var textureCoordinates = teapot.getStArray();
        var vboTextureCoordinates = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboTextureCoordinates);
        glBufferData(GL_ARRAY_BUFFER, textureCoordinates, GL_STATIC_DRAW);

        // texture coordinates vao
        var stAttribIndex = glGetAttribLocation(hProgram, "inSt");
        if (stAttribIndex != -1) {
            glEnableVertexAttribArray(stAttribIndex);
            glVertexAttribPointer(stAttribIndex, 2, GL_FLOAT, false, 0, 0);
        }

        // image texture
        ImageTexture imageTexture = ImageTexture.ofResource("/obj/axe.png");

        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_SRGB8,
                imageTexture.image().getWidth(),
                imageTexture.image().getHeight(),
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                imageTexture.getPixels()
        );
        glBindTexture(GL_TEXTURE_2D, 0);

        // check for errors during all previous calls
        var error = glGetError();
        if (error != GL_NO_ERROR)
            throw new Exception(Integer.toString(error));

        int[] widthArray = new int[1];
        int[] heightArray = new int[1];

        // switch to our shader
        glUseProgram(hProgram);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        glUniform1i(glGetUniformLocation(hProgram, "imageTexture"), 0);

        // light direction
        glUniform3fv(
                glGetUniformLocation(hProgram, "lightDirection"),
                new Vector3(-1, 1, -1).normalize().toArray()
        );

        glUniform3fv(
                glGetUniformLocation(hProgram, "cameraDirection"),
                new Vector3(0, 0, 1).toArray()
        );

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
                            new Vector3(0, 0, -4),
                            new Vector3(0, 0, 0),
                            new Vector3(0, 1, 0)
                    ).transpose()
            );

//            var mvp0 = Matrix4x4.multiply(
//                    vp,
//                    Matrix4x4.createTranslation(new Vector3(2f, 0f, 0f)).transpose(),
//                    Matrix4x4.createRotationX(frameTime).transpose(),
//                    Matrix4x4.createRotationY(frameTime).transpose()
//            );
//            glUniformMatrix4fv(glGetUniformLocation(hProgram, "inMatrix"), false, mvp0.toArray());
//            glUniform1f(glGetUniformLocation(hProgram, "inTime"), frameTime);
//            glDrawElements(GL_TRIANGLES, triangleIndices.length, GL_UNSIGNED_INT, 0);

            var mvp1 = Matrix4x4.multiply(
                    vp,
//                    Matrix4x4.createScale(30f).transpose(),
                    Matrix4x4.createTranslation(0f, 0f, 12f).transpose(),
                    Matrix4x4.createRotationZ(frameTime * 0.5f + 1f).transpose(),
                    Matrix4x4.createRotationY(frameTime + 1f).transpose(),
//                    Matrix4x4.createTranslation(0.025f, -0.1f, 0f).transpose()
                    Matrix4x4.createRotationX((float) -Math.PI / 2).transpose()
            );
            glUniformMatrix4fv(glGetUniformLocation(hProgram, "inMatrix"), false, mvp1.toArray());
            glUniform1f(glGetUniformLocation(hProgram, "inTime"), frameTime + 1f);
            glDrawElements(GL_TRIANGLES, triangleIndices.length, GL_UNSIGNED_INT, 0);
//
//            var mvp2 = Matrix4x4.multiply(
//                    vp,
//                    Matrix4x4.createTranslation(new Vector3(-2f, 0f, 0f)).transpose(),
//                    Matrix4x4.createRotationX(frameTime + 2f).transpose(),
//                    Matrix4x4.createRotationY(frameTime + 2f).transpose()
//            );
//            glUniformMatrix4fv(glGetUniformLocation(hProgram, "inMatrix"), false, mvp2.toArray());
//            glUniform1f(glGetUniformLocation(hProgram, "inTime"), frameTime + 2f);
//            glDrawElements(GL_TRIANGLES, triangleIndices.length, GL_UNSIGNED_INT, 0);

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
