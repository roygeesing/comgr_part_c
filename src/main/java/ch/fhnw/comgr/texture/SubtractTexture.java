package ch.fhnw.comgr.texture;

import ch.fhnw.comgr.vector.Vector2;
import ch.fhnw.comgr.vector.Vector3;

public record SubtractTexture(Texture left, Texture right) implements Texture {
    @Override
    public Vector3 getColor(Vector2 position) {
        return left.getColor(position).subtract(right.getColor(position));
    }
}
