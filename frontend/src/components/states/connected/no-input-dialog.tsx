import {
    Dialog, DialogClose,
    DialogContent,
    DialogDescription, DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog.tsx";
import {ExternalLink, LinkButton} from "@/components/common/link-button.tsx";
import {Button} from "@/components/ui/button.tsx";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs.tsx"

function WindowsTab() {
    return <div>
        <p>
            On Windows, you can install <ExternalLink href="https://vb-audio.com/Cable/">VB-Cable</ExternalLink> or <ExternalLink href="https://vb-audio.com/Voicemeeter/">VoiceMeeter</ExternalLink> to
            create a virtual audio device and route the audio to Discord.
        </p>
    </div>;
}

function MacosTab() {
    return <div>
        <p className="text-sm text-muted-foreground mb-3">
            (Disclaimer: I am not a Mac user, so I don't know if this works)
        </p>
        <p>
            On Mac, you can install <ExternalLink href="https://existential.audio/blackhole/">BlackHole</ExternalLink> or <ExternalLink href="https://ndi.video/tools/virtual-input/">NDI Tools</ExternalLink> to
            create a virtual audio device and route the audio to Discord.
        </p>
    </div>;
}

function LinuxTab() {
    return <div>
        <p className="text-sm text-muted-foreground mb-3">
            (Disclaimer: For advanced users only)
        </p>
        <p>
            On Linux, <ExternalLink href="https://jackaudio.org/">JACK</ExternalLink> with <ExternalLink href="https://kx.studio/Applications">Cadence</ExternalLink> is the best option to
            route the audio to Discord.
        </p>
    </div>;
}

export function NoInputDialog() {
    return <Dialog>
        <DialogTrigger asChild>
            <LinkButton className="text-sm">Huh.</LinkButton>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
            <DialogHeader>
                <DialogTitle>Huh.</DialogTitle>
                <DialogDescription>
                    Apparently you don't have any audio input devices on your system...
                </DialogDescription>
            </DialogHeader>
            <p>
                Due to limitations of the Java Sound API, only audio inputs (such as microphones, line-ins and virtual inputs) are supported.
            </p>
            <p className="text-sm text-muted-foreground">
                (This means that you cannot use the app with audio outputs, such as speakers or headphones)
            </p>
            <p>
                To fix this, it's strongly recommended to use a virtual audio device to route the audio to Discord.
            </p>
            <Tabs defaultValue="account" className="mt-3">
                <TabsList className="w-full mb-3">
                    <TabsTrigger value="windows">Windows</TabsTrigger>
                    <TabsTrigger value="macos">Mac OS</TabsTrigger>
                    <TabsTrigger value="linux">Linux</TabsTrigger>
                </TabsList>
                <TabsContent value="windows"><WindowsTab/></TabsContent>
                <TabsContent value="macos"><MacosTab/></TabsContent>
                <TabsContent value="linux"><LinuxTab/></TabsContent>
            </Tabs>
            <DialogFooter>
                <DialogClose asChild>
                    <Button type="submit">Got it!</Button>
                </DialogClose>
            </DialogFooter>
        </DialogContent>
    </Dialog>;
}
