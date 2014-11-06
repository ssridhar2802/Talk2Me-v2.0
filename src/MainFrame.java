

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import com.jeans.trayicon.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.EventObject;
import java.util.Vector;
import java.util.TreeSet;
import java.util.Hashtable;
import java.util.Date;
import java.sql.*;
public class MainFrame extends JFrame implements ActionListener,ItemListener, ChatConstants
{
        public LoginDialog dialog=null;
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private Dimension screensize = toolkit.getScreenSize();
	private int width= screensize.width,height=screensize.height;
	public static MainFrame frame;
	private JButton login;
	private JLabel label;
	private JTextField name,send;
	private JTextArea recv;
	private JMenuBar menubar;
	private JMenu menu,style,help,friends;
	private JMenuItem l_item,e_item,win_item,met_item,mot_item,about,conf;
	private JTree usrtree;
	private JScrollPane scrollpane;
	private Container container;
	public  static Client client;
	public String _username,_password,_server;
	public static Vector _userlist = new Vector();
	private TreeSet set;
	private User user;
	private MessageDispatcher _dispatcher;
	private UserTreePanel panel;
	private JComboBox combo;
	public static Hashtable frameTable = new Hashtable();


    public MainFrame(Client client) throws Exception
    {
		this.client = client;
		initAwtContainer();
		frame = this;
		if (WindowsTrayIcon.isRunning("Client")) {
				// App already running, show error message and exit
		}
		WindowsTrayIcon.initTrayIcon("Client");
		Image image = loadImage("plus.gif");
		WindowsTrayIcon icon = new WindowsTrayIcon(image, 16, 16);
		icon.setToolTipText("Talk2Me");
		icon.addActionListener(new RestoreListener(true));
		icon.setVisible(true);
		processLogin();
    }

