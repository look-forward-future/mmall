<%@ page language="java" contentType="text/html; charset=utf-8" %>

<html>
<body>
<h2>Hello World!</h2>

<!-- springMVC上传文件   要注意：enctype要使用：multipart/form-data -->
<form name="form1" action="/mmall/manage/product/upload.do" method="post" enctype="multipart/form-data">
	<input type="file" name="upload_file">
	<input type="submit" value="springMVC上传文件">
</form>

<!-- 富文本图片的上传 -->
<form name="form2" action="/mmall/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
	<input type="file" name="upload_file">
	<input type="submit" value="富文本图片的上传">
</form>
</body>
</html>
