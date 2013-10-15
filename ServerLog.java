import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ServerLog {
	//日志文件目录
	private String LOGFILE = "G:/Serverlog.log";
	//提取的消息存放文件
	private String NODEFILE = "G:/node.log";
	//辅助链表  实现排序  和  存储
	private List<String> temp = new LinkedList<String>(); 
	private List<String> tempp = new LinkedList<String>();
	/**
	 * 加载要读取的文件  返回BufferedReader对象 进行行读取
	 * @param file
	 * @return BufferedReader
	 * @throws Exception
	 */
	public BufferedReader loadFile(String file) throws Exception{
		FileInputStream in = new FileInputStream(file);
		BufferedReader rd = new BufferedReader(new InputStreamReader(in));
		return rd;
	}
	/**
	 * 	正则表达式提取压缩后的信息  每10条信息进行插入到tempp进行插入排序
	 * @param brd
	 * @param wd
	 * @throws Exception
	 */
	public void logSort(BufferedReader brd, BufferedWriter wd) throws Exception{
		String buffer = "";
		int i = 0;
		while((buffer = brd.readLine()) != null){
			i++;
			//提取关键字段
			String ss = handlerStr(buffer);
			if(ss != null){
				temp.add(ss);
			}
			if(i == 10){
				Sort(temp);
				i = 0;
			}
		}
		Sort(temp);
	}
	/**
	 * 插入排序  最后构成排好序的tempp链表
	 * @param temp
	 */
	public void Sort(List<String> temp){
		Collections.sort(temp);
		if(tempp.size() == 0){
			tempp.addAll(temp);
			temp.clear();
		}else{
			for(String str : temp){
				for(int i = tempp.size()-1; i >= 0; i--){
					if(tempp.get(i).charAt(0) <= str.charAt(0)){
						int index = tempp.indexOf(tempp.get(i));
						tempp.add(index+1, str);
						break;
					}
				}
			}
			temp.clear();
		}
	}
	/**
	 * 将排好序的tempp链表中的数据，存入node文件 进行换行保存
	 * @param wd
	 * @throws Exception
	 */
	public void handler(BufferedWriter wd) throws Exception{
		for(String buf : tempp){
			wd.write("node:"+ buf+"\r\n");//window 文件
			//wd.write("node:"+ buf+"\n");//linux 文件
			//wd.write("node:"+ buf+"\r");//mac 文件
		}
	}
	/**
	 * 字符串处理函数  对文件的每行字串进行 正则表达式 筛选
	 * @param str
	 * @return
	 */
	public String handlerStr(String str){
		String strr = "";
		String expStr =  "^(?:[\\S\\s]+)node:(\\S+?[;|,])(?:\\S*)(t:\\d+)(\\S*)$";
		Pattern pattern = Pattern.compile(expStr); 
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			strr = matcher.group(1)+matcher.group(2);
		}else return null;//该行没有匹配内容  返回null
		return strr;

	}
	/**
	 * 获取文件写字符流 返回 BufferedWriter
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public BufferedWriter wirteLog(String file) throws Exception{
		FileOutputStream out = new FileOutputStream(file);
		BufferedWriter rw = new BufferedWriter(new OutputStreamWriter(out));
		return rw;
	}
	/**
	 * main method
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ServerLog log = new ServerLog();
		BufferedReader rd = log.loadFile(log.LOGFILE);
		BufferedWriter wd = log.wirteLog(log.NODEFILE);
		log.logSort(rd, wd);
		log.handler(wd);
		rd.close();
		wd.close();
		System.out.print("--完成--");
	}
}
