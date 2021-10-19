package blade_oumuamua_joint_project.run_discord_bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.security.auth.login.LoginException;

import CommandPrompt.CommandPromptProcess;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot extends ListenerAdapter
{
	CommandPromptProcess cpp = new CommandPromptProcess("cmd");
    public static void main(String[] args) throws LoginException
    {

    	String Token = args[0];
    	
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
        if (msg.getContentRaw().equals("!ping"))
        {
            
            long time = System.currentTimeMillis();
            channel.sendMessage("Pong!") /* => RestAction<Message> */
                   .queue(response /* => Message */ -> {
                       response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                   });
        }
        
        if (msg.getContentRaw().contains("!run"))
        {
           
          
           try {
        	cpp.deployCommandPrompt();
        	//Thread.sleep(1000*100);
			cpp.sendCommand(msg.getContentRaw().substring(4).trim(), channel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           
        }
    }
    
    public void runBash (MessageReceivedEvent event) throws IOException {
    	Message msg = event.getMessage();
    	MessageChannel channel = event.getChannel();
    	String command = msg.getContentRaw().substring(4).strip();
    	int totalOutputCharacters = 0;
    	Process p = Runtime.getRuntime().exec("cmd "+command);  
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
        String line = null;  
        System.out.println ("we are here");
        while ((line = in.readLine()) != null) {  
        	if (line.length() > 0)
            channel.sendMessage(line).queue();
            System.out.println (line);
        }  
        
    }
}
