import common.constant.Constants;
import common.data.TestDataGenerator;
import common.exception.TimeoutGetCardException;
import common.model.Card;
import common.model.TerritoryBank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class Main {
    private static final int TIMEOUT_SEC = 60;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long clientId = 367938;
        List<TerritoryBank> banks = TestDataGenerator.generateTestBanks();

        TestDataGenerator.printClientIdMappings();
        banks.forEach(bank -> {
            try {
                System.out.println(bank.getName() + " " + bank.findClientCards(clientId));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }
}