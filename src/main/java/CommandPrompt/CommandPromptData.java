package CommandPrompt;

public class CommandPromptData {
	
	private StringBuilder commandPromptData ;
    private StringBuilder oldCommandPromptData ;
    
    
    public CommandPromptData () {
    	commandPromptData = new StringBuilder ();
        oldCommandPromptData = new StringBuilder ();
    }
    
    public synchronized String toStringCommandPromptData () {
		return commandPromptData.toString();
    	
    }
    
    public synchronized String toStringOldCommandPromptData () {
		return oldCommandPromptData.toString();
    	
    }
    
    public synchronized void clearCommandPromptData () {
		 commandPromptData.delete(0, commandPromptData.length());
    	
    }
    

    public synchronized void clearOldCommandPromptData () {
		 oldCommandPromptData.delete(0, oldCommandPromptData.length());
   	
   }
    

    public synchronized void appendCommandPromptData (char ch) {
		 commandPromptData.append(ch);
  	
  }
    
    public synchronized void appendOldCommandPromptData (String str) {
		 oldCommandPromptData.append(str);
 	
 }
    
}
