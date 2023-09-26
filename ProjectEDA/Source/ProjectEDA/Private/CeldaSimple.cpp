// Fill out your copyright notice in the Description page of Project Settings.


#include "CeldaSimple.h"
#include "Containers/Array.h"
#include "CoreMinimal.h"

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

void ACeldaSimple::Inicializar(int32 n)
{
    grid.Init(TArray<bool>(), n);
    visited.Init(TArray<bool>(), n);
    
    for (int i = 0; i < n; i++)
    {
        for (int j = 0; j < n; j++)
        {            
            grid[i].Add(false);
        }
    }
}

void ACeldaSimple::RayoCosmico(int32 i, int32 j)
{
    if (!grid[i][j])
    {
        grid[i][j] = true;
    }
    iterations++;
}

bool ACeldaSimple::Helper(int32 i, int32 j)
{
    if (i == (grid.Num() - 1))
    {
        UE_LOG(LogTemp, Warning, TEXT("%d %d"), i, j);
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