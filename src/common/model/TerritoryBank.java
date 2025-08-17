package common.model;

import common.constant.Constants;
import common.exception.TimeoutGetCardException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TerritoryBank {

    private long id;
    private String name;
    private final List<Client> clients;

    public TerritoryBank(long id, String name, List<Client> clients) {
        this.id = id;
        this.name = name;
        this.clients = new ArrayList<>(clients);
    }


    public List<Card> findClientCards(long clientId) throws TimeoutGetCardException, InterruptedException {

//        // @todo: сделать рандомную задержку и выкидываем исключение, если больше 60 секунд
        Random random = new Random();
        int delay = random.nextInt(100_000);
        if (delay > Constants.WAITING_TIME)
            throw new TimeoutGetCardException("Превышено время ожидания." + name + " не отвечает...");
        Thread.sleep(delay);

        return clients.stream()
                .filter(client -> client.id() == clientId)
                .findFirst()
                .map(Client::cards)
                .orElse(Collections.emptyList())
                .stream()
                .filter(card -> !card.isBlocked())
                .toList();

    }

    public void addClient(Client client) {
        clients.add(client);
    }

    public String getName() {
        return name;
    }

    public List<Client> getClients() {
        return clients;
    }
}
