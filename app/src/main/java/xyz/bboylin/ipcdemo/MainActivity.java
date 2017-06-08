package xyz.bboylin.ipcdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText etId, etName;
    private TextView tvBookList;
    private Button btnAddBook, btnConnect;
    private IBookManager bookManager = null;
    private List<Book> books;

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
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AIDLService.class);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }
        });
        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookManager == null) {
                    Intent intent = new Intent(MainActivity.this, AIDLService.class);
                    bindService(intent, connection, Context.BIND_AUTO_CREATE);
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

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookManager = IBookManager.Stub.asInterface(service);
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
        }
    };
}
