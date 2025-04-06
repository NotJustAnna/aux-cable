import {FC, ReactNode, useState} from 'react'
import {IdlePage} from "./components/IdlePage.tsx";
import {LoggedInPage} from "./components/LoggedInPage.tsx";
import {ConnectedPage} from "./components/ConnectedPage.tsx";
import {createPortal} from "react-dom";
import {useWebSocket} from "./contexts/WebSocketContext.tsx";
import {MessageModel, StateModel} from "./model.ts";
import {useMountEffect} from "./useMountEffect.ts";
import {MessageContext} from "./contexts/MessageContext.tsx";
import {ErrorMessage, InfoMessage, MessageCorner, SuccessMessage, WarningMessage} from "./components/common.tsx";

interface ComponentProps {
    state: StateModel
}

const components: Record<StateModel['type'], FC<ComponentProps>> = {
    'IDLE': IdlePage,
    'LOGGED_IN': LoggedInPage,
    'CONNECTED': ConnectedPage,
}

interface MessageProps {
    children: ReactNode
}
const MessageComponent: Record<MessageModel['type'], FC<MessageProps>> = {
    ERROR: ErrorMessage,
    INFO: InfoMessage,
    WARNING: WarningMessage,
    SUCCESS: SuccessMessage,
}

function App() {
    const ws = useWebSocket();

    const [currentState, setCurrentState] = useState<StateModel | null>(null);
    const [messages, setMessages] = useState([] as MessageModel[]);
    const postMessage = (message: MessageModel) => {
        setMessages((prev) => [...prev, message]);
        setTimeout(() => setMessages((prev) => prev.slice(1)), 5000);
    };
    useMountEffect(() => ws.subscribe("currentState", (data: StateModel) => setCurrentState(data)));
    useMountEffect(() => ws.subscribe("messages", (data: MessageModel) => postMessage(data)));

    if (currentState === null) return <p>Loading...</p>;
    const Component = components[currentState.type];
    return <>
        <MessageContext.Provider value={postMessage}>
            <Component state={currentState}/>
        </MessageContext.Provider>
        {messages.length > 0 && createPortal(
            <MessageCorner>
                {messages.map((message, i) => {
                    const Message = MessageComponent[message.type];
                    return <Message key={i}>{message.content}</Message>
                })}
            </MessageCorner>,
            document.body,
        )}
    </>;
}

export default App
