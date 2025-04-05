import {createContext, useContext} from "react";
import {WebSocketService} from "../services/WebSocketService.tsx";

export const WebSocketContext = createContext<WebSocketService | null>(null);

export function useWebSocket(): WebSocketService {
    const context = useContext(WebSocketContext);
    if (context === null) {
        throw new Error("useWebSocket must be used within a WebSocketProvider");
    }
    return context;
}