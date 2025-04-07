import {useState} from 'react';
import {useWebSocket} from "../contexts/websocket.tsx";
import {FlowModel, StateModel} from "../lib/model.ts";
import {useMountEffect} from "../useMountEffect.ts";
import {MainCard} from "@/app/styles.tsx";
import {Router} from "@/app/router.tsx";
import {AppTitle} from "@/components/flavored/app-title.tsx";
import {Toaster} from "@/components/ui/sonner.tsx";
import {AppBackground} from "@/components/flavored/app-background.tsx";
import {onFlow} from "@/app/logic.ts";
import {handleGatewayError} from "@/lib/error-handling.ts";

function App() {
    const ws = useWebSocket();
    const [state, setState] = useState<StateModel | null>(null);
    useMountEffect(() => ws.subscribe("currentState", (data: StateModel) => setState(data)));
    useMountEffect(() => ws.subscribe("flow", (data: FlowModel) => onFlow(data)));
    useMountEffect(() => ws.subscribe("error", handleGatewayError));

    return <>
        <MainCard>
            <AppTitle/>
            <Router state={state}/>
        </MainCard>
        <Toaster position="bottom-left"/>
        <AppBackground/>
    </>;
}

export default App
