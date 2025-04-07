import {AccountModel} from "@/lib/model.ts";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar.tsx";
import {LogOut} from "lucide-react";
import {fallbackName} from "@/utils/fallback-name.ts";
import {ActionButton} from "@/components/utils/action-button.tsx";

interface Props {
    account: AccountModel;
}

export function Profile({account}: Props) {
    return (
        <div className="border rounded-md py-2 px-3 flex gap-3 items-center">
            <Avatar className="size-12">
                <AvatarImage src={account.imageUrl}/>
                <AvatarFallback>{fallbackName(account.name)}</AvatarFallback>
            </Avatar>
            <h2 className="flex-1 text-left mb-0.5 text-md font-semibold">{account.name}</h2>
            <ActionButton method="post" action="/actions/logout" variant="destructive" toastId="action.logout">
                Logout <LogOut/>
            </ActionButton>
        </div>
    );
}