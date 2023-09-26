// Copyright Epic Games, Inc. All Rights Reserved.

#include "ProjectEDAGameMode.h"
#include "ProjectEDACharacter.h"
#include "UObject/ConstructorHelpers.h"

AProjectEDAGameMode::AProjectEDAGameMode()
{
	// set default pawn class to our Blueprinted character
	static ConstructorHelpers::FClassFinder<APawn> PlayerPawnBPClass(TEXT("/Game/ThirdPerson/Blueprints/BP_ThirdPersonCharacter"));
	if (PlayerPawnBPClass.Class != NULL)
	{
		DefaultPawnClass = PlayerPawnBPClass.Class;
	}
}
