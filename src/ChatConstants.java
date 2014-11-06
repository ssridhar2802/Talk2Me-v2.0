/***********************************************************
* File:	ChatConstants.java
*
* @author 		Alfred Jayaprakash
*
* @description 	Application constants are defined here
*
***********************************************************/

public interface ChatConstants
{
	final String 	SERVER_HOST 		= "127.0.0.1";
	final int		SERVER_PORT 		= 2979;
	final int 		BACKLOG				= 10;
	final int 		MAX_MESSAGE_SIZE	= 2048;

	//Message constants
	final int CLIENT_LOGIN  	= 1;
	final int CLIENT_LOGOUT 	= 2;
	final int PUBLIC_CHAT		= 3;
	final int PRIVATE_CHAT		= 4;
	final int SERVER_DOWN		= 5;
	final int USERS_LIST		= 6;
	final int CHANGE_STATUS		= 7;
	final int CONFERENCE_CREATE = 8;
	final int CONFERENCE_INVITE = 9;
	final int CONFERENCE_JOIN	= 10;
	final int CONFERENCE_DENY	= 11;
	final int CONFERENCE_LEAVE  = 12;
	final int CONFERENCE_LIST	= 13;

	//My Welcome image
	final java.net.URL IMAGE_ICON = ClassLoader.getSystemClassLoader().getResource("images/Talk2Me.jpg");

	//User status
	final int ONLINE		= 1;
	final int OFFLINE		= 2;
	final int BUSY			= 3;
	final int IDLE			= 4;


	//Look and feel
	final String WINDOWS ="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	final String METAL   ="javax.swing.plaf.metal.MetalLookAndFeel";
	final String MOTIF   = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";


	//Smileys
	final String SMILE = ClassLoader.getSystemClassLoader().getResource("images/1.gif").toString();
	final String SAD = ClassLoader.getSystemClassLoader().getResource("images/2.gif").toString();
	final String WINK = ClassLoader.getSystemClassLoader().getResource("images/3.gif").toString();
	final String SHY = ClassLoader.getSystemClassLoader().getResource("images/4.gif").toString();
	final String LOVE = ClassLoader.getSystemClassLoader().getResource("images/5.gif").toString();
	final String TEETH = ClassLoader.getSystemClassLoader().getResource("images/6.gif").toString();
	final String QUESTION = ClassLoader.getSystemClassLoader().getResource("images/7.gif").toString();
	final String BYE = ClassLoader.getSystemClassLoader().getResource("images/8.gif").toString();
	final String TONGUE = ClassLoader.getSystemClassLoader().getResource("images/9.gif").toString();
	final String KISS = ClassLoader.getSystemClassLoader().getResource("images/10.gif").toString();
	final String SURPRISE = ClassLoader.getSystemClassLoader().getResource("images/11.gif").toString();
	final String ANGRY = ClassLoader.getSystemClassLoader().getResource("images/12.gif").toString();
	final String MEAN = ClassLoader.getSystemClassLoader().getResource("images/13.gif").toString();
	final String COOL = ClassLoader.getSystemClassLoader().getResource("images/14.gif").toString();
	final String DEVIL = ClassLoader.getSystemClassLoader().getResource("images/16.gif").toString();
	final String CRY = ClassLoader.getSystemClassLoader().getResource("images/17.gif").toString();
	final String LAUGH = ClassLoader.getSystemClassLoader().getResource("images/18.gif").toString();
	final String EQUISMILE = ClassLoader.getSystemClassLoader().getResource("images/19.gif").toString();
	final String COFFEE = ClassLoader.getSystemClassLoader().getResource("images/20.gif").toString();


}