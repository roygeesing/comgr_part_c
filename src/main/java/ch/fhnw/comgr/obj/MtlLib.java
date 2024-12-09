package ch.fhnw.comgr.obj;

import ch.fhnw.comgr.texture.ImageTexture;
import ch.fhnw.comgr.vector.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record MtlLib(Map<String, Mtl> materials) {
    private MtlLib() {
        this(new HashMap<>());
    }

    private void parseFile(String filename) {
        Mtl currentMaterial = null;

        try (
                InputStream objStream = ImageTexture.class.getResourceAsStream(filename);
                BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(objStream)))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 1) {
                    continue;
                }

                switch (parts[0]) {
                    case "newmtl":
                        currentMaterial = new Mtl();
                        materials.put(parts[1], currentMaterial);
                        break;
                    case "Ka":
                        currentMaterial.setKa(parseVector3(parts));
                        break;
                    case "Kd":
                        currentMaterial.setKd(parseVector3(parts));
                        break;
                    case "Ks":
                        currentMaterial.setKs(parseVector3(parts));
                        break;
                    case "Ns":
                        currentMaterial.setNs(Float.parseFloat(parts[1]));
                        break;
//                    case "map_Ka":
//                        currentMaterial.setKa(ImageTexture.ofResource("/" + parts[1]));
//                        break;
                    case "map_Kd":
                        currentMaterial.setMapKd(ImageTexture.ofResource("/obj/" + parts[1]));
                        break;
//                    case "map_Ks":
//                        currentMaterial.setKs(ImageTexture.ofResource("/" + parts[1]));
//                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Vector3 parseVector3(String[] parts) {
        return new Vector3(
                Float.parseFloat(parts[1]),
                Float.parseFloat(parts[2]),
                Float.parseFloat(parts[3])
        );
    }

    public static MtlLib parse(String filename) {
        MtlLib mtlLib = new MtlLib();
        mtlLib.parseFile(filename);
        return mtlLib;
    }
}
