package controllers;

import play.*;
import play.libs.F.Promise;
import play.mvc.*;
import service.transcription.TranscriptionJob;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import models.*;

public class Transcription extends Controller {

	public static void transcribePost() throws InterruptedException, ExecutionException, IOException, TimeoutException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(request.body, baos);
		baos.flush();

		Promise job = new TranscriptionJob(baos.toString(request.encoding)).now();
		performProcessing(job);
	}

	public static void transcribeGet(String input) throws InterruptedException, ExecutionException, IOException, TimeoutException {

		Promise job = new TranscriptionJob(input).now();
		performProcessing(job);
	}

	private static void performProcessing(Promise job) throws InterruptedException, ExecutionException, TimeoutException {
		await(job);

		TranscriptionResult result = (TranscriptionResult) job.get(1L, TimeUnit.SECONDS);
		renderTemplate("Transcription/transcribe.html", result);
	}

}