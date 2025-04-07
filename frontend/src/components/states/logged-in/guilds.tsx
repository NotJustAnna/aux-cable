import {useWebSocket} from "@/contexts/websocket.tsx";
import {useState} from "react";
import {GuildModel} from "@/lib/model.ts";
import {useMountEffect} from "@/useMountEffect.ts";
import {Skeleton} from "@/components/ui/skeleton.tsx";
import {Button} from "@/components/ui/button.tsx";
import {LogIn, MailPlus} from "lucide-react";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar.tsx";
import {fallbackName} from "@/utils/fallback-name.ts";
import {ScrollArea} from "@/components/ui/scroll-area.tsx";
import {ActionButton} from "@/components/utils/action-button.tsx";

function Placeholder() {
    return <div className="space-y-5">
        <Skeleton className="h-24 rounded-md"/>
        <Skeleton className="h-24 rounded-md"/>
    </div>;
}

function NoGuilds() {
    return <div className="border rounded-md text-center space-y-3 p-3 py-5">
        <div>
            <h2 className="text-center text-md font-semibold">No servers</h2>
            <p className="text-sm text-center text-muted-foreground text-balance">Servers appear here by inviting
                them.</p>
        </div>
        <Button variant="secondary">Invite<MailPlus/></Button>
    </div>;
}

interface GuildProps {
    guild: GuildModel;
}

function Guild({guild}: GuildProps) {
    return <div className="border rounded-md py-3 px-3 space-y-3">
        <div className="flex gap-3 items-center">
            <Avatar className="size-8">
                <AvatarImage src={guild.iconUrl}/>
                <AvatarFallback>{fallbackName(guild.name)}</AvatarFallback>
            </Avatar>
            <h2 className="flex-1 text-left mb-0.5 text-md font-semibold">{guild.name}</h2>
        </div>
        {
            guild.voiceChannels.length > 0 && guild.voiceChannels.map(channel => (
                <ActionButton key={channel.id} method="post" action="/actions/connect" data={{channelId: channel.id}}
                              toastId="action.connect"
                              size="responsive" variant="secondary" className="flex py-2 pl-3 pr-4">
                    <div className="flex-1 text-left flex gap-2">
                        <span className="text-secondary-foreground/35">#</span>
                        <span>{channel.name}</span>
                    </div>
                    <LogIn/>
                </ActionButton>
            ))
        }
    </div>;
}

export function Guilds() {
    const ws = useWebSocket();
    const [guilds, setGuilds] = useState<GuildModel[] | null>(null);
    useMountEffect(() => ws.subscribe("guilds", (data: GuildModel[]) => setGuilds(data)));

    if (guilds === null) return <Placeholder/>;
    if (guilds.length === 0) return <NoGuilds/>;

    return <ScrollArea className="rounded-md" viewportClassName="max-h-[calc(100vh-15.5rem)] min-h-20 [&>*]:space-y-5">
        {
            guilds.map(guild => <Guild guild={guild} key={guild.id}/>)
        }
    </ScrollArea>;
}