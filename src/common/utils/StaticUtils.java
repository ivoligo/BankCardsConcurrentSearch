package common.utils;

import common.model.Card;

import java.util.List;

public interface StaticUtils {

    static void printResult(List<Card> activeClientCards, long clientId) {

        System.out.println("\nНайдено активных карт у клиента " + clientId + ": " + activeClientCards.size());
        System.out.println("Номера карт:");
        activeClientCards.forEach(card ->
                System.out.println(card.number())
        );
    }
}
