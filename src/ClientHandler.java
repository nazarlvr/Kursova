import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Server server;
    private Socket socket;
    private BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public String clientUsername;

    public ClientHandler(Socket socket, Server srv) {
        try {
            this.socket = socket;
            this.server = srv;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            this.server.messageQueue.add("Server: " + clientUsername + " has entered the chat!");
        } catch (IOException e) {
            this.closeEverything();
        }
    }

    @Override
    public void run()
    {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                this.server.messageQueue.add(this.clientUsername + ": " + messageFromClient);
            } catch (IOException e) {
                this.closeEverything();
                break;
            }
        }
    }

    public void sendMessage(String messageToSend)
    {
        try {
            this.bufferedWriter.write(messageToSend);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClientHandler() {
        this.server.clientHandlers.remove(this);
        this.server.broadcastMessage("Server: " + clientUsername + " has left the chat!");
    }

    public void closeEverything() {
        removeClientHandler();
        try {
            if (this.bufferedReader != null) {
                this.bufferedReader.close();
            }
            if (this.bufferedWriter != null) {
                this.bufferedWriter.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
