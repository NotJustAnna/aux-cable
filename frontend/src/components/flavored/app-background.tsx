import {Cable} from "lucide-react";

export function AppBackground() {
    return <div
        className="absolute top-0 left-0 w-full h-full bg-gradient-to-br from-[#1e1e2f] to-[#000010] opacity-50 -z-10 flex justify-center items-center overflow-clip">
        <Cable strokeWidth={1} className="size-[calc(min(90vh,90vw))] -rotate-20 text-slate-700"/>
    </div>;
}