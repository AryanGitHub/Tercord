package CommandPrompt;

import java.awt.Color;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class WritingCommpandPromptToDiscord {
	
	  MessageChannel channel;
	 private boolean sendNewMessage = false;// this is used to know if we need to make a new response or use the old response to keep refreshing, this value becomes false after 1st new response is sent; 
	 private String MessageID ;
	 public final ScheduledExecutorService DISCORD_MESSAGE_REFRESH_SCHEDULER = Executors.newScheduledThreadPool(1);
	 CommandPromptData commandPromptData;
	 
	 
	 
	 public WritingCommpandPromptToDiscord (CommandPromptData commandPromptData) {
		 this.commandPromptData = commandPromptData;
		 
	 }
	 
	 
	 Runnable runner = new Runnable() {
			
			@Override
			public void run() {
				
				String data = commandPromptData.toStringCommandPromptData();
				if (!(data.isBlank()) && channel != null)	{
				
				
				
				if (sendNewMessage) {
					
					EmbedBuilder eb = makeEmbed();
					System.out.println(data);
					eb.setDescription(data);
					MessageID = channel.sendMessage(eb.build()).complete().getId();					
					commandPromptData.clearOldCommandPromptData();
					
					sendNewMessage = false;
					
				}
				else {
					EmbedBuilder eb = makeEmbed();
					eb.setDescription(commandPromptData.toStringOldCommandPromptData()+commandPromptData.toStringCommandPromptData());
					channel.editMessageById(MessageID, eb.build()).queue();
				}
				
				
				commandPromptData.appendOldCommandPromptData(commandPromptData.toStringCommandPromptData());
				commandPromptData.clearCommandPromptData();
				}

				
			}
		};
		
	 
	 
	 
	 public void setMessageChannel (MessageChannel channel) {
			this.channel = channel;
		}

	 
	 public void setNewMessage (boolean value) {
			this.sendNewMessage = value;
		}
	 
	 public void setMessageID (String value) {
			this.MessageID = value;
		}
	 
	 public static EmbedBuilder makeEmbed () {
		 EmbedBuilder eb = new EmbedBuilder();
		// eb.setAuthor("Tercord","https://github.com/AryanGitHub/Tercord");
		 eb.setTitle("Output: ");
		 EmbedRandomColorGeneratoer(eb);
		 return eb;
	 }
	
	 private static EmbedBuilder EmbedRandomColorGeneratoer (EmbedBuilder eb) {
		 Color [] colors= {Color.yellow,Color.black,Color.blue, Color.cyan, Color.darkGray , Color.gray , Color.green , Color.lightGray , Color.magenta, Color.orange , Color.pink , Color.red , Color.white};
		 eb.setColor(colors [0+(int)Math.floor(Math.random()*colors.length)]);
		 return eb;
	 }
	 
	 public void startWritingCommandPromptToDiscord () {
		 DISCORD_MESSAGE_REFRESH_SCHEDULER.scheduleWithFixedDelay(runner, 0, CommandPromptProcess.REFRESH_DISCORD_MESSAGE_DELAY_TIME, TimeUnit.MILLISECONDS);
	 }
	 

}
