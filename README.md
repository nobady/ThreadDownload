# ThreadDownload.

多线程下载（不支持断线续传）

使用方法：
  1.在UI类中实现FileDownListener接口，然后重写其中的方法
  2.然后创建DownloadTask实例，将保存文件目录，线程个数和下载路径发送给下载任务
  3.然后调用start方法进行下载
