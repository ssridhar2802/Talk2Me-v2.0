/***********************************************************
* File:	ConferenceDialog.java
*
* @author 		Alfred Jayaprakash
*
* @description 	The ConferenceDialog class
*
***********************************************************/

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.Timer;
import javax.swing.tree.*;
import javax.swing.text.html.*;

public class ConferenceDialog extends JFrame implements ActionListener,Observer, ChatConstants
{
	private JMenuBar menubar;
	private JMenu menu;
	private JMenuItem conf;
	private MainFrame frame;
	private ConferenceDialog thisframe;
	private Container container;
	private JEditorPane recv;
	private JTextArea type;
	private JButton send;
	private User user;
	private int hwnd;
	private Timer timer=null;
	boolean focused = false;
	private int w,h;
	private DefaultListModel model;
	private JList list;
	public Vector yet_invited;
	private InviteesDialog invitee;

	public ConferenceDialog(MainFrame frame,User user)
	{
		this.frame = frame;
		this.user = user;
		initAwtContainer();
		appendData(null,
			"<FONT FACE=ARIAL COLOR=GREEN SIZE=2><B>you have now joined the conference "+user+"</B></FONT>",
			false);
	}

	public void initAwtContainer()
	{
		thisframe = this;
		container= this.getContentPane();
		container.setLayout(null);

		invitee = new InviteesDialog(this,MainFrame._userlist);
		menubar= new JMenuBar();
		menu = new JMenu("Conference");
		conf=new JMenuItem("Add to conference ...");
		conf.addActionListener(this);
		menu.add(conf);
		menubar.add(menu);

		recv = new JEditorPane();
		recv.setEditorKit(new HTMLEditorKit());
		//recv.setFont(new Font("Arial",Font.PLAIN,9));
		recv.setEditable(false);

		JScrollPane pane
			= new JScrollPane(recv,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setBounds(10,10,270,130);

		list = new JList();
		list.setModel(new DefaultListModel());
		list.setVisibleRowCount(10);
		JScrollPane pane1= new JScrollPane(list,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//pane1.setPreferredSize(new Dimension(90,180));
		pane1.setBounds(290,10,90,130);

		type = new JTextArea();
		type.setFont(new Font("Arial",Font.PLAIN,11));
		type.setLineWrap(true);

		JScrollPane typepane
			= new JScrollPane(type,
					JScrollPane.VERTICAL_SCROLLBAR_NEVER,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		typepane.setBounds(10,150,285,50);


		send = new JButton("Send");
		send.setBounds(305,150,65,50);
		send.addActionListener(this);

		container.add(pane);
		container.add(pane1);
		container.add(typepane);
		container.add(send);

		type.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke)
			{
				if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setVisible(false);
					frame.removeFrame(user);
				} else if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
					if(type.getText().length() == 0) return;
					appendData(frame.getMe().toString(),type.getText(),false);
					Message mymessage = new Message(PUBLIC_CHAT,type.getText());
					mymessage._destination = user.toString();
					try {
						frame.sendMessageToServer(mymessage);
					}
					catch(Exception e) {
						JOptionPane.showMessageDialog(container,
							"Error sending message! Please try again",
							"Error",JOptionPane.ERROR_MESSAGE);
						}
					type.setText("");
				}
			}
		});


		type.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke)
			{
				if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if(JOptionPane.showConfirmDialog(container,
								"Are you sure you want to leave the conference ?",
								"Talk2Me: Quit Conference",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null) == JOptionPane.YES_OPTION) {
						Message mymessage = new Message(CONFERENCE_LEAVE);
						mymessage._destination = user.toString();
						try {
							frame.sendMessageToServer(mymessage);
						}
						catch(Exception se) {
							System.out.println("Error sending message");
						}
						setVisible(false);
						frame.removeFrame(user);
					}
				}
			}
		});


		send.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke)
			{
				if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if(JOptionPane.showConfirmDialog(container,
								"Are you sure you want to leave the conference ?",
								"Talk2Me: Quit Conference",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null) == JOptionPane.YES_OPTION) {
						Message mymessage = new Message(CONFERENCE_LEAVE);
						mymessage._destination = user.toString();
						try {
							frame.sendMessageToServer(mymessage);
						}
						catch(Exception se) {
							System.out.println("Error sending message");
						}
						setVisible(false);
						frame.removeFrame(user);
					}
				}
			}
		});

		recv.addMouseListener(new MouseInputAdapter() {
			public void mouseClicked(MouseEvent me) {
				focused = true;
				if(timer != null)timer.stop();
			}
		});

		this.setJMenuBar(menubar);
		this.setResizable(false);
		this.setSize(400,260);
		this.setTitle(user+" - Conference");
		this.setLocation(300,300);

		this.addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent we) {
				focused = true;
				if(timer != null)timer.stop();
			}

			public void windowDeactivated(WindowEvent we) {
				focused = false;
			}

			public void windowClosing(WindowEvent e)
			{
				if(JOptionPane.showConfirmDialog(container,
							"Are you sure you want to leave the conference ?",
							"Talk2Me: Quit Conference",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null) == JOptionPane.YES_OPTION) {
					Message mymessage = new Message(CONFERENCE_LEAVE);
					mymessage._destination = user.toString();
					try {
						frame.sendMessageToServer(mymessage);
					}
					catch(Exception se) {
						System.out.println("Error sending message");
					}
					setVisible(false);
					frame.removeFrame(user);
				}
			}

		});

		this.setVisible(true);
		type.requestFocus();

		//sun.awt.SunToolkit tk = (sun.awt.SunToolkit)Toolkit.getDefaultToolkit();
		//hwnd = tk.getNativeWindowHandleFromComponent(this);
		timer = new Timer(500,new FlashwindowListener(thisframe));
		focused = false;
	}

	public void update(Observable observable,Object object)
	{
		Message message = (Message)object;

		if(message._header != CONFERENCE_LIST &&
			message._header != CLIENT_LOGIN &&
			message._header != CLIENT_LOGOUT ) {
			//IGNORE ANY MESSAGES I SENT
			if(message._username.equals(frame.getMe().toString()))return;

			if((!message._destination.equals(user.toString())))
				return;
		}

		switch(message._header)	{

			case CONFERENCE_JOIN:
				if(message._username.equals(user.toString())) return;
				((DefaultListModel)list.getModel()).addElement(message._username);
				invitee.addSelectUser(message._username);
				appendData(null,
					"<FONT COLOR='green' STYLE='font-size:10pt;font-family:Arial'><b>"+message._username+" has been added to the conference</b></font>",
					false);

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						list.ensureIndexIsVisible(((DefaultListModel)list.getModel()).getSize() -1);
					}
				});
			break;

			case CONFERENCE_DENY:
				appendData(null,
					"<FONT COLOR='red' STYLE='font-size:10pt;font-family:Arial'><b>"+message._username+" has declined to join the conference</b></font>",
					false);
			break;

			case CLIENT_LOGIN:
				invitee.removeSelectUser(message._username);
			break;

			case CLIENT_LOGOUT:
				((DefaultListModel)list.getModel()).removeElement(message._username);
				invitee.removeSelectUserComplete(message._username);
				appendData(null,
					"<FONT COLOR='red' STYLE='font-size:10pt;font-family:Arial'><b>"+message._username+" has logged off</b></font>",
					false);
			break;

			case CONFERENCE_LEAVE:
				((DefaultListModel)list.getModel()).removeElement(message._username);
				invitee.removeSelectUser(message._username);
				appendData(null,
					"<FONT COLOR='red' STYLE='font-size:10pt;font-family:Arial'><b>"+message._username+" has left the conference</b></font>",
					false);

			break;


			case CONFERENCE_LIST:
				DefaultListModel newmodel =(DefaultListModel)list.getModel();
				for(int count=0;count<message.userlist.size();count++) {
					if(newmodel.indexOf(message.userlist.elementAt(count)) == -1) {
						newmodel.addElement(message.userlist.elementAt(count));
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								list.ensureIndexIsVisible(((DefaultListModel)list.getModel()).getSize() -1);
							}
						});
					}
				}
				invitee.updateSelectList(message);

			break;

			case PUBLIC_CHAT:
				if(message._username.equals(user.toString())) return;
				appendData(message._username,message.getMessage(),true);
				if(!focused) timer.start();
			break;
		}
	}


	public String toString()
	{
		return user.toString();
	}

	public void requestConferenceList()
	{
		Message mymessage = new Message(CONFERENCE_LIST);
		mymessage._destination = user.toString();
		try {
			frame.sendMessageToServer(mymessage);
		}
		catch(Exception se) {
			System.out.println("Error sending message");
		}

	}

	private void appendData(String user,String str,boolean received)
	{
		StringBuffer  bfr= new StringBuffer(str);

		while(str.indexOf("X(") != -1) {
			int index = str.indexOf("X(");
			bfr.replace(index,index+2,"<IMG SRC=\""+ANGRY +"\">");
			str= bfr.toString();
		}
		while(str.indexOf("x(") != -1) {
			int index = str.indexOf("x(");
			bfr.replace(index,index+2,"<IMG SRC=\""+ANGRY +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":))") != -1) {
			int index = str.indexOf(":))");
			bfr.replace(index,index+3,"<IMG SRC=\""+LAUGH +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":((") != -1) {
			int index = str.indexOf(":((");
			bfr.replace(index,index+3,"<IMG SRC=\""+CRY +"\">");
			str= bfr.toString();
		}
		while(str.indexOf("B-)") != -1) {
			int index = str.indexOf("B-)");
			bfr.replace(index,index+3,"<IMG SRC=\""+COOL +"\">");
			str= bfr.toString();
		}
		while(str.indexOf("=;") != -1) {
			int index = str.indexOf("=;");
			bfr.replace(index,index+2,"<IMG SRC=\""+BYE +"\">");
			str= bfr.toString();
		}

		while(str.indexOf(">:)") != -1) {
			int index = str.indexOf(">:)");
			bfr.replace(index,index+3,"<IMG SRC=\""+DEVIL +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":|") != -1) {
			int index = str.indexOf(":|");
			bfr.replace(index,index+2,"<IMG SRC=\""+EQUISMILE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":-|") != -1) {
			int index = str.indexOf(":-|");
			bfr.replace(index,index+3,"<IMG SRC=\""+EQUISMILE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":-P") != -1) {
			int index = str.indexOf(":-P");
			bfr.replace(index,index+3,"<IMG SRC=\""+TONGUE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":P") != -1) {
			int index = str.indexOf(":P");
			bfr.replace(index,index+2,"<IMG SRC=\""+TONGUE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":-o") != -1) {
			int index = str.indexOf(":-o");
			bfr.replace(index,index+3,"<IMG SRC=\""+SURPRISE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":-O") != -1) {
			int index = str.indexOf(":-O");
			bfr.replace(index,index+3,"<IMG SRC=\""+SURPRISE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf("~0)") != -1) {
			int index = str.indexOf("~0)");
			bfr.replace(index,index+3,"<IMG SRC=\""+COFFEE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf("~o)") != -1) {
			int index = str.indexOf("~o)");
			bfr.replace(index,index+3,"<IMG SRC=\""+COFFEE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":>") != -1) {
			int index = str.indexOf(":>");
			bfr.replace(index,index+2,"<IMG SRC=\""+MEAN +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":->") != -1) {
			int index = str.indexOf(":->");
			bfr.replace(index,index+3,"<IMG SRC=\""+MEAN +"\">");
			str= bfr.toString();
		}

		while(str.indexOf(":\">") != -1) {
			int index = str.indexOf(":\">");
			bfr.replace(index,index+3,"<IMG SRC=\""+SHY +"\">");
			str= bfr.toString();
		}

		while(str.indexOf(":-/") != -1) {
			int index = str.indexOf(":-/");
			bfr.replace(index,index+3,"<IMG SRC=\""+QUESTION +"\">");
			str= bfr.toString();
		}

		while(str.indexOf(":O") != -1) {
			int index = str.indexOf(":O");
			bfr.replace(index,index+2,"<IMG SRC=\""+SURPRISE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":o") != -1) {
			int index = str.indexOf(":o");
			bfr.replace(index,index+2,"<IMG SRC=\""+SURPRISE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":(") != -1) {
			int index = str.indexOf(":(");
			bfr.replace(index,index+2,"<IMG SRC=\""+SAD +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":-(") != -1) {
			int index = str.indexOf(":-(");
			bfr.replace(index,index+3,"<IMG SRC=\""+SAD +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":)") != -1) {
			int index = str.indexOf(":)");
			bfr.replace(index,index+2,"<IMG SRC=\""+SMILE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":-)") != -1) {
			int index = str.indexOf(":-)");
			bfr.replace(index,index+3,"<IMG SRC=\""+SMILE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(";)") != -1) {
			int index = str.indexOf(";)");
			bfr.replace(index,index+2,"<IMG SRC=\""+WINK +"\">");
			str= bfr.toString();
		}

		while(str.indexOf(";-)") != -1) {
			int index = str.indexOf(";-)");
			bfr.replace(index,index+3,"<IMG SRC=\""+WINK +"\">");
			str= bfr.toString();
		}

		while(str.indexOf(":x") != -1) {
			int index = str.indexOf(":x");
			bfr.replace(index,index+2,"<IMG SRC=\""+LOVE +"\">");
			str= bfr.toString();
		}
		while(str.indexOf(":-x") != -1) {
			int index = str.indexOf(":-x");
			bfr.replace(index,index+3,"<IMG SRC=\""+LOVE +"\">");
			str= bfr.toString();
		}

		if(user != null) {

			if(received) {
				str ="<FONT COLOR='red' STYLE='font-size:10pt;font-family:Arial'>"+user+": </FONT><FONT STYLE='font-size:10pt;font-family:Arial'>"+str;
			} else {
				str ="<FONT COLOR='blue' STYLE='font-size:10pt;font-family:Arial'>"+user+": </FONT><FONT STYLE='font-size:10pt;font-family:Arial'>"+str;
			}

			str+="</FONT>";//Line break
		}

		try {
			((HTMLEditorKit)recv.getEditorKit()).read(new java.io.StringReader(str),
		 						recv.getDocument(), recv.getDocument().getLength());
		 	recv.setCaretPosition(recv.getDocument().getLength());

	 	} catch(Exception e){}

	}

	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == send) {
			if(type.getText().length() == 0) return;
			appendData(frame.getMe().toString(),type.getText(),false);
			Message mymessage = new Message(PUBLIC_CHAT,type.getText());
			mymessage._destination = user.toString();
			try {
				frame.sendMessageToServer(mymessage);
			}
			catch(Exception e) {
				JOptionPane.showMessageDialog(container,
					"Error sending message! Please try again",
					"Error",JOptionPane.ERROR_MESSAGE);
				}
			type.setText("");
		} else if(event.getSource() == conf) {
			invitee.clearSelected();
			invitee.setVisible(true);
			if(invitee.getSelected() != null) {
			//GET THE SELECTED LIST
				Message message = new Message(CONFERENCE_INVITE);
				message._user = user;
				message.userlist = invitee.getSelected();

				try {
					frame.client.sendMessageToServer(message);
				}
				catch(java.io.IOException ie) {
					JOptionPane.showMessageDialog(this,
								ie.toString(),
								"Messaging error",JOptionPane.ERROR_MESSAGE);
					//System.exit(-1);
				}
			}
		}
	}
}




