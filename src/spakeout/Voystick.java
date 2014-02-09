package spakeout;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class Voystick {

	static List<Voystickable> subscribers = new ArrayList<Voystickable>();
	private static Handler mHandler;

	static {

		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message inputMessage) {
				for (Voystickable sub : subscribers) {
					sub.onVoystickEvent((VoystickEvent) inputMessage.obj);
				}
			}
		};

		new Thread(new VoystickPoller()).start();
	}

	public static void unsubscribe(Voystickable sub) {
		subscribers.remove(sub);
	}

	public static void subscribe(Voystickable sub) {
		if (!subscribers.contains(sub)) {
  		subscribers.add(sub);
		}
	}

	public static void fireAudioJoystickEvent(VoystickEvent e) {
		mHandler.obtainMessage(0, e).sendToTarget();
	}

}