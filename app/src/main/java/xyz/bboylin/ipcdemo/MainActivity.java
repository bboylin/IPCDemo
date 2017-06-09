package xyz.bboylin.ipcdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static xyz.bboylin.ipcdemo.Constants.CLIENT_HELLO;
import static xyz.bboylin.ipcdemo.Constants.SERVER_HELLO;

public class MainActivity extends AppCompatActivity {
    private EditText etId, etName;
    private TextView tvBookList;
    private Button btnAddBook, btnMessenger, btnAIDL, btnSendMessage;
    private IBookManager bookManager = null;
    private List<Book> books;
    private Messenger serverMessenger = null, clientMessenger = null;
    private boolean boundAIDL = false, boundMessenger = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVER_HELLO:
                    Toast.makeText(MyApp.getContext(), "收到server hello", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        etId = (EditText) findViewById(R.id.et_book_id);
        etName = (EditText) findViewById(R.id.et_book_name);
        tvBookList = (TextView) findViewById(R.id.tv_book_list);
        btnAddBook = (Button) findViewById(R.id.btn_add_book);
        btnMessenger = (Button) findViewById(R.id.btn_connect_messenger);
        btnAIDL = (Button) findViewById(R.id.btn_connect_aidl);
        btnSendMessage = (Button) findViewById(R.id.btn_send_message);
        btnMessenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serverMessenger == null) {
                    Intent intent = new Intent(MainActivity.this, MessengerService.class);
                    bindService(intent, mMessengerConnection, BIND_AUTO_CREATE);
                }
            }
        });
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!boundMessenger) {
                    Toast.makeText(MainActivity.this, "请先连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                Message message = Message.obtain(null, CLIENT_HELLO, 0, 0);
                //将客户端messenger传递给server
                message.replyTo = clientMessenger;
                try {
                    serverMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        btnAIDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookManager == null) {
                    Intent intent = new Intent(MainActivity.this, AIDLService.class);
                    bindService(intent, mAIDLConnection, Context.BIND_AUTO_CREATE);
                }
            }
        });
        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!boundAIDL) {
                    Toast.makeText(MainActivity.this, "请先连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    bookManager.addBook(new Book(Integer.parseInt(etId.getText().toString()), etName.getText().toString()));
                    books = bookManager.getBooks();
                    tvBookList.setText(books.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ServiceConnection mAIDLConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookManager = IBookManager.Stub.asInterface(service);
            boundAIDL = true;
            try {
                books = bookManager.getBooks();
                tvBookList.setText(books.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            tvBookList.setText("连接关闭");
            bookManager = null;
            boundAIDL = false;
        }
    };

    private ServiceConnection mMessengerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serverMessenger = new Messenger(service);
            clientMessenger = new Messenger(handler);
            boundMessenger = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serverMessenger = null;
            boundMessenger = false;
        }
    };
}
