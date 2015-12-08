/**
 * @author M-Sh-97
 */
class Command {
    private final Command.CommandType type;

    static enum CommandType {NICK, DISCONNECT, ACCEPT, REJECT, MESSAGE}

    ;

    protected Command(CommandType t) {
        type = t;
    }

    public static Command getCommand(String text) {
        System.out.println(text);
        String capital_text = text.toUpperCase();
        int user_index = capital_text.indexOf(" USER ", 7);
        if ((capital_text.indexOf(Protocol.programName.toUpperCase() + " ", 0) == 0) && (user_index > 7))
            return new NickCommand(text.substring(user_index + 6, text.length() - 1));
        if (capital_text.indexOf("DISCONNECT", 0) == 0)
            return new Command(CommandType.DISCONNECT);
        if (capital_text.indexOf("ACCEPTED", 0) == 0)
            return new Command(CommandType.ACCEPT);
        if (capital_text.indexOf("REJECTED", 0) == 0)
            return new Command(CommandType.REJECT);
        if (capital_text.indexOf("MESSAGE", 0) == 0)
            return new MessageCommand("");
        return null;
    }

    public CommandType getType() {
        return type;
    }
}
