#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "CeldaSimple.generated.h"

UCLASS()
class PROJECTEDA_API ACeldaSimple : public AActor
{
	GENERATED_BODY()

public:
	// Sets default values for this actor's properties
	ACeldaSimple();

protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;

public:
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	UFUNCTION(BlueprintCallable, Category = "C++")
		void Inicializar(int32 n);
	UFUNCTION(BlueprintCallable, Category = "C++")
		void RayoCosmico(int32 i, int32 j);
	bool Helper(int32 i, int32 j);
	UFUNCTION(BlueprintCallable, Category = "C++")
		bool Cortocircuito();
	TArray<TArray<bool>> grid;
	TArray<TArray<bool>> visited;
	TArray<TArray<AActor*>> cubitosArray;
	TArray<AActor*> cubitosBordeArray;
	int32 iterations;
	AActor* SpawnCubito(float x, float y, float z, int32 ColorValue);
	void SetColor(int i, int j, int color);

	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Cubito")
		TSoftObjectPtr<class UBlueprint> CubitoBlueprint;
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Cubito")
		float space = 100;
};