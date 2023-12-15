## Triones Exception
> 为了支持异常信息国际化，将错误信息通过配置文件的方式加载，从而可以根据当前的语言返回对应的国际化信息


### 异常对象

- TrionesException 异常
- NotFoundException 未找到异常
- PermissionDeniedException 无权限异常
- InternalRequestException 调用接口异常

### 使用方式
- 通过代码注入的方式
  ```java
        Map<String,String> tmap = new HashMap<>();
        tmap.put("TEST","ce shi");
        ExceptionResourceProperties.codeMap.put(Locale.getDefault(),tmap );
        throw new TrionesException("TEST");
    ```

- 通过配置文件的方式
    
  ![img.png](images/img.png)

  ```text
    USER_NOT_FOUND=用户不存在
  ```      

  ```java
    ExceptionResourceProperties.setResourcePaths("i18n/error");
    throw new TrionesException("USER_NOT_FOUND");
  ```