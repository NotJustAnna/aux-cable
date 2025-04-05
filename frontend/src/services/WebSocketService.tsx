export class WebSocketService {
    private readonly socket: WebSocket;
    private readonly subscriptions: Map<string, ((data: any) => void)[]> = new Map();
    private subscribeOnReady: string[] | null = [];

    constructor(url: string) {
        this.socket = new WebSocket(url);
        this.socket.onmessage = (it) => this.handleSocket(it);

        let timer: any;
        timer = setInterval(() => {
            if (this.socket.readyState !== WebSocket.OPEN) {
                return;
            }
            if (this.subscribeOnReady !== null && this.subscribeOnReady.length > 0) {
                for (const type of this.subscribeOnReady) {
                    this.socket.send(JSON.stringify({ type, enabled: true }));
                }
                this.subscribeOnReady = null;
            }
            clearInterval(timer);
        }, 100);
    }

    subscribe(type: string, callback: (data: any) => void): () => void {
        if (!this.subscriptions.has(type)) {
            this.subscriptions.set(type, []);
        }
        if (type !== "error" && this.subscriptions.get(type)!.length === 0) {
            if (this.subscribeOnReady !== null) {
                this.subscribeOnReady.push(type);
            } else {
                this.socket.send(JSON.stringify({ type, enabled: true }));
            }
        }
        this.subscriptions.get(type)!.push(callback);
        return () => {
            const callbacks = this.subscriptions.get(type);
            if (callbacks) {
                const index = callbacks.indexOf(callback);
                if (index !== -1) {
                    callbacks.splice(index, 1);
                }
                if (callbacks.length === 0) {
                    this.socket.send(JSON.stringify({ type, enabled: false }));
                    this.subscriptions.delete(type);
                }
            }
        };
    }

    private handleSocket(it: MessageEvent) {
        const { type, content } = JSON.parse(it.data);
        const callbacks = this.subscriptions.get(type);
        if (callbacks !== undefined && callbacks.length > 0) {
            for (const callback of callbacks) {
                try {
                    callback(content);
                } catch (e) {
                    console.error(`Error in callback for type ${type}:`, e);
                }
            }
        }
    }
}