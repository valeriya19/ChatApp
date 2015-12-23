import javax.swing.*;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by 81k5_Pr0g3r on 22.12.15.
 */
public class Caller  {
    String ip;
    JFrame frame=new JFrame("Connecting to "+ip);
    Connection connection;
    public Caller(String ip){
        this.ip=ip;

        JProgressBar progressBar = new JProgressBar(0,20);
        progressBar.setIndeterminate(true);
        progressBar.setString("Connecting to " + ip);
        progressBar.setStringPainted(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(progressBar);

        frame.setSize(270,60);
        frame.setVisible(true);
        Call();
    }

    public void Call(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                try{
                    socket.connect(new InetSocketAddress(ip,ChatProtocol.port),10000);
                    connection= new Connection(socket,"");
                    if (connection.readCommand()!=null){
                        connection.Hello();
                    }
                    else{
                        connection.close();
                        return;
                    }
                    switch (connection.readCommand().getType()){
                        case CT_ACCEPT:
                            CallerAccept();
                            break;
                        case CT_REJECT:
                            CallerReject();
                    }

                } catch (UnknownHostException e) {
                    System.err.println("Host '"+ip+"' is not resolved");
                    return;
                } catch (SocketTimeoutException e){
                    CallerTimeOut();
                } catch (IOException e) {
                    System.err.println("can't connect");
                    return;
                }

            }
        }).start();

    }

    public void CallerAccept(){
        new ChatDialog(connection);
        frame.dispose();
    }
    public void CallerTimeOut(){
        System.err.println("Timeout while connecting to ip= "+ip);
        Object[] option = {"Yes", "No"};

        int n = JOptionPane.showOptionDialog(frame,
                ("Timeout while connecting to "+ip+". Try again?"),
                "Reject",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                option,
                option[0]);
        if (n==0){
            Call();
        }
        else
            frame.dispose();
    }
    public  void CallerReject(){
        System.err.println("Reject from ip= "+ip);
        Object[] option = {"Yes", "No"};

        int n = JOptionPane.showOptionDialog(frame,
                ("User from "+ip+" reject your request. Try again?"),
                "Reject",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                option,
                option[0]);
        if (n==0){
            Call();
        }
        else
            frame.dispose();
    }


}
