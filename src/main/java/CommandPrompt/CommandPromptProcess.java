package CommandPrompt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.dv8tion.jda.api.entities.MessageChannel;

public class CommandPromptProcess {
	
	public static final int timeout = 1000*60*60; //in microseconds
	
	private String shellType;
	private ProcessBuilder pb ;
	private Process p;
	private ReadingCommandPrompt readingCommandPrompt ;
	BufferedWriter bw; //to give command to the CommandPrompt 
	
	
	
	public CommandPromptProcess (String shellType ) {
		this.shellType = shellType;
		pb = new ProcessBuilder(shellType);
		p = null;
		readingCommandPrompt = null;
		
	}
	
	public void deployCommandPrompt () throws IOException {
		if (p == null || !p.isAlive()) {
		p = pb.start();
		readingCommandPrompt = new ReadingCommandPrompt(p.getInputStream());
		readingCommandPrompt.start();
		bw  = new BufferedWriter(new OutputStreamWriter (p.getOutputStream())); 
		}
	}
	
	public void sendCommand (String command , MessageChannel channel) throws IOException {
		readingCommandPrompt.setMessageChannel(channel);
		bw.write(command+"\n");
		bw.flush();
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
				if (ch == '\n' || ch == '>') {
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

