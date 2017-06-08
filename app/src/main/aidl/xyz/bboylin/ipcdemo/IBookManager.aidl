// IBookManager.aidl
package xyz.bboylin.ipcdemo;

import xyz.bboylin.ipcdemo.Book;

interface IBookManager {
    /**
     *  传参必须要加定向tag
     *  in 表示数据只能由客户端流向服务端，
     *  out 表示数据只能由服务端流向客户端，
     *  而 inout 则表示数据可在服务端与客户端之间双向流通。
     */
    List<Book> getBooks();
    void addBook(in Book book);
}