    private void initAwtContainer() throws Exception
    {
		container = this.getContentPane();
		container.setLayout(new FlowLayout());

		String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		//String lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
		//String lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		UIManager.setLookAndFeel(lookAndFeel);

		menubar= new JMenuBar();
		menu = new JMenu("Login");
		l_item=new JMenuItem("Login");
		e_item=new JMenuItem("Exit");
		menu.add(l_item);
		menu.add(e_item);
		menubar.add(menu);

		friends = new JMenu("Friends");
		conf = new JMenuItem("Start a conference...");
		conf.addActionListener(this);
		friends.add(conf);
		menubar.add(friends);


		style = new JMenu("Style");
		win_item = new JRadioButtonMenuItem("Windows");
		win_item.addItemListener(this);

		met_item = new JRadioButtonMenuItem("Metal");
		met_item.addItemListener(this);

		mot_item = new JRadioButtonMenuItem("Motif");
		mot_item.addItemListener(this);


		style.add(win_item);
		style.add(met_item);
		style.add(mot_item);

		ButtonGroup group = new ButtonGroup();
		group.add(win_item);
		group.add(met_item);
		group.add(mot_item);
		menubar.add(style);

		help = new JMenu("Help");
		about = new JMenuItem("About");
		about.addActionListener(this);
		help.add(about);
		menubar.add(help);



		this.setJMenuBar(menubar);


		l_item.addActionListener(this);
		e_item.addActionListener(this);

		label = new JLabel("Status");
		combo = new JComboBox();
		combo.addItem("I'm available");
		combo.addItem("Busy");
		combo.addItem("Invisible");
		combo.addItem("Away");
		combo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event){
				if(event.getStateChange() == ItemEvent.SELECTED) {
					sendStatus(((JComboBox)event.getSource()).getSelectedIndex());
				}
			}
		});

		this.addWindowListener(new WindowAdapter() {

			public void windowIconified(WindowEvent e)
			{
				MainFrame.hideMe();
			}

			public void windowClosing(WindowEvent e)
			{
				if(JOptionPane.showConfirmDialog(container,
							"Are you sure you want to quit Talk2Me ?",
							"Quit Talk2Me",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null) == JOptionPane.YES_OPTION) {
					sendLogout();
					WindowsTrayIcon.cleanUp();
					System.exit(0);
				}
			}
		});

		this.setSize(225, 400);
		this.setLocation(400,100);
		this.setVisible(true);
		this.setResizable(false);
		this.setTitle("Talk2Me");
	}


	public static void showMe()
	{
		frame.show();
	}

	public static void hideMe()
	{
		frame.hide();
	}

	public void itemStateChanged(ItemEvent e)
	{
		JRadioButtonMenuItem item = (JRadioButtonMenuItem) e.getSource();
		String source = item.getText();
		if(e.getStateChange() == ItemEvent.SELECTED) {
			if(source.equals("Windows")) {
				changeLookAndFeel(WINDOWS);
			} else if(source.equals("Metal")) {
				changeLookAndFeel(METAL);
			} else if(source.equals("Motif")) {
				changeLookAndFeel(MOTIF);
			}
		}
	}

	private void changeLookAndFeel(String looks)
	{
		try {
			UIManager.setLookAndFeel(looks);
		} catch (Exception ex) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(Exception exc) {}
		}
		SwingUtilities.updateComponentTreeUI(this);
	}

    public void actionPerformed(ActionEvent event)
    {
        if(event.getSource() == l_item) {
			processLogin();
        } else if(event.getSource() == e_item) {
			//sendLogout();
			WindowsTrayIcon.cleanUp();
			System.exit(0);
		} else if(event.getSource() == about) {
		      SplashScreen screen = new SplashScreen(0);
  		} else if(event.getSource() == conf) {
			//START A CONFERENCE - SHOW THE INVITEES DIALOG
			InviteesDialog dialog = new InviteesDialog(this,_userlist);
			dialog.setVisible(true);
			if(dialog.getSelected() != null) {
			//GET THE SELECTED LIST
				long time = (new Date()).getTime();
				User conf_user =
					new User(_username+time,
									Client._address,ONLINE);
				conf_user.isConference=true;
				Message message = new Message(CONFERENCE_CREATE);
				message._user = conf_user;
				message.userlist = dialog.getSelected();

				try {
					client.sendMessageToServer(message);
				}
				catch(java.io.IOException ie) {
					JOptionPane.showMessageDialog(this,
								ie.toString(),
								"Messaging error",JOptionPane.ERROR_MESSAGE);
					//System.exit(-1);
				}
				createFrame(conf_user,false);//Open the conference frame
			}
		}
    }

    public void processLogin()
    {
		//GET THE MESSAGE DISPATCHER FIRST
		_dispatcher=  new MessageDispatcher(this);
		showLoginDialog();
		if(_username == null || _password==null) return;
		if(_server == null) _server = SERVER_HOST;
		if((_username.length()!=0) || (_password.length() != 0)){
			l_item.setEnabled(false);
			client.connectToServer(_server);//CONNECT TO SERVER
			user = (new User(_username,Client._address,ONLINE));
			client.setUser(user);
			sendClientLogin();
		}
	}

	private void sendClientLogin()
	{
		Message message = new Message(CLIENT_LOGIN);
		message._message = _password;
		message._user = user;

		try {
			client.sendMessageToServer(message);
		}
		catch(java.io.IOException ie) {
			JOptionPane.showMessageDialog(this,
							ie.toString(),
							"Login error",JOptionPane.ERROR_MESSAGE);
			WindowsTrayIcon.cleanUp();
			System.exit(0);
		}
	}

    private void showLoginDialog()
    {
		dialog = new LoginDialog(this);
		_username= dialog.getUserName();
		_password= dialog.getPassword();
		_server = dialog.getServerHost();
	}

	private void sendLogout()
	{
		try {
			Message message = new Message(CLIENT_LOGOUT);
			user.isOnline = OFFLINE;
			message._user = user;
			client.sendMessageToServer(message);
                         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
           Connection conn=DriverManager.getConnection("jdbc:odbc:DB2COPY1","sridhar","sridhar");
            PreparedStatement ps=conn.prepareStatement("update users set isonline=0 where username=?");
            ps.setString(1,frame._username);
            ps.executeUpdate();
		}
		catch(Exception e) { System.exit(0);}
	}

	private void sendStatus(int status)
	{
		try {
			switch(status) {
				case 0:
					status = ONLINE;
				break;
				case 1:
					status = BUSY;
				break;
				case 2:
					status = OFFLINE;
				break;
				default:
					status = ONLINE;
			}

			Message message = new Message(CHANGE_STATUS);
			user.isOnline = status;
			message._user = user;
			client.sendMessageToServer(message);
		}
		catch(Exception e) { System.exit(0);}
	}

	public void processServerMessage(Message message) throws Exception
	{
		//System.out.println("RECEIVE MESSAGE "+message._header);
		switch(message._header)	{
			case USERS_LIST:
				for(int cnt=0;cnt<message.userlist.size();cnt++)
					_userlist.add(message.userlist.elementAt(cnt).toString());
				panel = new UserTreePanel(this,message.userlist);
				panel.setPreferredSize(new Dimension(220,300));
				container.add(panel);
				container.add(label);
				container.add(combo);
				container.validate();
			break;

			case CLIENT_LOGIN:
				if(message._username.equals(_username))return;
				_userlist.add(message._user.toString());
				panel.updateUser(message._user);
				_dispatcher.dispatchMessage(message);
			break;

			case CHANGE_STATUS:
				panel.updateUser(message._user);
			break;
			case CLIENT_LOGOUT:
				if(_userlist.contains(message._user.toString())) {
					_userlist.remove(message._user.toString());
					panel.removeUser(message._user);
					_dispatcher.dispatchMessage(message);
             
				}
			break;

			case CONFERENCE_CREATE:
			case CONFERENCE_INVITE:
				if(message._username.equals(user.toString()))return;
				showConferenceDialog(message);
			break;

			case CONFERENCE_LIST:
			case CONFERENCE_LEAVE:
			case PUBLIC_CHAT:
			case PRIVATE_CHAT:
			case CONFERENCE_JOIN:
			case CONFERENCE_DENY:
				_dispatcher.dispatchMessage(message);
			break;

			default:

		}
	}

	public MessageDispatcher getDispatcher()
	{
		return _dispatcher;
	}

	public synchronized void sendMessageToServer(Message message) throws Exception
	{
		message._user = user;
		client.sendMessageToServer(message);
	}

	private void showConferenceDialog(Message message)
	{
		message._header=CONFERENCE_DENY;
		//message._username = inmessage._username;
		message._destination = message._user.toString();
		//message._user = inmessage._user;

		Object[] options = { "ACCEPT", "DENY" };
		int chosen = JOptionPane.showOptionDialog(null,
					message._username+" invites you to join the conference "+message._user,
					"Talk2Me: Conference invitation",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);

		if(chosen == JOptionPane.OK_OPTION)
			message._header=CONFERENCE_JOIN;

		try {
			client.sendMessageToServer(message);
		}
		catch(java.io.IOException ie) {
			JOptionPane.showMessageDialog(frame,
						ie.toString(),
						"Messaging error",JOptionPane.ERROR_MESSAGE);
			//System.exit(-1);
		}
		if(chosen == JOptionPane.OK_OPTION) {
			createFrame(message._user,false);
		}
	}

	public User getMe()
	{
		return user;
	}

	public void createFrame(User user,boolean auto)
	{
		ChatDialog dialog ;
		synchronized(frameTable) {
			if(user.isConference == true) {
				ConferenceDialog
						cdialog = new ConferenceDialog(this,user);
				cdialog.setLocation(500, 500);
				frameTable.put(user.toString(),cdialog);
				getDispatcher().addObserver(cdialog);
				cdialog.requestConferenceList();
			} else {
				dialog = (ChatDialog) frameTable.get(user.toString());
				if(dialog == null) {
					dialog = new ChatDialog(this,user);
					dialog.setLocation(500, 500);
					if(auto){
						dialog.setState(Frame.ICONIFIED);
					}
					dialog.startFlashing();
					frameTable.put(user.toString(),dialog);
					getDispatcher().addObserver(dialog);
				}
			}
		}
	}

	public void removeFrame(User user)
	{
		synchronized(frameTable) {
			frameTable.remove(user.toString());
		}
	}

	// Load a gif image (used for loading the 16x16 icon gifs)
	public static Image loadImage(String fileName) {
		return Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemClassLoader().getResource("images/plus.gif"));
	}


	// Callback listener handles restore (click left on any icon / show popup menu)
	private class RestoreListener implements ActionListener {

        protected boolean from_menu;

        public RestoreListener(boolean fromMenu) {
            from_menu = fromMenu;
        }

		public void actionPerformed(ActionEvent evt) {
			// Make main window visible if it was hidden
			setState(Frame.NORMAL);
			setVisible(true);
			// Request input focus
			requestFocus();
		}

	}

}//END FRAME



