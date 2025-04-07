import {useMountEffect} from "@/useMountEffect.ts";
import {FlowModel} from "@/lib/model.ts";
import {useWebSocket} from "@/contexts/websocket.tsx";
import {Dispatch, SetStateAction, useMemo, useState} from "react";

interface FlowHints {
    last: FlowModel | null;
    all: FlowModel[];
    clear(): void;
    remove(model: FlowModel): void;
    setAll: Dispatch<SetStateAction<FlowModel[]>>
}

const compileHints = (hints: (string|RegExp)[]): RegExp[] => {
    return hints.map(hint => {
        if (typeof hint === "string") {
            // Convert string to regex
            hint = hint.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
            // Add double wildcard support
            hint = hint.replace(/\\\*\*/g, "(?:[^.].)*[^.]+");
            // Add wildcard support
            hint = hint.replace(/\\\*/g, "[^.]+");
            return new RegExp(hint);
        }
        return hint;
    });
}

export function useFlow(... hints: (string|RegExp)[]): FlowHints {
    const compiledHints = useMemo(() => compileHints(hints), [hints]);
    const ws = useWebSocket();
    const [all, setAll] = useState<FlowModel[]>([]);
    useMountEffect(() => ws.subscribe("flow", (data: FlowModel) => {
        if (compiledHints.length === 0) return;
        if (compiledHints.some(hint => hint.test(data.id))) {
            setAll(prev => [...prev, data]);
        }
    }));
    return useMemo(() => ({
        last: all.length > 0 ? all[all.length - 1] : null,
        all,
        clear: () => setAll([]),
        remove: (model: FlowModel) => setAll(prev => prev.filter(m => m !== model)),
        setAll,
    }), [all]);
}
