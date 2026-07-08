package games.brennan.bogie.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StructureFrameTest {

    private static final double EPSILON = 1e-9;

    @Test
    void stepAdvancesOriginByVelocity() {
        StructureFrame frame = StructureFrame.at(Vec3.ZERO).withVelocity(new Vec3(2, 0, 0));
        StructureFrame stepped = frame.step(0.5);
        assertEquals(1.0, stepped.origin().x(), EPSILON);
        assertEquals(0.0, frame.origin().x(), EPSILON, "step must not mutate the original frame");
    }

    @Test
    void localToWorldAppliesYawThenTranslation() {
        StructureFrame frame = StructureFrame.at(new Vec3(10, 0, 0)).withYaw(90);
        Vec3 world = frame.localToWorld(new Vec3(1, 0, 0));
        assertEquals(10.0, world.x(), EPSILON);
        assertEquals(1.0, world.z(), EPSILON);
    }

    @Test
    void worldToLocalInvertsLocalToWorld() {
        StructureFrame frame = StructureFrame.at(new Vec3(5, 64, -3)).withYaw(37.5);
        Vec3 local = new Vec3(2, 1, -4);
        Vec3 roundTripped = frame.worldToLocal(frame.localToWorld(local));
        assertEquals(local.x(), roundTripped.x(), EPSILON);
        assertEquals(local.y(), roundTripped.y(), EPSILON);
        assertEquals(local.z(), roundTripped.z(), EPSILON);
    }
}
