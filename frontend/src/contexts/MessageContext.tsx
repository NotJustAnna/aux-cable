import {createContext, useContext} from "react";
import {MessageModel} from "../model.ts";

export const MessageContext = createContext<((message: MessageModel) => void) | null>(null);

export function usePostMessage() {
    const postMessage = useContext(MessageContext);
    if (postMessage === null) {
        throw new Error("usePostMessage must be used within a MessageProvider");
    }
    return postMessage;
}