package ch.fhnw.comgr.texture;

import ch.fhnw.comgr.vector.Vector2;
import ch.fhnw.comgr.vector.Vector3;

public record LerpTexture(Texture left, Texture right, float t) implements Texture {
    @Override
    public Vector3 getColor(Vector2 position) {
        return Vector3.lerp(left.getColor(position), right.getColor(position), t);
    }
}
