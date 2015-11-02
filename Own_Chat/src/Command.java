/**
 *
 * @author M-Sh-97
 */
class Command {
  
  private final byte code;
  private static enum Command_Type {GREETING, FAREWELL, ACCEPTION, REJECTION, MESSAGE};
  //KATE
  //{DISCONNECT,REJECT,ACCEPT,NICK,MESSAGE} 
  protected Command(byte command_type_index) {
    code = command_type_index;
  }
  
  public static Command get_command(String text) {
    for (Command_Type ct: Command_Type.values())
      if (text.equals(ct.name()))
	return new Command((byte) ct.ordinal());
    return null;
  }
}
