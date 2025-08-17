package executor_service;

import common.model.Card;
import common.model.TerritoryBank;
import common.utils.StaticUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceSolver {

    public static void runExecutiveService(List<TerritoryBank> banks, long clientId) {

        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            List<Future<List<Card>>> futures = new ArrayList<>();

            for (TerritoryBank bank : banks) {
                futures.add(executor.submit(() ->
                        bank.findClientCards(clientId)));
            }

            List<Card> activeClientCards = new ArrayList<>();
            for (Future<List<Card>> future : futures) {
                try {
                    activeClientCards.addAll(future.get());
                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }

            StaticUtils.printResult(activeClientCards, clientId);
        }
    }
}
