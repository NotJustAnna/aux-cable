import {StateModel} from "@/lib/model.ts";
import {FC} from "react";
import {IdleState} from "@/app/state/idle.tsx";
import {LoggedInState} from "@/app/state/logged-in.tsx";
import {NoState} from "@/app/state/none.tsx";
import {ConnectedState} from "@/app/state/connected.tsx";

interface Props {
    state: StateModel | null;
}

interface InnerProps {
    state: StateModel;
}

const states: Record<StateModel['type'], FC<InnerProps>> = {
    'IDLE': IdleState,
    'LOGGED_IN': LoggedInState,
    'CONNECTED': ConnectedState,
}

export function Router({ state }: Props) {
    if (state === null) return <NoState/>;
    const Component = states[state.type];
    return <Component state={state}/>;
}