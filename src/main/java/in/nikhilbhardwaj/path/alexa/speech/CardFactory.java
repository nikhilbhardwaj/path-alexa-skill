package in.nikhilbhardwaj.path.alexa.speech;

import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.StandardCard;

public class CardFactory {
  
  public Card repromptCard(final String responseText) {
    StandardCard standardCard = new StandardCard();
    standardCard.setTitle("Where do you want to go?");
    standardCard.setText(responseText);
    return standardCard;
  }

  public Card tripsResponseCard(String responseText) {
    StandardCard standardCard = new StandardCard();
    standardCard.setTitle("Here are your train timings.");
    standardCard.setText(responseText);
    return standardCard;
  }

  public Card helpCard(String speechText) {
    SimpleCard card = new SimpleCard();
    card.setTitle("Welcome to Path Tracker!");
    card.setContent(speechText);
    return card;
  }

}