//
//	MessageDispatcher: Master message dispatcher for the Talk2Me application.
//

class MessageDispatcher extends java.util.Observable implements ChatConstants
{
	private static MessageDispatcher _instance = null;
	MainFrame frame;

	public MessageDispatcher(MainFrame frame)
	{
		this.frame=frame;
	}

	void dispatchMessage(Message message)
	{
		setChanged();
		switch(message._header)	{
			case PRIVATE_CHAT://Open a chat window for user
				frame.createFrame(message._user,true);
			break;
		}
		notifyObservers(message);
	}
}


//
//	SplashScreen: Opening screen for Talk2Me app
//


class SplashScreen extends JWindow implements ChatConstants
{
	public SplashScreen(int time)
	{
		JPanel mains = new JPanel(new BorderLayout());
		ImageIcon icon = new ImageIcon(IMAGE_ICON);

		mains.add(new JLabel(icon, SwingConstants.CENTER));
		getContentPane().add(mains);
		//Show the window
		this.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me)
			{
				dispose();
			}
		});
		pack();
		centerScreen();
		show();
		if(time !=0) {
			try {
				Thread.sleep(time);
				dispose();
			} catch(InterruptedException ie){}
		}
	}
	//Simple method to center in screen
	private void centerScreen()
	{
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((d.getWidth() - getWidth()) / 2);
		int y = (int) ((d.getHeight() - getHeight()) / 2);
		setLocation(x, y);
	}
}
