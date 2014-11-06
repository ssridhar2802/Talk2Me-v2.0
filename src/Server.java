/***********************************************************
* File:	Server.java
*
* @author 		Alfred Jayaprakash
*
* @description 	The ChatServer for Talk2Me, a multithreaded
*				application.
*
***********************************************************/

import java.util.*;
import java.net.*;
import java.io.*;

class Server implements ChatConstants
{
	private static Vector list;
	//private static Vector _userList;
	private ServerSocket ssocket ;
	private Service service;
    private static Socket socket;
    private boolean done=false;
    private static Hashtable userTable = new Hashtable();
	private static Hashtable _userList = new Hashtable();
	private static Hashtable _conflist = new Hashtable();


	public Server() throws UnknownHostException
	{
		System.out.println("Initializing...");
		list=new Vector(BACKLOG);
		//_userList=new Vector(BACKLOG);
		try	{
			ssocket= new ServerSocket(SERVER_PORT,BACKLOG);
		}
		catch(Exception e) {
			System.out.println("Inside constructor"+e);
		}
		start();
	}



	public void start() throws UnknownHostException
	{

		byte[] data;
		int header;
		Socket _socket = null;
		String hostname = null;
		System.out.println("Server successfully started at "
				+InetAddress.getLocalHost().toString()
				+" port "+SERVER_PORT);
		while(!done) {
			try	{
				_socket=ssocket.accept();
				if(_socket != null)	{
					synchronized(list) {
						list.addElement(_socket);
					}

					DataInputStream dis=new DataInputStream(_socket.getInputStream());
					data = new byte[MAX_MESSAGE_SIZE];
					dis.read(data);
					Message message = ((Message)ChatUtils.bytesToObject(data));
					System.out.println("Joined client "
								+message._username+" at "+message._host+"...");
					synchronized(userTable)	{
						userTable.put(message._username,_socket);
					}
					addUser(message);
					sendUserList(message);
					writeToClients(message);
					service = new Service(_socket,hostname,message._user);
				}
				//System.out.println("NUMBER OF CLIENTS :"+list.size());
			}

			catch(Exception e) {
				System.out.println("Thread exception"+e);
				try {
					_socket.close();
				}
				catch(Exception ex) {
					System.out.println("ERROR CLOSING SOCKET");
				}
			}
		}//END WHILE
	}


	private void addUser(Message message)
	{
		synchronized(_userList) {
			_userList.put(message._user.toString(),message._user);
		}
	}

	public static void updateUser(User user)
	{
		User myuser;
		synchronized(_userList) {
			_userList.put(user.toString(),user);
		}
	}

	public static synchronized void writeToClients(Message message)
	{
		byte[] data;
		DataOutputStream dos;
		//System.out.println("Sending message "+message._header);
		for(int count=0;count<list.size();count++) {
			try {
				dos=new DataOutputStream(((Socket)list.elementAt(count)).getOutputStream());
				data=ChatUtils.objectToBytes(message);
				dos.write(data,0,data.length);
			}
			catch(Exception e) {
				System.out.println("Output exception");
			}
		}//END FOR
	}

	public static void writeToClient(Message message)
	{
		Socket socket;
		byte[] data;
		DataOutputStream dos;
		synchronized(userTable) {
			try {
				socket = (Socket)userTable.get(message._destination);
				dos=new DataOutputStream(socket.getOutputStream());
				data=ChatUtils.objectToBytes(message);
				dos.write(data,0,data.length);
			}
			catch(Exception e) {
				System.out.println("SEND EXCEPTION"+e);
			}
		}
	}


	public static void sendConferenceListToClient(Message message)
	{
		Socket socket;
		byte[] data;
		DataOutputStream dos;
		synchronized(userTable) {
			try {
				Message mymessage= new Message(CONFERENCE_LIST);
				Vector vector = (Vector)
					_conflist.get(message._destination);

				mymessage._username = message._username;
				mymessage._destination = message._destination;
				mymessage.userlist = vector;

				socket = (Socket)userTable.get(message._username);

				if(socket!=null) {
					dos=new DataOutputStream(socket.getOutputStream());
					data=ChatUtils.objectToBytes(mymessage);
					dos.write(data,0,data.length);
				}
			}
			catch(Exception e) {
				System.out.println("CONFERENCE LIST EXCEPTION"+e);
			}
		}
	}


	public static void writeToPublicChat(Message message)
	{
		Socket socket;
		byte[] data;
		DataOutputStream dos;
		synchronized(_conflist) {
			try {
				Vector svector = (Vector)_conflist.get(message._destination);
				for(int cnt=0;cnt<svector.size();cnt++) {
					synchronized(userTable) {
						try {
							socket = (Socket)userTable.get((svector.get(cnt).toString()));
							if(socket!=null) {
								dos=new DataOutputStream(socket.getOutputStream());
								data=ChatUtils.objectToBytes(message);
								dos.write(data,0,data.length);
							}
						}
						catch(Exception e) {
							System.out.println("PUBLIC CHAT EXCEPTION"+e);
						}
					}
				}
			} catch(Exception e){
				System.out.println("PUBLIC EXCEPTION"+e);
			}
		}
	}


