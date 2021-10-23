package CommandPrompt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.dv8tion.jda.api.entities.MessageChannel;

enum ProcessState {notInitialized , initialized , deploying, idle , running , dead};

public class CommandPromptProcess {
	
	public static final int timeout = 1000*60*60; //in microseconds
	
	private String shellType;
	private ProcessBuilder pb ;
	private Process p;
	private ReadingCommandPrompt readingCommandPrompt ;
	BufferedWriter bw; //to give command to the CommandPrompt 
	private ProcessState processState = ProcessState.notInitialized; 
	
	
	
	public CommandPromptProcess (String shellType ) {
		this.shellType = shellType;
		pb = new ProcessBuilder(shellType);
		p = null;
		readingCommandPrompt = null;
		processState = ProcessState.initialized; 
	}
	
	public void deployCommandPrompt () throws IOException {
		if (p == null || !p.isAlive() || processState == ProcessState.initialized) {
		processState = ProcessState.deploying; //not much use cus its set to idle after deployed
		pb.redirectErrorStream(true);//now we will also see "errors of commandPrompt" while reading the output
		p = pb.start();
		readingCommandPrompt = new ReadingCommandPrompt(p.getInputStream());
		readingCommandPrompt.start();
		bw  = new BufferedWriter(new OutputStreamWriter (p.getOutputStream())); 
		processState = ProcessState.idle;
		}
	}
	
	public void sendCommand (String command , MessageChannel channel) throws IOException {
		//if (processState == ProcessState.idle) { this is commented because finding back when process becomes idle from running is a lot difficult to implemented. 
		//processState = ProcessState.running;
		readingCommandPrompt.setMessageChannel(channel);
		bw.write(command+"\n");
		bw.flush();
		
		//}
	}
	
	
	

}

class ReadingCommandPrompt extends Thread {

	 InputStreamReader ir;
	 MessageChannel channel;
	 public ReadingCommandPrompt (InputStream is){
		 ir = new InputStreamReader(is);
		 this.channel = null;
	 }
	 public void setMessageChannel (MessageChannel channel) {
			this.channel = channel;
		}
   public void run() {
      //System.out.println("Hello from a thread!");
       char ch;
       try { 
    	    StringBuilder sb = new StringBuilder ();
			while ((ch =  (char) ir.read())!= -1) {
				System.out.print(ch);
				sb.append(ch);
				//do the stuff here
				if (ch == '\n' || ch == '>' || ch == '$') {
				String data = sb.toString();
				if (!(data.isBlank()) && channel != null)	
				channel.sendMessage(data).queue();
				sb = new StringBuilder ();
				}
			}
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
   }



}

