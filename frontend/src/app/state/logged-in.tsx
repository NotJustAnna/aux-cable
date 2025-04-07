import {StateModel} from "@/lib/model.ts";
import {Profile} from "@/components/states/logged-in/profile.tsx";
import {Guilds} from "@/components/states/logged-in/guilds.tsx";

interface Props {
    state: StateModel;
}

export function LoggedInState({state}: Props) {
    return (
        <div className="mt-3 space-y-5">
            <Guilds/>
            <Profile account={state.account!}/>
        </div>
    );
}