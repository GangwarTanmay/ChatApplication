import com.sun.jdi.event.ExceptionEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.*;
import java.io.*;

public class Client extends JFrame{

    Socket socket;
    PrintWriter out;
    BufferedReader br;

    private JLabel clientHeading = new JLabel("Client Area");
    private JTextArea messageBox = new JTextArea();
    private JTextField msg = new JTextField();

    Client(){
        try{
            System.out.println("Sending request to server");
            socket = new Socket("2401:4900:8022:f77e:606a:e91e:3071:1b3d",7778);  // we have to specify the ip address and port number
                                                                 // where server is running currently
            System.out.println("Connected to server");
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();

              startReading();
//            startWriting();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createGUI(){

        //this refers to window
        this.setTitle("Client");
        this.setSize(500, 500);      // set size of window in px
        this.setLocationRelativeTo(null);       // this will set window in center of screen
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //SwingConstants is an interface which is used to specify orientation and spacing of components
        clientHeading.setHorizontalAlignment(SwingConstants.CENTER);

        // BorderFactory is a class used to give borders
        clientHeading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //createEmptyBorder will give spacing from all sides
        messageBox.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageBox.setEditable(false);      //user should not be able to edit messages

        //createCompoundBorder is used to give border of multiple types, here LineBorder will give border of black color and EmptyBorder will give padding
        msg.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.black),new EmptyBorder(10, 10, 10, 10)));

        //The BorderLayout is used to arrange the components in five regions: north, south, east, west, and center.
        this.setLayout(new BorderLayout());  //using borderLayout for window(this)

        //adding components to frame
        this.add(clientHeading,BorderLayout.NORTH);     //adding heading to NORTH (TOP) of window
        JScrollPane scrollBar = new JScrollPane(messageBox);    //JScrollPane is used to make scrollable view of a component
        this.add(scrollBar, BorderLayout.CENTER);
        this.add(msg,BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void handleEvents(){
        msg.addKeyListener(new KeyListener() {   //KeyListener is an interface hence to implement it,  we need to override its methods
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {    // key code for enter key is 10, hence if enter key is released
                    String content = msg.getText();     // extract text for msg box
                    out.println(content);       // send content to the client side
                    out.flush();
                    msg.setText("");    //when msg is sent, then empty the msg box
                    messageBox.append("Me : "+content+"\n");
                }
            }
        });
    }

    public void startReading(){
        Runnable r1 = ()->{
            while(true){
                try {
                    String content = br.readLine();
                    if (content.equals("exit") || socket.isClosed()) {
                        //if server send exit message then instead of showing message on console, we would show message dialog box
                        socket.close();
                        JOptionPane.showMessageDialog(this, "Server left the chat!");
                        msg.setEditable(false);  // if server left then disable the message box
                        return;
                    }
//                    System.out.println("Server : "+msg);
                      messageBox.append("Server : "+content+"\n");
                }
                catch(Exception e){
                    System.out.println("Connection Lost!");
                    return;
                }
            }
        };
        new Thread(r1).start();
    }

    public void startWriting(){
        Runnable r2 = ()->{
            while(true){
                try {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String msg = br1.readLine();
                    out.println(msg);
                    out.flush();

                    if(msg.equals("exit") || socket.isClosed()){
                        socket.close();
                        return;
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is client");
        new Client();
    }
}
