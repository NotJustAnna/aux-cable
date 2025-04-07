import {StateModel} from "@/lib/model.ts";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {SavedAccounts} from "@/components/states/idle/saved-accounts.tsx";
import {LoginWithToken} from "@/components/states/idle/login-with-token.tsx";

interface Props {
    state: StateModel;
}

export function IdleState({}: Props) {
    return <>
        <Tabs defaultValue="account" className="mt-3">
            <TabsList className="w-full mb-3">
                <TabsTrigger value="account">Accounts</TabsTrigger>
                <TabsTrigger value="token">Token</TabsTrigger>
            </TabsList>
            <TabsContent value="account"><SavedAccounts/></TabsContent>
            <TabsContent value="token"><LoginWithToken/></TabsContent>
        </Tabs>
    </>;
}