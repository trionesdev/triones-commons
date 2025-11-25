# moensun commons core

> commons包的核心模块

- util 工具类
    - IdentityUtils 
    - BigDecimalUtils 
    - FilePathUtils 文件路径工具类
    - PageUtils 分页工具类
    - JsonUtils 基于jackson的json工具类
- jackson jackson的序列化与反序列化
- constant 常量
- page 分页
  > 为了统一不同的ORM组件的返回值，抽象出了统一的分页对象
- jwt
  - JwtFacade Jwt门面
  ```java
  class Jwt{
    @Test
    public void generateJwt() {
        JwtConfig jwtConfig = JwtConfig.builder().secret("123456789").expiration(3600).build();
        JwtFacade jwtFacade = new JwtFacade(jwtConfig);
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "bane");
        String token = jwtFacade.generate("bane", claims);
        System.out.println(token);
    }
  
    @Test
    public void generateToken() {
        JwtConfig jwtConfig = JwtConfig.builder().secret("123456789").expiration(3600).build();
        JwtFacade jwtFacade = new JwtFacade(jwtConfig);
        String token = jwtFacade.generate("userId", "role", "tenantId", "tenantMemberId");
        System.out.println(token);
    }
  }
  ```