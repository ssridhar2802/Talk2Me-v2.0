/***********************************************************
* File:	Message.java
*
* @author 		Alfred Jayaprakash
*
* @description 	Message object which is passed btwn clients
*				and server.
*
***********************************************************/

public class Message implements java.io.Serializable
{
	public int 	  			_header;
	public String 			_username;
	public String 			_destination;
	public String 			_message;
	public String 			_host;
	public User 			_user;
	public java.util.Vector userlist;
	public String 			_data;

	public Message()
	{
		//EMPTY MESSAGE
	}

	public Message(int header)
	{
		_header = header;
	}

	public Message(int header,String message)
	{
		_header = header;
		_message=message;
	}

	public String getMessage()
	{
		return _message;
	}
}