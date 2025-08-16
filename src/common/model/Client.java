package common.model;

import java.util.List;

public record Client(long id, String name, List<Card> cards) {

}
