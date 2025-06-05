import React, { createContext, useContext, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const WebSocketContext = createContext();

export const WebSocketProvider = ({ children }) => {
  const clientRef = useRef(null);
  
  useEffect(() => {
    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, []);

  const connect = (userId, onOrderUpdate) => {
    if (clientRef.current) {
      clientRef.current.deactivate();
    }

    const stompClient = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      reconnectDelay: 5000,
    });

    stompClient.onConnect = () => {
      const topic = `/topic/orders/${userId}`;
      stompClient.subscribe(topic, (message) => {
        const order = JSON.parse(message.body);
        onOrderUpdate(order);
      });
    };

    stompClient.activate();
    clientRef.current = stompClient;
  };

  return (
    <WebSocketContext.Provider value={{ connect }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => useContext(WebSocketContext);