package ch.fhnw.comgr.texture;

import ch.fhnw.comgr.vector.Vector2;
import ch.fhnw.comgr.vector.Vector3;

public class GradientTexture implements Texture {
    @Override
    public Vector3 getColor(Vector2 position) {
        return Vector3.lerp(
                Vector3.lerp(Vector3.RED, Vector3.GREEN, position.x()),
                Vector3.lerp(Vector3.BLUE, Vector3.WHITE, position.x()),
                position.y()
        );
    }
}
