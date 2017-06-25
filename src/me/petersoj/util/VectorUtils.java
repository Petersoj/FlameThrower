package me.petersoj.util;

import org.bukkit.util.Vector;

public class VectorUtils {

    // http://www.gamedev.net/topic/338987-aabb---line-segment-intersection-test
    public static boolean hasIntersection(Vector start, Vector end, Vector min, Vector max) {
        Vector d = end.clone().subtract(start).multiply(0.5);
        Vector e = max.clone().subtract(min).multiply(0.5);
        Vector c = start.clone().add(d).subtract(min.clone().add(max).multiply(0.5));
        Vector ad = new Vector(Math.abs(d.getX()), Math.abs(d.getY()), Math.abs(d.getZ()));

        if (Math.abs(c.getX()) > e.getX() + ad.getX())
            return false;
        if (Math.abs(c.getY()) > e.getY() + ad.getY())
            return false;
        if (Math.abs(c.getZ()) > e.getZ() + ad.getZ())
            return false;
        if (Math.abs(d.getY() * c.getZ() - d.getZ() * c.getY()) > e.getY() * ad.getZ() + e.getZ() * ad.getY() + 0.0001f)
            return false;
        if (Math.abs(d.getZ() * c.getX() - d.getX() * c.getZ()) > e.getZ() * ad.getX() + e.getX() * ad.getZ() + 0.0001f)
            return false;
        if (Math.abs(d.getX() * c.getY() - d.getY() * c.getX()) > e.getX() * ad.getY() + e.getY() * ad.getX() + 0.0001f)
            return false;
        return true;
    }

}
