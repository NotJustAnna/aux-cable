import {AudioInputModel} from "@/lib/model.ts";
import {
    AudioLines,
    Cable, Gamepad2,
    Headphones,
    Headset,
    Mic,
    Microchip,
    Shapes,
    Speaker, Webcam
} from "lucide-react";

type Icon = typeof Shapes;

const iconsByInputName: Record<string, Icon> = {
    'headset': Headset,
    'microphone': Mic,
    'microfone': Mic,
    'headphones': Headphones,
    'speakers': Speaker,
    'line': Cable,
    'linha': Cable,
    'stereo mix': AudioLines,
    'mixagem': AudioLines,
}

const iconsByDeviceName: Record<string, Icon> = {
    'nvidia broadcast': Microchip,
    'hyperx quadcast': Mic,
    'jbl quantum one chat': Headset,
    'webcam': Webcam,
    'wireless controller': Gamepad2,
    'vb-audio': Microchip,
    'steam streaming speakers': Gamepad2,
}

const defaultIcon = Shapes;

export function iconOf({name, device}: AudioInputModel): Icon {
    for (const [key, value] of Object.entries(iconsByDeviceName)) {
        if (device.toLowerCase().includes(key)) {
            return value;
        }
    }
    for (const [key, value] of Object.entries(iconsByInputName)) {
        if (name.toLowerCase().includes(key)) {
            return value;
        }
    }
    return defaultIcon;
}