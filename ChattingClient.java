import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ChattingClient {
	public static void main(String[] args) {
		Socket socket = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		
		try {
			socket = new Socket("192.168.1.32", 10001);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.print("접속 ID 입력 : ");
			String id = keyboard.readLine();
			pw.println(id);
			pw.flush();
			InputThread inputTh = new InputThread(socket, br);
			inputTh.start();
			String line = null;
			while((line = keyboard.readLine()) != null){
				pw.println(line);
				pw.flush();
				if(line.equalsIgnoreCase("/quit")) {
					inputTh.stop();
					break;
				}
			}
			System.out.println("채팅 서버 접속 종료");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pw != null) pw.close();
				if(br != null) br.close();
				if(socket != null) socket.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}

class InputThread extends Thread{
	private Socket socket;
	private BufferedReader br = null;
	public InputThread(Socket socket, BufferedReader br) {
		this.socket = socket;
		this.br = br;
	}
	@Override
	public void run() {
		String line = null;
		try {
			while((line = br.readLine()) != null){
				System.out.println(line);
				if(line.equalsIgnoreCase("/quit")) break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) br.close();
				if(socket != null) socket.close(); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}