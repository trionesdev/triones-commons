## MoenSun core spring 

### spring context setter
>功能
> 
> 通过静态方法获取到 spring 的 ConfigurableApplicationContext 对象


### act event
> 功能描述：
> 
> 用于记录操作日志
 
> 用法：
> 
> 1.在需要处理的函数上加 @ActEvent
> 
> 2.实现 ActEventHandler 接口，在实现类里填充业务逻辑
