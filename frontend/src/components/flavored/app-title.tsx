import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem, DropdownMenuSeparator,
    DropdownMenuShortcut,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {Bolt, Cable} from "lucide-react";
import {Button} from "@/components/ui/button.tsx";
import { useHttp } from "@/contexts/http";
import {handleIfError} from "@/lib/error-handling.ts";
import {useMountEffect} from "@/useMountEffect.ts";

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
    const openItselfOnBrowser = typeof Webview__openUrl !== "undefined" && (() => Webview__openUrl(window.location.href));

    const http = useHttp();
    const exit = typeof Webview__shutdown !== "undefined" ? Webview__shutdown : (() => http.post('/actions/shutdown').then(handleIfError("action.shutdown")));

    useMountEffect(() => {
        const listener = (e: KeyboardEvent) => {
            if (e.key === 'q' && (e.metaKey || e.ctrlKey)) {
                e.preventDefault();
                exit();
            }
        };
        window.addEventListener('keydown', listener);
        return () => window.removeEventListener('keydown', listener);
    });

    return <div className="flex gap-2 items-center">
        <Cable/>
        <div className="text-lg font-[Jua] flex-1">Aux Cable</div>
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="outline" className="gap-2.5"><span>Options</span><Bolt size={16}/></Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
                {
                    openItselfOnBrowser && <><DropdownMenuItem onSelect={() => openItselfOnBrowser()}>
                        Open in Browser
                    </DropdownMenuItem>
                    <DropdownMenuSeparator/>
                </>
                }
                <DropdownMenuItem onSelect={exit}>
                    Exit
                    <DropdownMenuShortcut>{modKey}Q</DropdownMenuShortcut>
                </DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    </div>;
}
