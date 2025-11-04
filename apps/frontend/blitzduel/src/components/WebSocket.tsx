import { useState } from 'react';
import { Client, type IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// Backend response types
interface LobbyResponse {
    code: string;
    status: string;
}

interface LobbyUpdate {
    players?: string[];
    status?: string;
    [key: string]: unknown;
}

interface LobbyRequest {
    nickname: string;
}

function TestWebSocket() {
    const [nickname, setNickname] = useState<string>('');
    const [lobbyCode, setLobbyCode] = useState<string>('');
    const [joinCode, setJoinCode] = useState<string>('');
    const [logs, setLogs] = useState<string[]>([]);

    const addLog = (message: string): void => {
        setLogs(prev => [...prev, `${new Date().toLocaleTimeString()}: ${message}`]);
        console.log(message);
    };

    const handleCreateLobby = (): void => {
        if (!nickname) {
            alert('Enter nickname!');
            return;
        }

        const socket = new SockJS('https://api.ittt.se/ws');
        const client = new Client({
            webSocketFactory: () => socket,

            onConnect: () => {
                addLog(`✅ Connected to WebSocket`);

                // Subscribe till response
                client.subscribe('/user/queue/lobby', (message: IMessage) => {
                    const data: LobbyResponse = JSON.parse(message.body);
                    addLog(`📨 Lobby created: ${data.code}`);
                    setLobbyCode(data.code);

                    client.subscribe(`/topic/lobby/${data.code}`, (updateMessage: IMessage) => {
                        const updateData: LobbyUpdate = JSON.parse(updateMessage.body);
                        addLog(`📨 Lobby updated: ${JSON.stringify(updateData)} `);
                    });
                });

                // Skicka create request
                const payload: LobbyRequest = { nickname };
                client.publish({
                    destination: '/app/lobby/create',
                    body: JSON.stringify(payload)
                });

                addLog(`📤 Sent create lobby request (${nickname})`);
            },

            onStompError: (frame) => {
                addLog('❌ Error: ' + frame.headers.message);
            }
        });

        client.activate();
    };

    const handleJoinLobby = (): void => {
        if (!nickname || !joinCode) {
            alert('Enter nickname and lobby code!');
            return;
        }

        const socket = new SockJS('https://api.ittt.se/ws');
        const client = new Client({
            webSocketFactory: () => socket,

            onConnect: () => {
                addLog(`✅ Connected to WebSocket`);


                client.subscribe('/user/queue/lobby', (message: IMessage) => {
                    const data: LobbyResponse = JSON.parse(message.body);

                    if (data.status === "FULL") {
                        console.log("vad")
                        addLog('Lobby is full!');
                        client.deactivate();
                        return;
                    }
                });

                // Subscribe till lobby updates
                client.subscribe(`/topic/lobby/${joinCode}`, (message: IMessage) => {
                    const data: LobbyUpdate = JSON.parse(message.body);
                    addLog(`📨 Lobby update: ${JSON.stringify(data)}`);

                });

                // Skicka join request
                setTimeout(() => {
                    const payload: LobbyRequest = { nickname };
                    client.publish({
                        destination: `/app/lobby/${joinCode}/join`,
                        body: JSON.stringify(payload)
                    });

                    addLog(`📤 Sent join request to ${joinCode}`);
                }, 100);
            },

            onStompError: (frame) => {
                addLog(`❌ Error:` + frame.headers.message);
            }
        });

        client.activate();
    };

    return (
        <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
            <h1>🎮 WebSocket Test</h1>

            <div style={{ marginBottom: '20px' }}>
                <input
                    type="text"
                    placeholder="Your nickname"
                    value={nickname}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setNickname(e.target.value)}
                    style={{
                        padding: '10px',
                        width: '100%',
                        marginBottom: '10px',
                        fontSize: '16px'
                    }}
                />
            </div>

            <div style={{ marginBottom: '30px' }}>
                <button
                    onClick={handleCreateLobby}
                    style={{
                        padding: '10px 20px',
                        fontSize: '16px',
                        cursor: 'pointer',
                        width: '100%',
                        marginBottom: '10px'
                    }}
                >
                    Create Lobby
                </button>

                {lobbyCode && (
                    <div style={{
                        padding: '15px',
                        background: '#d4edda',
                        borderRadius: '5px',
                        fontSize: '18px',
                        fontWeight: 'bold'
                    }}>
                        Your Code: {lobbyCode}
                    </div>
                )}
            </div>

            <div style={{ marginBottom: '30px' }}>
                <input
                    type="text"
                    placeholder="Enter lobby code"
                    value={joinCode}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setJoinCode(e.target.value.toUpperCase())}
                    style={{
                        padding: '10px',
                        width: '100%',
                        marginBottom: '10px',
                        fontSize: '16px'
                    }}
                />
                <button
                    onClick={handleJoinLobby}
                    style={{
                        padding: '10px 20px',
                        fontSize: '16px',
                        cursor: 'pointer',
                        width: '100%'
                    }}
                >
                    Join Lobby
                </button>
            </div>

            <div>
                <h3>Logs:</h3>
                <div style={{
                    background: '#f8f9fa',
                    padding: '10px',
                    maxHeight: '300px',
                    overflow: 'auto',
                    fontFamily: 'monospace',
                    fontSize: '12px',
                    border: '1px solid #ddd'
                }}>
                    {logs.map((log, idx) => (
                        <div key={idx} style={{ marginBottom: '5px' }}>
                            {log}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default TestWebSocket;
