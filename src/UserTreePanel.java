/***********************************************************
* File:	UserTreePanel.java
*
* @author 		Alfred Jayaprakash
*
* @description 	The Userlist tree class.
*
***********************************************************/

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import java.sql.*;
public class UserTreePanel extends JPanel
{
	private Vector _userlist ;
	private JTree usrtree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootNode;
	private MainFrame frame;
    private Hashtable nodeTable = new Hashtable();
	private static final String libName = "client";


	public UserTreePanel(MainFrame frame,Vector vector) throws Exception
	{
		this.frame = frame;
		this._userlist = vector;
		try	{
			System.loadLibrary( libName );
		}
		catch (Throwable t) {
			JOptionPane.showMessageDialog(frame,
						"Cannot load library client.dll",
						"Talk2Me: Error",JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}

		frame.getDispatcher().addObserver(new DefaultObserver(frame));
		initAwtContainer();

	}

	private void initAwtContainer() throws Exception
	{
		this.setLayout(new FlowLayout());

		//Create the nodes.
		rootNode = new DefaultMutableTreeNode("Friends");
		createNodes(rootNode);

		//Create a tree that allows one selection at a time.
		treeModel = new DefaultTreeModel(rootNode);
		usrtree = new JTree(treeModel);
		usrtree.getSelectionModel().setSelectionMode
						(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Enable tool tips.
		ToolTipManager.sharedInstance().registerComponent(usrtree);


		usrtree.setCellRenderer(new MyRenderer());
		usrtree.addMouseListener(new MyMouseAdapter(frame,usrtree));

		JScrollPane scrollpane;
		scrollpane=new JScrollPane(usrtree,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		scrollpane.setPreferredSize(new Dimension(200,330));
		this.add(scrollpane);
	}

   	private void createNodes(DefaultMutableTreeNode top){
		DefaultMutableTreeNode user = null;
                int n=0;
                String host,isonline;
                ResultSet rs=null;
                Connection conn=null;
                PreparedStatement ps=null;
                try
                {
             Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            conn=DriverManager.getConnection("jdbc:odbc:DB2COPY1","sridhar","sridhar");
                ps=conn.prepareStatement("select friend from friends where username=?");
                ps.setString(1,frame._username);
                rs=ps.executeQuery();
              
                
                }
                catch(Exception e)
                {
                    
                }
                try
                {
                while(rs.next())
                {
                String name=new String();
                System.out.println("hi1");
                name=rs.getString(1);
                System.out.println(name);
                PreparedStatement ps2=conn.prepareStatement("select *from users where username=?");
                ps2.setString(1,name);
                ResultSet rs2=ps2.executeQuery();
                rs2.next();
                host=rs2.getString("hostname");
                int online=rs2.getInt("isonline");
                System.out.println(name+" "+host+" "+online);
                User usr=new User(name,host,online);
                user=new DefaultMutableTreeNode(usr);
                nodeTable.put(usr.toString(),user);
                top.add(user);
                System.out.println("hi");
                //updateUser(usr);
                }
                /*ps=conn.prepareStatement("select *from users where username=?");
                ps.setString(1,frame._username);
                rs=ps.executeQuery();
                rs.next();
                host=rs.getString("hostname");
                User my=new User(frame._username,host,1);
                updateUser(my);*/
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Exception caught");}
		/*for(int count = 0; count<_userlist.size();count++) {
			user = new DefaultMutableTreeNode((User)_userlist.elementAt(count));
			nodeTable.put(((User)_userlist.elementAt(count)).toString(),user);
                      
        	top.add(user);
		}*/
    }


	public void addUser(Object child)
	{
		DefaultMutableTreeNode childNode =
				new DefaultMutableTreeNode(child);
		treeModel.insertNodeInto(childNode,rootNode,rootNode.getChildCount());
		nodeTable.put(((User)child).toString(),childNode);

	}

	public void removeUser(User user)
	{
		MutableTreeNode node = (MutableTreeNode)nodeTable.get(user.toString());
		node.setUserObject(user);
		treeModel.reload(node);
	}

	public void updateUser(User user)
	{
		MutableTreeNode node;
		node = (MutableTreeNode)nodeTable.get(user.toString());
		if(node == null) {
			//addUser(user);
			return;
		}
		node.setUserObject(user);
		treeModel.reload(node);
		nodeTable.put(user.toString(),node);
	}

}

//
//	MyRenderer: Tree cell rendering class
//

class MyRenderer extends DefaultTreeCellRenderer implements ChatConstants{
	final ClassLoader loader = ClassLoader.getSystemClassLoader();
	final ImageIcon rootIcon = new ImageIcon(loader.getResource("images/root.gif")),
					onlineIcon = new ImageIcon(loader.getResource("images/online.gif")),
					offlineIcon = new ImageIcon(loader.getResource("images/offline.gif")),
					busyIcon = new ImageIcon(loader.getResource("images/busy.gif")),
					idleIcon = new ImageIcon(loader.getResource("images/idle.gif"));

	public MyRenderer()
	{

	}

	public Component getTreeCellRendererComponent(
						JTree tree,
						Object value,
						boolean sel,
						boolean expanded,
						boolean leaf,
						int row,
						boolean hasFocus) {

		super.getTreeCellRendererComponent(
						tree, value, sel,
						expanded, leaf, row,
						hasFocus);


		if (leaf) {
			User user=getUser(value);
			switch(user.isOnline) {
				case ONLINE :
					setIcon(onlineIcon);//ONLINE USER
					setToolTipText("I am online @"+user.hostname);
					break;
				case OFFLINE:
					setIcon(offlineIcon);//OFFLINE
					setToolTipText("Offline");
					break;
				case BUSY :
					setIcon(busyIcon);//BUSY USER
					setToolTipText("I am busy");
					break;
				case IDLE :
					setIcon(idleIcon);//IDLE USER
					setToolTipText("Away from computer");
					break;
				default:
					setIcon(offlineIcon);//OFFLINE
					setToolTipText("Offline");
				}
		} else {
			setIcon(rootIcon);//ROOT
			setToolTipText(null);
		}

		return this;
	}

	private User getUser(Object value)
	{
		DefaultMutableTreeNode node =
					(DefaultMutableTreeNode)value;
		User nodeInfo =
					(User)(node.getUserObject());
		return nodeInfo;
	}

}

class MyMouseAdapter extends MouseAdapter implements ChatConstants
{
	private MainFrame frame;
	private JTree tree;

	public MyMouseAdapter(MainFrame frame,JTree tree)
	{
		this.frame = frame;
		this.tree = tree;
	}

	public void mouseClicked(MouseEvent e) {
		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		ChatDialog dialog;
		DefaultMutableTreeNode node;
		if(selRow > 0 ) {
			if(e.getClickCount() == 2) {
				node = (DefaultMutableTreeNode)selPath.getLastPathComponent();
				User user = (User)(node.getUserObject());
				if(user.isOnline != OFFLINE) {
					frame.createFrame(user,false);
				} else {
					JOptionPane.showMessageDialog(frame,
							"Click on an online user to send a message",
							"Error",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
}


class DefaultObserver implements Observer,ChatConstants
{

	private MainFrame frame;
	private ChatDialog dialog;

	public DefaultObserver(MainFrame frame)
	{
		this.frame=frame;
	}

	public void update(Observable observable,Object object)
	{
		Message message = (Message)object;

	    switch(message._header)	{
			case PRIVATE_CHAT:
				frame.createFrame(message._user,true);
			break;
		}
	}
}