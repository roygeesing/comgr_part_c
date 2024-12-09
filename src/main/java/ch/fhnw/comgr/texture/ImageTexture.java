package ch.fhnw.comgr.texture;

import ch.fhnw.comgr.vector.Vector2;
import ch.fhnw.comgr.vector.Vector3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static ch.fhnw.comgr.vector.Vector3.BYTE_MAX_VALUE;

public record ImageTexture(BufferedImage image, float multiplier, float offset) implements Texture {
    public static ImageTexture ofResource(String resourcePath, float multiplier, float offset) {
        try (InputStream imageStream = ImageTexture.class.getResourceAsStream(resourcePath)) {
            return new ImageTexture(ImageIO.read(imageStream), multiplier, offset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ImageTexture ofResource(String resourcePath, float multiplier) {
        return ofResource(resourcePath, multiplier, 0);
    }

    public static ImageTexture ofResource(String resourcePath) {
        return ofResource(resourcePath, 1);
    }

    @Override
    public Vector3 getColor(Vector2 position) {
        int width = image.getWidth();
        int height = image.getHeight();

        float u = (position.x() + offset);
        float v = (1 - position.y());

        float exactX = (float) ((u - Math.floor(u)) * width);
        float exactY = (float) ((v - Math.floor(v)) * height);

        int x = (int) exactX;
        int y = (int) exactY;

        Vector3 a = Vector3.fromSrgb(image.getRGB(x, y));
        Vector3 b = Vector3.fromSrgb(image.getRGB(Math.min(x + 1, width - 1), y));
        Vector3 c = Vector3.fromSrgb(image.getRGB(x, Math.min(y + 1, height - 1)));
        Vector3 d = Vector3.fromSrgb(image.getRGB(Math.min(x + 1, width - 1), Math.min(y + 1, height - 1)));

        float fracX = exactX - (float) Math.floor(exactX);
        float fracY = exactY - (float) Math.floor(exactY);

//        return a;
        return Vector3.lerp(
                Vector3.lerp(a, b, fracX),
                Vector3.lerp(c, d, fracX),
                fracY
        );
    }

    @Override
    public Vector3 getColorWithoutGammaCorrection(Vector2 position) {
        int width = image.getWidth();
        int height = image.getHeight();

        int rgb = image.getRGB(
                (int) ((position.x() + offset) * width) % width,
                (int) (position.y() * height) % height
        );

        float red = ((rgb >> 16 & BYTE_MAX_VALUE) / (float) BYTE_MAX_VALUE);
        float green = ((rgb >> 8 & BYTE_MAX_VALUE) / (float) BYTE_MAX_VALUE);
        float blue = ((rgb & BYTE_MAX_VALUE) / (float) BYTE_MAX_VALUE);

        return new Vector3(red, green, blue);
    }

    public int[] getPixels() {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[x + y * width] = image.getRGB(x, y);
            }
        }

        return pixels;
    }
}
