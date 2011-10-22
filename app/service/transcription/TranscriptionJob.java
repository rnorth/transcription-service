package service.transcription;

import java.io.File;
import java.util.List;

import models.HiraganaRuby;
import models.KanjiRuby;
import models.KatakanaRuby;
import models.PunctuationRuby;
import models.Ruby;
import models.TranscriptionResult;
import play.Logger;
import play.jobs.Job;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.ibm.icu.text.Transliterator;

public class TranscriptionJob extends Job {
	
	private final String inputString;

	public TranscriptionJob(String inputString) {
		this.inputString = inputString;
	}

	@Override
	public Object doJobWithResult() throws Exception {
		final TranscriptionResult transcriptionResult = new TranscriptionResult();
		
		File tempDir = Files.createTempDir();
		File rawText = new File(tempDir, "raw.txt");
		File taggedTextFile = new File(tempDir, "tagged.txt");
		File commandFile = new File(tempDir, "mecab.sh");
		File stderrFile = new File(tempDir, "stderr.txt");
		stderrFile.createNewFile();
		
		Files.write(inputString.getBytes(Charsets.UTF_8), rawText);
		
		Files.write("#!/bin/bash \n" +
		            "mecab < " + rawText.getAbsolutePath() + " 1> " + taggedTextFile.getAbsolutePath() + " 2> " + stderrFile.getAbsolutePath(), 
		            commandFile, Charsets.UTF_8);
		commandFile.setExecutable(true);
		
		Logger.info("Starting job with command file %s", commandFile.getAbsolutePath());
		ProcessBuilder pb = new ProcessBuilder(commandFile.getAbsolutePath());
		Process process = pb.start();
		
		int exitCode = process.waitFor();
		if (exitCode != 0) {
			Logger.error("Job failed, exit code: %d, stderr: %s", exitCode, Files.toString(stderrFile, Charsets.UTF_8));
			throw new RuntimeException("Job failed!");
		}
		Logger.info("Mecab processing done");
		
		List<String> taggedText = Files.readLines(taggedTextFile, Charsets.UTF_8);
		
		for (String line : taggedText) {
			if (! "EOS".equals(line) ) {
				transcriptionResult.rubies.add(convertMecabTaggedLineToRuby(line));
			}
		}
		Logger.info("Parsing of Mecab output done with %d rubies found", transcriptionResult.getSize());
		
		return transcriptionResult;
	}

	private Ruby convertMecabTaggedLineToRuby(String line) {
		Logger.debug("Splitting: %s", line);
		final String[] initialSplit = line.split("\t");
		String[] secondSplit = initialSplit[1].split(",");
		
		if (secondSplit.length==7) {
			return new RomajiRuby(initialSplit[0]);
		}
		
		Transliterator tx = Transliterator.getInstance("Katakana-Hiragana");
		
		String lemma = initialSplit[0];
		String pos = secondSplit[1];
		String inflection = secondSplit[5];
		String pronunciation = secondSplit[8];
		if (lemma.equals(pronunciation)) {
			// katakana
			return new KatakanaRuby(lemma, pos, inflection, pronunciation);
		} else if (lemma.equals( tx.transliterate(pronunciation) )) {
			// hiragana
			return new HiraganaRuby(lemma, pos, inflection, pronunciation);
		} else if (pos.equals(PunctuationRuby.PUNCTUATION)){
			return new PunctuationRuby(lemma);
		} else {
			return new KanjiRuby(lemma,pos,inflection,pronunciation);
		}
	}
}
