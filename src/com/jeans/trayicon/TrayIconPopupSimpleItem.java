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

import java.util.*;
import java.awt.event.*;

// Menu item for Tray Icon
public class TrayIconPopupSimpleItem implements TrayIconPopupItem {

	// Menu item's name
	protected String mItem;
	// Menu item's id (used by native code)
	protected int mMenuId;
	// Enable / Disable menu item
	protected boolean mEnabled;
	// Owner of this menu item
	protected WindowsTrayIcon mTrayIcon;	
	// Action listeners for menu item
	private Vector listeners;

/**
 * Create a new menu item
 *
 * Param item = name of new item
 */
	public TrayIconPopupSimpleItem(String item) {
		mItem = item;
		mEnabled = true;
	}

/**
 * Add an ActionLister to this menu item
 * Just like with java.awt.Button or javax.swing.JButton
 *
 * Param listener = your listener
 */
	public void addActionListener(ActionListener listener) {
		if (listeners == null) listeners = new Vector();
		listeners.addElement(listener);
	}

/****************************************************************************************************************
 *                                                                                                              *
 * Next section is for inter use only -- or for hackers :O)                                                     *
 *                                                                                                              *
 ****************************************************************************************************************/

/**
 * Return submenu depth - used by WindowsTrayIcon.setPopup()/initPopup()
 */
	public int getNbLevels() {
		return 0;
	}
	
/**
 * Enable/Disable item
 *
 * Param enable = enable/disable item?
 */
	public void setEnabled(boolean enable) {
		mEnabled = enable;
		if (mTrayIcon != null) mTrayIcon.enablePopup(mMenuId, mEnabled);
	}	

/**
 * Callback when user selects menu item (find it by comparing menu id's)
 *
 * Param menuId = the id of the selected item
 */
	public boolean onSelected(int menuId) {
		boolean selected = menuId == mMenuId;
		if (selected && listeners != null) {
			ActionEvent evt = new ActionEvent(this,0,"");
			for (Enumeration enu = listeners.elements(); enu.hasMoreElements(); ) {
				ActionListener listener = (ActionListener)enu.nextElement();
				listener.actionPerformed(evt);
			}
		}
		return selected;
	}

/**
 * Create menu in native library - used by WindowsTrayIcon.setPopup()
 *
 * Param trayicon = the owner of this menu
 * Param id = the icon's id
 * Param level = the level (submenu depth)
 */
	public void setTrayIcon(WindowsTrayIcon trayicon, int id, int level) {
	    int extra = mEnabled ? 0 : WindowsTrayIcon.POPUP_MODE_DISABLED;
		mMenuId = trayicon.subPopup(id, level, mItem, WindowsTrayIcon.POPUP_TYPE_ITEM, extra);
        mTrayIcon = trayicon;		
	}

}
