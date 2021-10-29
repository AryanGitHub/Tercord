package commandHandling;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {
	
	String token;
	List <Command> subCommands = new ArrayList <Command> ();
	public List <String> arguments = new ArrayList <String> ();
	
	public Command (String token) {
		this.token = token.trim();
	}
	
	public void addSubCommand (Command subCommand) {
		subCommands.add(subCommand);
	}

	public abstract void action (); 	
	
	

}
