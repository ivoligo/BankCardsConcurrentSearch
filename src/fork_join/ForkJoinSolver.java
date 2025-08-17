package fork_join;

import common.model.Card;
import common.model.TerritoryBank;
import common.utils.StaticUtils;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinSolver {

    private static final int startIndex = 0;

    public static void runForkJoin(List<TerritoryBank> banks, long clientId) {

        try (ForkJoinPool forkJoinPool = new ForkJoinPool(10)) {
            BankRequestTask mainTask = new BankRequestTask(banks, clientId, startIndex, banks.size());
            List<Card> activeClientCards = forkJoinPool.invoke(mainTask);

            StaticUtils.printResult(activeClientCards, clientId);
        }
    }
}
