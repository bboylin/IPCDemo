package xyz.bboylin.ipcdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2017/6/8.
 */

public class AIDLService extends Service {

    public final String TAG = this.getClass().getSimpleName();
    private List<Book> books = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Book book = new Book(0, "Android开发艺术探索");
        books.add(book);
    }

    private final IBookManager.Stub iBookManager = new IBookManager.Stub() {
        @Override
        public List<Book> getBooks() throws RemoteException {
            Log.e(TAG, "invoking getBooks() method , now the list is : " + books.toString());
            if (books != null) {
                return books;
            }
            return new ArrayList<>();
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            synchronized (this) {
                if (books == null) {
                    books = new ArrayList<>();
                }
                if (book == null) {
                    Log.e(TAG, "Book is null in In");
                    book = new Book(-1, "未知书籍");
                }
                //尝试修改book的参数，主要是为了观察其到客户端的反馈
                book.setTag("特殊");
                if (!books.contains(book)) {
                    books.add(book);
                }
                //打印mBooks列表，观察客户端传过来的值
                Log.e(TAG, "invoking addBooks() method , now the list is : " + books.toString());
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(getClass().getSimpleName(), String.format("on bind,intent = %s", intent.toString()));
        return iBookManager;
    }
}
