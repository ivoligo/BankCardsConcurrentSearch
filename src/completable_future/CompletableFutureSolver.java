package completable_future;

import common.exception.TimeoutGetCardException;
import common.model.Card;
import common.model.TerritoryBank;
import common.utils.StaticUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static common.constant.Constants.WAITING_TIME_IN_SECOND;

public class CompletableFutureSolver {

    public static void runCompletableFuture(List<TerritoryBank> banks, long clientId) {

        List<Card> activeClientCards = getActiveClientCards(banks, clientId);
        StaticUtils.printResult(activeClientCards, clientId);
    }

    private static List<Card> getActiveClientCards(List<TerritoryBank> banks, long clientId) {

        List<CompletableFuture<List<Card>>> futures = banks.stream()
                .map(bank -> fetchCardsAsync(bank, clientId)
//                        .orTimeout(WAITING_TIME_IN_SECOND, TimeUnit.SECONDS)
//                        .exceptionally(ex -> {
//                            System.err.println("Testing: " + ex.getMessage());
//                            return List.of();
//                        })
                )
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray((CompletableFuture[]::new)));

        try {
            allFutures.get(WAITING_TIME_IN_SECOND, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            System.err.println("Ошибка: " + ex.getMessage());
        } catch (ExecutionException | InterruptedException ex) {
            System.err.println("Ошибка: " + ex.getMessage());
            throw new RuntimeException(ex);
        }

        return futures.stream()
                .filter(future -> future.isDone() && !future.isCompletedExceptionally())
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
    }

    private static CompletableFuture<List<Card>> fetchCardsAsync(TerritoryBank bank, long clientId) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return bank.findClientCards(clientId);
            } catch (TimeoutGetCardException ex) {
                System.err.println("Ошибка: " + ex.getMessage());
                return Collections.<Card>emptyList();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return Collections.<Card>emptyList();
            }
        });
    }
}
