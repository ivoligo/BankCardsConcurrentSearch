package common.data;

import common.model.Card;
import common.model.Client;
import common.model.TerritoryBank;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class TestDataGenerator {

    private static final int CLIENTS_PER_BANK = 5;
    private static final List<String> CLIENT_NAMES = List.of(
            "Александр Петров", "Михаил Иванов", "Дмитрий Смирнов",
            "Сергей Кузнецов", "Андрей Попов", "Иван Васильев", "Алексей Соколов",
            "Владимир Николаев", "Евгений Фёдоров", "Роман Морозов", "Максим Волков",
            "Артем Зайцев", "Никита Соловьев", "Егор Козлов", "Даниил Новиков",
            "Павел Григорьев", "Илья Фролов", "Кирилл Беляев", "Тимофей Ковалев",
            "Роман Капустин", "Дмитрий Медведев", "Александр Колесников",
            "Михаил Осипов", "Сергей Михайлов", "Андрей Гришин", "Иван Лебедев",
            "Алексей Комаров", "Владимир Егоров", "Евгений Семёнов", "Роман Кузьмин"
    );
    private static final Map<String, Long> clientNameToIdMap = new HashMap<>();

    public static List<TerritoryBank> generateTestBanks() {
        return List.of(
                createBank(1, "Первый банк"),
                createBank(2, "Второй банк"),
                createBank(3, "Третий банк"),
                createBank(4, "Четвертый банк"),
                createBank(5, "Пятый банк"),
                createBank(6, "Шестой банк"),
                createBank(7, "Седьмой банк"),
                createBank(8, "Восьмой банк"),
                createBank(9, "Девятый банк"),
                createBank(10, "Десятый банк")
        );
    }

    /**
     * Создает банк с уникальными клиентами внутри одного банка (без повторений имен),
     * но допуская одинаковые имена между разными банками. Клиенты получают
     * случайные имена из общего пула CLIENT_NAMES.
     *
     * @param bankId   уникальный идентификатор банка (1-10)
     * @param bankName название банка (для отображения)
     * @return новый объект TerritoryBank с:
     * - Указанными ID и названием
     * - 5 клиентами с уникальными именами внутри банка
     * - Картами для каждого клиента
     * @throws IllegalArgumentException если bankId < 1
     * @see #createOrGetClient(String)
     * @see #CLIENT_NAMES
     */
    private static TerritoryBank createBank(long bankId, String bankName) {

        List<String> clientNames = new ArrayList<>(CLIENT_NAMES);
        Collections.shuffle(clientNames);
        clientNames = clientNames.subList(0, CLIENTS_PER_BANK);
        clientNames.add("Александр Петров"); // id = 367938

        List<Client> clients = clientNames.stream()
                .map(TestDataGenerator::createOrGetClient)
                .toList();

        return new TerritoryBank(bankId, bankName, clients);
    }

    /**
     * Создает или возвращает существующего клиента с гарантированно одинаковым ID
     * для повторяющихся имен. Генерирует случайное количество банковских карт (1-3)
     * для каждого клиента, где первая карта всегда активна, а остальные - случайно.
     *
     * @param clientName имя клиента (должно быть не null и содержаться в CLIENT_NAMES)
     * @return новый объект Client с:
     * - ID, привязанным к имени (через getClientId())
     * - Указанным именем
     * - Списком карт (1-3 шт)
     * @throws NullPointerException если clientName == null
     * @see #getClientId(String)
     * @see #createCard(long, int)
     */
    private static Client createOrGetClient(String clientName) {

        long clientId = getClientId(clientName);

        int cardsCount = ThreadLocalRandom.current().nextInt(1, 4);
        List<Card> cards = IntStream.rangeClosed(1, cardsCount)
                .mapToObj(cardOrdinal -> createCard(clientId, cardOrdinal))
                .toList();

        return new Client(clientId, clientName, cards);
    }

    /**
     * Возвращает ID для имени клиента.
     * Гарантирует одинаковый ID для одинаковых имен.
     *
     * @param clientName имя клиента (не null)
     * @return существующий или вновь сгенерированный ID
     */
    private static long getClientId(String clientName) {

        return clientNameToIdMap.computeIfAbsent(
                clientName,
                k -> generateClientId(clientName));
    }

    /**
     * Генерирует детерминированный ID клиента на основе хеша имени.
     * Гарантирует одинаковый ID для одинаковых имен в разных банках.
     *
     * @param name имя клиента (не null)
     * @return уникальный ID в диапазоне 100000-999999
     * @throws NullPointerException если name == null
     */
    private static long generateClientId(String name) {

        return Math.abs(name.hashCode()) % 900_000 + 100_000;
    }

    /**
     * Создает банковскую карту для указанного клиента.
     * Первая карта (cardOrdinal=1) всегда активна, последующие
     * блокируются случайным образом.
     *
     * @param clientId    ID клиента-владельца карты
     * @param cardOrdinal порядковый номер карты (начиная с 1)
     * @return объект Card с:
     * - Уникальным ID (clientId * 10 + cardOrdinal)
     * - Случайным номером карты
     * - Статусом блокировки
     */
    private static Card createCard(long clientId, int cardOrdinal) {

        return new Card(
                clientId * 10 + cardOrdinal,
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
}
