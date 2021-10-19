package blade_oumuamua_joint_project.run_discord_bot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class runningCMD {
/*
 * this file has nothing to do with bot building, its a code we made to run cmd terminal from java
 * run and enjoy
 */
	public static void main(String[] args) throws IOException {
	
		ProcessBuilder pb = new ProcessBuilder("cmd");
		Process p = pb.start();
		Scanner scn = new Scanner (System.in);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (p.getOutputStream()));
		//InputStreamReader ir = new InputStreamReader(p.getInputStream());
		
		Thread reading = new readingCMD(p.getInputStream());
		try {
		
		reading.start();
		while (true) {
		String command = scn.nextLine()+"\n";
		if (command.trim().equalsIgnoreCase("EXITNOW")) 
			break;
		bw.write(command);
		bw.flush();

		}
		}
		finally {
		p.destroy();
		reading.stop();
		}

	}

}


 class readingCMD extends Thread {

	 InputStreamReader ir;
	 public readingCMD (InputStream is){
		 ir = new InputStreamReader(is);
	 }
    public void run() {
       //System.out.println("Hello from a thread!");
        char ch;
        try {
			while ((ch = (char) ir.read())!= -1) {
				System.out.print(ch);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }



}
