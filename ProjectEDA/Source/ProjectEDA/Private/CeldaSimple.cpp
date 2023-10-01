#include "CeldaSimple.h"
#include "Kismet/GameplayStatics.h"
#include "Containers/Array.h"
#include "CoreMinimal.h"
#include "Async/Async.h"

// Sets default values
ACeldaSimple::ACeldaSimple()
{
    // Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
    PrimaryActorTick.bCanEverTick = true;

}

// Called when the game starts or when spawned
void ACeldaSimple::BeginPlay()
{
    Super::BeginPlay();

}

// Called every frame
void ACeldaSimple::Tick(float DeltaTime)
{
    Super::Tick(DeltaTime);

}

void ACeldaSimple::SetSeed(int32 seed) {
    stream = FRandomStream(seed);
}

void ACeldaSimple::SimularAsync(int32 n)
{
    AsyncTask(ENamedThreads::AnyBackgroundThreadNormalTask, [=]()
        {
            TArray<double> tValues;
            for (int i = 2; i <= n; i++)
            {
                InicializarSim(i);
                double t = FPlatformTime::Seconds();
                while (!CortocircuitoSim()) {
                    
                    RayoCosmicoSim(stream.RandRange(0, i - 1), stream.RandRange(0, i - 1));
                }
                t = FPlatformTime::Seconds() - t;
                UE_LOG(LogTemp, Warning, TEXT("%i iteration executed in %f seconds."), i, t);

                tValues.Add(t);
            }
            FString FilePath = FPaths::ProjectSavedDir() + TEXT("t_values.txt");
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

void ACeldaSimple::InicializarSim(int32 n)
{
    grid.Init(TArray<bool>(), n);
    visited.Init(TArray<bool>(), n);

    for (int i = 0; i < n; i++)
    {
        for (int j = 0; j < n; j++)
        {
            grid[i].Add(false);
            visited[i].Add(false);
        }
    }
}

void ACeldaSimple::RayoCosmicoSim(int32 i, int32 j)
{
    if (!grid[i][j])
    {
        grid[i][j] = true;
    }
    iterations++;
}

bool ACeldaSimple::HelperSim(int32 i, int32 j)
{
    if (i == (grid.Num() - 1))
    {       
        return true;
    }

    visited[i][j] = true;
    TArray<TArray<int32>> nei = {
        {FMath::Max(0, i - 1), j},
        {FMath::Min(grid.Num() - 1, i + 1), j},
        {i, FMath::Max(0, j - 1)},
        {i, FMath::Min(grid[0].Num() - 1, j + 1)},
        {FMath::Max(0, i - 1), FMath::Max(0, j - 1)},
        {FMath::Min(grid.Num() - 1, i + 1), FMath::Min(grid[0].Num() - 1, j + 1)},
        {FMath::Max(0, i - 1), FMath::Min(grid[0].Num() - 1, j + 1)},
        {FMath::Min(grid.Num() - 1, i + 1), FMath::Max(0, j - 1)}
    };

    for (auto k : nei)
    {
        if (!visited[k[0]][k[1]] && grid[k[0]][k[1]])
        {
            if (HelperSim(k[0], k[1]))
            {
                return true;
            }
        }
    }
    return false;
}

bool ACeldaSimple::CortocircuitoSim()
{
    for (int32 i = 0; i < grid.Num(); i++)
    {
        for (int32 j = 0; j < grid[0].Num(); j++)
        {
            visited[i][j] = false;
        }
    }

    for (int32 i = 0; i < grid[0].Num(); i++)
    {
        if (grid[0][i])
        {
            if (HelperSim(0, i))
            {
                return true;
            }
        }
    }
    return false;
}


//TEST FUNCTIONS
void ACeldaSimple::Inicializar(int32 n)
{
    if (cubitosArray.Num() > 0) {
        for (int i = 0; i < cubitosArray.Num(); i++)
        {            
            for (int j = 0; j < cubitosArray[0].Num(); j++)
            {
                cubitosArray[i][j]->Destroy();
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

    grid.Init(TArray<bool>(), n);
    visited.Init(TArray<bool>(), n);
    cubitosArray.SetNum(n);

    for (int i = 0; i < n; i++)
    {
        cubitosArray[i].SetNum(n);
        for (int j = 0; j < n; j++)
        {
            grid[i].Add(false);
            visited[i].Add(false);
            cubitosArray[i][j] = SpawnCubito(i * space, j * space, 0);
            SetColor(cubitosArray[i][j], 1);
            SetText(cubitosArray[i][j], FString::Printf(TEXT("(%d, %d)"), i, j));
        }
    }

    cubitosBordeArray.SetNum(2 * n);
    for (int i = 0; i < n; i++)
    {
        AActor* cubo1 = SpawnCubito(-space, i * space, 0);
        AActor* cubo2 = SpawnCubito(n * space, i * space, 0);
        cubitosBordeArray.Add(cubo1);
        cubitosBordeArray.Add(cubo2);
        SetColor(cubo1, 0);
        SetColor(cubo2, 0);

        SetText(cubo1, FString::Printf(TEXT("")));
        SetText(cubo2, FString::Printf(TEXT("")));
    }
}

void ACeldaSimple::SetText(AActor* Actor, FString text) {
    UFunction* CustomEventFunction = Actor->FindFunction("SetText");
    Actor->ProcessEvent(CustomEventFunction, &text);
}

void ACeldaSimple::SetColor(AActor* Actor, int32 color) {
    UFunction* CustomEventFunction = Actor->FindFunction("SetColor");
    Actor->ProcessEvent(CustomEventFunction, &color);
}

AActor* ACeldaSimple::SpawnCubito(float x, float y, float z)
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

void ACeldaSimple::RayoCosmico(int32 i, int32 j)
{
    //UE_LOG(LogTemp, Warning, TEXT("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
    if (!grid[i][j])
    {
        grid[i][j] = true;
        SetColor(cubitosArray[i][j], 2);
    }
    iterations++;
}

bool ACeldaSimple::Helper(int32 i, int32 j)
{
    SetColor(cubitosArray[i][j], 3);
    if (i == (grid.Num() - 1))
    {
        //UE_LOG(LogTemp, Warning, TEXT("%d %d"), i, j);        
        return true;
    }

    visited[i][j] = true;
    TArray<TArray<int32>> nei = {
        {FMath::Max(0, i - 1), j},
        {FMath::Min(grid.Num() - 1, i + 1), j},
        {i, FMath::Max(0, j - 1)},
        {i, FMath::Min(grid[0].Num() - 1, j + 1)},
        {FMath::Max(0, i - 1), FMath::Max(0, j - 1)},
        {FMath::Min(grid.Num() - 1, i + 1), FMath::Min(grid[0].Num() - 1, j + 1)},
        {FMath::Max(0, i - 1), FMath::Min(grid[0].Num() - 1, j + 1)},
        {FMath::Min(grid.Num() - 1, i + 1), FMath::Max(0, j - 1)}
    };

    for (auto k : nei)
    {
        if (!visited[k[0]][k[1]] && grid[k[0]][k[1]])
        {
            if (Helper(k[0], k[1]))
            {
                return true;
            }
        }
    }
    return false;
}

bool ACeldaSimple::Cortocircuito()
{
    for (int32 i = 0; i < grid.Num(); i++)
    {
        for (int32 j = 0; j < grid[0].Num(); j++)
        {
            visited[i][j] = false;
        }
    }

    for (int32 i = 0; i < grid[0].Num(); i++)
    {
        if (grid[0][i])
        {
            if (Helper(0, i))
            {
                return true;
            }
        }
    }
    return false;
}