#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "CeldaSimple3D.generated.h"


UCLASS()
class PROJECTEDA_API ACeldaSimple3D : public AActor
{
	GENERATED_BODY()
protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;

public:
	// Called every frame
	virtual void Tick(float DeltaTime) override;
	// Sets default values for this actor's properties
	ACeldaSimple3D();
	UFUNCTION(BlueprintCallable, Category = "C++")
		void Inicializar(int32 n);
	UFUNCTION(BlueprintCallable, Category = "C++")
		void RayoCosmico(int32 i, int32 j, int32 k);
	UFUNCTION(BlueprintCallable, Category = "C++")
		bool Cortocircuito();
	UFUNCTION(BlueprintCallable, Category = "C++")
		void SimularAsync(int32 n);
	bool Helper(int32 i, int32 j, int32 k);
	TArray<TArray<TArray<bool>>> grid;
	TArray<TArray<TArray<bool>>> visited;
	TArray<TArray<TArray<AActor*>>> cubitosArray;
	TArray<AActor*> cubitosBordeArray;
	int32 iterations;
	AActor* SpawnCubito(float x, float y, float z);
	void SetColor(AActor* Actor, int32 color);
	void SetText(AActor* Actor, FString text);
	void InicializarSim(int32 n);
	void RayoCosmicoSim(int32 i, int32 j, int32 k);
	bool CortocircuitoSim();
	bool HelperSim(int32 i, int32 j);
	void OnSimulationCompleted(int32 SimulationIndex);

	UPROPERTY(EditAnywhere, Category = "Cubito")
		TSubclassOf<AActor> CubitoBlueprint;
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Cubito")
		float space = 100;
};