import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1234;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMessage;
    private String clientName;
    private boolean f;

    public Client(){
        f = false;
        /*try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }*/
        try
        {
            this.socket = new Socket(SERVER_HOST, SERVER_PORT);
            bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBounds(600, 300, 600, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
        add(jsp, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSendMessage = new JButton("Set username");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMessage = new JTextField("");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        jtfName = new JTextField("Username:");
        jtfName.setEditable(false);
        bottomPanel.add(jtfName, BorderLayout.WEST);
        jbSendMessage.addActionListener(e -> {
            try {
                if (!f && !jtfMessage.getText().isEmpty())
                {
                    String messageStr = jtfMessage.getText();
                    this.clientName = messageStr;
                    bufferedWriter.write(messageStr);
                    bufferedWriter.newLine();

                    bufferedWriter.flush();

                    jtfMessage.setText("");
                    jtfName.setText(messageStr);
                    jtfMessage.grabFocus();
                    jbSendMessage.setText("Send");
                    f = true;
                }

                else if (!jtfMessage.getText().trim().isEmpty())
                {
                    String messageStr = jtfMessage.getText();
                    jtaTextAreaMessage.append(messageStr + '\n');
                    bufferedWriter.write(messageStr);
                    bufferedWriter.newLine();

                    bufferedWriter.flush();

                    jtfMessage.setText("");
                    jtfMessage.grabFocus();
                }
            } catch (IOException ex) {
                closeEverything();
            }
        });
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });

        new Thread(() -> {
            try {
                while (socket.isConnected())
                {
                    String inMes = bufferedReader.readLine();
                    jtaTextAreaMessage.append(inMes);
                    jtaTextAreaMessage.append("\n");

                }
            } catch (Exception e)
            {
                closeEverything();
            }
        }).start();
        setVisible(true);
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Client client = new Client ();
    }

}
