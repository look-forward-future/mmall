package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 这是文件服务的接口类
 * @author Administrator
 *
 */

public interface IFileService {
	//这是上传文件的接口
	public String upload(MultipartFile file,String path);

}
