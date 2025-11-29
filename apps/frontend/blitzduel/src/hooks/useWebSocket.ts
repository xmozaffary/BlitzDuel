import { Client } from "@stomp/stompjs";
import { useState } from "react";
import SockJS from "sockjs-client";
import { config } from "../config/config";

export const useWebSocket = () => {
  const [logs, setLogs] = useState<string[]>([]);

  const addLog = (message: string): void => {
    setLogs((prev) => [
      ...prev,
      `${new Date().toLocaleDateString()}: ${message}`,
    ]);
    console.log(message);
  };

  const createClient = (onConnecet: (client: Client) => void) => {
    const socket = new SockJS(config.wsUrl);
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => onConnecet(client),
      onStompError: (frame) => {
        addLog("❌ Error: " + frame.headers.message);
      },
    });
    return client;
  };

  return { logs, addLog, createClient };
};
