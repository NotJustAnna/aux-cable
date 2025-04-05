import {Card, DangerButton} from "./common.tsx";
import {GuildModel, StateModel, AudioInputModel, VoiceChannelModel} from "../model.ts";
import {usePostMessage} from "../contexts/MessageContext.tsx";
import {useWebSocket} from "../contexts/WebSocketContext.tsx";
import {useHttp} from "../contexts/HttpContext.ts";
import {useState} from "react";
import {useMountEffect} from "../useMountEffect.ts";
import {faHeadset} from "@fortawesome/free-solid-svg-icons/faHeadset";
import {IconDefinition} from "@fortawesome/free-brands-svg-icons";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faMicrophone} from "@fortawesome/free-solid-svg-icons/faMicrophone";
import {faHeadphonesSimple} from "@fortawesome/free-solid-svg-icons/faHeadphonesSimple";
import {faVolumeHigh} from "@fortawesome/free-solid-svg-icons/faVolumeHigh";
import {faMicrochip} from "@fortawesome/free-solid-svg-icons/faMicrochip";
import {faIcons} from "@fortawesome/free-solid-svg-icons/faIcons";

interface Props {
    state: StateModel
}

const iconsByInputName: Record<string, IconDefinition> = {
    'headset': faHeadset,
    'microphone': faMicrophone,
    'headphones': faHeadphonesSimple,
    'speakers': faVolumeHigh
}

const iconsByDeviceName: Record<string, IconDefinition> = {
    'voicemeeter': faMicrochip
}

const getIcon = (name: string, device: string) => {
    for (const [key, value] of Object.entries(iconsByInputName)) {
        if (name.toLowerCase().includes(key)) {
            return value;
        }
    }
    for (const [key, value] of Object.entries(iconsByDeviceName)) {
        if (device.toLowerCase().includes(key)) {
            return value;
        }
    }
    return faIcons;
}

export function ConnectedPage({state}: Props) {
    const postMessage = usePostMessage();
    const ws = useWebSocket();
    const http = useHttp();

    const [guilds, setGuilds] = useState<GuildModel[] | null>(null);
    useMountEffect(() => ws.subscribe("guilds", (data: GuildModel[]) => setGuilds(data)));

    const [inputs, setInputs] = useState<AudioInputModel[] | null>(null);
    useMountEffect(() => ws.subscribe("audioInputs", (data: AudioInputModel[]) => setInputs(data)));

    const [channel, setChannel] = useState<VoiceChannelModel | null>(null);
    useMountEffect(() => ws.subscribe("currentVoiceChannel", (data: VoiceChannelModel) => setChannel(data)));

    const guild = guilds?.find(g => g.voiceChannels.some(c => c.id === channel?.id));

    const account = state.account!;

    return <Card>
        <div className="flex justify-center">
            <div className="flex gap-3 justify-items-center">
                <img src={account.imageUrl} alt="" className="rounded-full max-w-8 max-h-8 aspect-square"/>
                <div className="flex flex-col justify-center">
                    <h2 className="mb-0.5 text-xl font-semibold">{account.name}</h2>
                </div>
            </div>
        </div>
        <div className="flex gap-3 justify-items-center my-3">
            <div className="flex-1  text-white border border-slate-700 rounded-md py-3 px-2 flex gap-3 justify-items-center">
                {
                    guild && <>
                        <img src={guild.iconUrl} alt="" className="rounded-full max-w-7 max-h-7 aspect-square"/>
                        <div className="flex flex-1 flex-col">
                            <h2 className="text-sm">{guild.name}</h2>
                            <div className="text-sm ps-2"><span className="text-slate-400 p-1">#</span> {channel?.name!}</div>
                        </div>
                    </> || <p>Loading...</p>
                }
            </div>
            <DangerButton size="sm" >Leave</DangerButton>
        </div>
        {
            inputs?.map(input => <div className="bg-slate-700 hover:bg-slate-600 text-white border border-slate-600 rounded-md py-2 px-3 flex gap-3 justify-items-center my-3">
                <div className="flex flex-col justify-center">
                    <FontAwesomeIcon fixedWidth icon={getIcon(input.name, input.device)} className="text-slate-400 text-2xl"/>
                </div>
                <div className="flex-1">
                    <div className="">{input.name}</div>
                    <div className="text-slate-400 text-sm">{input.device}</div>
                </div>
            </div>) ?? false
        }
    </Card>;
}