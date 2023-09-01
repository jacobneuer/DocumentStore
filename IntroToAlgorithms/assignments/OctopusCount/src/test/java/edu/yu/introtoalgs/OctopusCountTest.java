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

    @DisplayName("Test Trivial Inequality of Octopuses")
    @Test
    public void testTwo() {
        OctopusCount oc = new OctopusCount();
        OctopusCountI.ArmTexture[] armTextures = {SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        OctopusCountI.ArmColor[] armColors = {GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY};
        int[] armLengths = {1, 1, 1, 1, 1, 1, 1, 1};
        int[] armLengths2 = {2, 1, 1, 1, 1, 1, 1, 1};
        oc.addObservation(1, armColors, armLengths, armTextures);
        oc.addObservation(2, armColors, armLengths2, armTextures);
        assertEquals(2, oc.countThem());
    }

    @DisplayName("Test Complicated Equality of Octopuses")
    @Test
    public void testThree() {
        OctopusCount oc = new OctopusCount();
        OctopusCountI.ArmTexture[] armTextures = {STICKY, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        OctopusCountI.ArmColor[] armColors = {GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY};
        int[] armLengths = {1, 1, 1, 1, 1, 1, 1, 1};
        OctopusCountI.ArmTexture[] armTextures2 = {SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, STICKY, SMOOTH, SMOOTH};
        OctopusCountI.ArmColor[] armColors2 = {GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY};
        int[] armLengths2 = {1, 1, 1, 1, 1, 1, 1, 1};
        oc.addObservation(1, armColors, armLengths, armTextures);
        oc.addObservation(2, armColors2, armLengths2, armTextures2);
        assertEquals(1, oc.countThem());
    }

    @DisplayName("Test Complicated Equality of Octopuses")
    @Test
    public void testFour() {
        OctopusCount oc = new OctopusCount();
        OctopusCountI.ArmTexture[] armTextures = {STICKY, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        OctopusCountI.ArmColor[] armColors = {GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, RED};
        int[] armLengths = {6, 1, 1, 1, 1, 1, 1, 7};
        OctopusCountI.ArmTexture[] armTextures2 = {SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, STICKY, SMOOTH, SMOOTH};
        OctopusCountI.ArmColor[] armColors2 = {GRAY, RED, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY};
        int[] armLengths2 = {1, 7, 1, 1, 1, 6, 1, 1};
        oc.addObservation(1, armColors, armLengths, armTextures);
        oc.addObservation(2, armColors2, armLengths2, armTextures2);
        assertEquals(1, oc.countThem());
    }

    @DisplayName("Test Complicated Equality of Octopuses Multiple Times")
    @Test
    public void testFive() {
        OctopusCount oc = new OctopusCount();
        for (int i = 0; i < 10; i++) {
            OctopusCountI.ArmTexture[] armTextures = {STICKY, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
            OctopusCountI.ArmColor[] armColors = {GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, RED};
            int[] armLengths = {6, 1, 1, 1, 1, 1, 1, 2};
            OctopusCountI.ArmTexture[] armTextures2 = {SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, STICKY, SMOOTH, SMOOTH};
            OctopusCountI.ArmColor[] armColors2 = {GRAY, RED, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY};
            int[] armLengths2 = {1, 2, 1, 1, 1, 6, 1, 1};
            oc.addObservation(1, armColors, armLengths, armTextures);
            oc.addObservation(2, armColors2, armLengths2, armTextures2);
        }
        assertEquals(1, oc.countThem());
    }

    @DisplayName("Test Randomized Equality of Octopuses With Large Data Set")
    @Test
    public void testSix() {
        OctopusCount oc = new OctopusCount();
        for (int i = 0; i < 32000; i++) {
            OctopusCountI.ArmTexture[] armTextures = new OctopusCountI.ArmTexture[8];
            OctopusCountI.ArmColor[] armColors = new OctopusCountI.ArmColor[8];
            int[] armLengths = new int[8];
            for (int k = 0; k < 8; k++) {
                double random = Math.random();
                if (random < .33) {
                    armTextures[k] = STICKY;
                }
                if (random >= .33 && random < .66) {
                    armTextures[k] = SMOOTH;
                }
                else {
                    armTextures[k] = SLIMY;
                }
            }
            for (int k = 0; k < 8; k++) {
                double random = Math.random();
                if (random < .33) {
                    armColors[k] = RED;
                }
                if (random >= .33 && random < .66) {
                    armColors[k] = GRAY;
                }
                else {
                    armColors[k] = BLACK;
                }
            }
            for (int k = 0; k < 8; k++) {
                double random = Math.random();
                armLengths[k] = (int) (10 * random);
            }

            oc.addObservation(i, armColors, armLengths, armTextures);
            //(System.currentTimeMillis()) / 1000)
        }
        System.out.println(oc.countThem());
    }

    @DisplayName("Test Timing")
    @Test
    public void testSeven() {
        for (int i = 0; i < 1000000; i++) {
            double random = Math.random();
            int randomInt = (int) (10 * random);
            double random2 = Math.random();
            int randomInt2 = (int) (10 * random);
            boolean bigger = randomInt > randomInt2;
        }
    }

    @DisplayName("Test Complicated Inequality of Octopuses")
    @Test
    public void testEight() {
        OctopusCount oc = new OctopusCount();
        OctopusCountI.ArmTexture[] armTextures = {SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, STICKY};
        OctopusCountI.ArmColor[] armColors = {GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY};
        int[] armLengths = {1, 1, 1, 1, 1, 1, 1, 1};
        OctopusCountI.ArmTexture[] armTextures2 = {SMOOTH, STICKY, STICKY, STICKY, STICKY, STICKY, STICKY, STICKY};
        OctopusCountI.ArmColor[] armColors2 = {GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY, GRAY};
        int[] armLengths2 = {1, 1, 1, 1, 1, 1, 1, 1};
        oc.addObservation(1, armColors, armLengths, armTextures);
        oc.addObservation(2, armColors2, armLengths2, armTextures2);
        assertEquals(2, oc.countThem());
    }

}
