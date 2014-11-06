/***********************************************************
* File:	ChatDialog.java
*
* @author 		Alfred Jayaprakash
*
* @description 	The private chat message window.
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

public class ChatDialog extends JFrame implements ActionListener,Observer, ChatConstants
{
	private MainFrame frame;
	private ChatDialog thisframe;
	private Container container;
	private JEditorPane recv;
	private JTextArea type;
	private JButton send;
	private User user;
	private int hwnd;
	private Timer timer=null;
	boolean isFocused = false;
	private int w,h;

	public ChatDialog(MainFrame frame,User user)
	{
		this.frame = frame;
		this.user = user;
		initAwtContainer();
	}

	public void initAwtContainer()
	{
		thisframe = this;
		container= this.getContentPane();
		container.setLayout(null);

		recv = new JEditorPane();
		recv.setEditorKit(new HTMLEditorKit());
		//recv.setFont(new Font("Arial",Font.PLAIN,9));
		recv.setEditable(false);

		JScrollPane pane
			= new JScrollPane(recv,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setBounds(10,10,290,100);

		type = new JTextArea();
		type.setFont(new Font("Arial",Font.PLAIN,11));
		type.setLineWrap(true);

		JScrollPane typepane
			= new JScrollPane(type,
					JScrollPane.VERTICAL_SCROLLBAR_NEVER,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		typepane.setBounds(10,120,220,50);


		send = new JButton("Send");
		send.setBounds(235,120,65,50);
		send.addActionListener(this);

		container.add(pane);
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
					Message mymessage = new Message(PRIVATE_CHAT,type.getText());
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
					setVisible(false);
					frame.removeFrame(user);
				}
			}
		});


		send.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent ke)
					{
						if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
							setVisible(false);
							frame.removeFrame(user);
						}
					}
		});

		recv.addMouseListener(new MouseInputAdapter() {
			public void mouseClicked(MouseEvent me) {
				isFocused = true;
				if(timer != null)timer.stop();
			}
		});

		this.setResizable(false);
		this.setSize(310,210);
		this.setTitle(user+" - Message");
		this.setLocation(300,300);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				setVisible(false);
				if(timer != null) timer.stop();
				frame.removeFrame(user);
			}

			public void windowActivated(WindowEvent ae) {
				isFocused = true;
				if(timer != null) timer.stop();
			}

			public void windowDeactivated(WindowEvent ae) {
				isFocused = false;
			}
    		public void windowOpened( WindowEvent e ){
    		    type.requestFocus();
    	    }
		});

		this.setVisible(true);
		type.requestFocus();

		//sun.awt.SunToolkit tk = (sun.awt.SunToolkit)Toolkit.getDefaultToolkit();
		//hwnd = tk.getNativeWindowHandleFromComponent(this);
		timer = new Timer(500,new FlashwindowListener(thisframe));
		isFocused = false;
	}


	public void startFlashing()
	{
		isFocused = false;
		timer.start();
	}

	public void update(Observable observable,Object object)
	{
		Message message = (Message)object;
		if(!message._username.equals(user.toString()))
			return;
		switch(message._header)	{
			case CLIENT_LOGOUT:
				appendData(null,
					"<FONT COLOR='red' STYLE='font-size:10pt;font-family:Arial'><b>"+user+" logged off at "+(new Date())+"<b></font>",
					false);
				type.setEnabled(false);
				send.setEnabled(false);
			break;
			case PRIVATE_CHAT:
				appendData(user.toString(),message.getMessage(),true);
				if(!isFocused) timer.start();
			break;
		}
	}


	public String toString()
	{
		return user.toString();
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
		} else {
			str ="<FONT COLOR='red' STYLE='font-size:10pt;font-family:Arial'><B>"+str;
		}

		str+="</FONT>";//Line break

		try {
		((HTMLEditorKit)recv.getEditorKit()).read(new java.io.StringReader(str),
		 						recv.getDocument(), recv.getDocument().getLength());
		 recv.setCaretPosition(recv.getDocument().getLength());
	 	} catch(Exception e){}
	}

	public void actionPerformed(ActionEvent event)
	{
		if((event.getSource() == type)||(event.getSource() == send)) {
			if(type.getText().length() == 0) return;
			appendData(frame.getMe().toString(),type.getText(),false);
			Message mymessage = new Message(PRIVATE_CHAT,type.getText());
			mymessage._destination = user.toString();
			try {
				frame.sendMessageToServer(mymessage);
			}
			catch(Exception e) {
				System.out.println("Error sending message");
			}
			type.setText("");
		}
	}
}


//
//	FlashWindowListener: Class for flashing the msg window.
//						 Uses JNI call to achieve that
//


class FlashwindowListener implements ActionListener
{
	private Window chatwindow;
	private final native void flashWindow(Window chatwindow);

	public FlashwindowListener(Window window)
	{
		this.chatwindow = window;
	}

	public void actionPerformed(ActionEvent ae)
	{
		flashWindow(chatwindow);
	}
}