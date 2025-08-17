import common.data.TestDataGenerator;
import common.model.TerritoryBank;
import completable_future.CompletableFutureSolver;
import executor_service.ExecutorServiceSolver;

import java.util.List;

import static common.constant.Constants.TARGET_CLIENT_ID;

public class Main {

    public static void main(String[] args) {

        TestDataGenerator.printClientIdMappings();
        List<TerritoryBank> banks = TestDataGenerator.generateTestBanks();

        System.out.println("Executor Service:");
        ExecutorServiceSolver.runExecutiveService(banks, TARGET_CLIENT_ID);

        System.out.println("\nCompletableFuture:");
        CompletableFutureSolver.runCompletableFuture(banks, TARGET_CLIENT_ID);
    }
}