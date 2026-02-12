package com.MBM.KOMaster.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Servidor que espera conexiones y maneja la comunicación con el cliente.
 * El servidor es siempre el Jugador 1.
 */
public class GameServer {
    
    private static final int PORT = 25565;
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    private boolean running;
    private BlockingQueue<NetworkPacket> receivedPackets;
    private Thread listenerThread;
    
    public GameServer() {
        receivedPackets = new LinkedBlockingQueue<>();
    }
    
    /**
     * Inicia el servidor y espera una conexión
     */
    public boolean start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciado en puerto " + PORT);
            return true;
        } catch (IOException e) {
            System.err.println("Error al iniciar servidor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Espera a que un cliente se conecte (bloqueante)
     */
    public boolean waitForClient() {
        try {
            System.out.println("Esperando cliente...");
            clientSocket = serverSocket.accept();
            
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
            
            // Iniciar hilo de escucha
            running = true;
            listenerThread = new Thread(this::listenForPackets);
            listenerThread.start();
            
            // Enviar confirmación de conexión
            NetworkPacket connectPacket = new NetworkPacket(NetworkPacket.PacketType.CONNECT);
            connectPacket.playerId = 2; // El cliente es jugador 2
            sendPacket(connectPacket);
            
            return true;
        } catch (IOException e) {
            System.err.println("Error esperando cliente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envía un paquete al cliente
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
     * Cierra el servidor
     */
    public void stop() {
        running = false;
        
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error cerrando servidor: " + e.getMessage());
        }
    }
    
    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }
}