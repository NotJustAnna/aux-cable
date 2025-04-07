import {useHttp} from "@/contexts/http.ts";
import {useState} from "react";
import {AccountModel} from "@/lib/model.ts";
import {useMountEffect} from "@/useMountEffect.ts";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {LogIn} from "lucide-react";
import {ScrollArea} from "@/components/ui/scroll-area.tsx";
import {Skeleton} from "@/components/ui/skeleton.tsx";
import {fallbackName} from "@/utils/fallback-name.ts";
import {handleError, isError} from "@/lib/error-handling.ts";
import {ActionButton} from "@/components/utils/action-button.tsx";

function Placeholder() {
    return <div className="space-y-5">
        <Skeleton className="h-16 rounded-md"/>
        <Skeleton className="h-16 rounded-md"/>
    </div>;
}

function NoAccounts() {
    return <div className="border rounded-md min-h-20 max-w-80 flex flex-col justify-center items-center p-3 py-5">
        <h2 className="text-center text-md font-semibold">No accounts</h2>
        <p className="text-sm text-center text-muted-foreground text-balance">Accounts appear here by choosing "Remember account" when you log in.</p>
    </div>;
}

interface AccountButtonProps {
    account: AccountModel;
}

function AccountButton({account}: AccountButtonProps) {
    return <ActionButton method="post" action="/actions/login" data={{ token: `RememberMe.id=${account.id}`, remember: true }}
                         toastId="action.login" variant="outline" size="responsive"
                         className="py-2 px-3 flex gap-3 justify-items-center items-center">
        <Avatar className="size-12">
            <AvatarImage src={account.imageUrl}/>
            <AvatarFallback>{fallbackName(account.name)}</AvatarFallback>
        </Avatar>
        <h2 className="flex-1 text-left mb-0.5 text-lg font-semibold">{account.name}</h2>
        <LogIn strokeWidth={2.5} className="m-3 size-6"/>
    </ActionButton>;
}

export function SavedAccounts() {
    const http = useHttp();
    const [accounts, setAccounts] = useState<AccountModel[] | null>(null);

    useMountEffect(() => {
        http.get("/accounts").then(res => {
            if (isError(res)) {
                handleError("query.accounts", res);
                setAccounts([]);
                return;
            }
            setAccounts(res.data);
        });
    });

    if (accounts === null) return <Placeholder/>;
    if (accounts.length === 0) return <NoAccounts/>;

    // I wish I didn't have to do this, but no way to escape min/max-height in scroll-areas
    return <ScrollArea className="rounded-md"  viewportClassName="max-h-[calc(100vh-14rem)] min-h-20 [&>*]:space-y-5">
        {accounts.map(account => <AccountButton key={account.id} account={account}/>)}
    </ScrollArea>;
}