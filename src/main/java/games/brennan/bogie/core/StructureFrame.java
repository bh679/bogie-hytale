package games.brennan.bogie.core;

/**
 * The kinematic frame of a moving structure: where its local origin sits in
 * world space, its yaw, and its velocity. Immutable — advancing time returns
 * a new frame.
 *
 * Local (structure) space is the coordinate space blocks are authored in;
 * world space is where the structure currently is. This mirrors the
 * ship-space / world-space split Dungeon Train already builds against.
 */
public record StructureFrame(Vec3 origin, double yawDegrees, Vec3 velocity) {

    public static StructureFrame at(Vec3 origin) {
        return new StructureFrame(origin, 0, Vec3.ZERO);
    }

    /** Advance the frame by dt seconds of linear motion. */
    public StructureFrame step(double dtSeconds) {
        return new StructureFrame(origin.add(velocity.scale(dtSeconds)), yawDegrees, velocity);
    }

    public StructureFrame withVelocity(Vec3 newVelocity) {
        return new StructureFrame(origin, yawDegrees, newVelocity);
    }

    public StructureFrame withYaw(double newYawDegrees) {
        return new StructureFrame(origin, newYawDegrees, velocity);
    }

    /** Transform a point from structure-local space into world space. */
    public Vec3 localToWorld(Vec3 local) {
        double radians = Math.toRadians(yawDegrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        Vec3 rotated = new Vec3(
                local.x() * cos - local.z() * sin,
                local.y(),
                local.x() * sin + local.z() * cos);
        return rotated.add(origin);
    }

    /** Transform a point from world space into structure-local space. */
    public Vec3 worldToLocal(Vec3 world) {
        Vec3 translated = world.subtract(origin);
        double radians = Math.toRadians(-yawDegrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new Vec3(
                translated.x() * cos - translated.z() * sin,
                translated.y(),
                translated.x() * sin + translated.z() * cos);
    }
}
