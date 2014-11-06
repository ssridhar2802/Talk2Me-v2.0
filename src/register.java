/****************************************************************/
/*                      UsingBoxLayout	                            */
/*                                                              */
/****************************************************************/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
/**
 * Summary description for UsingBoxLayout
 *
 */
public class register extends JFrame
{
	// Variables declaration
	private JLabel labH1;
	private JLabel labH2;
	private JLabel labV1;
	private JPanel contentPane;
	//-----
	private JPanel pnlCenterBoxLayout;
	//-----
	private JButton btnOK;
	private JButton btnCancel;
	private JButton btnApply;
	private JPanel pnlBottomFlowLayout;
	//-----
	private JLabel jLabel1;
	private JTextField txfSurname;
	private JPanel pnlSurname;
	//-----
	private JLabel jLabel2;
	private JTextField txfFirstname;
	private JPanel pnlFirstname;
	//-----
	private JLabel jLabel5;
	private JRadioButton rbtnMale;
	private JRadioButton rbtnFemale;
	private JPanel pnlSex;
	//-----
	private JLabel jLabel6;
	private JComboBox cmbAgeGroup;
	private JPanel pnlAge;
	//-----
	private JLabel jLabel3;
	private JTextField txfEmail;
	private JPanel pnlEmail;
	//-----
	private JLabel jLabel4;
	private JTextField txfPhone;
	private JPanel pnlPhone;
	//-----
	private ButtonGroup btnGroup_Sex;
	// End of variables declaration


