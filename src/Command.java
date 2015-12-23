/**
 * Created by 81k5_Pr0g3r on 22.12.15.
 */
public class Command {
    public enum CommandType{CT_HELLO, CT_HELLO_BUSY, CT_ACCEPT, CT_REJECT, CT_MESSAGE, CT_DISCONNECT};
    private CommandType type;
    private String param;

    public Command(CommandType type, String param) {
        this.type = type;
        this.param = param;
    }

    public CommandType getType() {
        return type;
    }

    public String getParam() {
        return param;
    }
}
