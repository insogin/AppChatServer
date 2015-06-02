package com.csu.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.csu.bean.User;
import com.csu.constant.ContentFlag;
import com.csu.tool.FormatDate;
import com.csu.tool.StreamTool;
import com.csu.tool.XmlParser;

public class ChatServer { // To the end
	private ExecutorService executorService;						// 线程池
	private int port;												// 监听端口
	private boolean quit = false;									// 退出标识
	private ServerSocket server;
	private List<SocketTask> taskList = new ArrayList<SocketTask>();// 保存所有启动的socket集合

	public ChatServer(int port) {									//ChatServer constructor
		this.port = port;
		// 创建线程池，池中具有(cpu个数*50)条线程
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors() * 50); //Returns the number of processors available to the Java virtual machine.
	}

	/**
	 * 服务器终止,关闭所有线程
	 */
	public void quit() {
		this.quit = true;
		try {
			for (SocketTask tast : taskList) { 						//For-Each 在socket集合
				tast.input.close();
			}
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 服务器启动
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		server = new ServerSocket(port); //Creates a server socket, bound to the specified port
		new Thread(new Runnable() {									//Thread class Runnable interface
			public void run() { //线程入口
				while (!quit) {
					try {
						System.out.println("等待用户的socket请求");
						
						//Listens for a connection to be made to this socket and accepts it. 
						//The method blocks until a connection is made.
						Socket socket = server.accept();
						
						SocketTask newTask = new SocketTask(socket);
						taskList.add(newTask);
						executorService.execute(newTask);
						
						System.out.println("启动一个线程开始处理socket请求");
					} catch (Exception e) {
						System.out.println("服务器终止！关闭所有线程");
					}
				}
			}
		}).start();
	}

	/**
	 * 内部线程类,负责与每个客户端的数据通信
	 * 
	 * @author Administrator
	 */
	private final class SocketTask implements Runnable {
		private Socket s;
		private DataInputStream input;
		private DataOutputStream output;
		private User curUser;

		public SocketTask(Socket socket) {					//SocketTask constructor
			s = socket;
			try {
				input = new DataInputStream(s.getInputStream());
				output = new DataOutputStream(s.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 发送消息
		 * 
		 * @param msg
		 * @param datas
		 */
		public void sendMsg(String msg, byte[] datas) {
			try {
				if (null != msg) {
					output.writeUTF(msg); //UTF 编码？
				}
				if (null != datas) {
					output.writeInt(datas.length);
					output.write(datas, 0, datas.length);
				}
				output.flush(); //Flushes this data output stream. 
				//This forces any buffered output bytes to be written out to the stream.
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {  //SocketTask.run()
			try {
				while (true) {
					String msgCtn = input.readUTF();
					if (msgCtn.startsWith(ContentFlag.REGOSTER_FLAG)) { // 处理注册消息
						String name = msgCtn.substring(
								ContentFlag.REGOSTER_FLAG.length()).trim();
						String userId = XmlParser.saveUserInfo(name, input); // save user's info
						System.out.println("生成的用户id" + userId);
						this.sendMsg(userId, null);  //写到服务器
						taskList.remove(this); //关闭任务
						break;
					} else if (msgCtn.startsWith(ContentFlag.ONLINE_FLAG)) { // 处理登录消息
						// 获得用户的唯一标识
						String loginId = msgCtn.substring(
								ContentFlag.ONLINE_FLAG.length()).trim();
						System.out.println("当前登录用户的ID：" + loginId);
						curUser = XmlParser.queryUserById(Integer
								.parseInt(loginId)); //Parses the string argument as a signed decimal integer
						int imgNums = taskList.size() - 1; // 文件的数量，减去“自己”
						System.out.println("当前在线的人数：" + taskList.size());
						this.output.writeInt(imgNums);
						if (imgNums > 0) { //如果还有其他用户，将头像缓存到客户端
							for (SocketTask task : taskList) {
								if (task != this) {
									long userId = task.curUser.getId();
									File img = new File("image"
											+ File.separatorChar
											+ XmlParser.queryUserById(userId)
													.getImg());  
									FileInputStream flleInput = new FileInputStream(
											img);
									byte data[] = StreamTool
											.readStream(flleInput);
									this.sendMsg(String.valueOf(userId), data);    //
								}
							}
						}
						// 加载用户头像资源
						File imgFile = new File("image" + File.separatorChar
								+ curUser.getImg());
						FileInputStream flleInput = new FileInputStream(imgFile);
						byte datas[] = StreamTool.readStream(flleInput);
						String send_person = curUser.getName(); // 发送者
						String send_ctn = "进入聊天室！"; 
						String send_date = FormatDate.getCurDate(); // 发送时间
						StringBuilder json = new StringBuilder();
						json.append("[{"); // (JavaScript Object Notation) is a lightweight data-interchange format
						json.append("id:").append(loginId)
								.append(",send_person:\"").append(send_person)
								.append("\",send_ctn:\"").append(send_ctn)
								.append("\",send_date:\"").append(send_date)
								.append("\",msg_id:\"").append(XmlParser.getUniqueMsgId()); //append : +
						json.append("\"}]");
						System.out.println("json:" + json);
						// 循环向连接的每个Socket客户端发送 自己登录的消息
						for (SocketTask tast : taskList) {
							System.out.println("循环向客户端发送消息");
							tast.sendMsg(
									ContentFlag.ONLINE_FLAG
											+ this.curUser.getId(), null);
							tast.sendMsg(json.toString(), datas);
						}
						flleInput.close();
					} else if (msgCtn.startsWith(ContentFlag.OFFLINE_FLAG)) { // 处理退出消息
						taskList.remove(this);
						// 获得退出用户的ID
						String id = msgCtn.substring(
								ContentFlag.OFFLINE_FLAG.length()).trim();
						StringBuilder json = new StringBuilder();
						json.append("[{");
						json.append("id:").append(this.curUser.getId())
								.append(",send_person:\"")
								.append(this.curUser.getName())
								.append("\",send_ctn:\"").append("退出聊天室！")
								.append("\",send_date:\"").append(FormatDate.getCurDate())
								.append("\",msg_id:\"").append(XmlParser.getUniqueMsgId());
						json.append("\"}]");
						for (SocketTask tast : taskList) {
							if (tast != this) {
								tast.sendMsg(ContentFlag.OFFLINE_FLAG + id,
										null);
								tast.sendMsg(json.toString(), null);
							}
						}
						System.out.println("用户" + curUser.getName() + "退出！,关闭线程"
										+ Thread.currentThread().getName());
						break;
					} else { // 处理普通消息
						StringBuilder json = new StringBuilder();
						json.append("[{");
						json.append("id:").append(this.curUser.getId())
								.append(",send_person:\"")
								.append(this.curUser.getName())
								.append("\",send_ctn:\"").append(msgCtn)
								.append("\",send_date:\"")
								.append(FormatDate.getCurDate())
								.append("\",msg_id:\"").append(XmlParser.getUniqueMsgId());
						json.append("\"}]");
						for (SocketTask tast : taskList) {
							tast.sendMsg(json.toString(), null);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				taskList.remove(this);
				System.out.println("关闭线程" + Thread.currentThread().getName());
			} finally {
				try { 
					if (null != input)
						input.close();
					if (null != output)
						output.close();
					if (null != s)
						s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
