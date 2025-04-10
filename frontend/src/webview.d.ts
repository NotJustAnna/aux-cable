type openUrl = ((url: string) => void) | undefined;

declare const Webview__openUrl: openUrl;

type shutdown = (() => void) | undefined;
declare const Webview__shutdown: shutdown;