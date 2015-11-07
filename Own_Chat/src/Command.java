/**
 *
 * @author M-Sh-97
 */
class Command {
  
  private final byte code;
  private static enum CommandType {NICK, DISCONNECT, ACCEPT, REJECT, MESSAGE};
  
  protected Command(byte commandTypeIndex) {
    code = commandTypeIndex;
  }
  
  public static Command getCommand(String text) {
    String capital_text = text.toUpperCase();
    if ((capital_text.indexOf("CHATAPP ", 0) > -1) && (text.indexOf(" USER ", 9) > 8) && (text.indexOf("\n", 16) > 15))
      return new Command((byte) CommandType.NICK.ordinal());
    if (capital_text.equals("DISCONNECT\n"))
      return new Command((byte) CommandType.DISCONNECT.ordinal());
    if (capital_text.equals("ACCEPTED\n"))
      return new Command((byte) CommandType.ACCEPT.ordinal());
    if (capital_text.equals("REJECTED\n"))
      return new Command((byte) CommandType.REJECT.ordinal());
    if (capital_text.equals("MESSAGE\n"))
      return new Command((byte) CommandType.MESSAGE.ordinal());
    return null;
  }
  
  public CommandType getType() {
    return CommandType.values()[code];
  }
}
