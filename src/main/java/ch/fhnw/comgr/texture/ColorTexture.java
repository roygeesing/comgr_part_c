package ch.fhnw.comgr.texture;

import ch.fhnw.comgr.vector.Vector2;
import ch.fhnw.comgr.vector.Vector3;

public record ColorTexture(Vector3 color) implements Texture {
    @Override
    public Vector3 getColor(Vector2 position) {
        return color;
    }
}
