package xyz.bboylin.ipcdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import static xyz.bboylin.ipcdemo.Constants.CLIENT_HELLO;
import static xyz.bboylin.ipcdemo.Constants.SERVER_HELLO;

public class MessengerService extends Service {
    public final String TAG = this.getClass().getSimpleName();
    public final Messenger messenger = new Messenger(new ServiceHandler());

    public static final class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLIENT_HELLO:
                    Toast.makeText(MyApp.getContext(), "收到client hello", Toast.LENGTH_SHORT).show();
                    Messenger clientMessenger = msg.replyTo;
                    try {
                        clientMessenger.send(Message.obtain(null, SERVER_HELLO, 0, 0));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "bound");
        return messenger.getBinder();
    }
}
