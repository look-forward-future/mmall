package com.mmall.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;

@Service("iFileService")
public class FileServiceImpl implements IFileService{
	
	//由于我们要经常使用这个文件上传，故需要定义一个Logger
	public static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	
	//下面的方法要将上传的文件名返回回去
	public String upload(MultipartFile file,String path){
		//首先，我们要获取它的文件名,调用file的getOriginalFilename（）方法来获取在文件系统客服端中的原始的文件名
		String fileName = file.getOriginalFilename();
		//接着，获取文件的扩展名
		String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
		/*
		 * 然后，我们要定义一个上传文件的文件名,我们还需要考虑到到我们第一次上传的文件名为：abc.jpg，第二次再上传一个文件的文件名也是：abc.jpg的话，
		 *第二次上传的文件名就会覆盖前一次上传的文件，故需要在上传的文件名前面加上一个UUID随机生成的字符串来避免
		 */
		String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
		logger.info("上传文件开始，上传文件的文件名：{},路径：{},新文件名：{},",fileName,path,uploadFileName);
		//我们将路径转到path下的目录
		File fileDir = new File(path);
		//接下来，我们判断一下这个文件夹是否存在
		if(!fileDir.exists()){
			//此时，这个文件夹不存在，就需要设置一个权限(可写)
			fileDir.setWritable(true);
			//在该路径下创建文件夹，这个创建文件夹有两个方法，还有一个是：.mkdir()方法，它指的是：只能在当前的路径下，创建单个文件夹，然而，.mkdirs()方法可以创建多个文件夹
			fileDir.mkdirs();
		}
		//接着，创建我们的文件,这就是一个完整的路径啦
		File targetFile = new File(path,uploadFileName);
		//然后，进行文件的上传，它会将我们的文件上传到指定的目录：upload中
		try {
			file.transferTo(targetFile);
			//文件上传成功
			
			//将targetFile上传到我们的FTP服务器上
			FTPUtil.uploadFile(Lists.newArrayList(targetFile));
			//上传成功后，删除upload下面的文件，由于它是tomcat的文件夹，随着文件的增加，文件会越来越大
			targetFile.delete();
		} 
		catch (IOException e) {
			logger.error("上传文件异常！", e);
			return null;
		}
		return targetFile.getName();
	}
	
	
}
