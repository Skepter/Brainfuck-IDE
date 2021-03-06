package io.github.skepter.brainfuckide;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

public class BrainfuckIntegrator {

	BrainfuckEngine engine;
	String code;
	JTextArea memoryOutput;
	byte bits;
	int builderLength;
	String format;

	public BrainfuckIntegrator(BrainfuckEngine engine, String code, JTextArea memoryOutput) {
		this.engine = engine;
		this.code = code;
		this.memoryOutput = memoryOutput;
		this.bits = engine.bits;
		setBuilderLength();
		convert();
		interpret();
		debugInfo();
	}

	private void setBuilderLength() {
		switch (bits) {
			case 8:
				builderLength = 48;
				format = "%03d";
				break;
			case 16:
				builderLength = 72;
				format = "%05d";
				break;
			case 32:
				builderLength = 132;
				format = "%010d";
				break;
		}
	}

	private void convert() {
		/* Converts Ook! back to brainfuck */

		if (code.contains("Ook")) {

			String comments = code.replaceAll("(Ook)[.!?]", "");
			comments = comments.trim();
			String finalCode = "";
			finalCode = code.replace(comments, "");
			finalCode = finalCode.replace("  ", "");
			String out = "";
			List<String> arr = getParts(finalCode, 10);

			for (String s : arr) {
				s = s.trim();
				if (s.equals("Ook! Ook?"))
					out = out + "[";
				if (s.equals("Ook? Ook!"))
					out = out + "]";
				if (s.equals("Ook! Ook."))
					out = out + ".";
				if (s.equals("Ook. Ook!"))
					out = out + ",";
				if (s.equals("Ook. Ook?"))
					out = out + ">";
				if (s.equals("Ook? Ook."))
					out = out + "<";
				if (s.equals("Ook! Ook!"))
					out = out + "-";
				if (s.equals("Ook. Ook."))
					out = out + "+";
			}
			code = out;
		}
	}

	private void interpret() {
		System.out.println(code);
		try {
			engine.interpretWithoutReset(code);
		} catch (Exception e) {
		}
	}

	private void debugInfo() {
		Main.setStatusLabel(engine.dataPointer, false);

		/* Formats it into a nice grid */
		StringBuilder builder = new StringBuilder();
		for (long l : engine.data) {
			builder.append(String.format(format, l)).append(" ");
		}
		for (String part : getParts(builder.toString(), builderLength)) {
			if (!(memoryOutput.getText().equals("")))
				memoryOutput.setText(memoryOutput.getText() + "\n" + part);
			else
				memoryOutput.setText(part);
			memoryOutput.setCaretPosition(0);
		}

		Main.highlight(engine.dataPointer, bits);
	}

	private List<String> getParts(String string, int partitionSize) {
		List<String> parts = new ArrayList<String>();
		int len = string.length();
		for (int i = 0; i < len; i += partitionSize) {
			parts.add(string.substring(i, Math.min(len, i + partitionSize)));
		}
		return parts;
	}

}
