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

void ACeldaSimple::SimularAsync(int32 n)
{
    AsyncTask(ENamedThreads::AnyBackgroundThreadNormalTask, [=]()
        {
            for (int i = 2; i < n; i++)
            {
                InicializarSim(i);
                double startSeconds = FPlatformTime::Seconds();
                while (!CortocircuitoSim()) {
                    int a = FMath::RandRange(0, i - 1);
                    int b = FMath::RandRange(0, i - 1);
                    RayoCosmicoSim(a, b);
                }
                double secondsElapsed = FPlatformTime::Seconds() - startSeconds;
                UE_LOG(LogTemp, Warning, TEXT("%i iteration executed in %f seconds."), i, secondsElapsed);

                // Notify when one simulation is done (if needed)
                if (i < n - 1)
                {
                    FGraphEventRef CompletionTask = FFunctionGraphTask::CreateAndDispatchWhenReady([&]()
                        {
                            OnSimulationCompleted(n);
                        }, TStatId(), nullptr, ENamedThreads::GameThread);
                }
            }
        });
}

void ACeldaSimple::OnSimulationCompleted(int32 SimulationIndex)
{
    // Handle the completion of a simulation
    UE_LOG(LogTemp, Warning, TEXT("Simulation %d completed."), SimulationIndex);
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
            cubitosBordeArray[2 * i]->Destroy();
            cubitosBordeArray[(2 * i) + 1]->Destroy();
            for (int j = 0; j < cubitosArray[0].Num(); j++)
            {
                cubitosArray[i][j]->Destroy();
            }
        }
    }
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
            cubitosArray[i][j] = SpawnCubito(i * space, j * space, 0, 1);
        }
    }

    cubitosBordeArray.SetNum(2 * n);
    for (int i = 0; i < n; i++)
    {
        cubitosBordeArray[2 * i] = SpawnCubito(-space, i * space, 0, 0);
        cubitosBordeArray[(2 * i) + 1] = SpawnCubito(n * space, i * space, 0, 0);
    }
}

AActor* ACeldaSimple::SpawnCubito(float x, float y, float z, int32 ColorValue)
{
    if (CubitoBlueprint.IsValid())
    {
        UWorld* const World = GetWorld();
        if (World)
        {
            FActorSpawnParameters SpawnParams;
            SpawnParams.SpawnCollisionHandlingOverride = ESpawnActorCollisionHandlingMethod::AlwaysSpawn;

            UBlueprint* LoadedBlueprint = Cast<UBlueprint>(CubitoBlueprint.Get());

            if (LoadedBlueprint)
            {
                UClass* CubitoClass = LoadedBlueprint->GeneratedClass;
                AActor* SpawnedActor = World->SpawnActor<AActor>(CubitoClass, FVector(x, y, z), FRotator(0, 0, 0), SpawnParams);
                
                if (SpawnedActor)
                {
                    UFunction* CustomEventFunction = SpawnedActor->FindFunction(FName(TEXT("SetColor")));
                    SpawnedActor->ProcessEvent(CustomEventFunction, &ColorValue);
                    return SpawnedActor;
                }
            }
        }
    }
    return nullptr;
}

void ACeldaSimple::SetColor(int i, int j, int color) {
    AActor* Actor = cubitosArray[i][j];
    UFunction* CustomEventFunction = Actor->FindFunction(FName(TEXT("SetColor")));
    int32 ColorValue = 2;
    Actor->ProcessEvent(CustomEventFunction, &color);
}

void ACeldaSimple::RayoCosmico(int32 i, int32 j)
{
    //UE_LOG(LogTemp, Warning, TEXT("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
    if (!grid[i][j])
    {
        grid[i][j] = true;

        SetColor(i, j, 2);
    }
    iterations++;
}

bool ACeldaSimple::Helper(int32 i, int32 j)
{

    SetColor(i, j, 3);
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