

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.EventObject;
import java.util.Vector;
import com.jeans.trayicon.*;

class Client implements ChatConstants,Runnable
{

	private Socket socket;
	private Thread thread;
	private DataInputStream dis;
	private DataOutputStream dos;
	private boolean done=false;
	private boolean connected = false;
	public MainFrame frame;
	public static String _address;
	private String username;
	private User user;


    public Client() throws Exception
    {
		_address = InetAddress.getLocalHost().toString();
		frame = new MainFrame(this);
    }

	public void connectToServer(String server)
	{
		try	{
			socket = new Socket(server, SERVER_PORT);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			connected = true;
			thread = new Thread(this,"USER THREAD");
			thread.start();
		}
		catch(Exception exception) {
			JOptionPane.showMessageDialog(frame,"Cannot find server !! Please check your settings and try again","Connection error",JOptionPane.ERROR_MESSAGE);
			WindowsTrayIcon.cleanUp();
			System.exit(-1);
		}

	}
    public void run()
    {
        //System.out.println("USER THREAD STARTED");
        byte[] data;
        while(connected && !done) {
			try {
				processServerMessage();//RECEIVE MESSAGES FROM SERVER
			}
			catch(java.net.SocketException se)
			{
				JOptionPane.showMessageDialog(frame,"Server connection reset!! Please login again","Talk2Me: Error",JOptionPane.ERROR_MESSAGE);
				done = true;
			}
   			catch(NullPointerException se)
			{
				//DO NOTHING
			}
   			catch(java.io.StreamCorruptedException se)
			{
				//DO NOTHING
			}
            catch(Exception e) {
				JOptionPane.showMessageDialog(frame,"Message processing error !!"+e,"Talk2Me: Error",JOptionPane.ERROR_MESSAGE);
				done = true;
			}
	   	}//END WHILE
	   	//IF IT COMES HERE: IT MEANS ERROR
	   	WindowsTrayIcon.cleanUp();
	   	System.exit(-1);
    }

    public void sendMessageToServer(String message) throws java.io.IOException
    {

		sendMessageToServer(new Message(PUBLIC_CHAT,message));
	}

	public void sendMessageToServer(Message message) throws java.io.IOException
	{
		byte[] data;
		//PACK THE USERNAME AND HOST ADDRESS EVERYTIME
		message._username = username;
		message._host = _address;
      	data = ChatUtils.objectToBytes(message);
		dos.write(data,0,data.length);
		dos.flush();
	}

	public void processServerMessage() throws java.io.IOException,
											java.net.SocketException,Exception
	{
		byte[] data;
    	data = new byte[MAX_MESSAGE_SIZE];
		dis.read(data);
		Message message = (Message)ChatUtils.bytesToObject(data);
		frame.processServerMessage(message);
	}

	public void setUser(User user)
	{
		this.username = user.toString();
		this.user = user;
	}

	public static void main(String args[]) throws Exception
    {
		//SplashScreen screen = new SplashScreen(3000);
  		Client myclient = new Client();
    }
}

