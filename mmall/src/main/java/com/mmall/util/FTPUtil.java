package com.mmall.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FTP工具类
 * @author Administrator
 *
 */

public class FTPUtil {
	
	//定义Logger 
	private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

	//将mmall.properties配置中的FTP的ip、user、pass拿过来
	private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
	private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
	private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");
	
	//在声明几个字段
	private String ip;
	private int port;
	private String user;
	private String pwd;
	private FTPClient ftpClient;
	
	//创建一个构造器
	public FTPUtil(String ip,int port,String user,String pwd){
		this.ip=ip;
		this.port=port;
		this.user =user;
		this.pwd=pwd;
	}
	//下面我们在写开放出去的静态方法
	public static  boolean uploadFile(List<File> fileList) throws IOException{
		FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
		logger.info("开始连接ftp服务器");
		boolean result = ftpUtil.uploadFile("img", fileList);
		logger.info("开始连接ftp服务器，结束上传，上传结果{}");
		return result;
	}
	
	//下面是把上传的具体逻辑放到下面,这个方法要传入两个参数，这个参数：remotePath表示的是：使我们上传的路径在多一些
	private boolean uploadFile(String remotePath,List<File> file) throws IOException{
		//首先，我们要判断一些是否传了
		boolean upload = true;
		//接着，我们在定义一个文件输入流对象
		FileInputStream fis = null;
		//连接FTP服务器
		if(connectServer(this.ip,this.port,this.user,this.pwd)){
			
			try {
				//当FTP服务器登录成功后，我们设置改变工作目录
				ftpClient.changeWorkingDirectory(remotePath);
				//设置我们缓冲区大小
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				//设置ftp的文件类型为：二进制类型，是为了防止乱码的问题
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				//由于我们的ftp之前设置的是被动模式,还设置了被动模式端口范围,打开被动模式
				ftpClient.enterLocalPassiveMode();
				//遍历我们上传的文件List
				for(File fileItem:file){
					fis = new FileInputStream(fileItem);
					//存储文件,其中，第一个方法中的参数是：文件的名称，第二个参数是：这个输入流对象
					ftpClient.storeFile(fileItem.getName(), fis);
				}
				
			} catch (IOException e) {
				upload = false;
				logger.error("上传文件异常！", e);
				
			}
			finally{
				//关闭Ftp服务器
				fis.close();
				ftpClient.disconnect();
			}
			
		}
		return upload;
	}
	//这是将连接FTP服务器封装一下
	private boolean connectServer(String ip,int port,String user,String pwd){
		
		boolean isSuccess = false;
		
		//首先，我们实例化一下FTPClient
		 ftpClient = new FTPClient(); 
		 try {
			 //连接FTP
			ftpClient.connect(ip);
			//接下来，登录FTP服务器
			isSuccess = ftpClient.login(user, pwd);
		} 
		catch (Exception e) {
			logger.error("连接FTP服务器异常！", e);
		}
		return isSuccess; 
	}
	
	
	
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public FTPClient getFtpClient() {
		return ftpClient;
	}
	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
	
	
}
