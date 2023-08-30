package edu.yu.introtoalgs;

import org.junit.Test;
import org.junit.jupiter.api.*;

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

    @DisplayName("Test Trivial Equality of Arm Textures")
    @Test
    public void testOne() {
        OctopusCount oc = new OctopusCount();
        OctopusCountI.ArmTexture[] armsOne = {SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        OctopusCountI.ArmTexture[] armsTwo = {SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        assertTrue(oc.equalArmTextures(armsOne, armsTwo));
    }

    @DisplayName("Test Non-Trivial Equality of Arm Textures")
    @Test
    public void testTwo() {
        OctopusCount oc = new OctopusCount();
        OctopusCountI.ArmTexture[] armsOne = {SMOOTH, SLIMY, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        OctopusCountI.ArmTexture[] armsTwo = {SMOOTH, SLIMY, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        assertTrue(oc.equalArmTextures(armsOne, armsTwo));
    }

    @DisplayName("Test Complicated Equality of Arm Textures")
    @Test
    public void testThree() {
        OctopusCount oc = new OctopusCount();
        OctopusCountI.ArmTexture[] armsOne = {SMOOTH, SMOOTH, SLIMY, STICKY, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        OctopusCountI.ArmTexture[] armsTwo = {SMOOTH, SLIMY, STICKY, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        assertTrue(oc.equalArmTextures(armsOne, armsTwo));
    }

    @DisplayName("Test False Equality of Arm Textures")
    @Test
    public void testFour() {
        OctopusCount oc = new OctopusCount();
        OctopusCountI.ArmTexture[] armsOne = {SMOOTH, SMOOTH, SLIMY, STICKY, SMOOTH, SMOOTH, STICKY, SMOOTH};
        OctopusCountI.ArmTexture[] armsTwo = {SMOOTH, SLIMY, STICKY, SMOOTH, SMOOTH, SMOOTH, SMOOTH, SMOOTH};
        assertFalse(oc.equalArmTextures(armsOne, armsTwo));
    }

    @DisplayName("Test Trivial Equality of Arm Lengths")
    @Test
    public void testFive() {
        OctopusCount oc = new OctopusCount();
        int[] armsOne = {1, 1, 1, 1, 1, 1, 1, 1};
        int[] armsTwo = {1, 1, 1, 1, 1, 1, 1, 1};
        assertTrue(oc.equalArmLengths(armsOne, armsTwo));
    }

    @DisplayName("Test Trivial Inequality of Arm Lengths")
    @Test
    public void testSix() {
        OctopusCount oc = new OctopusCount();
        int[] armsOne = {2, 1, 1, 1, 1, 1, 1, 1};
        int[] armsTwo = {1, 1, 1, 1, 1, 1, 1, 1};
        assertFalse(oc.equalArmLengths(armsOne, armsTwo));
    }

    @DisplayName("Test Complicated Equality of Arm Lengths")
    @Test
    public void testSeven() {
        OctopusCount oc = new OctopusCount();
        int[] armsOne = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] armsTwo = {6, 7, 8, 1, 2, 3, 4, 5};
        assertTrue(oc.equalArmLengths(armsOne, armsTwo));
    }

}
