package net.notjustanna.audio;

import io.micronaut.core.annotation.NonNull;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.util.*;

public class AudioInputs {
    private AudioInputs() {
        throw new UnsupportedOperationException();
    }

    public static List<AudioInput> all() {
        Map<String, AudioInput.Builder> builders = new HashMap<>();
        Set<AudioInput.Builder> validBuilders = new HashSet<>();

        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            // get, put or rename builder
            String mixerName = mixerInfo.getName();
            String key = AudioInputs.findInputByName(builders.keySet(), mixerName);
            AudioInput.Builder builder = builders.computeIfAbsent(key, k -> new AudioInput.Builder());
            if (!key.equals(mixerName) && key.length() == PORT_NAME_CUTOFF_LENGTH && key.startsWith("Port ")) {
                // Rename (change the map key) to the full name if it was truncated.
                builders.remove(key);
                key = mixerName;
                builders.put(key, builder);
            }

            try {
                Line.Info[] targetLineInfo = AudioSystem.getMixer(mixerInfo).getTargetLineInfo();


                if (targetLineInfo != null && targetLineInfo.length > 0) {
                    builder.addMixer(mixerInfo);

                    for (Line.Info info : targetLineInfo) {

                        if (TargetDataLine.class.isAssignableFrom(info.getLineClass())) {
                            validBuilders.add(builder);
                        }
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // This is thrown when the mixer is not available.
                // It is not a problem, more of a inconsistent state.
                // It'll probably get fixed in the next scan.
            }
        }

        return builders.entrySet().stream()
            .filter(entry -> {
                AudioInput.Builder builder = entry.getValue();
                return !builder.mixers().isEmpty() && validBuilders.contains(builder);
            })
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                String fullName = entry.getKey();
                AudioInput.Builder builder = entry.getValue();
                String[] nameAndDevice = extractNameAndDevice(fullName);
                return builder.name(nameAndDevice[0])
                    .device(nameAndDevice[1])
                    .build();
            })
            .toList();
    }

    private static String[] extractNameAndDevice(String fullName) {
        String name = fullName;
        String device = "Unknown Device";

        if (name.length() <= PORT_NAME_CUTOFF_LENGTH && name.startsWith("Port ")) {
            name = name.substring(5);
            if (name.contains(" (") && !name.endsWith(")")) {
                // Device name is at the end of the string, but the string was truncated.
                // Although technically we could extrapolate the device name from the truncated string by
                // looking up the truncated string in the full mixer scan, it's highly unlikely that this
                // would recover the name. (Reasoning being that the device name is likely to be truncated
                // across all the mixers related to that device/manufacturer.)
                name = name.substring(0, name.lastIndexOf(" (")); // remove the truncated device name
            }
        }

        if (name.contains(" (") && name.endsWith(")")) {
            // Thanks Windows and Mac, we have the device name in parentheses at the end.
            device = name.substring(name.lastIndexOf(" (") + 2, name.length() - 1);
            name = name.substring(0, name.lastIndexOf(" ("));
        } else if (name.contains(" [") && name.endsWith("]")) {
            device = name.substring(name.lastIndexOf(" [") + 2, name.length() - 1);
            name = name.substring(0, name.lastIndexOf(" ["));
        }

        return new String[]{name, device};
    }

    private static final int PORT_NAME_CUTOFF_LENGTH = 36;

    @NonNull
    private static String findInputByName(@NonNull Set<String> groups, @NonNull String mixerName) {
        String exactNameMatch = groups.stream().filter(mixerName::equals).findFirst().orElse(null);
        if (exactNameMatch != null) return exactNameMatch;

        if (mixerName.length() <= PORT_NAME_CUTOFF_LENGTH && mixerName.startsWith("Port ")) {
            // If the name is exactly 36 characters long, it was likely truncated.
            // ALSO, "Port " was added to the beginning of the name.
            // We will try to find a group that matches the truncated name.
            String possibleTruncatedName = mixerName.substring(5);
            List<String> matches = groups.stream().filter(name -> name.startsWith(possibleTruncatedName)).toList();
            if (matches.size() == 1) return matches.getFirst();
            if (matches.size() > 1) throw new IllegalStateException("TOO MANY MATCHES: " + matches);
        } else {
            // Similarly, if the name is not 36 characters long, it was likely not truncated.
            // We will try to find a group with a name that was truncated to 36 characters.
            // (Also, we have to account for the "Port " prefix.)
            String cutoffName = "Port " + mixerName;
            if (cutoffName.length() > PORT_NAME_CUTOFF_LENGTH) {
                cutoffName = cutoffName.substring(0, PORT_NAME_CUTOFF_LENGTH);
            }
            List<String> matches = groups.stream().filter(cutoffName::equals).toList();
            if (matches.size() == 1) return matches.getFirst();
            if (matches.size() > 1) throw new IllegalStateException("TOO MANY MATCHES: " + matches);
        }

        return mixerName;
    }
}
