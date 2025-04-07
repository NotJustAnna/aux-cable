import {CurrentVoiceChannelModel, StateModel} from "@/lib/model.ts";
import {ConnectedProfile} from "@/components/states/connected/connected-profile.tsx";
import {AudioInputs} from "@/components/states/connected/audio-inputs.tsx";
import {useState} from "react";
import {useMountEffect} from "@/useMountEffect.ts";
import {useWebSocket} from "@/contexts/websocket.tsx";

interface Props {
    state: StateModel;
}

export function ConnectedState({state}: Props) {
    const ws = useWebSocket();
    const [channel, setChannel] = useState<CurrentVoiceChannelModel | null>(null);
    useMountEffect(() => ws.subscribe("currentVoiceChannel", (data: CurrentVoiceChannelModel) => setChannel(data)));

    return (
        <div className="mt-3 space-y-5">
            <AudioInputs channel={channel}/>
            <ConnectedProfile account={state.account!} channel={channel}/>
        </div>
    );
}