package engine.entities;

/**
 * Collision layers represented as bit flags.
 *
 * <p>
 * Each constant uses `1 << n` so exactly one bit is set:
 * PLAYER=0001, ENEMY=0010, PLAYER_PROJECTILE=0100, ENEMY_PROJECTILE=1000.
 * This allows combining layers with bitwise OR and checking membership with
 * bitwise AND &.
 *
 * For example, an entity on the PLAYER layer that should collide with ENEMY and
 * PLAYER_PROJECTILE:
 *
 * <pre>
 * int playerLayer = CollisionLayer.PLAYER;
 * int playerMask = CollisionLayer.ENEMY | CollisionLayer.PLAYER_PROJECTILE;
 * </p>
 */
public final class CollisionLayer {
    public static final int PLAYER = 1 << 0;
    public static final int ENEMY = 1 << 1;
    public static final int PLAYER_PROJECTILE = 1 << 2;
    public static final int ENEMY_PROJECTILE = 1 << 3;
}
