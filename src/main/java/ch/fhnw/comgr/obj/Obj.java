package ch.fhnw.comgr.obj;

import ch.fhnw.comgr.mesh.Tri;
import ch.fhnw.comgr.mesh.Vertex;
import ch.fhnw.comgr.texture.ImageTexture;
import ch.fhnw.comgr.vector.Vector2;
import ch.fhnw.comgr.vector.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

public record Obj(
        List<Vertex> vertices,
        List<Tri> tris,
        List<Vector3> vList,
        List<Vector3> vnList,
        List<Vector2> vtList,
        Map<String, Mtl> materials) {

    private Obj() {
        this(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new HashMap<>()
        );
    }

    private void parseFile(String filename) {
        Mtl currentMaterial = Mtl.DEFAULT;

        try (
                InputStream objStream = ImageTexture.class.getResourceAsStream("/obj/" + filename + ".obj");
                BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(objStream)))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 1) {
                    continue;
                }

                switch (parts[0]) {
                    case "v":
                        vList.add(new Vector3(
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                -Float.parseFloat(parts[3])
                        ));
                        break;
                    case "vn":
                        vnList.add(new Vector3(
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                -Float.parseFloat(parts[3])
                        ));
                        break;
                    case "vt":
                        vtList.add(new Vector2(
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2])
                        ));
                        break;
                    case "mtllib":
                        MtlLib mtlLib = MtlLib.parse("/obj/" + parts[1]);
                        materials.putAll(mtlLib.materials());
                        break;
                    case "usemtl":
                        currentMaterial = materials.get(parts[1]);
                        break;
                    case "f":
                        String[] a = parts[1].split("/");
                        String[] b = parts[2].split("/");
                        String[] c = parts[3].split("/");

                        Vector3 va = vList.get(Integer.parseInt(a[0]) - 1);
                        Vector3 vb = vList.get(Integer.parseInt(b[0]) - 1);
                        Vector3 vc = vList.get(Integer.parseInt(c[0]) - 1);

                        Vector2 vta = Vector2.ZERO;
                        Vector2 vtb = Vector2.ZERO;

                        if (a.length >= 2) {
                            vta = vtList.get(Integer.parseInt(a[1]) - 1);
                            vtb = vtList.get(Integer.parseInt(b[1]) - 1);
                        }

                        Vector3 vna;
                        Vector3 vnb;

                        if (a.length >= 3) {
                            vna = vnList.get(Integer.parseInt(a[2]) - 1);
                            vnb = vnList.get(Integer.parseInt(b[2]) - 1);
                        } else {
                            Vector3 normal = Vector3.cross(
                                    vb.subtract(va),
                                    vc.subtract(va)
                            ).negate();

                            vna = normal;
                            vnb = normal;
                        }

                        int start = vertices.size();
                        vertices.add(new Vertex(va, vta, vna));
                        vertices.add(new Vertex(vb, vtb, vnb));

                        Vector3 vFirst = va;
                        Vector3 vPrevious = vb;

                        for (int i = 3; i < parts.length; i++) {
                            String[] elements = parts[i].split("/");

                            Vector3 v = vList.get(Integer.parseInt(elements[0]) - 1);
                            Vector2 vt = Vector2.ZERO;
                            Vector3 vn;

                            if (elements.length >= 2) {
                                vt = vtList.get(Integer.parseInt(elements[1]) - 1);
                            }

                            if (elements.length >= 3) {
                                vn = vnList.get(Integer.parseInt(elements[2]) - 1);
                            } else {
                                vn = Vector3.cross(
                                        v.subtract(vFirst),
                                        v.subtract(vPrevious)
                                ).negate();
                            }

                            vertices.add(new Vertex(v, vt, vn));
                            vPrevious = v;

                            tris.add(new Tri(start, start + i - 1, start + i - 2, currentMaterial));
                        }

                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("tris: " + tris.size());
    }

    public float[] getVertexArray() {
        float[] vertexArray = new float[vertices.size() * 3];

        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            int offset = i * 3;
            vertexArray[offset] = vertex.position().x();
            vertexArray[offset + 1] = vertex.position().y();
            vertexArray[offset + 2] = vertex.position().z();
        }

        return vertexArray;
    }

    public float[] getStArray() {
        float[] stArray = new float[vertices.size() * 2];

        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            int offset = i * 2;
            stArray[offset] = vertex.st().x();
            stArray[offset + 1] = -vertex.st().y();
        }

        return stArray;
    }

    public float[] getNormalArray() {
        float[] normalArray = new float[vertices.size() * 3];

        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            int offset = i * 3;
            normalArray[offset] = vertex.normal().x();
            normalArray[offset + 1] = vertex.normal().y();
            normalArray[offset + 2] = vertex.normal().z();
        }

        return normalArray;
    }

    public int[] getTriangleArray() {
        int[] triangleArray = new int[tris.size() * 3];

        for (int i = 0; i < tris.size(); i++) {
            Tri tri = tris.get(i);
            int offset = i * 3;
            triangleArray[offset] = tri.a();
            triangleArray[offset + 1] = tri.b();
            triangleArray[offset + 2] = tri.c();
        }

        return triangleArray;
    }

    public static Obj parse(String filename) {
        Obj obj = new Obj();
        obj.parseFile(filename);
        return obj;
    }
}
