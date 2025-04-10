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

export function NewUserDialog() {
    return <Dialog>
        <DialogTrigger asChild>
            <LinkButton className="block mt-1 ms-4">I don't have a bot token...</LinkButton>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
            <DialogHeader>
                <DialogTitle>Creating your first bot!</DialogTitle>
                <DialogDescription>
                    To be able to use this bot, you need to create your own Discord bot! Below is a simple guide to help you get started.
                </DialogDescription>
            </DialogHeader>
            <ul className="ml-6 list-disc [&>li]:mt-2">
                <li>
                    <span className="font-semibold">First,</span> go to <ExternalLink href="https://discord.com/developers/applications">Discord's Developer Portal</ExternalLink> and log in with your own account.
                </li>
                <li>
                    Click on <span className="font-semibold">"New Application"</span> button,
                    give your bot a name and preferably an icon as well.
                </li>
                <li>
                    Click on the <span className="font-semibold">"Bot"</span> tab and then click on <span className="font-semibold">"Reset Token"</span> button.
                </li>
                <li>
                    Click on <span className="font-semibold">"Copy"</span> to copy the token to your clipboard.
                </li>
            </ul>
            <p>
                That's it! You can now input the token to log in to your bot.
            </p>
            <DialogFooter>
                <DialogClose asChild>
                    <Button type="submit">Got it!</Button>
                </DialogClose>
            </DialogFooter>
        </DialogContent>
    </Dialog>;
}
