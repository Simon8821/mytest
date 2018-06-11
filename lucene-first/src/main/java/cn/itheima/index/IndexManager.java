package cn.itheima.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import cn.itheima.dao.BookDao;
import cn.itheima.dao.impl.BookDaoImpl;
import cn.itheima.po.Book;

/** 
 * @ClassName: IndexManager 
 * @Description: 索引管理类
 * @author 传智 小杨老师  
 * @date 2018-6-4 下午4:48:22 
 *  
 */
public class IndexManager {
	
	private static final String INDEX_PATH = "E:\\teach\\0347\\index\\";
	/**
	 * 索引流程实现
	 * @throws IOException 
	 */
	@Test
	public void createIndex() throws IOException{
//		1.采集原始数据
		BookDao bookDao = new BookDaoImpl();
		List<Book> bookList = bookDao.findAllBookList();
		
//		2.建立文档对象（Document）
		List<Document> docList = new ArrayList<Document>();
		for(Book book:bookList){
			// 创建文档对象
			Document doc = new Document();
			
			// 给文档对象中添加域（Field）
//			图书Id
			/**
			 * add方法：把域添加到文档中
			 * 参数：
			 * 		TextField:文本域。name：域的名称；value：域值；store：是否把域值保存到文档对象中
			 */
			doc.add(new TextField("bookId", book.getId()+"", Store.YES));
			
//			图书名称
			doc.add(new TextField("bookName", book.getBookname(), Store.YES));
//			图书价格
			doc.add(new TextField("bookPrice", book.getPrice()+"", Store.YES));
//			图书图片
			doc.add(new TextField("bookPic", book.getPic(), Store.YES));
//			图书描述
			doc.add(new TextField("bookDesc", book.getBookdesc(), Store.YES));
			
			docList.add(doc);
		}
		
//		3.建立分析器对象（Analyzer），用于分析文档
		//Analyzer analyzer = new StandardAnalyzer();

		//使用Ik分词器
		Analyzer analyzer = new IKAnalyzer();
		
//		4.建立索引配置对象（IndexWriterConfig），用于配置索引库
		/**
		 * 参数：
		 * 	matchVersion：当前使用的lucene版本
		 * analyzer：当前使用的分析器对象
		 */
		IndexWriterConfig iwc= new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		
//		5.建立索引库的目录对象（Directory），指定索引库的位置
		File path = new File("E:\\teach\\0347\\index\\");
		Directory directory = FSDirectory.open(path);
		
//		6.建立索引库操作对象（IndexWriter），操作索引库
		IndexWriter writer = new IndexWriter(directory, iwc);
		
//		7.使用IndexWriter对象，把文档对象写入索引库
		for(Document doc:docList){
			// addDocument方法：把文档对象，写入索引库
			writer.addDocument(doc);
		}
		
//		8.释放资源
		writer.close();
	}
	/**
	 * 检索流程实现
	 * @throws Exception 
	 */
	@Test
	public void readIndex() throws Exception{
//		1.建立分析器对象（Analyzer），用于分词
		//Analyzer analyzer = new StandardAnalyzer();
		
		//使用ik分词器
		Analyzer analyzer = new IKAnalyzer();
		
//		2.建立查询对象（Query）
		// 2.1.建立查询解析器对象
		/**
		 * 参数：
		 * 	f：默认搜索域
		 * 	a:使用的分析器对象
		 */
		QueryParser qp = new QueryParser("bookName", analyzer);
		
		// 2.2.使用查询解析器对象，解析表达式，实例化查询对象
		// bookName:java
		Query query = qp.parse("bookName:java");

//		3.建立索引库的目录对象（Directory），指定索引库的位置
		Directory directory = FSDirectory.open(new File(INDEX_PATH));
		
//		4.建立索引读取对象（IndexReader），把索引数据读取到内存中
		IndexReader reader = DirectoryReader.open(directory);
		
//		5.建立索引搜索对象（IndexSearcher），执行搜索
		IndexSearcher searcher = new IndexSearcher(reader);
		
//		6.使用IndexSearcher执行索索，返回搜索结果集TopDocs
		/**
		 * search方法：执行搜索
		 * 参数：
		 * 	query：查询对象
		 *  n：返回搜索结果中排序后的前n个
		 * 
		 */
		TopDocs topDoc = searcher.search(query, 10);
		
//		7.处理结果集
		// 7.1.实际搜索到的结果数量
		System.out.println("实际搜索到的结果数量："+topDoc.totalHits);
		
		// 7.2.获取搜索结果数量
		/**
		 * ScoreDoc对象中包含两个信息：文档的Id；文档的分值
		 */
		ScoreDoc[] scoreDocs = topDoc.scoreDocs;
		for(ScoreDoc sd:scoreDocs){
			
			System.out.println("------------华丽丽分割线---------------");
			
			// 取出当前文档的Id和分值
			int docId = sd.doc;
			float score = sd.score;
			
			System.out.println("当前文档的Id和分值："+docId+","+score);
			
			// 根据文档Id获取文档对象
			Document doc = searcher.doc(docId);
			
			System.out.println("图书Id："+doc.get("bookId"));
			System.out.println("图书名称："+doc.get("bookName"));
			System.out.println("图书价格："+doc.get("bookPrice"));
			System.out.println("图书图片："+doc.get("bookPic"));
			System.out.println("图书描述："+doc.get("bookDesc"));
		}
		
//		8.释放资源
		reader.close();
	}
	/**
	 * 检索流程实现(分页实现)
	 * @throws Exception 
	 */
	@Test
	public void readIndexPage() throws Exception{
//		1.建立分析器对象（Analyzer），用于分词
		//Analyzer analyzer = new StandardAnalyzer();
		
		// 使用ik分词器
		Analyzer analyzer = new IKAnalyzer();
		
//		2.建立查询对象（Query）
		// 2.1.建立查询解析器对象
		/**
		 * 参数：
		 * 	f：默认搜索域
		 * 	a:使用的分析器对象
		 */
		QueryParser qp = new QueryParser("bookName", analyzer);
		
		// 2.2.使用查询解析器对象，解析表达式，实例化查询对象
		// bookName:java
		Query query = qp.parse("bookName:java");

//		3.建立索引库的目录对象（Directory），指定索引库的位置
		Directory directory = FSDirectory.open(new File(INDEX_PATH));
		
//		4.建立索引读取对象（IndexReader），把索引数据读取到内存中
		IndexReader reader = DirectoryReader.open(directory);
		
//		5.建立索引搜索对象（IndexSearcher），执行搜索
		IndexSearcher searcher = new IndexSearcher(reader);
		
//		6.使用IndexSearcher执行索索，返回搜索结果集TopDocs
		/**
		 * search方法：执行搜索
		 * 参数：
		 * 	query：查询对象
		 *  n：返回搜索结果中排序后的前n个
		 * 
		 */
		TopDocs topDoc = searcher.search(query, 10);
		
//		7.处理结果集
		// 7.1.实际搜索到的结果数量
		System.out.println("实际搜索到的结果数量："+topDoc.totalHits);
		
		// 7.2.获取搜索结果数量
		/**
		 * ScoreDoc对象中包含两个信息：文档的Id；文档的分值
		 */
		ScoreDoc[] scoreDocs = topDoc.scoreDocs;
		
		// 增加分页处理=======================start
		// 1.当前页
		int page=2;
		
		// 2.每一页显示大小
		int pageSize = 2;
		
		// 3.开始索引
		int startIndex =(page-1)*pageSize;
		
		// 4.结束索引
		// 普通情况：startIndex+pageSize
		// 最后一页：scoreDocs.length;
		int endIndex = Math.min(startIndex+pageSize, scoreDocs.length);
		
		// 增加分页处理=======================end
		//for(ScoreDoc sd:scoreDocs){
	   for(int i=startIndex;i<endIndex;i++){
			
			System.out.println("------------华丽丽分割线---------------");
			
			// 取出当前文档的Id和分值
			int docId = scoreDocs[i].doc;
			float score = scoreDocs[i].score;
			
			System.out.println("当前文档的Id和分值："+docId+","+score);
			
			// 根据文档Id获取文档对象
			Document doc = searcher.doc(docId);
			
			System.out.println("图书Id："+doc.get("bookId"));
			System.out.println("图书名称："+doc.get("bookName"));
			System.out.println("图书价格："+doc.get("bookPrice"));
			System.out.println("图书图片："+doc.get("bookPic"));
			System.out.println("图书描述："+doc.get("bookDesc"));
		}
		
//		8.释放资源
		reader.close();
	}

}
