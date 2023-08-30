package edu.yu.introtoalgs;

import java.util.*;

public class OctopusCount implements OctopusCountI {

    Set<Octopus> octopuses = new HashSet<>();

    class Octopus {
        int observationId;
        ArmColor[] armColors;
        int[] lengthOfArmsInCM;
        ArmTexture[] armTextures;

        int redArms;
        int slimyArms;

        public Octopus(int observationId, ArmColor[] colors, int[] lengthInCM, ArmTexture[] textures) {
            this.observationId = observationId;
            this.armColors = colors;
            this.lengthOfArmsInCM = lengthInCM;
            this.armTextures = textures;
            for (int i = 0; i < 8; i++) {
                if (armColors[i].equals(ArmColor.RED)) {
                    redArms++;
                }
                if (armTextures[i].equals(ArmTexture.SLIMY)) {
                    slimyArms++;
                }
            }
        }

        @Override
        public int hashCode() {
            return 17 * (redArms + slimyArms);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Octopus other = (Octopus) obj;
            Object[][] octopusOne = combineArrays(this.armTextures, this.armColors, convertIntegerArray(this.lengthOfArmsInCM));
            Object[][] octopusTwo = combineArrays(other.armTextures, other.armColors, convertIntegerArray(other.lengthOfArmsInCM));
            Set<String> armsOne = new HashSet<>();
            Set<String> armsTwo = new HashSet<>();
            for (int i = 0; i < 8; i++) {
                String octopusOneArm = octopusOne[0][i].toString() + octopusOne[1][i].toString() + octopusOne[2][i].toString();
                String octopusTwoArm = octopusTwo[0][i].toString() + octopusTwo[1][i].toString() + octopusTwo[2][i].toString();
                armsOne.add(octopusOneArm);
                armsTwo.add(octopusTwoArm);
            }
            if (armsOne.size() != armsTwo.size()) {
                return false;
            }
            else {
                armsOne.addAll(armsTwo);
                return armsOne.size() == armsTwo.size();
            }
        }
    }


    public Integer[] convertIntegerArray(int[] lengthInCM) {
        Integer[] integerArray = new Integer[8];
        for (int i = 0; i < 8; i++) {
            if (lengthInCM[i] < 0) {
                throw new IllegalArgumentException("You can't have an arm with a negative length value");
            }
            integerArray[i] = lengthInCM[i];
        }
        return integerArray;
    }

    public Object[][] combineArrays(Object[] array1, Object[] array2, Object[] array3) {
        Object[][] combinedArray = new Object[3][];
        combinedArray[0] = array1;
        combinedArray[1] = array2;
        combinedArray[2] = array3;
        return combinedArray;
    }

    @Override
    public void addObservation(int observationId, ArmColor[] colors, int[] lengthInCM, ArmTexture[] textures) {
        if (colors == null || lengthInCM == null || textures == null) {
            throw new IllegalArgumentException("Inputted null data");
        }
        if (observationId < 0) {
            throw new IllegalArgumentException("ID can't be a negative number");
        }
        if (colors.length != 8 || lengthInCM.length != 8 || textures.length != 8) {
            throw new IllegalArgumentException("All octopuses have eight legs. The data must reflect that.");
        }
        octopuses.add(new Octopus(observationId, colors, lengthInCM, textures));
    }

    @Override
    public int countThem() {
        return octopuses.size();
    }
}
