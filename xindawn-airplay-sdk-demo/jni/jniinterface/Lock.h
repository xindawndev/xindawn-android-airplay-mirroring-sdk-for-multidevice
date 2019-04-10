#ifndef _Lock_H
#define _Lock_H

#include <threads.h>

class ILock
{
public:
	virtual ~ILock() {}

	virtual void Lock() const = 0;
	virtual void Unlock() const = 0;
};


class CMutex : public ILock
{
public:
	CMutex();
	~CMutex();

	virtual void Lock() const;
	virtual void Unlock() const;

private:
	mutable mutex_handle_t m_mutex;
};


class CMyLock
{
public:
	CMyLock(const ILock&);
	~CMyLock();

private:
	const ILock& m_lock;
};


#endif
