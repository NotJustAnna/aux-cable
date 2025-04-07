import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuShortcut,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {Bolt, Cable} from "lucide-react";
import {Button} from "@/components/ui/button.tsx";
import { useHttp } from "@/contexts/http";
import {handleIfError} from "@/lib/error-handling.ts";


let modKey = '^';

if ("userAgentData" in navigator) {
    const uad = navigator.userAgentData as { platform?: string };
    if (uad.platform?.startsWith("Mac") || uad.platform === "iPhone") {
        modKey = '⌘';
    }
} else if ("platform" in navigator) {
    const platform = navigator.platform;
    if (platform.startsWith("Mac") || platform === "iPhone") {
        modKey = '⌘';
    }
}

export function AppTitle() {
    const http = useHttp();
    const exit = () => http.post('/actions/shutdown').then(handleIfError("action.shutdown"));

    return <div className="flex gap-2 items-center">
        <Cable/>
        <div className="text-lg font-[Jua] flex-1">Aux Cable</div>
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="outline" className="gap-2.5"><span>Options</span><Bolt size={16}/></Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
                <DropdownMenuItem onSelect={exit}>
                    Exit
                    <DropdownMenuShortcut>{modKey}Q</DropdownMenuShortcut>
                </DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    </div>;
}
