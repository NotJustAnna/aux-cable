import {AxiosResponse, HttpStatusCode} from "axios";
import {toast} from "sonner";

interface ErrorText {
    title: string
    description?: string
}

export const errorTexts: Record<string, ErrorText> = {
    "NO_SUCH_ACCOUNT": {
        title: "Couldn't log in, the account does not exist"
    },
    "NO_SUCH_CHANNEL": {
        title: "Couldn't connect, the channel does not exist"
    },
    "NO_SUCH_AUDIO_INPUT": {
        title: "Couldn't stream, the audio input does not exist"
    },
    "INVALID_COMMAND": {
        title: "A gateway error occurred",
        description: "Invalid command (contact developer)"
    },
    "INVALID_SUBSCRIPTION": {
        title: "A gateway error occurred",
        description: "Invalid subscription (contact developer)"
    },
    "UNSUPPORTED_ACTION": {
        title: "The action cannot be performed at this time",
    },
    "INCONSISTENT_STATE": {
        title: "Inconsistent state detected",
        description: "You may need to reload the application"
    },
    "INVALID_TOKEN": {
        title: "Couldn't log in, the token is invalid",
        description: "You may need to reset your token"
    },
    "EMPTY_TOKEN": {
        title: "Couldn't log in, the provided token is empty",
    },
    "REMEMBER_ME_FAILED": {
        title: "Couldn't log in, remember me failed",
        description: "Reload application or log in with your token"
    },
    "REMEMBER_ME_INVALID_TOKEN": {
        title: "Couldn't log in, the account's token is invalid",
        description: "You need to manually log in with your token"
    },
    "UNKNOWN": {
        title: "An unknown error occurred",
        description: "Contact developer"
    }
};

export function isError(res: AxiosResponse): boolean {
    return res.status >= 400 && res.status <= 500;
}

function codesToText() : Map<number, string> {
    const codes = new Map<number, string>();
    for (const [key, value] of Object.entries(HttpStatusCode)) {
        if (typeof value === "number") {
            codes.set(value, key);
        }
    }
    return codes;
}

function dePascalCase(str: string): string {
    return str.replace(/([a-z])([A-Z])/g, '$1 $2');
}

const codes = codesToText();

export const handleGatewayError = (code: string) => {
    const errorText: ErrorText = errorTexts[code] ?? {
        title: errorTexts["UNKNOWN"].title,
        description: `Unknown Gateway error "${code}" (contact developer)`
    };
    toast.error(errorText.title, { description: errorText.description, dismissible: true });
}

export const handleError = (id: string, res: AxiosResponse) => {
    const errorText: ErrorText = errorTexts[res.data.errorType] ?? {
        title: errorTexts["UNKNOWN"].title,
        description: `${codes.has(res.status) ? dePascalCase(codes.get(res.status)!) : `Unknown HTTP code ${res.status}`} (contact developer)`
    };

    toast.dismiss(id);
    toast.error(errorText.title, { description: errorText.description, dismissible: true });
}

export const handleIfError = (id: string) => (res: AxiosResponse) => {
    if (isError(res)) {
        handleError(id, res);
    }
}