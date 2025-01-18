package ch.fhnw.comgr.texture;

import ch.fhnw.comgr.vector.Vector2;
import ch.fhnw.comgr.vector.Vector3;

public interface Texture {
    Vector3 getColor(Vector2 position);

    int getWidth();
    int getHeight();

    default Vector3 getColorWithoutGammaCorrection(Vector2 position) {
        return getColor(position);
    }

    int[] getPixels();
}
