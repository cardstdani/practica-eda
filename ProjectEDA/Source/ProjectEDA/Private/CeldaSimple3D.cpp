#include "CeldaSimple3D.h"
#include "Kismet/GameplayStatics.h"
#include "Containers/Array.h"
#include "CoreMinimal.h"
#include "Async/Async.h"

// Sets default values
ACeldaSimple3D::ACeldaSimple3D()
{
	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;

}

// Called when the game starts or when spawned
void ACeldaSimple3D::BeginPlay()
{
	Super::BeginPlay();

}

// Called every frame
void ACeldaSimple3D::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

}

void ACeldaSimple3D::SetSeed(int32 seed) {
	stream = FRandomStream(seed);
}

void ACeldaSimple3D::SimularAsync(int32 n)
{
	AsyncTask(ENamedThreads::AnyBackgroundThreadNormalTask, [=]()
		{
			TArray<double> tValues;
			for (int i = 2; i <= n; i++)
			{
				InicializarSim(i);
				double t = FPlatformTime::Seconds();
				while (!CortocircuitoSim()) {
					RayoCosmicoSim(stream.RandRange(0, i - 1), stream.RandRange(0, i - 1), stream.RandRange(0, i - 1));
				}
				t = FPlatformTime::Seconds() - t;
				UE_LOG(LogTemp, Warning, TEXT("%i iteration executed in %f seconds."), i, t);

				tValues.Add(t);
			}
			FString FilePath = FPaths::ProjectSavedDir() + TEXT("t_values3D.txt");
			FString TextToSave;

			for (double t : tValues)
			{
				TextToSave += FString::Printf(TEXT("%f\n"), t);
			}
			FFileHelper::SaveStringToFile(TextToSave, *FilePath);

			FString AbsolutePath = FPaths::ConvertRelativePathToFull(FilePath);
			FString DirectoryPath = FPaths::GetPath(AbsolutePath);
			FPlatformProcess::ExploreFolder(*DirectoryPath);
		});
}

void ACeldaSimple3D::InicializarSim(int32 n)
{
	grid.SetNum(n);
	visited.SetNum(n);
	for (int i = 0; i < n; i++)
	{
		grid[i].SetNum(n);
		visited[i].SetNum(n);
		for (int j = 0; j < n; j++)
		{
			grid[i][j].SetNum(n);
			visited[i][j].SetNum(n);
			for (int k = 0; k < n; k++)
			{
				grid[i][j].Add(false);
				visited[i][j].Add(false);
			}
		}
	}
}

void ACeldaSimple3D::RayoCosmicoSim(int32 i, int32 j, int32 k)
{
	if (!grid[i][j][k])
	{
		grid[i][j][k] = true;
	}
	iterations++;
}

bool ACeldaSimple3D::HelperSim(int32 i, int32 j, int32 k)
{
	if (i == (grid.Num() - 1))
	{
		return true;
	}

	visited[i][j][k] = true;
	TArray<TArray<int32>> nei = {
	{FMath::Max(0, i - 1), j, k}, {FMath::Min(grid.Num() - 1, i + 1), j, k},
	{i, FMath::Max(0, j - 1), k}, {i, FMath::Min(grid[0].Num() - 1, j + 1), k},
	{FMath::Max(0, i - 1), j, FMath::Max(0, k - 1)}, {FMath::Min(grid.Num() - 1, i + 1), j, FMath::Min(grid[0][0].Num() - 1, k + 1)},
	{FMath::Max(0, i - 1), FMath::Max(0, j - 1), k}, {FMath::Min(grid.Num() - 1, i + 1), FMath::Min(grid[0].Num() - 1, j + 1), k},
	{FMath::Max(0, i - 1), j, FMath::Min(grid[0][0].Num() - 1, k + 1)}, {FMath::Min(grid.Num() - 1, i + 1), j, FMath::Max(0, k - 1)},
	{FMath::Max(0, i - 1), FMath::Min(grid[0].Num() - 1, j + 1), k}, {FMath::Min(grid.Num() - 1, i + 1), FMath::Max(0, j - 1), k},
	{i, FMath::Max(0, j - 1), FMath::Max(0, k - 1)}, {i, FMath::Min(grid[0].Num() - 1, j + 1), FMath::Min(grid[0][0].Num() - 1, k + 1)},
	{i, FMath::Min(grid[0].Num() - 1, j + 1), FMath::Max(0, k - 1)}, {i, FMath::Max(0, j - 1), FMath::Min(grid[0][0].Num() - 1, k + 1)}
	};

	for (auto n : nei) {
		if (!visited[n[0]][n[1]][n[2]] && grid[n[0]][n[1]][n[2]])
		{
			if (HelperSim(n[0], n[1], n[2]))
			{
				return true;
			}
		}
	}
	return false;
}

