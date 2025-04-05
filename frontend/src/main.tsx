import {createRoot} from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {WebSocketContext} from "./contexts/WebSocketContext.tsx";
import {WebSocketService} from "./services/WebSocketService.tsx";
import axios from "axios";
import {HttpContext} from "./contexts/HttpContext.ts";

// const wsProto: Record<string, string> = { 'http:': 'ws:', 'https:': 'wss:' };

//`${wsProto[window.location.protocol]}//${window.location.host}/api/gateway`,
const ws = new WebSocketService('ws://localhost:3000/api/gateway');
const http = axios.create({
    // `${(window.location.origin)}/api`
    baseURL: 'http://localhost:3000/api',
    validateStatus: status => status >= 200 && status <= 500,
});

createRoot(document.getElementById('root')!).render(
      <WebSocketContext.Provider value={ws}>
          <HttpContext.Provider value={http}>
              <App />
          </HttpContext.Provider>
      </WebSocketContext.Provider>
)
