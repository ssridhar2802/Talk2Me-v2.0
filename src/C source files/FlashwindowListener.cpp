#include <jawt_md.h>
#include <assert.h>
#include <jawt.h>
#include "FlashwindowListener.h"

// A Function to flash the passed window
// AJ
//

JNIEXPORT void JNICALL Java_FlashwindowListener_flashWindow
  (JNIEnv *env, jobject obj, jobject panel)
{

	JAWT awt;
	JAWT_DrawingSurface* ds;
	JAWT_DrawingSurfaceInfo* dsi;
	JAWT_Win32DrawingSurfaceInfo* dsi_win;

	jboolean result;
	jint lock;

	// Get the AWT
	awt.version = JAWT_VERSION_1_3;
	result = JAWT_GetAWT(env, &awt);
	assert(result != JNI_FALSE);
	// Get the drawing surface
	ds = awt.GetDrawingSurface(env, panel);
	if(ds == NULL)
		return;
	// Lock the drawing surface
	lock = ds->Lock(ds);
	assert((lock & JAWT_LOCK_ERROR) == 0);

	// Get the drawing surface info
	dsi = ds->GetDrawingSurfaceInfo(ds);

	// Get the platform-specific drawing info
	dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;

	FlashWindow(dsi_win->hwnd,TRUE);

}