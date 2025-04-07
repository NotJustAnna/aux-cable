import {AccountModel, CurrentVoiceChannelModel} from "@/lib/model.ts";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar.tsx";
import {LogOut} from "lucide-react";
import {fallbackName} from "@/utils/fallback-name.ts";
import {Skeleton} from "@/components/ui/skeleton.tsx";
import {ActionButton} from "@/components/utils/action-button.tsx";

interface Props {
    account: AccountModel;
    channel: CurrentVoiceChannelModel | null;
}

function Placeholder() {
    return <div className="border rounded-md py-2 px-3 flex gap-3 items-center">
        <div className="flex -space-x-3.5">
            <Skeleton className="size-10 rounded-full"/>
            <Skeleton className="size-10 rounded-full"/>
        </div>
        <div className="space-y-1 flex-1">
            <Skeleton className="w-20 h-4 rounded-full"/>
            <Skeleton className="w-32 h-3 rounded-full"/>
        </div>
        <Skeleton className="w-20 h-8 rounded-md" />
    </div>;
}

export function ConnectedProfile({account, channel}: Props) {
    if (channel === null) {
        return <Placeholder/>;
    }

    return (
        <div className="border rounded-md py-2 px-3 flex gap-3 items-center">
            <div className="flex -space-x-3.5">
                <Avatar className="size-10">
                    <AvatarImage src={account.imageUrl}/>
                    <AvatarFallback>{fallbackName(account.name)}</AvatarFallback>
                </Avatar>
                <Avatar className="size-10">
                    <AvatarImage src={channel.guildIconUrl}/>
                    <AvatarFallback>{fallbackName(channel.guildName)}</AvatarFallback>
                </Avatar>
            </div>
            <div className="flex-1">
                <h3 className="text-sm font-semibold">{channel.guildName}</h3>
                <div className="text-xs ps-1.5 flex-1 text-left flex gap-2">
                    <span className="text-secondary-foreground/35">#</span>
                    <span className="text-secondary-foreground/65">{channel.name}</span>
                </div>
            </div>
            <ActionButton method="post" action="/actions/disconnect" toastId="action.disconnect" variant="destructive">
                Leave <LogOut/>
            </ActionButton>
        </div>
    );
}