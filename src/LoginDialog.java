

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.EventObject;
import java.util.Vector;
import java.sql.*;
public class LoginDialog extends JDialog implements ActionListener,ChatConstants
{
	private String _username=null,_password=null,_server=null;
	private JLabel label1,label2,label3,label4;
	private JTextField user,server,port;
	private JPasswordField password;
	private JButton ok,cancel;
	private Container container;
        private JButton register;

	public LoginDialog(JFrame frame)
	{
		super(frame,"Login",true);
		initDialogBox(frame);
	}

	private void initDialogBox(JFrame frame)
	{
		container = this.getContentPane();
		container.setLayout(null);
		label1= new JLabel("Login name :");
		label1.setBounds(10,10,80,20);
		label2= new JLabel("  Password :");
		label2.setBounds(10,40,80,20);
		user= new JTextField();
		user.setBounds(100,10,100,20);
		password=new JPasswordField();
		password.setBounds(100,40,100,20);

		label3= new JLabel("    Server :");
		label3.setBounds(10,70,80,20);
		label4= new JLabel("      Port :");
		label4.setBounds(10,100,80,20);
		server= new JTextField(SERVER_HOST);
		server.setBounds(100,70,100,20);
		port=new JTextField(SERVER_PORT+"");
		port.setBounds(100,100,100,20);
		port.setEditable(false);
		ok=new JButton("Login");
		ok.setBounds(30,130,70,20);
		cancel= new JButton("Cancel");
                register=new JButton("Register");
                register.setBounds(60,160,80,20);
		cancel.setBounds(110,130,80,20);

		container.add(label1);
		container.add(user);
		container.add(label2);
		container.add(password);
		container.add(label3);
		container.add(server);
		container.add(label4);
		container.add(port);
		container.add(ok);
		container.add(cancel);
                container.add(register);

		user.addActionListener(this);
		password.addActionListener(this);
		server.addActionListener(this);
		port.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);
                register.addActionListener(this);

		this.setSize(220,240);
		this.setResizable(false);
		this.setLocationRelativeTo(frame);
		this.setLocation(450,220);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent event)
	{
        if((event.getSource() == ok) || (event.getSource() == password)
        	||(event.getSource() == user) || (event.getSource() == server) ) {
			_username = user.getText();
			_password = new String(password.getPassword());
			_server = server.getText();
                        try {
                        if(dbconnection.login(_username,_password)!=1)
                        {
                            	JOptionPane.showMessageDialog(this,"Please enter a valid username and password","Error",
						JOptionPane.WARNING_MESSAGE);
                               
                                _username = null;
			_password = null;
			_server = null;
                                return;
                        }
                        else
                        {
                            //System.out.println(InetAddress.getLocalHost().toString());
              Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        Connection conn=DriverManager.getConnection("jdbc:odbc:DB2COPY1","sridhar","sridhar");
        PreparedStatement ps=conn.prepareStatement("update users set hostname=?,isonline=1 where username=?");
        ps.setString(1,InetAddress.getLocalHost().toString());
        ps.setString(2,_username);
        ps.executeUpdate();
        
		//this.setVisible(false);
                        }
                        }
                       
                        catch(Exception e)
                        {e.printStackTrace();}
                        
			if((_username.length()==0) || (_password.length() == 0))
			{
				JOptionPane.showMessageDialog(this,
					"Please enter a valid username and password","Error",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if(_server.length()== 0)
			{
				JOptionPane.showMessageDialog(this,
					"Invalid server host","Error",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

		} else if(event.getSource() == cancel) {
            
		//this.setVisible(false);
			//NOTHING
		}
                if(event.getSource()==register)
                {
                        new register();
                        this.setVisible(false);
                }
        
this.setVisible(false);
	}

	public String getUserName()
	{
		return _username;
	}

	protected String getPassword()
	{
		return _password;
	}

	protected String getServerHost()
	{
		return _server;
	}
}