import classed from "classed-components";
import {ComponentProps, FC} from "react";
import {AxiosRequestConfig} from "axios";
import {useHttp} from "@/contexts/http.ts";
import {handleIfError} from "@/lib/error-handling.ts";

// This is a button that looks like a link. Since we don't have pages in our app, we use it to navigate to other pages.

export const LinkButton: FC<ComponentProps<'button'>> = classed.button`
    text-indigo-400 hover:text-indigo-300 text-center underline italic
`;

interface ActionProps {
    method: AxiosRequestConfig['method'],
    action: string,
    data?: AxiosRequestConfig['data'],
    toastId?: string,
}

type ActionLinkButtonProps = ActionProps & ComponentProps<typeof LinkButton>;

export function ActionLinkButton({method, action, data, toastId, ...props}: ActionLinkButtonProps) {
    const http = useHttp();
    const onClick = () => {
        http.request({
            method,
            url: action,
            data: data === undefined ? {} : data,
        }).then(handleIfError(toastId === undefined ? `action:${action}` : toastId));
    };

    return (
        <LinkButton onClick={onClick} {...props} />
    )
}
