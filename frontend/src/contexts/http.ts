import {createContext, useContext} from "react";
import {Axios} from "axios";

export const Http = createContext<Axios | null>(null);

export function useHttp(): Axios {
    const context = useContext(Http);
    if (context === null) {
        throw new Error("useHttp must be used within a HttpProvider");
    }
    return context;
}