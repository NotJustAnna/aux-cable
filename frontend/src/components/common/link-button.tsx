import classed from "classed-components";
import {ComponentProps, FC, ReactNode, MouseEvent} from "react";

// This is a button that looks like a link. Since we don't have pages in our app, we use it to navigate to other pages.

export const LinkButton: FC<ComponentProps<'button'>> = classed.button`
    text-indigo-400 hover:text-indigo-300 text-center underline italic
`;

export const Link : FC<ComponentProps<'a'>> = classed.a`
    text-indigo-400 hover:text-indigo-300 text-center underline italic
`;

interface ExternalLinkProps {
    href: string,
    children: ReactNode,
}

export function ExternalLink({href, children}: ExternalLinkProps) {
    const onClick = (e: MouseEvent<HTMLAnchorElement>) => {
        if (typeof Webview__openUrl !== "undefined") {
            e.preventDefault();
            Webview__openUrl(href);
        }
    };

    return (
        <Link href={href} target="_blank" onClick={onClick}>
            {children}
        </Link>
    );
}
