import {Button} from "@/components/ui/button.tsx";
import {useHttp} from "@/contexts/http.ts";
import {AxiosRequestConfig} from "axios";
import {handleIfError} from "@/lib/error-handling.ts";
import {ComponentProps} from "react";

interface ActionProps {
    method: AxiosRequestConfig['method'],
    action: string,
    data?: AxiosRequestConfig['data'],
    toastId?: string,
}

type ActionButtonProps = ActionProps & ComponentProps<typeof Button>;

export function ActionButton({method, action, data, toastId, ...props}: ActionButtonProps) {
    const http = useHttp();
    const onClick = () => {
        http.request({
            method,
            url: action,
            data: data === undefined ? {} : data,
        }).then(handleIfError(toastId === undefined ? `action:${action}` : toastId));
    };

    return (
        <Button onClick={onClick} {...props} />
    )
}
