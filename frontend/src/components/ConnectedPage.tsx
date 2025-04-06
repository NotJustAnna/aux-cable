import {Card, DangerButton} from "./common.tsx";
import {StateModel, AudioInputModel, CurrentVoiceChannelModel} from "../model.ts";
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
import {errorStrings} from "../errorStrings.ts";
import classed from "classed-components";

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

const InputButton = classed.button`
    w-full text-left bg-slate-700 hover:bg-slate-600 text-white border border-slate-600 rounded-md py-2 px-3 flex gap-3 items-center my-3
`;

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

    const disconnect = () => {
        http.post('/actions/disconnect', {}).then(res => {
            if (res.status >= 400 && res.status <= 500) {
                postMessage({ type: 'ERROR', content: errorStrings[res.data.errorType] ?? res.data.errorType });
            }
        })
    };

    const [inputs, setInputs] = useState<AudioInputModel[] | null>(null);
    useMountEffect(() => ws.subscribe("audioInputs", (data: AudioInputModel[]) => setInputs(data)));

    const [channel, setChannel] = useState<CurrentVoiceChannelModel | null>(null);
    useMountEffect(() => ws.subscribe("currentVoiceChannel", (data: CurrentVoiceChannelModel) => setChannel(data)));

    const account = state.account!;

    const isActive = (input: AudioInputModel): Boolean => {
        return input.name === channel?.currentInput?.name && input.device === channel?.currentInput?.device;
    };

    const toggle = (input: AudioInputModel) => {
        http.post('/actions/stream', { input: isActive(input) ? null : input }).then(res => {
            if (res.status >= 400 && res.status <= 500) {
                postMessage({ type: 'ERROR', content: errorStrings[res.data.errorType] ?? res.data.errorType });
            }
        });
    };

    return <Card>
        <div className="flex justify-center gap-3">
            <img src={account.imageUrl} alt="" className="rounded-full max-w-8 max-h-8 aspect-square"/>
            <div className="flex flex-col justify-center">
                <h2 className="mb-0.5 text-xl font-semibold">{account.name}</h2>
            </div>
        </div>
        <div className="flex gap-3 justify-items-center my-3">
            <div className="flex-1 text-white border border-slate-700 rounded-md py-3 px-2 flex gap-3 items-center">
                {
                    <>
                        <img src={channel?.guildIconUrl} alt="" className="rounded-full max-w-7 max-h-7 aspect-square"/>
                        <div className="flex flex-1 flex-col">
                            <h2 className="text-sm">{channel?.guildName}</h2>
                            <div className="text-sm ps-2"><span className="text-slate-400 p-1">#</span> {channel?.name!}</div>
                        </div>
                    </> || <p>Loading...</p>
                }
            </div>
            <DangerButton size="sm" onClick={disconnect}>Leave</DangerButton>
        </div>
        {
            inputs?.map(input => <InputButton onClick={() => toggle(input)}>
                <FontAwesomeIcon fixedWidth icon={getIcon(input.name, input.device)} className={`text-2xl ${isActive(input) ? 'text-green-400' : 'text-slate-400'}`}/>
                <div className="flex-1">
                    <div className="">{input.name}</div>
                    <div className="text-slate-400 text-sm">{input.device}</div>
                </div>
            </InputButton>) ?? false
        }
    </Card>;
}