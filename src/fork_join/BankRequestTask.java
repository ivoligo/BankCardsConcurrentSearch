package fork_join;

import common.exception.TimeoutGetCardException;
import common.model.Card;
import common.model.TerritoryBank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class BankRequestTask extends RecursiveTask<List<Card>> {

    private final List<TerritoryBank> banks;
    private final long clientId;
    private final int startIndex;
    private final int lastIndex;

    private static final int THRESHOLD = 2;

    public BankRequestTask(List<TerritoryBank> banks, long clientId, int startIndex, int lastIndex) {
        this.banks = banks;
        this.clientId = clientId;
        this.startIndex = startIndex;
        this.lastIndex = lastIndex;
    }

    @Override
    protected List<Card> compute() {

        return lastIndex - startIndex <= THRESHOLD ? processBanks() : splitAndProcess();
    }

    private List<Card> processBanks() {

        List<Card> result = new ArrayList<>();
        for(int i = startIndex; i < lastIndex; i++) {
            TerritoryBank bank = banks.get(i);
            try {
                result.addAll(bank.findClientCards(clientId));
            } catch (TimeoutGetCardException ex) {
                System.err.println("Ошибка: " + ex.getMessage());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println("Запрос прерван");
            }
        }
        return result;
    }

    private List<Card> splitAndProcess(){

        int midIndex = startIndex + (lastIndex - startIndex) / 2;

        BankRequestTask leftTask = new BankRequestTask(banks, clientId, startIndex, midIndex);
        BankRequestTask rightTask = new BankRequestTask(banks, clientId, midIndex, lastIndex);

        leftTask.fork();
        List<Card> rightResult = rightTask.compute();
        rightResult.addAll(leftTask.join());

        return rightResult;
    }
}
