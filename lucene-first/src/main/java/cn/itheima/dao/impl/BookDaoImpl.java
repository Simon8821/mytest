package cn.itheima.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import cn.itheima.dao.BookDao;
import cn.itheima.po.Book;

public class BookDaoImpl implements BookDao{

	/**
	 * 查询全部图书列表
	 */
	
	public List<Book> findAllBookList() {
		// 定义图书列表集合list
		List<Book> bookList = new ArrayList<Book>();
		
		Connection con = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		
		try {
			//加载驱动
			Class.forName("com.mysql.jdbc.Driver");
			//创建数据库连接对象
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/79_lucene", "root", "");
			//定义sql语句
			String sql = "select * from book";
			//创建Statement语句对象
			psmt = con.prepareStatement(sql);
			//设置参数
			
			//执行
			rs = psmt.executeQuery();
			//处理结果集
			while (rs.next()) {
				// 创建图书对象
				Book book = new Book();
				//图书Id
				book.setId(rs.getInt("id"));
//				图书名称
				book.setBookname(rs.getString("bookname"));
//				图书价格
				book.setPrice(rs.getFloat("price"));
//				图书图片
				book.setPic(rs.getString("pic"));
//				图书描述
				book.setBookdesc(rs.getString("bookdesc"));
				
				bookList.add(book);
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//释放资源
			try {
				if(rs != null)rs.close();
				if(psmt != null)psmt.close();
				if(con != null)con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bookList;
	}
	/**
	 * 测试
	 */
	public static void main(String[] args) {
		// 创建图书dao对象
		BookDao bookDao = new BookDaoImpl();
		List<Book> list = bookDao.findAllBookList();
		for (Book book : list) {
			System.out.println(book);
		}
		
	}
	
}



















