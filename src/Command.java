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
    if ((capital_text.indexOf("CHATAPP ", 0) == 0) && (capital_text.indexOf(" USER ", 8) > 8))
      return new Command(CommandType.NICK);
    if (capital_text.equals("DISCONNECT"))
      return new Command(CommandType.DISCONNECT);
    if (capital_text.equals("ACCEPTED"))
      return new Command(CommandType.ACCEPT);
    if (capital_text.equals("REJECTED"))
      return new Command(CommandType.REJECT);
    if (capital_text.equals("MESSAGE"))
      return new Command(CommandType.MESSAGE);
    return null;
  }
  
  public CommandType getType() {
    return type;
  }
}
