#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "CeldaSimple.generated.h"

UCLASS()
class PROJECTEDA_API ACeldaSimple : public AActor
{
	GENERATED_BODY()
protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;

public:
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	// Sets default values for this actor's properties
	ACeldaSimple();
	UFUNCTION(BlueprintCallable, Category = "C++")
		void Inicializar(int32 n);
	UFUNCTION(BlueprintCallable, Category = "C++")
		void RayoCosmico(int32 i, int32 j);	
	UFUNCTION(BlueprintCallable, Category = "C++")
		bool Cortocircuito();
	UFUNCTION(BlueprintCallable, Category = "C++")
		void SimularAsync(int32 n);
	bool Helper(int32 i, int32 j);
	TArray<TArray<bool>> grid;
	TArray<TArray<bool>> visited;
	TArray<TArray<AActor*>> cubitosArray;
	TArray<AActor*> cubitosBordeArray;
	int32 iterations;
	AActor* SpawnCubito(float x, float y, float z, int32 ColorValue);
	void SetColor(int i, int j, int color);
	void InicializarSim(int32 n);
	void RayoCosmicoSim(int32 i, int32 j);
	bool CortocircuitoSim();
	bool HelperSim(int32 i, int32 j);
	void OnSimulationCompleted(int32 SimulationIndex);
	//void RunSimulation(int32 n);

	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Cubito")
		TSoftObjectPtr<class UBlueprint> CubitoBlueprint;
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Cubito")
		float space = 100;
};