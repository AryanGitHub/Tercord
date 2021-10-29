package commandHandling;

public class CommandHandler {
	
	private static String CommandInput;
	//private static String CommandInputForGetArgumentsMethod;
	
	public static void HandleCommand(String commandInput , Command command) {
		if (commandInput.trim().startsWith(command.token) || commandInput.trim().equals(command.token)) {
			command.arguments.add(getArguments(commandInput, command));
			command.action();
			CommandInput = commandInput.trim().substring(command.token.length());
			for (int  i = 0 ; i < command.subCommands.size() ; i++) {
				HandleCommand (CommandInput , command.subCommands.get(i));
				}
		}
	}
	
	public static String getArguments (String commandInput , Command command) {
		
		if (commandInput.trim().startsWith(command.token)) {
			commandInput = commandInput.trim().substring(command.token.length());
			for (int  i = 0 ; i < command.subCommands.size() ; i++) {
				return getArguments (commandInput , command.subCommands.get(i));
				}
		}
		return commandInput;
	}

}
