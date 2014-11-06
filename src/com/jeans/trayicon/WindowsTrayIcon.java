/***
 * Windows Tray Icon
 * -----------------
 *
 * Written by Jan Struyf
 *
 *  jan.struyf@cs.kuleuven.ac.be
 *  http://jeans.studentenweb.org/java/trayicon/trayicon.html
 *
 * Please mail me if you
 *	- 've found bugs
 *	- like this program
 *	- don't like a particular feature
 *	- would like something to be modified
 *
 * I always give it my best shot to make a program useful and solid, but
 * remeber that there is absolutely no warranty for using this program as
 * stated in the following terms:
 *
 * THERE IS NO WARRANTY FOR THIS PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE
 * LAW. THE COPYRIGHT HOLDER AND/OR OTHER PARTIES WHO MAY HAVE MODIFIED THE
 * PROGRAM, PROVIDE THE PROGRAM "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER
 * EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.  THE ENTIRE RISK AS
 * TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU.  SHOULD THE
 * PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING,
 * REPAIR OR CORRECTION.
 *
 * IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW WILL ANY COPYRIGHT HOLDER,
 * OR ANY OTHER PARTY WHO MAY MODIFY AND/OR REDISTRIBUTE THE PROGRAM,
 * BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE
 * PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED
 * INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE OF THE
 * PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF SUCH HOLDER OR OTHER
 * PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * May the Force be with you... Just compile it & use it!
 */


package com.jeans.trayicon;

import java.awt.image.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

/**
 * WindowsTrayIcon
 * A Java Implementation for showing icons in the Windows System Tray
 *
 * Written by Jan Struyf
 *	(jan.struyf@cs.kuleuven.ac.be)
 *	(http://ace.ulyssis.org/~jeans)
 *
 * Instantiate this class for each icon
 * This file comes with native code in TRAYICON.DLL
 * The DLL should go in C:/WINDOWS/SYSTEM or in your current directory
 */
public class WindowsTrayIcon {

    private static TrayIconKeeper m_Keeper;

/****************************************************************************************************************
 *                                                                                                              *
 * Initialisation / Termination                                                                                 *
 *                                                                                                              *
 ****************************************************************************************************************/

/**
 * Init native library - call this method in the main() method of your app
 *
 * Param appName = the title for the hidden window
 *	Each app has it's own hidden window that receives the mouse/menu messages for it's Tray Icons
 *      The window title is used by sendWindowsMessage() and isRunning() to identify an app
 */
	public static native void initTrayIcon(String appName);

/**
 * Free all native resources - call this method before System.exit()
 */
	public static void cleanUp() {
	    if (m_Keeper != null) {
            m_Keeper.notify();
            m_Keeper = null;
        }
        termTrayIcon();
	}