bool ACeldaSimple3D::CortocircuitoSim()
{
	for (int i = 0; i < grid.Num(); i++)
	{
		for (int j = 0; j < grid[0].Num(); j++)
		{
			for (int k = 0; k < grid[0][0].Num(); k++)
			{
				visited[i][j][k] = false;
			}
		}
	}

	for (int32 i = 0; i < grid[0].Num(); i++)
	{
		for (int j = 0; j < grid[0].Num(); j++) {
			if (grid[0][i][j])
			{
				if (HelperSim(0, i, j))
				{
					return true;
				}
			}
		}
	}
	return false;
}


//TEST FUNCTIONS
void ACeldaSimple3D::Inicializar(int32 n)
{
	if (cubitosArray.Num() > 0)
	{
		for (int i = 0; i < cubitosArray.Num(); i++)
		{
			for (int j = 0; j < cubitosArray[i].Num(); j++)
			{
				for (int k = 0; k < cubitosArray[i][j].Num(); k++)
				{
					if (cubitosArray[i][j][k])
					{
						cubitosArray[i][j][k]->Destroy();
					}
				}
			}
		}
	}

	for (AActor* Actor : cubitosBordeArray)
	{
		if (Actor)
		{
			Actor->Destroy();
		}
	}
	cubitosBordeArray.Empty();

	grid.SetNum(n);
	visited.SetNum(n);
	cubitosArray.SetNum(n);

	for (int i = 0; i < n; i++)
	{
		cubitosArray[i].SetNum(n);
		grid[i].SetNum(n);
		visited[i].SetNum(n);
		for (int j = 0; j < n; j++)
		{
			cubitosArray[i][j].SetNum(n);
			grid[i][j].SetNum(n);
			visited[i][j].SetNum(n);
			for (int k = 0; k < n; k++)
			{
				grid[i][j].Add(false);
				visited[i][j].Add(false);

				cubitosArray[i][j][k] = SpawnCubito((i + 1) * space, j * space, k * space);
				SetColor(cubitosArray[i][j][k], 1);
				SetText(cubitosArray[i][j][k], FString::Printf(TEXT("(%d, %d, %d)"), i, j, k));
			}
		}
	}

	cubitosBordeArray.SetNum(2 * n * n);
	for (int i = 0; i < n; i++)
	{
		for (int j = 0; j < n; j++) {
			AActor* cubo1 = SpawnCubito(0, i * space, j * space);
			AActor* cubo2 = SpawnCubito((n + 1) * space, i * space, j * space);
			cubitosBordeArray.Add(cubo1);
			cubitosBordeArray.Add(cubo2);
			SetColor(cubo1, 0);
			SetColor(cubo2, 0);

			SetText(cubo1, FString::Printf(TEXT("")));
			SetText(cubo2, FString::Printf(TEXT("")));
		}
	}
}

void ACeldaSimple3D::SetText(AActor* Actor, FString text) {
	UFunction* CustomEventFunction = Actor->FindFunction("SetText");
	Actor->ProcessEvent(CustomEventFunction, &text);
}

