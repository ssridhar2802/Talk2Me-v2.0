/***********************************************************
* File:	ChatUtils.java
*
* @author 		Alfred Jayaprakash
*
* @description 	Utility class to create byte[] of an object.
*				Should be replaced in the future.
*
***********************************************************/

import java.io.IOException;

public class ChatUtils
{
	public static byte[] objectToBytes (Object object) throws IOException
	{
		java.io.ObjectOutputStream out;
		java.io.ByteArrayOutputStream bs;

		bs = new java.io.ByteArrayOutputStream ();
		out = new java.io.ObjectOutputStream (bs);
		out.writeObject (object);
		out.close ();

		return bs.toByteArray ();
	}

	public static Object bytesToObject (byte bytes[]) throws IOException,ClassNotFoundException
	{
		Object res;
		java.io.ObjectInputStream in;
		java.io.ByteArrayInputStream bs;

		bs = new java.io.ByteArrayInputStream (bytes);
		in = new java.io.ObjectInputStream(bs);
		res = in.readObject ();
		in.close ();
		bs.close ();
		return res;
	}
}