	public register()
	{
		super();
		initializeComponent();
		//
		// TODO: Add any constructor code after initializeComponent call
		//

		this.setVisible(true);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always regenerated
	 * by the Windows Form Designer. Otherwise, retrieving design might not work properly.
	 * Tip: If you must revise this method, please backup this GUI file for JFrameBuilder
	 * to retrieve your design properly in future, before revising this method.
	 */
	private void initializeComponent()
	{
		labH1 = new JLabel();
		labH2 = new JLabel();
		labV1 = new JLabel();
		contentPane = (JPanel)this.getContentPane();
		//-----
		pnlCenterBoxLayout = new JPanel();
		//-----
		btnOK = new JButton();
		btnCancel = new JButton();
		btnApply = new JButton();
		pnlBottomFlowLayout = new JPanel();
		//-----
		jLabel1 = new JLabel();
		txfSurname = new JTextField();
		pnlSurname = new JPanel();
		//-----
		jLabel2 = new JLabel();
		txfFirstname = new JTextField();
		pnlFirstname = new JPanel();
		//-----
		jLabel5 = new JLabel();
		rbtnMale = new JRadioButton();
		rbtnFemale = new JRadioButton();
		pnlSex = new JPanel();
		//-----
		jLabel6 = new JLabel();
		cmbAgeGroup = new JComboBox();
		pnlAge = new JPanel();
		//-----
		jLabel3 = new JLabel();
		txfEmail = new JTextField();
		pnlEmail = new JPanel();
		//-----
		jLabel4 = new JLabel();
		txfPhone = new JTextField();
		pnlPhone = new JPanel();
		//-----
		btnGroup_Sex = new ButtonGroup();

		//
		// labH1
		//
		labH1.setToolTipText("This empty label is used for Horizontal Gap Setting of BorderLayout to be effective, if necessarily.");
		//
		// labH2
		//
		labH2.setToolTipText("This empty label is used for Horizontal Gap Setting of BorderLayout to be effective, if necessarily.");
		//
		// labV1
		//
		labV1.setToolTipText("This empty label is used for Vertical Gap Setting of BorderLayout to be effective, if necessarily.");
		//
		// contentPane
		//
		contentPane.setLayout(new BorderLayout(10, 10));
		contentPane.add(pnlCenterBoxLayout, BorderLayout.CENTER);
		contentPane.add(pnlBottomFlowLayout, BorderLayout.SOUTH);
		contentPane.add(labH1, BorderLayout.WEST);
		contentPane.add(labH2, BorderLayout.EAST);
		contentPane.add(labV1, BorderLayout.NORTH);
		//
		// pnlCenterBoxLayout
		//
		pnlCenterBoxLayout.setLayout(new BoxLayout(pnlCenterBoxLayout, BoxLayout.Y_AXIS));
		pnlCenterBoxLayout.add(pnlSurname, 0);
		pnlCenterBoxLayout.add(pnlFirstname, 1);
		pnlCenterBoxLayout.add(pnlEmail, 2);
		pnlCenterBoxLayout.add(pnlPhone, 3);
		pnlCenterBoxLayout.add(pnlSex, 4);
		pnlCenterBoxLayout.add(pnlAge, 5);
		pnlCenterBoxLayout.setBorder(BorderFactory.createEtchedBorder());
		pnlCenterBoxLayout.setToolTipText("This JPanel is using BoxLayout manager.");
		//
		// btnOK
		//
		btnOK.setText("    OK    ");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				btnOK_actionPerformed(e);
                                String username=new String(txfFirstname.getText());
                                String password=new String(txfEmail.getText());
                                System.out.println(username+" "+password);
                                try
                                {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        Connection conn=DriverManager.getConnection("jdbc:odbc:DB2COPY1","sridhar","sridhar");
        PreparedStatement ps=conn.prepareStatement("insert into login values(?,?)");
        ps.setString(1,username);
        ps.setString(2,password);
        ps.executeUpdate();
       //this.getContentPane().setVisible(false);
        MainFrame.frame.processLogin();
        
        
                                }
                                catch(Exception es)
                                {}
			}

		});
		//
		// btnCancel
		//
		btnCancel.setText(" Cancel ");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				btnCancel_actionPerformed(e);
			}

		});
		//
		// btnApply
		//
		btnApply.setText("  Apply  ");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				btnApply_actionPerformed(e);
			}

		});
		//
		// pnlBottomFlowLayout
		//
		pnlBottomFlowLayout.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 5));
		pnlBottomFlowLayout.add(btnOK, 0);
		pnlBottomFlowLayout.add(btnCancel, 1);
		pnlBottomFlowLayout.add(btnApply, 2);
		pnlBottomFlowLayout.setToolTipText("This JPanel is using FlowLayout manager.");
		//
		// jLabel1
		//
		jLabel1.setText("Name:");
		jLabel1.setPreferredSize(new Dimension(106, 22));
		//
		// txfSurname
		//
		txfSurname.setColumns(20);
		txfSurname.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				txfSurname_actionPerformed(e);
			}

		});
		//
		// pnlSurname
		//
		pnlSurname.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnlSurname.add(jLabel1, 0);
		pnlSurname.add(txfSurname, 1);
		//
		// jLabel2
		//
		jLabel2.setText("Username:");
		jLabel2.setPreferredSize(new Dimension(106, 22));
		//
		// txfFirstname
		//
		txfFirstname.setColumns(20);
		txfFirstname.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				txfFirstname_actionPerformed(e);
			}

		});
		//
		// pnlFirstname
		//
		pnlFirstname.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnlFirstname.add(jLabel2, 0);
		pnlFirstname.add(txfFirstname, 1);
		//
		// jLabel5
		//
		jLabel5.setText("Sex:");
		jLabel5.setPreferredSize(new Dimension(106, 22));
		//
		// rbtnMale
		//
		rbtnMale.setText("Male");
		rbtnMale.setSelected(true);
		rbtnMale.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				rbtnMale_itemStateChanged(e);
			}

		});
		//
		// rbtnFemale
		//
		rbtnFemale.setText("Female");
		rbtnFemale.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				rbtnFemale_itemStateChanged(e);
			}

		});
		//
		// pnlSex
		//
		pnlSex.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnlSex.add(jLabel5, 0);
		pnlSex.add(rbtnMale, 1);
		pnlSex.add(rbtnFemale, 2);
		//
		// jLabel6
		//
		jLabel6.setText("Age:");
		jLabel6.setPreferredSize(new Dimension(106, 22));
		//
		// cmbAgeGroup
		//
		cmbAgeGroup.addItem("Select Age Group");
		cmbAgeGroup.addItem("Under 15");
		cmbAgeGroup.addItem("16 - 25");
		cmbAgeGroup.addItem("26 - 35");
		cmbAgeGroup.addItem("36 - 45");
		cmbAgeGroup.addItem("46 - 55");
		cmbAgeGroup.addItem("56+");
		cmbAgeGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				cmbAgeGroup_actionPerformed(e);
			}

		});
		//
		// pnlAge
		//
		pnlAge.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnlAge.add(jLabel6, 0);
		pnlAge.add(cmbAgeGroup, 1);
		//
		// jLabel3
		//
		jLabel3.setText("Password:");
		jLabel3.setPreferredSize(new Dimension(106, 22));
		//
		// txfEmail
		//
		txfEmail.setColumns(20);
		txfEmail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				txfEmail_actionPerformed(e);
			}

		});
		//
		// pnlEmail
		//
		pnlEmail.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnlEmail.add(jLabel3, 0);
		pnlEmail.add(txfEmail, 1);
		//
		// jLabel4
		//
		jLabel4.setText("Phone:");
		jLabel4.setPreferredSize(new Dimension(106, 22));
		//
		// txfPhone
		//
		txfPhone.setColumns(20);
		txfPhone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				txfPhone_actionPerformed(e);
			}

		});
		//
		// pnlPhone
		//
		pnlPhone.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnlPhone.add(jLabel4, 0);
		pnlPhone.add(txfPhone, 1);
		//
		// btnGroup_Sex
		//
		btnGroup_Sex.add(rbtnFemale);
		btnGroup_Sex.add(rbtnMale);
		//
		// UsingBoxLayout
		//
		this.setTitle("register!!!");
		this.setLocation(new Point(72, 91));
		this.setSize(new Dimension(376, 308));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	//
	// TODO: Add any appropriate code in the following Event Handling Methods
	//
	private void btnOK_actionPerformed(ActionEvent e)
	{
		System.out.println("\nbtnOK_actionPerformed(ActionEvent e) called.");
		// TODO: Add any handling code here

	}

	private void btnCancel_actionPerformed(ActionEvent e)
	{
		System.out.println("\nbtnCancel_actionPerformed(ActionEvent e) called.");
		// TODO: Add any handling code here

	}

	private void btnApply_actionPerformed(ActionEvent e)
	{
		System.out.println("\nbtnApply_actionPerformed(ActionEvent e) called.");
		// TODO: Add any handling code here

	}

	private void txfSurname_actionPerformed(ActionEvent e)
	{
		System.out.println("\ntxfSurname_actionPerformed(ActionEvent e) called.");
		// TODO: Add any handling code here

	}

	private void txfFirstname_actionPerformed(ActionEvent e)
	{
		System.out.println("\ntxfFirstname_actionPerformed(ActionEvent e) called.");
		// TODO: Add any handling code here

	}

	private void rbtnMale_itemStateChanged(ItemEvent e)
	{
		System.out.println("\nrbtnMale_itemStateChanged(ItemEvent e) called.");
		System.out.println(">>" + ((e.getStateChange() == ItemEvent.SELECTED) ? "selected":"unselected"));
		// TODO: Add any handling code here
		
	}

	private void rbtnFemale_itemStateChanged(ItemEvent e)
	{
		System.out.println("\nrbtnFemale_itemStateChanged(ItemEvent e) called.");
		System.out.println(">>" + ((e.getStateChange() == ItemEvent.SELECTED) ? "selected":"unselected"));
		// TODO: Add any handling code here
		
	}

	private void cmbAgeGroup_actionPerformed(ActionEvent e)
	{
		System.out.println("\ncmbAgeGroup_actionPerformed(ActionEvent e) called.");
		
		Object o = cmbAgeGroup.getSelectedItem();
		System.out.println(">>" + ((o==null)? "null" : o.toString()) + " is selected.");
		// TODO: Add any handling code here for the particular object being selected
		
	}

	private void txfEmail_actionPerformed(ActionEvent e)
	{
		System.out.println("\ntxfEmail_actionPerformed(ActionEvent e) called.");
		// TODO: Add any handling code here

	}

	private void txfPhone_actionPerformed(ActionEvent e)
	{
		System.out.println("\ntxfPhone_actionPerformed(ActionEvent e) called.");
		// TODO: Add any handling code here

	}
}

	//
	// TODO: Add any method code to meet your needs in the following area
	//






























 

/*//============================= Testing ================================//
//=                                                                    =//
//= The following main method is just for testing this class you built.=//
//= After testing,you may simply delete it.                            =//
//======================================================================//
	public static void main(String[] args)
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception ex)
		{
			System.out.println("Failed loading L&F: ");
			System.out.println(ex);
		}
		new UsingBoxLayout();
	}
//= End of Testing =


}
*/