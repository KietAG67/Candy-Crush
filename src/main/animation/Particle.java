package main.animation;

import java.awt.Color;

public class Particle {
    public float x, y;
    public float vx, vy;
    public Color color;
    public float life, maxLife;
    public float size;
    public float rotation;
    public float rotationSpeed;

    public Particle(float x, float y, float vx, float vy, Color color, float maxLife, float size, float rotation, float rotationSpeed) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.maxLife = maxLife;
        this.life = maxLife;
        this.size = size;
        this.rotation = rotation;
        this.rotationSpeed = rotationSpeed;
    }
}
