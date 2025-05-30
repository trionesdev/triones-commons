### Commons Context
> 上下文操作，基于threadlocal传递

ActorContext 中存放当前执行人的相关信息，
请求生命周期中可以获得执行人的ID等相关信息

actor使用方法

> 基于 ActorContextHolder 操作上下文中的对象

```java
Actor actor = new Actor();
ActorContextHolder.setActor(actor);
```