	public static void inviteToPublicChat(Vector svector,Message message)
	{
		Socket socket;
		byte[] data;
		DataOutputStream dos;
		synchronized(_conflist) {
			for(int cnt=0;cnt<svector.size();cnt++) {
				synchronized(userTable) {
					try {
						socket = (Socket)userTable.get((svector.get(cnt).toString()));
						if(socket != null) {
							dos=new DataOutputStream(socket.getOutputStream());
							data=ChatUtils.objectToBytes(message);
							dos.write(data,0,data.length);
						}
					}
					catch(Exception e) {
						System.out.println("PUBLIC INVITE EXCEPTION"+e);
					}
				}
			}
		}
	}

	private void sendUserList(Message message)
	{
		int header=0;
		String destination;

		header=message._header;
		destination = message._destination;

		message._header = USERS_LIST;
		message._destination = message._username;

		message.userlist = new Vector(_userList.values());
		writeToClient(message);

		//Restore the headers
		message._destination = destination;
		message._header = header;

	}

	public static synchronized void removeUser(User user)
	{
		try {
			Socket socket = (Socket)userTable.get(user.toString());
			list.removeElement(socket);
			_userList.remove(user.toString());
			userTable.remove(user.toString());
		}
		catch(Exception e) {
			System.out.println("ERROR REMOVING SOCKET "+e);
		}
	}


	public static synchronized void processClientMessage(Message message)
	{
		switch(message._header) {

			case CHANGE_STATUS:
				updateUser(message._user);
				writeToClients(message);
				break;

			case CLIENT_LOGOUT:
				removeUser(message._user);
				writeToClients(message);
				break;

			case CONFERENCE_CREATE:
				Vector myvector = new Vector();
				myvector.add(message._username);
				_conflist.put(message._user.toString(),myvector);
			case CONFERENCE_INVITE:
				inviteToPublicChat(message.userlist,message);
			break;

			case CONFERENCE_JOIN:
				Vector vector=null;
				vector = (Vector)
					_conflist.get(message._destination.toString());
				vector.add(message._username);
				_conflist.put(message._destination.toString(),vector);
				writeToPublicChat(message);
			break;

			case CONFERENCE_DENY:
				//_conflist.remove(message._user.toString(),message.userlist);
				writeToPublicChat(message);
			break;

			case CONFERENCE_LEAVE:
				Vector vectors =(Vector)
					_conflist.get(message._destination.toString());
				for(int count=0;count<vectors.size();count++) {
					if(message._username.equals((vectors.elementAt(count).toString())))
						vectors.remove(count);
				}
				if(vectors.size() != 0)
					_conflist.put(message._user.toString(),vectors);
				else//IF THERE ARE NO MORE USERS
					_conflist.remove(message._user.toString());//DONE CONFERENCE
				writeToPublicChat(message);
			break;

			case PUBLIC_CHAT:
				writeToPublicChat(message);
			break;

			case CONFERENCE_LIST:
				sendConferenceListToClient(message);
			break;

			default:
				writeToClient(message);
		}
	}


	public static void main(String args[]) throws Exception
	{
		Server chatserver=new Server();
	}
}


//
//	Service: Service class for each clients connected to server.
//


class Service implements Runnable, ChatConstants
{
	private DataInputStream dis;
	private Socket socket;
	private boolean done=false;
	private Thread thread;
	private String hostname;
	private User user;

	public Service(Socket _socket,String _hostname,User user)
	{
		try	{
			this.socket = _socket;
			this.hostname=_hostname;
			this.user = user;
			dis=new DataInputStream(socket.getInputStream());
			thread=new Thread(this,"SERVICE");
			thread.start();
		}
		catch(Exception e){
			System.out.println("service constructor"+e);
		}
	}

	public void run()
	{
		byte[] data;
		while(!done)
		{
			try	{
				data = new byte[MAX_MESSAGE_SIZE];
				dis.read(data);
				Message message = ((Message)ChatUtils.bytesToObject(data));
				Server.processClientMessage(message);
			}
			catch(Exception e) {
				done = true;
				Server.removeUser(user);
				Message message = new Message(CLIENT_LOGOUT);
				user.isOnline = OFFLINE;
				message._user = user;
				Server.writeToClients(message);
				try	{
					socket.close();
				} catch(Exception se) {
					System.out.println("ERROR CLOSING SOCKET "+se);
				}
				//System.out.println("SERVICE THREAD EXCEPTION"+e);
			}
		}//END WHILE
	}
}