package spakeout.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import spakeout.Analysis;
import spakeout.PeakFrequency;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Shorts;

public class AutocorrelationTest extends TestCase {

	public void assertFrequency(
			String inFileName,
			int samplingRate, 
			double correctVal) throws IOException {

		short[] soundData = loadSoundFromFile(inFileName);
		System.out.println(Joiner.on(", ").join(Shorts.asList(soundData)));


		double[] ac = Analysis.autoCorrelation(soundData.clone());
		String vals = Joiner.on("\n").join(Doubles.asList(ac));
		Files.write(vals.getBytes(), new File("/tmp/"+inFileName+".debug.dat"));

		PeakFrequency m = Analysis.findPeakFrequency(soundData, samplingRate);
		System.out.println("Peak frequency is: " + m.frequency);
		System.out.println("Peak strength: " + m.strength);
		if (correctVal > 0){ 
		assertTrue(Math.abs(correctVal - m.frequency) < 1);
		}
	}

	private short[] loadSoundFromFile(String inFileName) throws IOException {
		System.out.println("Loaindg " + inFileName);
	  URL url = this.getClass().getResource("/spakeout/test/resources/"+inFileName);
		File infile = new File(url.getFile());
		System.out.println("abs " + infile.getAbsolutePath());

		List<String> lines = Files.readLines(infile, Charset.defaultCharset());
		short[] soundData = new short[lines.size()];
		int i =0;
		for (String l : lines) {
			soundData[i] = (short) Double.parseDouble(lines.get(i));
			i++;
		}
		return soundData;
  }
	
	public void strideOver(String inFileName, int samplingRateHz, int strideMs) throws IOException {
		short[] soundData = loadSoundFromFile(inFileName);
		List<Short> soundDataList = Shorts.asList(soundData);
		
		List<String> vals = new ArrayList<String>();

		int samplesPerStride = (int) (samplingRateHz * strideMs * .001);
		if (samplesPerStride%2 ==1) {samplesPerStride++;}
		for (int i = 0; i < soundData.length - samplesPerStride; i += samplesPerStride) {
			short[] thisStride = Shorts.toArray(soundDataList.subList(i, i+samplesPerStride));
  		PeakFrequency m = Analysis.findPeakFrequency(thisStride, samplingRateHz);
  		double rms = Analysis.rms(thisStride);
  		vals.add(m.frequency + "\t"+ m.strength + "\t" + rms);
		}
		Files.write(Joiner.on('\n').join(vals).getBytes(), new File("/tmp/"+inFileName+".pitchtrack.dat"));
	}
	
	public void testSamples() throws IOException{
		//assertFrequency("100Hz", 44100, 100);
		//assertFrequency("300Hz", 44100, 300);
		//assertFrequency("eats00", 11025, -1);
		strideOver("eats00", 11025, 20);
	}

}
