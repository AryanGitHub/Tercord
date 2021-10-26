package CommandPrompt;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

enum ProcessState {notInitialized , initialized , deploying, idle , running , dead};

public class CommandPromptProcess {
	
	public static final int TIMEOUT_TIME = 10*1000; //in milliseconds
	public static final int REFRESH_DISCORD_MESSAGE_DELAY_TIME = 2*1000; //in milliseconds
	
	private String shellType;
	private ProcessBuilder pb ;
	private Process p;
	private ReadingCommandPrompt readingCommandPrompt ;
	BufferedWriter bw; //to give command to the CommandPrompt 
	private ProcessState processState = ProcessState.notInitialized; 
	private final  ExecutorService TIMEOUT_EXECUTION = Executors.newSingleThreadExecutor();
	
	
	
	public CommandPromptProcess (String shellType ) {
		this.shellType = shellType;
		pb = new ProcessBuilder(shellType);
		p = null;
		readingCommandPrompt = null;
		processState = ProcessState.initialized; 
	}
	
	public void deployCommandPrompt () throws IOException {
		if (/*p == null || !p.isAlive() ||*/ processState == ProcessState.initialized) {
		processState = ProcessState.deploying; //not much use cus its set to idle after deployed
		pb.redirectErrorStream(true);//now we will also see "errors of commandPrompt" while reading the output
		p = pb.start();
		readingCommandPrompt = new ReadingCommandPrompt(p.getInputStream());
		readingCommandPrompt.start();
		bw  = new BufferedWriter(new OutputStreamWriter (p.getOutputStream()));
		this.setAutoKillTimeout(CommandPromptProcess.TIMEOUT_TIME);
		processState = ProcessState.idle;
		}
	}
	
	
	public void killCommandPromptProcess () throws IOException, InterruptedException  {
		if (processState == ProcessState.idle || processState == ProcessState.deploying || processState == ProcessState.running) {
			try {
				bw.close();
			}
			finally {
				try {
					readingCommandPrompt.sendStopSignal();
				}
				finally {
						p.destroy();
						p.waitFor();
						processState = ProcessState.dead;
				}
			}
		}
	}
	
	public void sendCommand (String command , MessageChannel channel) throws IOException {
	
		if (processState == ProcessState.idle ||  processState == ProcessState.running) { 
		readingCommandPrompt.setNewMessage(true);
		readingCommandPrompt.setMessageChannel(channel);
		bw.write(command+"\n");
		bw.flush();
		
		}
		else {
			System.out.println("process is not idle or running...");
		}
	}
	
	private void setAutoKillTimeout(int milliseconds) {
		Runnable timeoutThread = new Runnable () {

			@Override
			public void run() {
				
				try {
					
					if(!p.waitFor(milliseconds, TimeUnit.MILLISECONDS)) {
					    //timeout - kill the process. 
					    killCommandPromptProcess(); 
					    System.out.println("process Autokilled");
					}
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
			}
			
		};
		
		
		this.TIMEOUT_EXECUTION.execute(timeoutThread);
	}
	
	
	

}

class ReadingCommandPrompt extends Thread {

	 InputStreamReader ir;
	 MessageChannel channel;
	 boolean sendNewMessage = false;// this is used to know if we need to make a new response or use the old response to keep refreshing, this value becomes false after 1st new response is sent; 
	 boolean continueReading = true;
	 String MessageID ;
	 final ScheduledExecutorService DISCORD_MESSAGE_REFRESH_SCHEDULER =
    	     Executors.newScheduledThreadPool(1);
	 
	 
	 public ReadingCommandPrompt (InputStream is){
		 ir = new InputStreamReader(is);
		 this.channel = null;
	 }
	 
	 public void setMessageChannel (MessageChannel channel) {
			this.channel = channel;
		}
	 
	 public void setNewMessage (boolean value) {
			this.sendNewMessage = value;
		}
	 
	 public void setMessageID (String value) {
			this.MessageID = value;
		}
	 
	 public void sendStopSignal() throws IOException, InterruptedException {
		 try {
			 sendFinalUpdate();
		 }
		 finally {
			 this.continueReading = false;
			 try {
				 DISCORD_MESSAGE_REFRESH_SCHEDULER.shutdown();
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
	 
/*   public void runOLD() {// working run method
      //System.out.println("Hello from a thread!");
       char ch;
       try { 
    	    StringBuilder sb = new StringBuilder ();
    	    StringBuilder oldDatasb = new StringBuilder ();
			while ((ch =  (char) ir.read())!= -1) {
				System.out.print(ch);
				sb.append(ch);
				
				if (ch == '\n' || ch == '>' || ch == '$') {
				String data = sb.toString();
				if (!(data.isBlank()) && channel != null)	{
				
				
				
				if (this.sendNewMessage) {
					
					EmbedBuilder eb = makeEmbed();
					eb.setDescription(data);
				
					this.MessageID = channel.sendMessage(eb.build()).complete().getId();
					
					
					
					oldDatasb = new StringBuilder();
					
					this.sendNewMessage = false;
					
				}
				else {
					EmbedBuilder eb = makeEmbed();
					eb.setDescription(oldDatasb.toString()+sb.toString());
					channel.editMessageById(this.MessageID, eb.build()).queue();
				}
				
				}
				oldDatasb.append(sb.toString());
				sb = new StringBuilder ();
				}
				
				
				
			}
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
   }
   
   */

   public void run() {// finally this is also working, runOLD is old way of printing without having separate Thread.
	      //System.out.println("Hello from a thread!");
	       char ch;
	       try { 
	    	    StringBuilder sb = new StringBuilder ();
	    	    StringBuilder oldDatasb = new StringBuilder ();
	    	   
	    	    
		Runnable runner = new Runnable() {
			
			@Override
			public void run() {
				
				String data = sb.toString();
				if (!(data.isBlank()) && channel != null)	{
				
				
				
				if (sendNewMessage) {
					
					EmbedBuilder eb = makeEmbed();
					System.out.println(data);
					eb.setDescription(data);
					MessageID = channel.sendMessage(eb.build()).complete().getId();					
					oldDatasb.delete(0, oldDatasb.length());
					
					sendNewMessage = false;
					
				}
				else {
					EmbedBuilder eb = makeEmbed();
					eb.setDescription(oldDatasb.toString()+sb.toString());
					channel.editMessageById(MessageID, eb.build()).queue();
				}
				
				
				oldDatasb.append(sb.toString());
				sb.delete(0, sb.length());
				}

				
			}
		};
					
					
		DISCORD_MESSAGE_REFRESH_SCHEDULER.scheduleWithFixedDelay(runner, 0, CommandPromptProcess.REFRESH_DISCORD_MESSAGE_DELAY_TIME, TimeUnit.MILLISECONDS);
										
					while (((ch =  (char) ir.read())!= -1 )&&continueReading) {
						sb.append(ch);
				}
			
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	   }

}

