
Saga-Transaction | 自己动手实现Saga长事务
---
通过托管Spring事务管理器实现分布式事务处理，简单，优雅，无代码侵入性。。。

### 理论基础

1987年普林斯顿大学的Hector Garcia-Molina和Kenneth Salem发表了一篇Paper Sagas，讲述的是如何处理long lived transaction（长活事务）。Saga是一个长活事务可被分解成可以交错运行的子事务集合。其中每个子事务都是一个保持数据库一致性的真实事务。

### 框架主要特点

- 两阶段提交本地事务（事务活动阶段、异步提交阶段），高性能，最终一致性
- 事件驱动架构，参与者可异步执行，高吞吐
- 异常框架自动回滚子事务，无需写Cancel代码
- 隔离级别支持读已提交
- 无代码侵入，集成简单

### 框架原理解析

（缺失的图）

1. 服务A开启事务，生成事务xid，在Consul设置全局锁，此时服务发起方持有全局锁

2. 服务A通过Feign调用服务B，设置header头传递事务xid

3. 服务B开启事务，执行业务

4. 服务B写入子事务的业务执行状态到Consul中

5. 服务B执行完业务后，返回Feign调用结果，不提交事务，异步线程等待全局锁。

6. 服务A调用完毕，释放全局锁，从Consul中获取子事务的业务执行状态，全部成功提交事务，有一个失败则回滚事务

7. 服务B获取到锁，从Consul中获取子事务的业务执行状态，全部成功提交事务，有一个失败则回滚事务


### 支持的数据库种类

因为是托管Spring事务管理器，理论上只要支持事务的受Spring事务管理的数据库都能够支持

### 中间件依赖

Spring、Consul

### 如何使用请看saga-simple这个工程（示例Demo）

- shop 商城系统分布式事务处理（订单保存、库存扣减、支付订单）
- account 模拟转账业务分布式事务处理

### TODO List
- 异常调用cancel方法自动补偿事务（不使用数据库事务场景）
- 超时机制
- 支持消息驱动的微服务架构事务处理（RabbitMQ）
- 事务状态存储支持Redis、Zookeeper（目前只支持Consul）