	private static native void termTrayIcon();

/****************************************************************************************************************
 *                                                                                                              *
 * Constructor                                                                                                  *
 *                                                                                                              *
 ****************************************************************************************************************/

/**
 * Construct a new Tray Icon
 * Using a Java Image - This can be loaded from a 16x16 GIF or JPG file
 *
 * Param image	16x16 icon - make sure it's loaded in memory - use MediaTracker
 * Param w	the icon width - eg. 16
 * Param h	the icon height - eg. 16
 *
 * Exception TrayIconException - if something goes wrong :O(
 *	- Too many icons allocated
 *	- Error initializing native code DLL
 *	- Error setting up Windows notify procedure
 *	- Error loading icon image
 * Exception InterruptedException - if the thread loading the image is interrupted
 */
	public WindowsTrayIcon(Image image, int w, int h) throws TrayIconException, InterruptedException {
		// Allocate new id for icon (native routine)
		my_id = getFreeId();
		if (my_id == TOOMANYICONS)
			throw new TrayIconException("Too many icons allocated");
		if (my_id == DLLNOTFOUND)
			throw new TrayIconException("Error initializing native code DLL");
		if (my_id == NOTIFYPROCERR)
			throw new TrayIconException("Error setting up Windows notify procedure");
		// Store image data and size
		setImage(image, w, h);
	}

/****************************************************************************************************************
 *                                                                                                              *
 * Methods                                                                                                      *
 *                                                                                                              *
 ****************************************************************************************************************/

/**
 * Change this icon's Image
 * Using a Java Image - This can be loaded from a 16x16 GIF or JPG file
 *
 * Param image	16x16 icon - make sure it's loaded in memory - use MediaTracker
 * Param w	the icon width
 * Param h	the icon height
 *
 * Exception TrayIconException - if something goes wrong :O(
 *	- Error loading icon image
 * Exception InterruptedException - if the thread loading the image is interrupted
 */
	public void setImage(Image image, int w, int h) throws TrayIconException, InterruptedException {
		try {
			// Collect pixel data in array
			int[] pixels = new int[w * h];
			PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
			pg.grabPixels();
			if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
				freeIcon();
				throw new TrayIconException("Error loading icon image");
		        }
		        // Send image data to the native library
			setIconData(my_id, w, h, pixels);
		} catch (InterruptedException ex) {
			freeIcon();
			throw ex;
		} catch (NullPointerException ex) {
			freeIcon();
			throw ex;
		}
	}

/**
 * Show/Hide this icon in the Windows System Tray
 *
 * Param status true = show, false = hide
 */
	public void setVisible(boolean status) {
		showIcon(my_id, status);
	}

/**
 * Test if this icon is currently visible in the Windows System Tray
 *
 * Returns true if visible
 */
	public boolean isVisible() {
		return testVisible(my_id) == 1;
	}

/**
 * Changes the text for the ToolTip of this icon
 * The ToolTip is displayed when the user mouses over the icon
 *
 * Param tip = the new text for the ToolTip
 */
	public void setToolTipText(String tip) {
		setToolTip(my_id, tip);
	}

/**
 * Add an ActionLister to this icon
 * Just like with java.awt.Button or javax.swing.JButton
 *
 * Param listener = your listener
 */
	public void addActionListener(ActionListener listener) {
		if (listeners == null) {
			listeners = new Vector();
			clickEnable(this, my_id, true);
		}
		listeners.addElement(listener);
	}

/**
 * Set new popup menu
 * The popup menu is displayed when the user right clicks the icon
 * See class TrayIconPopup, TrayIconPopupSimpleItem, ..
 *
 * Param popup = the popup menu
 */
	public void setPopup(TrayIconPopup popup) {
		if (mPopup == null) clickEnable(this, my_id, true);
		mPopup = popup;
		int levels = popup.getNbLevels();
		initPopup(my_id, levels);
		popup.setTrayIcon(this, my_id, -1);
	}

/**
 * Free all native resources for this icon
 * On exit use cleanUp()
 */
	public void freeIcon() {
		clickEnable(this, my_id, false);
		freeIcon(my_id);
	}

/**
 * Get coordinates of last mouse click
 */
	public static Point getMousePos() {
		return new Point(getMouseX(), getMouseY());
	}

/**
 * Return error code from native library - use for debugging
 */
	public static native int getLastError();

// No error occured since the last call to getLastError()
// There are a lot errors declared but they are only there for debug reasons
	public static final int NOERR = 0;

// The ActionListeners of the icon need to be notified when the user clicks it
// In the Windows API this is accomplished using a Notify Procedure
 	public static final int NOTIFYPROCERR = -1;

// The DLL has a fixed data structure that can contain up to 100 icons
// Hope that's enough for you
	public static final int TOOMANYICONS = -2;

// This happens when C++ is out of memory
	public static final int NOTENOUGHMEM = -3;

// Each icon has one unique id number
	public static final int WRONGICONID = -4;

// The native code can't locate the DLL
// Try moving it to C:/WINDOWS/SYSTEM or something like that
	public static final int DLLNOTFOUND = -5;

// Invocation code can't find your Java VM during callback
	public static final int NOVMS = -6;

