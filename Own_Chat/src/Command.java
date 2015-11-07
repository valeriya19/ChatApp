/**
 *
 * @author M-Sh-97
 */
class Command {
  
  private final byte code;
  private static enum CommandType {NICK, DISCONNECT, ACCEPT, REJECT, MESSAGE};
  
  protected Command(byte command_type_index) {
    code = command_type_index;
  }
  
  public static Command getCommand(String text) {
    String capital_text = text.toUpperCase();
    if ((capital_text.indexOf("CHATAPP ", 0) > -1) && (text.indexOf(" USER ", 9) > 8) && (text.indexOf("\n", 16) > 15))
      return new Command((byte) 0);
    if (capital_text.equals("DISCONNECT\n"))
      return new Command((byte) 1);
    if (capital_text.equals("ACCEPTED\n"))
      return new Command((byte) 2);
    if (capital_text.equals("REJECTED\n"))
      return new Command((byte) 3);
    if (capital_text.equals("MESSAGE\n"))
      return new Command((byte) 4);
    return null;
  }
}
