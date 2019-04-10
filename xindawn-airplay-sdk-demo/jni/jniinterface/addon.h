#ifndef __ADDON_H__
#define __ADDON_H__

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>



#ifdef __cplusplus
extern "C"
{
#endif
void XinDawn_SetVideoSurface(int nId, jobject objSurface);
void XinDawn_ShutDown(int nId);
int  XinDawn_StartMediaServer(JavaVM* JVM,char *friendname, char *dllpath, int width, int height, char *activecode);
void XinDawn_StopMediaServer();

#ifdef __cplusplus
}
#endif

#endif
