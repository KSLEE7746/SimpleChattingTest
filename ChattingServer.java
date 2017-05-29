import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class ChattingServer {
	public static void main(String[] args) {
		try {
			HashMap map = new HashMap<>();
			ServerSocket socket = new ServerSocket(10001);
			System.out.println("Server 접속자 대기중...");
			while(true){
				Socket client = socket.accept();
				UserThread uThread = new UserThread(client, map);
				uThread.start();		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
class UserThread extends Thread{
	private String id;
	private Socket socket;
	private HashMap map;
	private BufferedReader br;
	public UserThread(Socket socket, HashMap map) {
		this.socket = socket;
		this.map = map;
		
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			id = br.readLine();
			broadcast(id + "님이 접속함");
			System.out.println("접속한 사용자 ID는 [" + id + "]입니다.");
			synchronized (map) {
				map.put(this.id, pw);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		String line = null;
		try {
			while((line = br.readLine()) != null){
				if(line.equalsIgnoreCase("/quit")) break;
				if(line.indexOf("/to ") == 0) sendmsg(line);
				else broadcast("[" + id + "] : " + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			synchronized (map) {
				map.remove(id);
			}
			broadcast(id+"님이 접속 종료함");
			try{
				if(socket != null) socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void sendmsg(String msg){
		int start = msg.indexOf(" ")+1;
		int end = msg.indexOf(" ", start);
		if(end != -1){
			String to = msg.substring(start, end);
			String sendMsg = msg.substring(end+1);
			Object obj = map.get(to);
			if(obj != null){
				PrintWriter pw = (PrintWriter) obj;
				pw.println(id+"님이 보낸 내용 : " + sendMsg);
				pw.flush();
			}
		}
	}
	public void broadcast(String msg){
		synchronized (map) {
			Iterator it = map.values().iterator();
			while(it.hasNext()){
				PrintWriter pw = (PrintWriter) it.next();
				pw.println(msg);
				pw.flush();
			}
		}
	}	
}