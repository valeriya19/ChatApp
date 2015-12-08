import java.util.Date;
import java.util.Observable;
import java.util.Vector;

/**
 *
 * @author M-Sh-97
 */
class HistoryModel extends Observable {
  private Vector<Message> messages;
  
  static class Message {
    private String nick, text;
    private Date date;
    
    public Message(String nick, Date date, String text) {
      this.nick = nick;
      this.date = date;
      this.text = text;
    }
    
    public String getNick() {
      return nick;
    }
    
    public Date getDate() {
      return date;
    }
    
    public String getText() {
      return text;
    }
  }
  
  public HistoryModel() {
    messages = new Vector<Message>(0);
  }
  
  public void addMessage(String nick, Date date, String text) {
    messages.add(new Message(nick, date, text));
    notifyObservers(messages);
  }
  
  public void clear() {
    messages.clear();
    notifyObservers(messages);
  }
  
  public Message getMessage(int pos) {
    return messages.get(pos);
  }
  
  public int getSize() {
    return messages.size();
  }
}
