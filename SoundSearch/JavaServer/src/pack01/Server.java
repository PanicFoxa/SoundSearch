package pack01;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.LinkedList;



class Musiclist{
	int mnum;
	String mtitle;
	String msinger;
	String mindex;
	public Musiclist(int mnum, String mtitle, String msinger, String mindex) {
		this.mnum = mnum;
		this.mtitle = mtitle;
		this.msinger = msinger;
		this.mindex = mindex;
	}
	
}


class Serv extends Thread{
	ServerSocket mss;
	Socket sc;
	Serv(ServerSocket mss){
		this.mss = mss;
	}
	@Override
	public void run(){
		while(true){
			try{
				sc = mss.accept();
				System.out.println("클라이언트가 접속하였습니다.");
				Tes t = new Tes(sc);
				t.start();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
}

class Tes extends Thread{
	Socket sc;
	String read;
	Connection con=null;
	PreparedStatement ps=null; 
	ResultSet rs=null;
	Tes(Socket sc){
		this.sc=sc;
	}
	@Override
	public void run() {
		try{	
			String sql=null;
			String driver ="oracle.jdbc.driver.OracleDriver"; 
			String dburl="jdbc:oracle:thin:@127.0.0.1:1521:orcl";
			String username="root"; 
			String password="root"; 
			BufferedReader in = new BufferedReader(new InputStreamReader(sc.getInputStream(),"UTF-8"));
			read= in.readLine();
			System.out.println(read);
			con=DriverManager.getConnection(dburl,username,password);
			sql= String.format("SELECT * FROM MUSICLIST WHERE MTITLE LIKE '%s' OR SINGER LIKE '%s' OR MINDEX LIKE '%s'",read,read,read);
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			
			LinkedList<Musiclist> ml=new LinkedList<>();
			while(rs.next()){
				ml.add(new Musiclist(rs.getInt("MNUM"),rs.getString("MTITLE"),rs.getString("SINGER"),rs.getString("MINDEX")));
			}
			ps.close();
			
			Iterator<Musiclist> mll=ml.iterator();
			String mdata=new String();
			while(mll.hasNext()){
				Musiclist data=mll.next();
				System.out.println(data.mnum+"  "+data.mtitle+"  "+data.msinger+"  "+data.mindex);
				mdata += (data.mnum +" "+ data.mtitle+" "+data.msinger+" "+data.mindex+" ");
			}
			System.out.println(mdata);
			OutputStream wr = sc.getOutputStream();
			byte [] musicdata;
			musicdata = mdata.getBytes("UTF-8");
			wr.write(musicdata);
			System.out.println("전송완료");
			in.close();
			wr.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}




public class Server {
	public static void main(String[] args) throws Exception{
		ServerSocket mss=new ServerSocket();
		InetAddress address=InetAddress.getLocalHost();	
		mss.bind(new InetSocketAddress(address, 5678));
		System.out.println("서버 오픈");
		Serv srv=new Serv(mss);
		srv.start();
	}
}
