import java.io.IOException;
import java.net.Socket;

public class ClientConnector implements Runnable
{
    public Server server;

    public ClientConnector(Server srv)
    {
        this.server = srv;
    }

    public void run()
    {
        try {
            while (!this.server.serverSocket.isClosed()){
                Socket socket = this.server.serverSocket.accept();
                System.out.println("A new client connected!");
                ClientHandler clientHandler =  new ClientHandler(socket, this.server);
                this.server.clientHandlers.add(clientHandler);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}
