package main.animation;

import main.model.Candy;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DestroyAnimation extends Animation implements IParticleEffect {
    private List<Particle> particles;
    private static final Random random = new Random();

    public DestroyAnimation(Candy candy, boolean isSpecial) {
        this(candy, isSpecial, false);
    }

    public DestroyAnimation(Candy candy, boolean isSpecial, boolean isLight) {
        this.particles = new ArrayList<>();
        Color color = candy.getType().getColor();
        // The visual coordinates are based on grid cells (0 to cols-1). 
        // Adding 0.5f to center the particles on the cell.
        float startX = candy.getVisualX() + 0.5f;
        float startY = candy.getVisualY() + 0.5f;
        
        int particleCount;
        if (isLight) particleCount = 10;
        else particleCount = isSpecial ? 60 : 25;
        
        // Generate pixel particles
        for (int i = 0; i < particleCount; i++) {
            float angle = (float) (random.nextDouble() * Math.PI * 2);
            float speedMult = isSpecial ? 2.5f : 1.0f;
            if (isLight) speedMult *= 0.8f;
            float speed = (random.nextFloat() * 0.15f + 0.05f) * speedMult; // Faster out from center if special
            float vx = (float) Math.cos(angle) * speed;
            float vy = (float) Math.sin(angle) * speed;
            float sizeMult = isSpecial ? 1.5f : 1.0f;
            if (isLight) sizeMult *= 0.6f;
            float size = (random.nextFloat() * 0.25f + 0.1f) * sizeMult; // Bigger chunks if special
            float maxLife = random.nextFloat() * 20 + 15; // Frames (15 to 35 frames)
            if (isSpecial) maxLife += 15; // Longer duration for special
            if (isLight) maxLife *= 0.6f;
            float rotation = random.nextFloat() * 360f;
            float rotationSpeed = (random.nextFloat() - 0.5f) * 20f;
            
            // Add some gravity effect to vy if we want, but let's keep it mostly radial burst
            particles.add(new Particle(startX, startY, vx, vy, color, maxLife, size, rotation, rotationSpeed));
        }
        this.finished = false;
    }

    @Override
    public void update() {
        boolean allDead = true;
        for (Particle p : particles) {
            if (p.life > 0) {
                p.x += p.vx;
                p.y += p.vy;
                p.vy += 0.01f; // Slight gravity to make particles fall
                p.rotation += p.rotationSpeed;
                p.life--;
                allDead = false;
            }
        }
        if (allDead) {
            this.finished = true;
        }
    }

    @Override
    public List<Particle> getParticles() {
        return particles;
    }
}
