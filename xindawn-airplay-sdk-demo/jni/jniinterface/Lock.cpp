#include "Lock.h"



CMutex::CMutex()
{
	MUTEX_CREATE(m_mutex);
}


CMutex::~CMutex()
{
	MUTEX_DESTROY(m_mutex);
}


void CMutex::Lock() const
{
	MUTEX_LOCK(m_mutex);
}


void CMutex::Unlock() const
{
	MUTEX_UNLOCK(m_mutex);
}


CMyLock::CMyLock(const ILock& m) : m_lock(m)
{
	m_lock.Lock();
}

CMyLock::~CMyLock()
{
	m_lock.Unlock();
}

