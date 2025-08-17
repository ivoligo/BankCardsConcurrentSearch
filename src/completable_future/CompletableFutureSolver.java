package completable_future;

import common.exception.TimeoutGetCardException;
import common.model.Card;
import common.model.TerritoryBank;
import common.utils.StaticUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureSolver {

    public static void runCompletableFuture(List<TerritoryBank> banks, long clientId) {

        List<Card> activeClientCards = getActiveClientCards(banks, clientId);
        StaticUtils.printResult(activeClientCards, clientId);
    }

    private static List<Card> getActiveClientCards(List<TerritoryBank> banks, long clientId) {

        return banks.stream()
                .map(bank -> fetchCardsAsync(bank, clientId))
                .toList()
                .stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
    }

    private static CompletableFuture<List<Card>> fetchCardsAsync(TerritoryBank bank, long clientId) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return bank.findClientCards(clientId);
            } catch (TimeoutGetCardException e) {
                return Collections.<Card>emptyList();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Collections.<Card>emptyList();
            }
        });
    }
}