// Invocation API can't attach native thread to your Java VM
	public static final int ERRTHREAD = -7;

// Error in lookup of the notifyListeners() method in this class
// The DLL has to do this when the user clicks one of your icons
	public static final int METHODID = -8;

// Not really an error..
// This happens when the user clicks an icon that has no ActionListener yet
	public static final int NOLISTENER = -9;

// One of the Invocation JNI Functions returned an error
	public static final int JNIERR = -10;

/****************************************************************************************************************
 *                                                                                                              *
 * Windows messaging code for detecting previous instance                                                       *
 *                                                                                                              *
 ****************************************************************************************************************/

/**
 * Checks if there's an instance with hidden window title = appName running
 * Can be used to detect that another instance of your app is already running (so exit..)
 *
 * Param appName = the title of the hidden window to search for
 */
	public static native boolean isRunning(String appName);

/**
 * Send a message to another app (message can contain an integer)
 * Can be used to detect that another instance of your app is already running
 * That instance can for example restore it's window after it receives the windows
 * message - see demo app for more info
 *
 * Param appName = the title of the hidden window to search for
 * Param message = the integer message to send (only native int size supported)
 */
	public static native int sendWindowsMessage(String appName, int message);

/**
 * Set callback method for receiving windows messages
 * See sendWindowsMessage() for more information or take a look at the demo app
 *
 * Param callback = the callback method for this app
 */
	public static void setWindowsMessageCallback(TrayIconCallback callback) {
		mWindowsMessageCallback = callback;
	}

/****************************************************************************************************************
 *                                                                                                              *
 * Next section is for inter use only -- or for hackers :O)                                                     *
 *                                                                                                              *
 ****************************************************************************************************************/

// Each icon has a unique id ranging from 0..99
	private int my_id;
// Each icon can have a popup menu - activated when user right clicks the icon
	private TrayIconPopup mPopup;
// Each icon can have any number of ActionListeners - notified when user clicks (left/right) the icon
	private Vector listeners;
// Each application can have one WindowsMessageCallback - notified when another app uses sendWindowsMessage
	private static TrayIconCallback mWindowsMessageCallback;

/**
 * Private method called by native library when user clicks mouse button
 *
 * Param button = "Left" or "Right"
 */
	private void notifyMouseListeners(int button) {
		if (listeners != null) {
			ActionEvent evt = null;
			if (button == 0) evt = new ActionEvent(this,0,"Left");
			else evt = new ActionEvent(this,0,"Right");
			for (Enumeration enu = listeners.elements(); enu.hasMoreElements(); ) {
				ActionListener listener = (ActionListener)enu.nextElement();
				listener.actionPerformed(evt);
			}
		}
	}

/**
 * Private method called by native library when user selects popup menu item
 *
 * Param id = id of menu item (each menu item has unique id)
 */
	private void notifyMenuListeners(int id) {
		if (mPopup != null) mPopup.onSelected(id);
	}

/**
 * Private method called by native library when it receives a sendWindowsMessage event
 * See sendWindowsMessage() for more information or take a look at the demo app
 *
 * Param lParam = parameter send along with windows message
 */
	private static int callWindowsMessage(int lParam) {
		if (mWindowsMessageCallback != null) return mWindowsMessageCallback.callback(lParam);
		else return 0;
	}

/**
 * Set check mark for TrayIconPopupCheckItem
 * Used by TrayIconPopupCheckItem.setCheck()
 *
 * Param menuId = the id of the menu item
 * Param selected = true if check mark
 */
	void checkPopup(int menuId, boolean selected) {
		checkPopup(my_id, menuId, selected);
	}

	void enablePopup(int menuId, boolean selected) {
		enablePopup(my_id, menuId, selected);
	}

/**
 * Init new popup menu - used by setPopup()
 *
 * Param id = the icon's id
 * Param nblevels = the submenu depth of the new popup
 */
	static native void initPopup(int id, int nblevels);

