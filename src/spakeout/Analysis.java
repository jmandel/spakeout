package spakeout;

import android.util.Log;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Shorts;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class Analysis {

	private static double MINIMUM_PITCH_HZ = 75;
	private static double MAXIMUM_PITCH_HZ = 880;

	public static PeakFrequency findPeakFrequency(short[] soundData, int samplingRateHz) {
		double[] autocorrData = autoCorrelation(soundData.clone());

		int samplesToSkip = (int) (samplingRateHz / MAXIMUM_PITCH_HZ);

		double maxSoFar = -1;
		int maxIndex = -1;
		for (int i = samplesToSkip; i < autocorrData.length - samplesToSkip; i++) {

			if (autocorrData[i] / Math.abs(maxSoFar)> 1.01 || (autocorrData[i] > maxSoFar && (i-maxIndex) <  10)) {
				maxSoFar = autocorrData[i];
				maxIndex = i;
			}

		}

		if (maxSoFar == -1) {
			System.out.println("couldn't find a single damn positival value.!");
		}

		PeakFrequency r = new PeakFrequency();
		r.frequency = samplingRateHz / maxIndex;
		r.strength = maxSoFar / autocorrData[0];
		return r;
	}

	public static double[] autoCorrelation(short[] data) {
		double[] soundData = Analysis.autoCorrelation(Doubles.toArray(Shorts.asList(data)));
		return autoCorrelation(soundData);
	}
	
	public static double[] autoCorrelation(double[] data) {
		// assume even n
		int n = data.length;

		if (n%2==1) n--;
		
		double[] ac = new double[n];

		DoubleFFT_1D fft = new DoubleFFT_1D(n);
		fft.realForward(data);
		ac[0] = sqr(data[0]);
		ac[1] = sqr(data[1]);
		for (int i = 2; i < n; i += 2) {
			ac[i] = sqr(data[i]) + sqr(data[i + 1]);
			ac[i + 1] = 0;
		}
		DoubleFFT_1D ifft = new DoubleFFT_1D(n);
		ifft.realInverse(ac, true);
		return ac;
	}

	private static double sqr(double x) {
		return x * x;
	}

	public static double rms(short[] vals){
		double total = 0;
		for (short v : vals) {
			total += Math.pow(v, 2);
		}
		return Math.pow(total/vals.length,  0.5);
	}

}