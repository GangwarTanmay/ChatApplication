import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.awt.*;

public class Server extends JFrame {

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel serverHeading = new JLabel();
    private JTextArea messageBox = new JTextArea();
    private JTextField msg = new JTextField();

    Server(){

        try{
            server = new ServerSocket(7778);    // it might through an exception hence wrapped in try-catch block
            System.out.println("Server started!");
            System.out.println("Waiting for client to send request");
            socket = server.accept();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReadingInput();
//            startWritingOutput();


        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createGUI(){
        this.setTitle("Server");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        serverHeading.setText("Server");
        serverHeading.setHorizontalAlignment(SwingConstants.CENTER);
        serverHeading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        messageBox.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageBox.setEditable(false);

        //add scrollBar to messageBox
        JScrollPane scrollableMessageBox = new JScrollPane(messageBox);

        msg.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(10,10,10,10)));

        this.add(serverHeading, BorderLayout.NORTH);
        this.add(scrollableMessageBox,BorderLayout.CENTER);
        this.add(msg,BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void handleEvents(){
        msg.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == 10){
                    String content = msg.getText();
                    out.println(content);
                    out.flush();
                    msg.setText("");
                    messageBox.append("Me : "+content+"\n");
                }
            }
        });
    }

    public void startReadingInput(){

        //we have to do reading and writing simultaneously hence we would use mutithreading
        Runnable r1 = ()->{  //lambda expression
            // read input from client until client send "exit"
            while(true){
                try{
                    String content = br.readLine();
                    if(content.equals("exit") || socket.isClosed()) {
                        JOptionPane.showMessageDialog(this, "Client left!");
                        msg.setEditable(false);
                        socket.close();
                        return;
                    }
                    messageBox.append("Client : "+content+"\n");
                }
                catch (Exception e){
                    System.out.println("Connection Lost!");
                    return;
                }
            }
        };
        new Thread(r1).start();
    }

    public void startWritingOutput(){

        //we have to do reading and writing simultaneously hence we would use mutithreading
        Runnable r2 = ()->{  //lambda expression
            // read input from client until client send "exit"
            while(true){
                try{

                    //to send data to client, first be need to read input from console(i.e., System.in)
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String msg = br1.readLine();    //read msg from console
                    out.println(msg);   // send msg to client
                    out.flush();

                    if(msg.equals("exit") || socket.isClosed()){
                        socket.close();
                        return;
                    }
                }
                catch (Exception e){
                    System.out.println("Connection lost");
                    return;
                }
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("Hello, this is server");
        new Server();
    }
}
