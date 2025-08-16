package common.data;

import common.model.Card;
import common.model.Client;
import common.model.TerritoryBank;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class TestDataGenerator {

    private static final List<String> CLIENT_NAMES = List.of(
            "Александр Петров", "Михаил Иванов", "Дмитрий Смирнов",
            "Сергей Кузнецов", "Андрей Попов", "Иван Васильев", "Алексей Соколов"
    );
    private static final Map<String, Long> CLIENT_NAME_TO_ID = new HashMap<>();

    static {

        CLIENT_NAMES.forEach(name ->
                CLIENT_NAME_TO_ID.put(name, Math.abs(name.hashCode()) % 900_000L + 100_000L));
    }

    public static List<TerritoryBank> generateTestBanks() {

        List<String> shuffledNames = new ArrayList<>(CLIENT_NAMES);
        Collections.shuffle(shuffledNames);

        return IntStream.rangeClosed(1, 10)
                .mapToObj(bankId -> createBank(bankId, "Банк №" + bankId, shuffledNames))
                .toList();
    }


    private static TerritoryBank createBank(long bankId, String bankName, List<String> allNames) {

        int clientsCount = ThreadLocalRandom.current().nextInt(3, 6);
        List<Client> clients = new ArrayList<>();

        for (int i = 0; i < clientsCount && !allNames.isEmpty(); i++) {
            String name = allNames.removeFirst();
            clients.add(createClient(name));

            if (allNames.isEmpty()) {
                allNames.addAll(CLIENT_NAMES);
                Collections.shuffle(allNames);
            }
        }

        return new TerritoryBank(bankId, bankName, clients);
    }


    private static Client createClient(String name) {

        long clientId = CLIENT_NAME_TO_ID.get(name);
        int cardsCount = ThreadLocalRandom.current().nextInt(1, 4); // 1-3 карты

        List<Card> cards = IntStream.rangeClosed(1, cardsCount)
                .mapToObj(cardOrdinal -> createCard(clientId, cardOrdinal))
                .toList();

        return new Client(clientId, name, cards);
    }


    private static Card createCard(long clientId, int cardOrdinal) {

        return new Card(
                clientId * 10L + cardOrdinal,
                generateCardNumber(),
                cardOrdinal > 1 && ThreadLocalRandom.current().nextBoolean()
        );
    }

    /**
     * Генерирует номер банковской карты.
     *
     * @return номер карты в формате "XXXX XXXX XXXX XXXX",
     * где X - цифра от 0 до 9
     */
    private static String generateCardNumber() {

        return String.format("%04d %04d %04d %04d",
                ThreadLocalRandom.current().nextInt(1000, 9999),
                ThreadLocalRandom.current().nextInt(1000, 9999),
                ThreadLocalRandom.current().nextInt(1000, 9999),
                ThreadLocalRandom.current().nextInt(1000, 9999));
    }

    /**
     * Печатает фиксированные соответствия имен и ID
     */
    public static void printClientIdMappings() {

        System.out.println("=== Фиксированные ID клиентов ===");
        CLIENT_NAME_TO_ID.forEach((name, id) ->
                System.out.printf("%-18s → ID: %d%n", name, id));
    }
}
