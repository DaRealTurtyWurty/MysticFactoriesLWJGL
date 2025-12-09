package dev.turtywurty.mysticfactories.client.world.biome;

import java.util.ArrayList;
import java.util.List;

public record VisualProfile(int fogColor, int waterColor, int foliageColor,
                            List<AmbientParticleSettings> ambientParticles, List<AmbientSoundSettings> ambientSounds,
                            List<MusicTrack> musicTracks) {
    public static class Builder {
        private final List<AmbientParticleSettings> ambientParticles = new ArrayList<>();
        private final List<AmbientSoundSettings> ambientSounds = new ArrayList<>();
        private final List<MusicTrack> musicTracks = new ArrayList<>();
        private int fogColor = 0xC0D8FF;
        private int waterColor = 0x3F76E4;
        private int foliageColor = 0x4C9A2A;

        public Builder fogColor(int fogColor) {
            this.fogColor = fogColor;
            return this;
        }

        public Builder waterColor(int waterColor) {
            this.waterColor = waterColor;
            return this;
        }

        public Builder foliageColor(int foliageColor) {
            this.foliageColor = foliageColor;
            return this;
        }

        public Builder addAmbientParticle(AmbientParticleSettings particle) {
            this.ambientParticles.add(particle);
            return this;
        }

        public Builder addAmbientSound(AmbientSoundSettings sound) {
            this.ambientSounds.add(sound);
            return this;
        }

        public Builder addMusicTrack(MusicTrack track) {
            this.musicTracks.add(track);
            return this;
        }

        public VisualProfile build() {
            return new VisualProfile(fogColor, waterColor, foliageColor, ambientParticles, ambientSounds, musicTracks);
        }
    }
}
