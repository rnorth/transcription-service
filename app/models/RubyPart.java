package models;

import java.util.List;

public class RubyPart {

	public String written;
	public String furigana;

	public RubyPart(char c, char d) {
		this.written = String.valueOf(c);
		this.furigana = String.valueOf(d);
	}

	public RubyPart(List<Character> writtenForLastPart, List<Character> readingForLastPart) {
		StringBuilder sb = new StringBuilder();
		for (Character character : writtenForLastPart) {
			sb.append(character);
		}
		this.written = sb.toString();
		sb = new StringBuilder();
		
		for (Character character : readingForLastPart) {
			sb.append(character);
		}
		this.furigana = sb.toString();
	}

	public RubyPart(String written, String furigana) {
		this.written = written;
		this.furigana = furigana;
	}

	public RubyPart(String string) {
		this.written = string;
		this.furigana = string;
	}

	public Boolean isJustHiragana() {
//		return this.written.matches("[\\p{InHiragana}]+");
		return this.written.equals(this.furigana);
	}

	public void mergeIn(RubyPart thisPart) {
		this.written = this.written + thisPart.written;
		this.furigana = this.furigana + thisPart.furigana;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return written + "[" + furigana + "]";
	}
}
