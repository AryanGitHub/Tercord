package BotFeaturesCodes;

public class testRunBotFeatures {

	public static void main(String[] args) {
		String code =  "```c\r\n"
				+ "#include <stdio.h>\r\n"
				+ "\r\n"
				+ "int main (){\r\n"
				+ "\r\n"
				+ "printf(\"%d\",-6/7);\r\n"
				+ "}\r\n"
				+ "```";
		System.out.println(CProgramRunner.getCCodeFromArguments(code));

	}

}
