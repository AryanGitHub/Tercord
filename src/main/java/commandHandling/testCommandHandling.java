package commandHandling;

public class testCommandHandling {

	public static void main(String[] args) {
		Command printHello = new Command("!run") {
			
			@Override
			public void action() {
				System.out.println("Hello world.");
				
			}
		};
		
Command printSubArguments = new Command("c") {
			
			@Override
			public void action() {
				System.out.println("subcommand "+ this.arguments.get(0));
				
			}
		};
		
		
		printHello.subCommands.add(printSubArguments);
		
		
		
		
		CommandHandler.HandleCommand("!run c | 4 2\r\n"
				+ "```c\r\n"
				+ "#include <stdio.h>\r\n"
				+ "\r\n"
				+ "int main (){\r\n"
				+ "\r\n"
				+ "printf(\"%d\",-6/7);\r\n"
				+ "}\r\n"
				+ "```", printHello);
		
		

	}

}