// Constants for builing a popup menu
// Used by subclasses of TrayIconPopupItem
	final static int POPUP_TYPE_ITEM        = 0;	// Simple item
	final static int POPUP_TYPE_SEPARATOR   = 1;	// Separator
	final static int POPUP_TYPE_CHECKBOX    = 2;	// Checkbox item
	final static int POPUP_TYPE_INIT_LEVEL  = 3;	// First item of submenu
	final static int POPUP_TYPE_DONE_LEVEL  = 4;	// Last item of submenu

// Enable/Disable and friends
    final static int POPUP_MODE_DISABLED    = 1;
    final static int POPUP_MODE_CHECKED     = 2;

/**
 * Add popup menu item - used by setTrayIcon() in subclasses of TrayIconPopupItem
 *
 * Param id = the icon's id
 * Param level = the submenu level
 * Param name = the name of the menu item
 * Param type = POPUP_TYPE_ITEM or POPUP_TYPE_SEPARATOR or..
 */
	static native int subPopup(int id, int level, String name, int type, int extra);

/**
 * Set check mark for TrayIconPopupCheckItem
 * Used by checkPopup(int menuId, boolean selected)
 *
 * Param id = the icon's id
 * Param menuId = the id of the menu item
 * Param selected = true if check mark
 */
	private static native void checkPopup(int id, int menuId, boolean selected);

	private static native void enablePopup(int id, int menuId, boolean selected);

/**
 * Allocate a new id for icon - used in constructor
 */
	private static native int getFreeId();

/**
 * Set bitmap data for icon - used in constructor and setImage()
 *
 * Param id = the icon's id
 * Param w, h = the images size
 * Param pixels = the pixel array
 */
	private static native void setIconData(int id, int w, int h, int pixels[]);

/**
 * Make Tray Icon visible/invisible - used by setVisible()
 *
 * Param id = the icon's id
 * Param hide = visible/invisible?
 */
	private static native void showIcon(int id, boolean hide);

/**
 * Test if Tray Icon is in the system tray - used by isVisible()
 *
 * Param id = the icon's id
 */
	private static native int testVisible(int id);

/**
 * Enable mouse/menu messages for icon - used by addActionListener() and setPopup()
 *
 * Param ico = the icons class (this)
 * Param id = the icon's id
 * Param enable = enable/disable mouse events?
 */
	private static native void clickEnable(WindowsTrayIcon ico, int id, boolean enable);

/**
 * Set tooltip - used by setToolTip(String tip)
 *
 * Param id = the icon's id
 * Param tip = the new tooltip
 */
	private static native void setToolTip(int id, String tip);

/**
 * Free all native resources for this icon - used by freeIcon()
 *
 * Param id = the icon's id
 */
	private static native void freeIcon(int id);

	private static native void detectAllClicks(int id);

/**
 * Get coordinates of last mouse click
 */
	private static native int getMouseX();
	private static native int getMouseY();

/**
 * Keep TrayIcon alive
 */
    public static void keepAlive() {
        if (m_Keeper == null) {
            m_Keeper = new TrayIconKeeper();
            m_Keeper.start();
        }
    }

	// Init the native library
	static {
		boolean ok = false;
		String version = System.getProperty("java.version");
		/*if (version.length() >= 3) {
			String v1 = version.substring(0,3);
			if (v1.equals("1.1")) {
				System.loadLibrary("TrayIcon11");
				ok = true;
			} else {
				System.loadLibrary("TrayIcon12");
				ok = true;
			}
		}
		if (!ok) {
			System.out.println("Wrong Java VM version: "+version);
			System.exit(-1);
		}*/

		try {
			System.loadLibrary("TrayIcon12");
		}
		catch(Throwable t) {
                    t.printStackTrace();
			javax.swing.JOptionPane.showMessageDialog(null,
				"Cannot load library TrayIcon12.dll",
				"Talk2Me: Error",javax.swing.JOptionPane.ERROR_MESSAGE);
			//System.exit(-1);
		}
	}
}
