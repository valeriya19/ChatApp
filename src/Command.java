/**
 *
 * @author M-Sh-97
 */
class Command {
  
  private final Command.CommandType type;
  static enum CommandType {NICK, DISCONNECT, ACCEPT, REJECT, MESSAGE};
  
  protected Command(CommandType t) {
    type = t;
  }
  
  public static Command getCommand(String text) {
    String capital_text = text.toUpperCase();
    if ((capital_text.indexOf("CHATAPP ", 0) == 0) && (capital_text.indexOf(" USER ", 8) > 8) && (capital_text.lastIndexOf("\n") == text.length() - 1))
      return new Command(CommandType.NICK);
    if (capital_text.equals("DISCONNECT\n"))
      return new Command(CommandType.DISCONNECT);
    if (capital_text.equals("ACCEPTED\n"))
      return new Command(CommandType.ACCEPT);
    if (capital_text.equals("REJECTED\n"))
      return new Command(CommandType.REJECT);
    if (capital_text.equals("MESSAGE\n"))
      return new Command(CommandType.MESSAGE);
    return null;
  }
  
  public CommandType getType() {
    return type;
  }
}
