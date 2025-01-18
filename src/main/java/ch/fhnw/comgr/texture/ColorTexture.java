package ch.fhnw.comgr.texture;

import ch.fhnw.comgr.vector.Vector2;
import ch.fhnw.comgr.vector.Vector3;

public record ColorTexture(Vector3 color) implements Texture {
    @Override
    public Vector3 getColor(Vector2 position) {
        return color;
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @Override
    public int[] getPixels() {
        return new int[0];
    }
}
