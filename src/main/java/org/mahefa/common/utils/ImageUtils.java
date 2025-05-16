package org.mahefa.common.utils;

import org.mahefa.common.enumerator.Rotate;
import org.springframework.util.CollectionUtils;

import java.util.List;

public final class ImageUtils {

    public static double getRotationAngle(List<Rotate> moves) {
        double currentAngle = 0d;

        if (!CollectionUtils.isEmpty(moves)) {
            for (Rotate rotate : moves) {
                if (rotate.equals(Rotate.FORWARD))
                    continue;

                double angle = currentAngle + rotate.getAngle();

                // Normalize the angle to be within 0-360 degrees
                angle = (angle % 360 + 360) % 360;
                currentAngle = angle;
            }
        }

        return currentAngle;
    }
}
