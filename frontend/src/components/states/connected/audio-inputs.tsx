import {useWebSocket} from "@/contexts/websocket.tsx";
import {useState} from "react";
import {AudioInputModel, CurrentVoiceChannelModel} from "@/lib/model.ts";
import {useMountEffect} from "@/useMountEffect.ts";
import {ScrollArea} from "@/components/ui/scroll-area.tsx";
import {Skeleton} from "@/components/ui/skeleton.tsx";
import {NoInputDialog} from "@/components/states/connected/no-input-dialog.tsx";
import {ActionButton} from "@/components/utils/action-button.tsx";
import {iconOf} from "@/lib/audio-icons.ts";

function Placeholder() {
    return <div className="space-y-5">
        <Skeleton className="h-12 rounded-md"/>
        <Skeleton className="h-12 rounded-md"/>
    </div>;
}

function NoInputs() {
    return <div className="border rounded-md text-center space-y-3 p-3 py-5">
        <div>
            <h2 className="text-center text-md font-semibold">No inputs</h2>
            <NoInputDialog/>
        </div>
    </div>;
}

interface AudioInputProps {
    input: AudioInputModel
    active: Boolean;
}

function AudioInput({ input, active }: AudioInputProps) {
    const Icon = iconOf(input);

    return <ActionButton method="post" action="/actions/stream" data={{ input: active ? null : input }}
                         toastId="action.stream"
                         variant="outline" size="responsive"
                         className="py-2 px-3 flex gap-3 justify-items-center items-center">
        <Icon strokeWidth={active ? 2.5 : undefined} className={`size-5.5 mx-1.5 my-2 ${active ? 'text-green-500' : ''}`} />
        <div className="flex-1 text-left">
            <h2 className="text-md font-semibold">{input.name}</h2>
            {input.device.length > 0 && <p className="text-xs text-muted-foreground">{input.device}</p>}
        </div>
    </ActionButton>;
}

interface AudioInputsProps {
    channel: CurrentVoiceChannelModel | null;
}

export function AudioInputs({ channel }: AudioInputsProps) {
    const ws = useWebSocket();
    const [inputs, setInputs] = useState<AudioInputModel[] | null>(null);
    useMountEffect(() => ws.subscribe("audioInputs", (data: AudioInputModel[]) => setInputs(data)));

    const isActive = (input: AudioInputModel): Boolean => {
        return input.name === channel?.currentInput?.name && input.device === channel?.currentInput?.device;
    };

    if (inputs === null) return <Placeholder/>;
    if (inputs.length === 0) return <NoInputs/>;

    return <ScrollArea className="rounded-md" viewportClassName="max-h-[calc(100vh-15.5rem)] min-h-20 [&>*]:space-y-3">
        {
            inputs.map(input => <AudioInput input={input} active={isActive(input)} key={`${input.name}:${input.device}`}/>)
        }
    </ScrollArea>;
}