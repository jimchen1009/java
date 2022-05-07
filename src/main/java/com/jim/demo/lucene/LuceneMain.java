package com.jim.demo.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.Random;

public class LuceneMain {
    
    public static void main(String[] args) throws IOException, ParseException {
        Analyzer analyzer = new StandardAnalyzer();

        //将索引存储到内存中
        Directory directory = new RAMDirectory();
        //如下想把索引存储到硬盘上，使用下面的代码代替
        //Directory directory = FSDirectory.open(Paths.get("/tmp/testindex"));

        //写入索引库
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        String[] texts = new String[]{
                "Mybatis分页插件 - 示例",
                "Mybatis 贴吧问答 第一期",
                "Mybatis 示例之 复杂(complex)属性(property)",
                "Mybatis极其(最)简(好)单(用)的一个分页插件",
                "Mybatis的Log4j日志输出问题 - 以及有关日志的所有问题",
                "Mybatis示例之foreach （下）",
                "Mybatis示例之foreach （上）",
                "Mybatis示例之SelectKey",
                "Mybatis示例之Association (2)",
                "Mybatis示例之Association"
        };
        for (String text : texts) {
            Document doc = new Document();
            // doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
            doc.add(new TextField("title", text, Field.Store.YES));
            doc.add(new StringField("isbn", "" + String.valueOf(Math.random()), Field.Store.YES));
            iwriter.addDocument(doc);
        }

        String message= "通过以此点击Netbeans菜单栏上的工具，然后选择“库管理器”，把Lucene的jar文件作为外部类库加进来。" +
                "在Lucene项目上面右键，选择“属性”" +
                "在弹出来的对话框中，以此选择“类库”,”添加jar或文件夹”选项" +
                "定位到从lucene-[version].tar.gz解压出来的文件夹上，选择 lucene-core-[version].jar" +
                "点击“确定”，现在jar文件就已经添加到你项目的classpath中去了。";
        int length = message.length() - 5;
        Random random = new Random();
        for (int i = 0; i < 1000000; i++) {
            Document doc = new Document();
            int index = random.nextInt(length);
            String text = message.substring(index, index + 5) + random.nextLong();
            // doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
            doc.add(new TextField("title", text, Field.Store.YES));
            doc.add(new StringField("isbn", "" + String.valueOf(Math.random()), Field.Store.YES));

            iwriter.addDocument(doc);
        }

        iwriter.close();

        long current = System.currentTimeMillis();
        //读取索引并查询
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(reader);
        //解析一个简单的查询
        QueryParser parser = new QueryParser("title", analyzer);
        Query query = parser.parse("示例");
        ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
        //迭代输出结果
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            System.out.println(hitDoc.get("title"));
            System.out.println(hitDoc.get("isbn"));
        }
        reader.close();
        directory.close();
        System.out.println(System.currentTimeMillis() - current);
    }

}
