package ch.fhnw.comgr.texture;

import ch.fhnw.comgr.vector.Vector2;
import ch.fhnw.comgr.vector.Vector3;

import static ch.fhnw.comgr.vector.Vector3.BYTE_MAX_VALUE;

public record CheckerboardTexture(int width, int height, float tileSize, Vector3 color0, Vector3 color1) implements Texture {
    @Override
    public Vector3 getColor(Vector2 position) {
        int widthTiles = (int) (position.x() / tileSize);
        int heightTiles = (int) (position.y() / tileSize);
        return (widthTiles + heightTiles) % 2 == 0 ? color1 : color0;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int[] getPixels() {
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vector3 color = getColor(new Vector2(x, y));

                pixels[x + y * width] = (BYTE_MAX_VALUE << 24) + ((int) (color.x() * BYTE_MAX_VALUE)) + ((int) (color.y() * BYTE_MAX_VALUE) << 8) + ((int) (color.z() * BYTE_MAX_VALUE) << 16);
            }
        }

        return pixels;
    }
}
