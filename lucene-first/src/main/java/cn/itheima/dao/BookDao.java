package cn.itheima.dao;

import java.util.List;
import cn.itheima.po.Book;

public interface  BookDao {

	/**
	 * 查询全部图书列表
	 */
	List<Book> findAllBookList();
}
