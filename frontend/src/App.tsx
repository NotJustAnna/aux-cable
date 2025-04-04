import {FC, useState} from 'react'
import {useSubscription, gql} from '@apollo/client';
import {IdlePage} from "./components/IdlePage.tsx";
import {LoggedInPage} from "./components/LoggedInPage.tsx";
import {ConnectedPage} from "./components/ConnectedPage.tsx";
import {createPortal} from "react-dom";

const CURRENT_STATE_TYPE = gql`
    subscription CurrentStateType {
        currentState {
            type
        }
    }`;

const NEW_MESSAGE = gql`
    subscription NewMessage {
        newMessage {
            type
            content
        }
    }`;


interface ComponentProps {
    state?: any
}

const components: Record<string, FC<ComponentProps>> = {
    'IDLE': IdlePage,
    'LOGGED_IN': LoggedInPage,
    'CONNECTED': ConnectedPage,
}

function App() {
    const {loading, error, data} = useSubscription(CURRENT_STATE_TYPE);
    const [messages, setMessages] = useState([] as any[]);
    useSubscription(NEW_MESSAGE, {
        onData({data}) {
            console.log(data);
            setMessages([...messages, data.data.newMessage]);
            setTimeout(() => setMessages((prev) => prev.slice(1)), 5000);
        }
    });

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error : {error.message}</p>;
    const Component = components[data.currentState.type];
    return <>
        <Component state={data.currentState}/>
        {messages.length > 0 && createPortal(
            <div className="absolute bottom-0 left-0 p-4 gap-8">
                {messages.map((message, i) => <div key={i} className="bg-slate-700 p-2 rounded">{message.content}</div>)}
            </div>,
            document.body,
        )}
    </>;
}

export default App
