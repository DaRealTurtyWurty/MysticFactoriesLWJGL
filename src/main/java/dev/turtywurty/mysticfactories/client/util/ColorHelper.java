package dev.turtywurty.mysticfactories.client.util;

import org.joml.Vector4f;

public class ColorHelper {
    public static Vector4f unpackARGB(int color) {
        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        return new Vector4f(r, g, b, a);
    }

    public static int packARGB(float r, float g, float b, float a) {
        int ir = (int) (r * 255.0f) & 0xFF;
        int ig = (int) (g * 255.0f) & 0xFF;
        int ib = (int) (b * 255.0f) & 0xFF;
        int ia = (int) (a * 255.0f) & 0xFF;
        return (ia << 24) | (ir << 16) | (ig << 8) | ib;
    }

    public static int packARGB(Vector4f color) {
        return packARGB(color.x, color.y, color.z, color.w);
    }

    public static int packARGB(int r, int g, int b, int a) {
        int ir = r & 0xFF;
        int ig = g & 0xFF;
        int ib = b & 0xFF;
        int ia = a & 0xFF;
        return (ia << 24) | (ir << 16) | (ig << 8) | ib;
    }

    public static float getAlpha(int argbColor) {
        return ((argbColor >> 24) & 0xFF) / 255.0f;
    }

    public static float getRed(int argbColor) {
        return ((argbColor >> 16) & 0xFF) / 255.0f;
    }

    public static float getGreen(int argbColor) {
        return ((argbColor >> 8) & 0xFF) / 255.0f;
    }

    public static float getBlue(int argbColor) {
        return (argbColor & 0xFF) / 255.0f;
    }

    public static int withAlpha(int argbColor, float alpha) {
        int a = (int) (alpha * 255.0f) & 0xFF;
        return (argbColor & 0x00FFFFFF) | (a << 24);
    }

    public static int withAlpha(int argbColor, int alpha) {
        int a = alpha & 0xFF;
        return (argbColor & 0x00FFFFFF) | (a << 24);
    }

    public static int blendColors(int colorA, int colorB, float t) {
        float aA = getAlpha(colorA);
        float rA = getRed(colorA);
        float gA = getGreen(colorA);
        float bA = getBlue(colorA);

        float aB = getAlpha(colorB);
        float rB = getRed(colorB);
        float gB = getGreen(colorB);
        float bB = getBlue(colorB);

        float a = aA + (aB - aA) * t;
        float r = rA + (rB - rA) * t;
        float g = gA + (gB - gA) * t;
        float b = bA + (bB - bA) * t;

        return packARGB(r, g, b, a);
    }

    public static int blendColors(int colorA, int colorB, int t) {
        float tf = (t & 0xFF) / 255.0f;
        return blendColors(colorA, colorB, tf);
    }
}
