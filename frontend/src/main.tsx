import {createRoot} from 'react-dom/client'
import './index.css'
import App from './app/index.tsx'
import {Websocket} from "./contexts/websocket.tsx";
import {WebSocketService} from "./services/WebSocketService.tsx";
import axios from "axios";
import {Http} from "./contexts/http.ts";

const wsProto: Record<string, string> = { 'http:': 'ws:', 'https:': 'wss:' };
const env = import.meta.env.MODE === 'development' ? 'dev' : 'prod';

const urls: Record<typeof env, Record<'ws' | 'http', string>> = {
    dev: {
        ws: 'ws://localhost:3000/api/gateway',
        http: 'http://localhost:3000/api',
    },
    prod: {
        ws: `${wsProto[window.location.protocol]}//${window.location.host}/api/gateway`,
        http: `${(window.location.origin)}/api`,
    }
};

const ws = new WebSocketService(urls[env].ws);
const http = axios.create({
    baseURL: urls[env].http,
    validateStatus: status => status >= 200 && status <= 500,
});

createRoot(document.getElementById('root')!).render(
    <Websocket.Provider value={ws}>
        <Http.Provider value={http}>
            <App />
        </Http.Provider>
    </Websocket.Provider>
)
