package com.lightning.northstar.api.planet;

import javax.annotation.Nullable;

/**
 * Defines a simple 2D orbit used by telescope rendering and fuel distance.
 * <p>
 * This is intentionally small for phase 1. Future JSON/datapack support can
 * map directly to these fields before more advanced orbit models are added.
 */
public record OrbitDefinition(
        /** Optional parent planet id. When set, originX/originY are ignored. */
        @Nullable String parentId,
        /** Fixed/orbit center X when there is no parent. */
        double originX,
        /** Fixed/orbit center Y when there is no parent. */
        double originY,
        /** Horizontal orbit radius. */
        double radiusX,
        /** Vertical orbit radius. */
        double radiusY,
        /** Radians advanced per tick-time unit. */
        double speed
) {

    /** Creates a body that never moves in telescope space. */
    public static OrbitDefinition fixed(double x, double y) {
        return new OrbitDefinition(null, x, y, 0, 0, 0);
    }

    /** Creates an orbit around an absolute origin. */
    public static OrbitDefinition aroundOrigin(double originX, double originY, double radiusX, double radiusY, double speed) {
        return new OrbitDefinition(null, originX, originY, radiusX, radiusY, speed);
    }

    /** Creates an orbit around another registered planet id. */
    public static OrbitDefinition around(String parentId, double radiusX, double radiusY, double speed) {
        return new OrbitDefinition(parentId, 0, 0, radiusX, radiusY, speed);
    }

    /** Computes current telescope-space position. */
    public PlanetPosition position(long time, @Nullable PlanetPosition parentPosition) {
        if (speed == 0 && radiusX == 0 && radiusY == 0) {
            return new PlanetPosition(originX, originY);
        }

        double centerX = parentPosition == null ? originX : parentPosition.x();
        double centerY = parentPosition == null ? originY : parentPosition.y();
        double radian = speed * time;
        return new PlanetPosition(
                centerX + Math.cos(radian) * radiusX,
                centerY + Math.sin(radian) * radiusY
        );
    }

}
