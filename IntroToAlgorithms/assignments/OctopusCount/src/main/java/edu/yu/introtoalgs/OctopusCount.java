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
        Set<String> octopusArms;

        public Octopus(int observationId, ArmColor[] colors, int[] lengthInCM, ArmTexture[] textures) {
            this.observationId = observationId;
            this.armColors = colors;
            this.lengthOfArmsInCM = lengthInCM;
            this.armTextures = textures;
            this.octopusArms = new HashSet<>();
            for (int i = 0; i < 8; i++) {
                String arm = armTextures[i].toString() + armColors[i].toString() + lengthOfArmsInCM[i];
                this.octopusArms.add(arm);
                if (armColors[i].equals(ArmColor.RED)) {
                    this.redArms++;
                }
                if (armTextures[i].equals(ArmTexture.SLIMY)) {
                    this.slimyArms++;
                }
            }
        }

        @Override
        public int hashCode() {
            return 17 * (this.redArms + this.slimyArms);
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
            if (this.octopusArms.size() != other.octopusArms.size()) {
                return false;
            }
            else {
                Set<String> combinedArms = new HashSet<>(this.octopusArms);
                return !combinedArms.addAll(other.octopusArms);
            }
        }
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
