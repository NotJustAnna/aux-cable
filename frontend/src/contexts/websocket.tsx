import {createContext, useContext} from "react";
import {WebSocketService} from "../services/WebSocketService.tsx";

export const Websocket = createContext<WebSocketService | null>(null);

export function useWebSocket(): WebSocketService {
    const context = useContext(Websocket);
    if (context === null) {
        throw new Error("useWebSocket must be used within a WebSocketProvider");
    }
    return context;
}