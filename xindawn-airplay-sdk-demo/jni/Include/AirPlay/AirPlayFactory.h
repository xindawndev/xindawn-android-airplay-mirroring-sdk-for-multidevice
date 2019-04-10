#pragma once

#include "AirPlayEx.h"


# define EXPORT_API __attribute__((visibility("default")))

class CAirPlayFactory
{
public:
	EXPORT_API CAirPlayEx* Create();
	EXPORT_API void Destroy(CAirPlayEx *);

};
