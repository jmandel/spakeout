package spakeout;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.primitives.Shorts;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class VoystickPoller implements Runnable {

	private static final int WINDOW_TIME_MS = 20;
	private static final int RECORDER_SAMPLERATE = 11025;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	static int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS,
	    RECORDER_AUDIO_ENCODING);

	private AudioRecord mRecorder = null;

	public VoystickPoller() { }

	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, RECORDER_SAMPLERATE,
		    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);

		Log.d("voystick-poller", "Poller thread running with buffer sizse: " + bufferSize);
		mRecorder.startRecording();

		int nSamples = (int) ((int) RECORDER_SAMPLERATE * WINDOW_TIME_MS / 1000); // 20 milliseconds

		short sData[] = new short[nSamples];

		@SuppressWarnings("unused")
		int i = 0;

		@SuppressWarnings("unused")
		int thisRead = 0;
		
		File sDataLog = new File("/sdcard/sdata.log");
		File voystickEventLog = new File("/sdcard/voystick-event.log");

		while (true) {
  		VoystickEvent e = new VoystickEvent();
			thisRead = mRecorder.read(sData, 0, sData.length);
			e.volume = Analysis.rms(sData);
			e.f0 = Analysis.findPeakFrequency(sData, RECORDER_SAMPLERATE);
			Voystick.fireAudioJoystickEvent(e);

//  		try {
//	      Files.append(Joiner.on('\n').join(Shorts.asList(sData))+"\n", sDataLog,  Charset.defaultCharset());
//    		Files.append(""+i*sData.length + "\t" + e.f0.frequency+'\t'+e.f0.strength+'\t'+e.volume+"\n", voystickEventLog,  Charset.defaultCharset());
//      } catch (IOException e1) {
//      	Log.d("voystick-poller","Fialed to write to file " + e1.getMessage());
//      }

			i++;
		}
	}

}
