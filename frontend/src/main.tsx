import {createRoot} from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {WebSocketContext} from "./contexts/WebSocketContext.tsx";
import {WebSocketService} from "./services/WebSocketService.tsx";
import axios from "axios";
import {HttpContext} from "./contexts/HttpContext.ts";

const wsProto: Record<string, string> = { 'http:': 'ws:', 'https:': 'wss:' };

const ws = new WebSocketService(`${wsProto[window.location.protocol]}//${window.location.host}/api/gateway`);
const http = axios.create({
    baseURL: `${(window.location.origin)}/api`,
    validateStatus: status => status >= 200 && status <= 500,
});

createRoot(document.getElementById('root')!).render(
      <WebSocketContext.Provider value={ws}>
          <HttpContext.Provider value={http}>
              <App />
          </HttpContext.Provider>
      </WebSocketContext.Provider>
)