class InviteesDialog extends JDialog implements ActionListener
{
	JButton invite,cancel,add,remove;
	private Vector show,selected,users;
	private JList showlist,selectlist;
	private Container container;
	private JScrollPane pane1,pane2;
	private DefaultListModel model;
	private JLabel label1,label2;

	public InviteesDialog(JFrame frame,Vector vector)
	{
		super(frame,"Invite users to conference",true);
		this.users= vector;
		this.show = vector;
		selected= new Vector(3);
		initAwtContainer();
	}

	private void initAwtContainer()
	{


		container= getContentPane();
		container.setLayout(null);

		label1 = new JLabel("Available users");
		label1.setBounds(20,5,100,20);
		container.add(label1);

		label2 = new JLabel("Selected users");
		label2.setBounds(220,5,100,20);
		container.add(label2);

		model = new DefaultListModel();
		for(int count=0;count<show.size();count++)
			model.addElement(show.elementAt(count));

		showlist = new JList(model);

		showlist.setVisibleRowCount(10);
		pane1= new JScrollPane(showlist,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane1.setPreferredSize(new Dimension(90,180));
		pane1.setBounds(20,25,100,140);
		container.add(pane1);

		add = new JButton(">>>");
		add.addActionListener(this);
		remove= new JButton("<<<");
		remove.addActionListener(this);
		add.setBounds(135,50,70,30);
		remove.setBounds(135,100,70,30);
		container.add(add);
		container.add(remove);

		invite = new JButton("Invite");
		invite.addActionListener(this);
		cancel= new JButton("Cancel");
		cancel.addActionListener(this);
		invite.setBounds(70,180,90,20);
		cancel.setBounds(180,180,90,20);
		container.add(invite);
		container.add(cancel);


		selectlist = new JList(selected);
		selectlist.setModel(new DefaultListModel());
		selectlist.setVisibleRowCount(10);
		pane2= new JScrollPane(selectlist,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane2.setPreferredSize(new Dimension(90,280));
		pane2.setBounds(220,25,100,140);
		container.add(pane2);


		setSize(350,240);
		setLocation(300,300);
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == add) {
			Object value = showlist.getSelectedValue();
			addSelectUser(value);
		} else if(ae.getSource() == remove) {
			Object value = selectlist.getSelectedValue();
			removeSelectUser(value);
		} else if(ae.getSource() == invite) {
			//SEND INVITE MESSAGE
			setVisible(false);
		} else if(ae.getSource() == cancel) {
			selected=null;
			setVisible(false);
		}
	}


	public void addSelectUser(Object value)
	{
		if(value == null) return;
		try {
			((DefaultListModel)showlist.getModel()).removeElement(value);

			if(((DefaultListModel)selectlist.getModel()).indexOf(value) == -1) {
				((DefaultListModel)selectlist.getModel()).addElement(value);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						selectlist.ensureIndexIsVisible(((DefaultListModel)selectlist.getModel()).getSize() -1);
					}
				});
				selected.add(value);
			}
		} catch(Exception e){System.out.println(e);}
	}


	public void removeSelectUser(Object value)
	{
		if(value==null)return;
		try {
			((DefaultListModel)selectlist.getModel()).removeElement(value);
			((DefaultListModel)showlist.getModel()).addElement(value);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					showlist.ensureIndexIsVisible(((DefaultListModel)showlist.getModel()).getSize() -1);
				}
			});
			selected.remove(value);
		}catch(Exception e){System.out.println(e);}

	}

	public void removeSelectUserComplete(Object value)
	{
		if(value==null)return;
		try {
			((DefaultListModel)selectlist.getModel()).removeElement(value);
			selected.remove(value);
		}catch(Exception e){System.out.println(e);}
	}

	public void updateSelectList(Message message)
	{
		DefaultListModel newmodel =(DefaultListModel)selectlist.getModel();
		if(message.userlist.size()!=0) selected.clear();
		for(int count=0;count<message.userlist.size();count++) {
			if(newmodel.indexOf(message.userlist.elementAt(count)) == -1) {
				String element = (String)message.userlist.elementAt(count);
				((DefaultListModel)showlist.getModel()).removeElement(element);
				newmodel.addElement(element);
				selected.add(element);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						selectlist.ensureIndexIsVisible(((DefaultListModel)selectlist.getModel()).getSize() -1);
					}
				});
			}
		}
	}

	public Vector getSelected()
	{
		if((selected == null) || (selected.size() == 0))
			return null;
		return selected;
	}

	public void clearSelected()
	{
		if(selected !=null) selected.clear();
	}
}
