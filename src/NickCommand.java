/**
 *
 * @author M-Sh-97
 */
class NickCommand extends Command {
  private final String nick;
  private final boolean busy;
  
  protected NickCommand(String nickInfo) {
    super(Command.CommandType.NICK);
    nick = nickInfo.substring(0, nickInfo.indexOf(" "));
    busy = nickInfo.contains(" busy");
  }
  
  public String getNick() {
    return nick;
  }
  
  public boolean getBusyStatus() {
    return busy;
  }
}
