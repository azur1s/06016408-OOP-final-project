package engine.entities;

import java.util.List;

/**
 * A manager responsible for detecting collisions between collidable entities
 * and dispatching collision events.
 */
public class CollisionManager {
    /**
     * Detects collisions between active collidables and dispatches
     * {@link Collidable#onCollision(Collidable)} for both participants when
     * overlap is found.
     *
     * @param collidables objects to test for collisions
     */
    public void detectAndDispatch(List<Collidable> collidables) {
        for (int i = 0; i < collidables.size(); i++) {
            Collidable a = collidables.get(i);
            if (!a.isActive())
                continue;

            for (int j = i + 1; j < collidables.size(); j++) {
                Collidable b = collidables.get(j);
                if (!b.isActive())
                    continue;

                // Check layer and mask
                boolean aCollidesWithB = (a.getMask() & b.getLayer()) != 0;
                boolean bCollidesWithA = (b.getMask() & a.getLayer()) != 0;
                if (!(aCollidesWithB && bCollidesWithA)) {
                    continue; // No collision possible based on layers
                }

                // Check bounding box intersection
                if (a.getBoundingBox().intersects(b.getBoundingBox())) {
                    a.onCollision(b);
                    b.onCollision(a);
                }
            }
        }
    }
}
