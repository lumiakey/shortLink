# shortLink
应西瓜创客招聘要求 完成的短网址服务

# 系统亮点
考虑到网址映射的高并发低延时情况
  主要从生成短网址和短网址映射长网址两个方向优化：
  
  生成短网址：
  后端将短网址按照不同类型生成20个放在队列中，请求获取短网址的时候可以直接从队列中取出，新建一个去完成落库。详细流程后面完善。
  
  映射长网址：
  映射的时候先去Redis中读，如果没有就去查库，查库后写到redis里，第二次请求直接从redis中返回，启动一个线程去完成访问次数+1操作。
  
  数据库按照不同类型分成最多186个表。非永久短链接可以用一个线程去扫描非永久表
  