void ACeldaSimple3D::SetColor(AActor* Actor, int32 color) {
	UFunction* CustomEventFunction = Actor->FindFunction("SetColor");
	Actor->ProcessEvent(CustomEventFunction, &color);
}

AActor* ACeldaSimple3D::SpawnCubito(float x, float y, float z)
{

	UWorld* const World = GetWorld();
	if (World)
	{
		FActorSpawnParameters SpawnParams;
		SpawnParams.SpawnCollisionHandlingOverride = ESpawnActorCollisionHandlingMethod::AlwaysSpawn;

		AActor* SpawnedActor = World->SpawnActor<AActor>(CubitoBlueprint, FVector(x, y, z), FRotator(0, 0, 0), SpawnParams);

		if (SpawnedActor)
		{
			return SpawnedActor;
		}
	}
	return nullptr;
}

void ACeldaSimple3D::RayoCosmico(int32 i, int32 j, int32 k)
{
	//UE_LOG(LogTemp, Warning, TEXT("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
	if (!grid[i][j][k])
	{
		grid[i][j][k] = true;
		SetColor(cubitosArray[i][j][k], 2);
	}
	iterations++;
}

bool ACeldaSimple3D::Helper(int32 i, int32 j, int32 k)
{
	SetColor(cubitosArray[i][j][k], 3);
	if (i == (grid.Num() - 1))
	{
		//UE_LOG(LogTemp, Warning, TEXT("%d %d"), i, j);        
		return true;
	}

	visited[i][j][k] = true;
	TArray<TArray<int32>> nei = {
	{FMath::Max(0, i - 1), j, k}, {FMath::Min(grid.Num() - 1, i + 1), j, k},
	{i, FMath::Max(0, j - 1), k}, {i, FMath::Min(grid[0].Num() - 1, j + 1), k},
	{FMath::Max(0, i - 1), j, FMath::Max(0, k - 1)}, {FMath::Min(grid.Num() - 1, i + 1), j, FMath::Min(grid[0][0].Num() - 1, k + 1)},
	{FMath::Max(0, i - 1), FMath::Max(0, j - 1), k}, {FMath::Min(grid.Num() - 1, i + 1), FMath::Min(grid[0].Num() - 1, j + 1), k},
	{FMath::Max(0, i - 1), j, FMath::Min(grid[0][0].Num() - 1, k + 1)}, {FMath::Min(grid.Num() - 1, i + 1), j, FMath::Max(0, k - 1)},
	{FMath::Max(0, i - 1), FMath::Min(grid[0].Num() - 1, j + 1), k}, {FMath::Min(grid.Num() - 1, i + 1), FMath::Max(0, j - 1), k},
	{i, FMath::Max(0, j - 1), FMath::Max(0, k - 1)}, {i, FMath::Min(grid[0].Num() - 1, j + 1), FMath::Min(grid[0][0].Num() - 1, k + 1)},
	{i, FMath::Min(grid[0].Num() - 1, j + 1), FMath::Max(0, k - 1)}, {i, FMath::Max(0, j - 1), FMath::Min(grid[0][0].Num() - 1, k + 1)}
	};

	for (auto n : nei) {
		if (!visited[n[0]][n[1]][n[2]] && grid[n[0]][n[1]][n[2]])
		{
			if (Helper(n[0], n[1], n[2]))
			{
				return true;
			}
		}
	}
	return false;
}

bool ACeldaSimple3D::Cortocircuito()
{
	for (int i = 0; i < grid.Num(); i++)
	{
		for (int j = 0; j < grid[0].Num(); j++)
		{
			for (int k = 0; k < grid[0][0].Num(); k++)
			{
				visited[i][j][k] = false;
			}
		}
	}

	for (int32 i = 0; i < grid[0].Num(); i++)
	{
		for (int j = 0; j < grid[0].Num(); j++) {
			if (grid[0][i][j])
			{
				if (Helper(0, i, j))
				{
					return true;
				}
			}
		}
	}
	return false;
}