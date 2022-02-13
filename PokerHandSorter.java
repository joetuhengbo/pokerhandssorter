package joe;

import static java.util.Arrays.copyOfRange;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PokerHandSorter {

  static class Player implements Comparable<Player> {

    int won = 0;
    private List<Card> cards;

    public List<Card> getCards() {
      return cards;
    }

    public void setCards(List<Card> cards) {
      this.cards = cards;
    }

    public int getWon() {
      return won;
    }

    public void win() {
      this.won += 1;
    }

    @Override
    public int compareTo(Player o) {
      int result = getRoyalFlushCheckResult(this.cards) - getRoyalFlushCheckResult(o.getCards());
      if (result == 0) {
        result = getStraightFlushResult(this.cards) - getStraightFlushResult(o.getCards());
        if (result == 0) {
          result = compareResult(getFourOfaKindResult(this.cards),
              getFourOfaKindResult(o.getCards()));
          if (result == 0) {
            result = compareResult(getFullHouseResult(this.cards),
                getFullHouseResult(o.getCards()));
            if (result == 0) {
              result = compareResult(getFlushResult(this.cards), getFlushResult(o.getCards()));
              if (result == 0) {
                result = getStraightResult(this.cards) - getStraightResult(o.getCards());
                if (result == 0) {
                  result = compareResult(getThreeOfaKindResult(this.cards),
                      getThreeOfaKindResult(o.getCards()));
                  if (result == 0) {
                    //two pairs
                    result = compareResult(getPairsResult(this.cards, 3),
                        getPairsResult(o.getCards(), 3));
                    if (result == 0) {
                      //Pair
                      result = compareResult(getPairsResult(this.cards, 4),
                          getPairsResult(o.getCards(), 4));
                      if (result == 0) {
                        //Pair
                        result = compareResult(cards.stream()
                                .map(Card::getValueOrder)
                                .sorted(Comparator.reverseOrder())
                                .collect(Collectors.toList()),
                            o.getCards().stream()
                                .map(Card::getValueOrder)
                                .sorted(Comparator.reverseOrder())
                                .collect(Collectors.toList()));
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      return result;
    }
  }

  public static void main(String[] args) {

    Player player1 = new Player();
    Player player2 = new Player();
    Scanner scanner = new Scanner(System.in);

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
//    String line = "9C 9D 8D 7C 3C 2S KD TH 9H 8H";
      String[] cards = line.split(" ");

      player1.setCards(Stream.of(copyOfRange(cards, 0, cards.length / 2)).map(Card::new).sorted().collect(
              Collectors.toList()));
      player2.setCards(Stream.of(copyOfRange(cards, cards.length/2, cards.length)).map(Card::new).sorted().collect(
          Collectors.toList()));

      if (player1.compareTo(player2) > 0) player1.win(); else player2.win();

    }
    System.out.println("Player 1: "+player1.getWon());
    System.out.println("Player 2: "+player2.getWon());
  }

  public static boolean isConsecutive(List<Card> cards) {
    for (int i = 0; i < cards.size() - 1; i++) {
      if (cards.get(i).getValueOrder() != cards.get(i + 1).getValueOrder() - 1) {
        return false;
      }
    }
    return true;
  }

  static boolean isSameSuit(List<Card> cards) {
    return cards.stream().map(c -> c.getSuit()).distinct().count() == 1;
  }

  static boolean isStraightFlush(List<Card> cards) {
      return isConsecutive(cards) && isSameSuit(cards);
  }

  static int getStraightResult(List<Card> cards) {
    return isConsecutive(cards) ? cards.get(4).getValueOrder() + 1 : 0;
  }

  static int getStraightFlushResult(List<Card> cards) {
    return isStraightFlush(cards) ? cards.get(4).getValueOrder() + 1 : 0;
  }

  static int getRoyalFlushCheckResult(List<Card> cards) {
    return isStraightFlush(cards) && cards.get(0).getVal() == 'T' ? 1 : 0;
  }

  private static List<Integer> getCardValAggr(List<Card> cards, int numOfSameVal) {
    Map<Character, Long> valCount = cards.stream().map(c -> c.getVal()).collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
    if (valCount.size() == 2 && valCount.entrySet().stream().anyMatch(e->e.getValue() == numOfSameVal)) {
      return valCount.entrySet().stream()
          .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
          .map(e->Card.getValueOrder(e.getKey()))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  static List<Integer> getFourOfaKindResult(List<Card> cards) {
    return getCardValAggr(cards, 4);
  }

  static List<Integer> getFullHouseResult(List<Card> cards) {
    return getCardValAggr(cards, 3);
  }

  static List<Integer> getThreeOfaKindResult(List<Card> cards) {
    Map<Character, Long> valCount = cards.stream().map(c -> c.getVal()).collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
    if (valCount.entrySet().stream().anyMatch(e->e.getValue() == 3)) {
      return valCount.entrySet().stream()
          .sorted((e1, e2) -> {
            int result = e2.getValue().compareTo(e1.getValue());
            if (result == 0) result = Card.getValueOrder(e2.getKey()) - Card.getValueOrder(e1.getKey());
            return result;
          })
          .map(e->Card.getValueOrder(e.getKey()))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  static List<Integer> getFlushResult(List<Card> cards) {
    return isSameSuit(cards) ? cards.stream()
        .map(Card::getValueOrder)
        .sorted(Comparator.reverseOrder())
        .collect(Collectors.toList())
        : Collections.emptyList();
  }

//  static boolean isTwoPairs(List<Card> cards) {
//    return cards.stream().map(c -> c.getVal()).distinct().count() == 3;
//  }

  static List<Integer> getPairsResult(List<Card> cards, int valKind) {
    Map<Character, Long> valCount = cards.stream().map(c -> c.getVal()).collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
    if (valCount.entrySet().size() == valKind) {
      return valCount.entrySet().stream()
          .sorted((e1, e2) -> {
            int result = e2.getValue().compareTo(e1.getValue());
            if (result == 0) result = Card.getValueOrder(e2.getKey()) - Card.getValueOrder(e1.getKey());
            return result;
          })
          .map(e->Card.getValueOrder(e.getKey()))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  static int compareResult(List<Integer> result1, List<Integer> result2){
      int result = result1.size() - result2.size();
      if (result == 0) {
        for (int i = 0; i < result1.size(); i++) {
          result = result1.get(i).compareTo(result2.get(i));
          if (result != 0)
            break;
        }
      }
      return result;
  }

}
