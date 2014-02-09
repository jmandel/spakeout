package spakeout;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class VoystickPoller implements Runnable {

	private static final int RECORDER_SAMPLERATE = 44100;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	static int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS,
	    RECORDER_AUDIO_ENCODING);

	private AudioRecord mRecorder = null;

	public VoystickPoller() { }

	public void run() {
		
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, RECORDER_SAMPLERATE,
		    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);

		Log.d("voystick-poller", "Poller thread running with buffer sizse: " + bufferSize);
		mRecorder.startRecording();

		int nSamples = (int) ((int) RECORDER_SAMPLERATE * .02); // 20 milliseconds

		short sData[] = new short[nSamples];

		@SuppressWarnings("unused")
		int i = 0;

		@SuppressWarnings("unused")
		int thisRead = 0;

		while (true) {
			i++;
			thisRead = mRecorder.read(sData, 0, sData.length);
			VoystickEvent e = new VoystickEvent();
			e.volume = Analysis.rms(sData);
			e.f0 = Analysis.findPeakFrequency(sData, RECORDER_SAMPLERATE);
			Voystick.fireAudioJoystickEvent(e);
		}

	}

}
