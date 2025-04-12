package net.notjustanna.audio;

import io.micronaut.core.annotation.NonNull;

import javax.sound.sampled.Mixer;
import java.util.*;

public record AudioInput(@NonNull String name, @NonNull String device, @NonNull Set<Mixer.Info> mixers) {
    public String fullName() {
        return name + " (" + device + ")";
    }

    public static class Builder {
        private String name = "";
        private String device = "";
        private final Set<Mixer.Info> mixers = new HashSet<>();

        public String name() {
            return name;
        }

        public Set<Mixer.Info> mixers() {
            return mixers;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder device(String device) {
            this.device = device;
            return this;
        }

        public Builder addMixer(Mixer.Info mixer) {
            this.mixers.add(mixer);
            return this;
        }

        public AudioInput build() {
            return new AudioInput(name, device, Set.copyOf(mixers));
        }
    }
}
