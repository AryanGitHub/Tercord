package CommandPrompt;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ReadingCommandPrompt extends Thread {

	InputStreamReader ir;
	boolean continueReading = true;
	CommandPromptData commandPromptData = new CommandPromptData();
	public WritingCommpandPromptToDiscord writingCommpandPromptToDiscord = new WritingCommpandPromptToDiscord (commandPromptData);
	
	
	
	 
	 
	
	 public ReadingCommandPrompt (InputStream is){
		 ir = new InputStreamReader(is);
		 writingCommpandPromptToDiscord.channel = null;
	 }
	public void sendStopSignal() throws IOException, InterruptedException {
		 try {
			 sendFinalUpdate();
		 }
		 finally {
			 this.continueReading = false;
			 try {
				 
				 writingCommpandPromptToDiscord.DISCORD_MESSAGE_REFRESH_SCHEDULER.shutdown();
				 
			 }
			 finally {
				 try {
					 ir.close();
				 }
				 finally {
					 try {
						 this.stop();
					 }
					 finally {
						 this.join();
					 }
				 }
			 }
		 }
		
	}
	 
	 private void sendFinalUpdate() {
		// TODO Auto-generated method stub
		
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
	 


  public void run() {
	      //System.out.println("Hello from a thread!");
	       char ch;
	       try { 
	    	    writingCommpandPromptToDiscord.startWritingCommandPromptToDiscord();
										
					while (((ch =  (char) ir.read())!= -1 )&&continueReading) {
						commandPromptData.appendCommandPromptData(ch);
				}
			
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	   }

}

