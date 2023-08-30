package edu.yu.introtoalgs;

import java.util.*;

public class OctopusCount implements OctopusCountI {

    Set<Octopus> octopuses = new HashSet<>();

    class Octopus {
        int observationId;
        ArmColor[] armColors;
        int[] lengthOfArmsInCM;
        ArmTexture[] armTextures;

        public Octopus(int observationId, ArmColor[] colors, int[] lengthInCM, ArmTexture[] textures) {
            this.observationId = observationId;
            this.armColors = colors;
            this.lengthOfArmsInCM = lengthInCM;
            this.armTextures = textures;
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
            if (!equalArmTextures(this.armTextures, other.armTextures)) {
                return false;
            }
            if (!equalArmLengths(this.lengthOfArmsInCM, other.lengthOfArmsInCM)) {
                return false;
            }
            if (!equalArmColors(this.armColors, other.armColors)) {
                return false;
            }
            return true;
        }
    }
    public boolean equalArmLengths(int[] octopusOneArmLengths, int[] octopusTwoArmLengths) {
        for (int i = 0; i < 8; i++) {
            int k;
            for (k = 0; k < 8; k++) {
                if (octopusOneArmLengths[(i + k) % 8] != octopusTwoArmLengths[k]) {
                    break;
                }
            }
            if (k == 8) {
                return true;
            }
        }
        return false;
    }

    public boolean equalArmColors(ArmColor[] octopusOneArmColors, ArmColor[] octopusTwoArmColors) {
        for (int i = 0; i < 8; i++) {
            int k;
            for (k = 0; k < 8; k++) {
                if (!octopusOneArmColors[(i + k) % 8].equals(octopusTwoArmColors[k])) {
                    break;
                }
            }
            if (k == 8) {
                return true;
            }
        }
        return false;
    }

    public boolean equalArmTextures(ArmTexture[] octopusOneArmTextures, ArmTexture[] octopusTwoArmTextures) {
        for (int i = 0; i < 8; i++) {
            int k;
            for (k = 0; k < 8; k++) {
                if (!octopusOneArmTextures[(i + k) % 8].equals(octopusTwoArmTextures[k])) {
                    break;
                }
            }
            if (k == 8) {
                return true;
            }
        }
        return false;
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
        for (int i = 0; i < 8; i++) {
            if (lengthInCM[i] < 0) {
                throw new IllegalArgumentException("You can't have an arm with a negative length value");
            }
        }
        octopuses.add(new Octopus(observationId, colors, lengthInCM, textures));
    }

    @Override
    public int countThem() {
        return octopuses.size();
    }
}
