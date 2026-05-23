package main.animation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnimationSystem {
    private List<Animation> animations;

    public AnimationSystem() {
        this.animations = new ArrayList<>();
    }

    public void addAnimation(Animation animation) {
        this.animations.add(animation);
    }

    public void update() {
        Iterator<Animation> it = animations.iterator();
        while (it.hasNext()) {
            Animation anim = it.next();
            anim.update();
            if (anim.isFinished()) {
                it.remove();
            }
        }
    }

    public void removeAnimation(Animation animation) {
        this.animations.remove(animation);
    }

    public boolean isAnimating() {
        return !this.animations.isEmpty();
    }

    public List<Animation> getAnimations() {
        return animations;
    }
}
