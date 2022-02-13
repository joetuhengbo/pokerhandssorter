package joe;

import java.util.Objects;

public class Card implements Comparable<Card> {
  static String valOrder = "23456789TJQKA";
  public char val;
  public char suit;

  public Card(String s) {
    this.val = s.charAt(0);
    this.suit = s.charAt(1);
  }

  public char getSuit() {
    return suit;
  }

  public void setSuit(char suit) {
    this.suit = suit;
  }

  public char getVal() {
    return val;
  }

  public void setVal(char val) {
    this.val = val;
  }

  public int getValueOrder(){
    return valOrder.indexOf(this.val);
  }

  public static int getValueOrder(char c){
    return valOrder.indexOf(c);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Card card = (Card) o;
    return val == card.val;
  }

  @Override
  public int hashCode() {
    return Objects.hash(val);
  }

  @Override
  public int compareTo(Card o) {
    return this.getValueOrder() - o.getValueOrder();
  }
}
