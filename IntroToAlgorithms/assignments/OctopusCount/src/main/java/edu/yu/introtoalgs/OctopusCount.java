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
        int blackArms;
        int grayArms;

        int slimyArms;
        int stickyArms;
        int smoothArms;

        String[] octopusArms;
        int armsHashcode;

        public Octopus(int observationId, ArmColor[] colors, int[] lengthInCM, ArmTexture[] textures) {
            this.observationId = observationId;
            this.armColors = colors;
            this.lengthOfArmsInCM = lengthInCM;
            this.armTextures = textures;
            this.octopusArms = new String[8];
            for (int i = 0; i < 8; i++) {
                if (armColors[i] == null) {
                    throw new IllegalArgumentException("Color Can't be Null");
                }
                if (armTextures[i] == null) {
                    throw new IllegalArgumentException("Arm Texture Can't be Null");
                }
                if (lengthInCM[i] < 0) {
                    throw new IllegalArgumentException("Length of Arm Can't be Less Than Zero");
                }
                StringBuilder builder = new StringBuilder();
                builder.append(armTextures[i].toString()).append(armColors[i].toString()).append(lengthOfArmsInCM[i]);
                this.octopusArms[i] = builder.toString();

                if (armColors[i].equals(ArmColor.RED)) {
                    this.redArms++;
                }
                else if (armColors[i].equals(ArmColor.BLACK)) {
                    this.blackArms++;
                }
                else {
                    this.grayArms++;
                }
                if (armTextures[i].equals(ArmTexture.SLIMY)) {
                    this.slimyArms++;
                }
                else if (armTextures[i].equals(ArmTexture.STICKY)) {
                    this.stickyArms++;
                }
                else {
                    this.smoothArms++;
                }
            }
            Arrays.sort(this.octopusArms);
            this.armsHashcode = Arrays.hashCode(this.octopusArms);
        }

        @Override
        public int hashCode() {
            int value = 17 * (this.redArms + this.slimyArms + this.blackArms);
            value = value  * (this.blackArms + this.stickyArms);
            return value + this.smoothArms;
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
            return this.armsHashcode == other.armsHashcode;
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
