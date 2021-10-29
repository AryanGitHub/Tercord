package blade_oumuamua_joint_project.run_discord_bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.security.auth.login.LoginException;

import CommandPrompt.CommandPromptProcess;
import commandHandling.Command;
import commandHandling.CommandHandler;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;

public class Bot extends ListenerAdapter
{
	static CommandPromptProcess cpp ;
    public static void main(String[] args) throws LoginException
    {

    	String Token = args[0];
    	cpp = new CommandPromptProcess(args[1]);
        JDABuilder.createLight(Token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
            .addEventListeners(new Bot())
            //.setActivity(Activity.playing("Type !ping"))
            .build();
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        MessageChannel channel = event.getChannel();
        
        
        
        
	Command startCommand = new Command("!run") {
			
			@Override
			public void action() {				
			}
		};
		
	Command ping = new Command("ping") {
			
			@Override
			public void action() {
				long time = System.currentTimeMillis();
	            channel.sendMessage("Pong!") /* => RestAction<Message> */
	                   .queue(response /* => Message */ -> {
	                       response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
	                   });
				
			}
		};
		startCommand.addSubCommand(ping);
       
	Command bashRunner = new Command("bash") {
			
			@Override
			public void action() {
				
				 try {
			        	cpp.deployCommandPrompt();
			        	//Thread.sleep(1000*100);
						cpp.sendCommand(this.arguments.get(0).trim() , channel);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
		};
       
		startCommand.addSubCommand(bashRunner);
		
		CommandHandler.HandleCommand(msg.getContentRaw(), startCommand);
     
    }
    

}
