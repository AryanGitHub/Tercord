package BotFeaturesCodes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import CommandPrompt.CommandPromptProcess;
import net.dv8tion.jda.api.entities.MessageChannel;

public class CProgramRunner {
	
	public static void runCCode (String dir , String args, CommandPromptProcess cpp, MessageChannel channel) throws IOException {
		
		File f = new File (dir+"/tempfile.c");
		f.createNewFile();
		FileWriter w = new FileWriter(f);
		w.write(getCCodeFromArguments(args));
		w.flush();
		w.close();
		cpp.sendCommand("cd "+dir, channel);
		String commandToCompile = "gcc -Wall "+f.getAbsolutePath()+" -o tempfile";
		cpp.sendCommand(commandToCompile, channel);
		String commandToRunFile = "./tempfile";
		cpp.sendCommand(commandToRunFile, channel);
		
		
		
	}
	
	public static String getCCodeFromArguments (String arg) {
		String code = arg;
		code = code.substring(code.indexOf('\n')+1, code.length() - 3).trim();
		return code;
	}
	

}
