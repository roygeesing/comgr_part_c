package ch.fhnw.comgr.obj;

import ch.fhnw.comgr.texture.ColorTexture;
import ch.fhnw.comgr.texture.Texture;
import ch.fhnw.comgr.vector.Vector2;
import ch.fhnw.comgr.vector.Vector3;

public class Mtl {
    public static final Mtl DEFAULT = new Mtl();

    private Vector3 ka;
    private Vector3 kd;
    private Vector3 ks;
    private float ns;
    private Texture mapKd;

    public Mtl(Vector3 ka, Vector3 kd, Vector3 ks, float ns, Texture mapKd) {
        this.ka = ka;
        this.kd = kd;
        this.ks = ks;
        this.ns = ns;
        this.mapKd = mapKd;
    }

    public Mtl() {
        this(Vector3.BLACK, Vector3.WHITE, Vector3.BLACK, 50, new ColorTexture(Vector3.WHITE));
    }

    public Vector3 getKa() {
        return ka;
    }

    public void setKa(Vector3 ka) {
        this.ka = ka;
    }

    public Vector3 getKd() {
        return kd;
    }

    public void setKd(Vector3 kd) {
        this.kd = kd;
    }

    public Vector3 getKs() {
        return ks;
    }

    public void setKs(Vector3 ks) {
        this.ks = ks;
    }

    public float getNs() {
        return ns;
    }

    public void setNs(float ns) {
        this.ns = ns;
    }

    public Texture getMapKd() {
        return mapKd;
    }

    public void setMapKd(Texture mapKd) {
        this.mapKd = mapKd;
    }

    public Vector3 getAmbient(Vector2 st) {
        return ka.multiply(getDiffuse(st));
    }

    public Vector3 getDiffuse(Vector2 st) {
        return kd.multiply(mapKd.getColor(st));
    }

    public Vector3 getSpecular() {
        return ks;
    }
}
