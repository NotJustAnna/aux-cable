import {createContext, useContext} from "react";
import {Axios} from "axios";

export const HttpContext = createContext<Axios | null>(null);

export function useHttp(): Axios {
    const context = useContext(HttpContext);
    if (context === null) {
        throw new Error("useHttp must be used within a HttpProvider");
    }
    return context;
}