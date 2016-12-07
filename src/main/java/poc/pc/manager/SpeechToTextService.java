package poc.pc.manager;

import javax.sound.sampled.AudioInputStream;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

public class SpeechToTextService {

	private SpeechToText service;

	public SpeechToTextService() {
		service = new SpeechToText("1c38461a-eb9b-49c1-aac5-aabc1f528f48", "ZbEiouskIJYa");
	}

	public String getText(AudioInputStream audio) {
		RecognizeOptions options = new RecognizeOptions.Builder().continuous(true).interimResults(true).contentType(HttpMediaType.AUDIO_FLAC).build();

		service.recognizeUsingWebSocket(audio, options, new BaseRecognizeCallback() {

			@Override
			public void onTranscription(SpeechResults speechResults) {
				System.out.println(speechResults);
			}
		});

		return null;
	}
}
