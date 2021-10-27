package CommandPrompt;

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
		readingCommandPrompt.writingCommpandPromptToDiscord.setNewMessage(true);
		readingCommandPrompt.writingCommpandPromptToDiscord.setMessageChannel(channel);
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
