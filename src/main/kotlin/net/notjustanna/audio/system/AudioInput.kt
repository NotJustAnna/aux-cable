package net.notjustanna.audio.system

import net.notjustanna.audio.native.MixerInfo

data class AudioInput(
    val name: String,
    val device: String,
    val mixers: Set<MixerInfo>
) {
    val fullName = "$name ($device)"

    class Builder {
        public var name: String = ""
            private set
        public var device: String = ""
            private set
        public var mixers: MutableSet<MixerInfo> = mutableSetOf()
            private set

        fun name(name: String) = apply { this.name = name }

        fun device(device: String) = apply { this.device = device }

        fun mixers(mixers: Collection<MixerInfo>) = apply { this.mixers = mixers.toMutableSet() }
        fun addMixer(mixer: MixerInfo) = apply { mixers.add(mixer) }
        fun addMixers(mixers: Collection<MixerInfo>) = apply { this.mixers.addAll(mixers) }
        fun addMixers(mixers: Array<MixerInfo>) = apply { this.mixers.addAll(mixers) }

        fun build() = AudioInput(name, device, mixers)
    }
}