import java.net.*;
import java.io.*;

class CallerListener {
        private String remoteNick;
        private String localNick;
        private ServerSocket ss;
        private String localIp;
        private boolean busy;
        private SocketAddress ListenerAddress, remoteAddress;
        private Connection ic;

        public CallerListener(){
                localNick = "unnamed";
                localIp = "127.0.0.1";
                try {
                        ss = new ServerSocket(28411);
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public Connection getConnection() throws IOException{
                ic = new Connection(ss.accept());
                System.out.println("Client Connected");
                ic.sendNickHello("2015","Client");
                /*if (ic.receive().getType() == Command.CommandType.NICK) {
                        remoteNick = ic.getNick();
                        if (localNick == null) {
                                ic.disconnect();
                                ic = null;
                        }
                        else {
                                if (busy) {
                                        ic.sendNickBusy("2015", localNick);
                                        ic = null;
                                }
                                else
                                        ic.sendNickHello("2015", localNick);
                        }
                }*/
                return ic;
        }

        // Ждать входящего соединения. В состоянии "busy" обрывает соединение и возвращает null, но информацию о звонящем можно узнать

        public String getLocalNick() {
                return localNick;
        }

        public boolean isBusy() {
                return false;
        }

        public SocketAddress getListenAddress(){
                return ListenerAddress;
        }


        public String getRemoteNick(){
                return remoteNick;
        }


        public SocketAddress getRemoteAddress(){
                return remoteAddress;
        }

        public void setLocalNick(String localNick){
                this.localNick = localNick;
        }

        public void setBusy(boolean busy){
                this.busy=busy;
        }

        public void setListenAddress(SocketAddress listenAddress){
                this.ListenerAddress = listenAddress;
        }

}