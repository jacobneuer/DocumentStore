package edu.yu.introtoalgs;

import org.junit.Test;
import org.junit.jupiter.api.*;

import static edu.yu.introtoalgs.OctopusCountI.ArmColor.GRAY;
import static edu.yu.introtoalgs.OctopusCountI.ArmColor.RED;
import static edu.yu.introtoalgs.OctopusCountI.ArmColor.BLACK;
import static edu.yu.introtoalgs.OctopusCountI.ArmTexture.SMOOTH;
import static edu.yu.introtoalgs.OctopusCountI.ArmTexture.SLIMY;
import static edu.yu.introtoalgs.OctopusCountI.ArmTexture.STICKY;
import static org.junit.jupiter.api.Assertions.*;

/*
Example Tests:
assertEquals - assertEquals(4, calculator.multiply(2, 2),"optional failure message");
assertNotEquals - assertNotEquals(3, calculator.multiply(2, 2),"optional failure message");
assertTrue - assertTrue('a' < 'b', () → "optional failure message");
assertFalse - assertFalse('a' > 'b', () → "optional failure message");
assertNotNull - assertNotNull(yourObject, "optional failure message");
assertNull - assertNull(yourObject, "optional failure message");
assertThrows - assertThrows(IllegalArgumentException.class, () -> user.setAge("23"));
disable test - @Disabled/@Disabled("Why Disabled")
*/
public class OctopusCountTest {

    @DisplayName("Test Trivial Equality of Octopuses")
    @Test
    public void testOne() {
        OctopusCount oc = new OctopusCount();
        OctopusCountI.ArmTexture[] armTextures = {SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        OctopusCountI.ArmColor[] armColors = {GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY};
        int[] armLengths = {1, 1, 1, 1, 1, 1, 1, 1};
        oc.addObservation(1, armColors, armLengths, armTextures);
        oc.addObservation(2, armColors, armLengths, armTextures);
        assertEquals(1, oc.countThem());
    }

}
