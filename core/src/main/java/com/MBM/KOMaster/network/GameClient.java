package com.MBM.KOMaster.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Cliente que se conecta al servidor.
 * El cliente es siempre el Jugador 2.
 */
public class GameClient {
    
    private static final int PORT = 25565;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    private boolean running;
    private BlockingQueue<NetworkPacket> receivedPackets;
    private Thread listenerThread;
    
    public GameClient() {
        receivedPackets = new LinkedBlockingQueue<>();
    }
    
    /**
     * Conecta al servidor en la IP especificada
     */
    public boolean connect(String serverIp) {
        try {
            socket = new Socket(serverIp, PORT);
            
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            System.out.println("Conectado al servidor: " + serverIp);
            
            // Iniciar hilo de escucha
            running = true;
            listenerThread = new Thread(this::listenForPackets);
            listenerThread.start();
            
            return true;
        } catch (IOException e) {
            System.err.println("Error conectando al servidor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envía un paquete al servidor
     */
    public void sendPacket(NetworkPacket packet) {
        if (out != null) {
            try {
                out.writeObject(packet);
                out.flush();
                out.reset(); // Importante para evitar caché
            } catch (IOException e) {
                System.err.println("Error enviando paquete: " + e.getMessage());
            }
        }
    }
    
    /**
     * Obtiene el siguiente paquete recibido (no bloqueante)
     */
    public NetworkPacket receivePacket() {
        return receivedPackets.poll();
    }
    
    /**
     * Hilo que escucha paquetes entrantes
     */
    private void listenForPackets() {
        while (running) {
            try {
                NetworkPacket packet = (NetworkPacket) in.readObject();
                receivedPackets.offer(packet);
            } catch (IOException | ClassNotFoundException e) {
                if (running) {
                    System.err.println("Error recibiendo paquete: " + e.getMessage());
                    running = false;
                }
            }
        }
    }
    
    /**
     * Cierra la conexión
     */
    public void disconnect() {
        running = false;
        
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }
    
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}