package poc.pc.manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/speech2text")
public class Speech2TextManager {

	private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

	private Conversation conversation;
	private SpeechToTextService speechToText;

	public Speech2TextManager() {
		conversation = new Conversation();
		speechToText = new SpeechToTextService();

	}

	@OnOpen
	public void open(Session session, EndpointConfig conf) {
		sessions.add(session);

		System.out.println("opem connection!!");
	}

	@OnMessage
	public String message(String message) {
		return "{\"result\":\"" + conversation.createHelloMessage(message) + "\"}";
	}

	@OnMessage
	public String message(ByteBuffer byteBuffer) {
		String text = "";

		for (Session session : sessions) {
			try {
				text = speechToText.getText(convertByteBufferToAudioInputStream(byteBuffer));
				session.getBasicRemote().sendBinary(byteBuffer);

			} catch (IOException ex) {
				Logger.getLogger(Speech2TextManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return text;
	}

	private AudioInputStream convertByteBufferToAudioInputStream(ByteBuffer byteBuffer) {
		float frameRate = 44100f; // 44100 samples/s
		int channels = 2;
		double duration = 1.0;
		int sampleBytes = Short.SIZE / 8;
		int frameBytes = sampleBytes * channels;
		AudioFormat format = new AudioFormat(Encoding.PCM_SIGNED, frameRate, Short.SIZE, channels, frameBytes, frameRate, true);
		int nFrames = (int) Math.ceil(frameRate * duration);

		double freq = 440.0;
		// Generate all samples
		for (int i = 0; i < nFrames; ++i) {
			double value = Math.sin((double) i / (double) frameRate * freq * 2 * Math.PI) * (Short.MAX_VALUE);
			for (int c = 0; c < channels; ++c) {
				int index = (i * channels + c) * sampleBytes;
				byteBuffer.putShort(index, (short) value);
			}
		}

		return new AudioInputStream(new ByteArrayInputStream(byteBuffer.array()), format, nFrames * 2);

	}

	@OnError
	public void error(Session session, Throwable error) {
		System.out.println("error: " + error.getMessage());
	}

	@OnClose
	public void close(Session session, CloseReason reason) {
		sessions.remove(session);
		System.out.println("close: ");
	}
}
