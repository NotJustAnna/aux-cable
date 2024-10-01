package net.notjustanna.audio.system

import net.notjustanna.audio.native.NativeAudio
import net.notjustanna.audio.native.mixer
import javax.sound.sampled.TargetDataLine
import kotlin.collections.MutableMap.MutableEntry

object AudioInputs {
    inline val all: List<AudioInput> get() = findAll()

    fun findAll(): List<AudioInput> {
        val inputMap = mutableMapOf<String, AudioInput.Builder>()
        val validBuilders = mutableSetOf<AudioInput.Builder>()

        NativeAudio.mixerInfo.forEach { mixerInfo ->
            // get, put or rename builder
            val mixerName = mixerInfo.name
            var key = findInputByName(inputMap.keys, mixerName)
            val builder = inputMap.getOrPut(key, AudioInput::Builder)
            if (key != mixerName && key.length == PORT_NAME_CUTOFF_LENGTH && key.startsWith("Port ")) {
                // Rename (change the map key) to the full name if it was truncated.
                inputMap.remove(key)
                key = mixerName
                inputMap[key] = builder
            }

            mixerInfo.mixer.targetLineInfo.forEach { lineInfo ->
                builder.addMixer(mixerInfo)
                if (lineInfo.lineClass == TargetDataLine::class.java) {
                    validBuilders.add(builder)
                }
            }
        }

        return inputMap
            .entries
            .filterNot { (k, v) ->
                k.startsWith("Primary Sound") && k.endsWith("Driver")
                        || v.mixers.isEmpty()
                        || v !in validBuilders
            }
            .sortedBy(MutableEntry<String, AudioInput.Builder>::key)
            .map { (fullNamme, builder) ->
                val (name, device) = extractNameAndDevice(fullNamme)

                builder.name(name)
                    .device(device)
                    .build()
            }
    }

    private fun extractNameAndDevice(fullName: String): Pair<String, String> {
        var name = fullName
        var device = "Unknown Device"

        if (name.length <= PORT_NAME_CUTOFF_LENGTH && name.startsWith("Port ")) {
            name = name.substring(5)

            if (name.contains(" (") && !name.endsWith(")")) {
                // Device name is at the end of the string, but the string was truncated.
                // Although technically we could extrapolate the device name from the truncated string by
                // looking up the truncated string in the full mixer scan, it's highly unlikely that this
                // would recover the name. (Reasoning being that the device name is likely to be truncated
                // across all the mixers related to that device/manufacturer.)

                name = name.substringBeforeLast(" (") // remove the truncated device name
            }
        }

        if (name.contains(" (") && name.endsWith(")")) {
            // Thanks Windows, we have the device name in parentheses at the end.
            device = name.substringAfterLast(" (").substringBeforeLast(")")
            name = name.substringBeforeLast(" (")
        }
        return Pair(name, device)
    }

    /**
     * Used to detect if a port name was truncated by the Java Sound API.
     * Only some implementations of the Java Sound API truncate port names,
     * while others do not.
     */
    private const val PORT_NAME_CUTOFF_LENGTH = 36

    private fun findInputByName(groups: MutableSet<String>, name: String): String {
        val exactNameMatch = groups.find { it == name }
        if (exactNameMatch != null) return exactNameMatch

        if (name.length <= PORT_NAME_CUTOFF_LENGTH && name.startsWith("Port ")) {
            // If the name is exactly 36 characters long, it was likely truncated.
            // ALSO, "Port " was added to the beginning of the name.
            // We will try to find a group that matches the truncated name.
            val possibleTruncatedName = name.substring(5)
            val matches = groups.filter { it.startsWith(possibleTruncatedName) }
            if (matches.size == 1) return matches[0]
            if (matches.size > 1) throw IllegalStateException("TOO MANY MATCHES: $matches")
        } else {
            // Similarly, if the name is not 36 characters long, it was likely not truncated.
            // We will try to find a group with a name that was truncated to 36 characters.
            // (Also, we have to account for the "Port " prefix.)
            val cutoffName = "Port $name".take(PORT_NAME_CUTOFF_LENGTH)
            val matches = groups.filter { it == cutoffName }
            if (matches.size == 1) return matches[0]
            if (matches.size > 1) throw IllegalStateException("TOO MANY MATCHES: $matches")
        }

        return name
    }
}