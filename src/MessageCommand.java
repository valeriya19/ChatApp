/**
 * @author M-Sh-97
 */
class MessageCommand extends Command {
    private String text;

    protected MessageCommand(String messageText) {
        super(Command.CommandType.MESSAGE);
        text = messageText;
    }

    public void setMessage(String msg){ text= msg;}
    public String getMessage() {
        return text;
    }
}
