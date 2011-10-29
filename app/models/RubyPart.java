package models;

import java.util.List;

public class RubyPart {

	public String written;
	public String furigana;

	public RubyPart(String written, String furigana) {
		this.written = written;
		this.furigana = furigana;
	}

	public Boolean isJustHiragana() {
		return this.written.equals(this.furigana);
	}

	public void mergeIn(RubyPart thisPart) {
		this.written = this.written + thisPart.written;
		this.furigana = this.furigana + thisPart.furigana;
	}
	
	@Override
	public String toString() {
		return written + "[" + furigana + "]";
	}
}
