Saga-Transaction
---
## 自己动手实现saga长事务

主要特点：
- 一阶段提交本地事务，高性能
- 事件驱动架构，参与者可异步执行，高吞吐
- 自动补偿
- 隔离级别支持读已提交
- 托管Spring事务管理器无代码侵入

### 支持的数据库种类

理论上只要支持事务的数据库都能够支持

### 中间件依赖

Spring、Consul

### 如何使用请看saga-simple这个工程（示例Demo）

- shop 商城系统分布式事务处理（订单保存、库存扣减、支付订单）
- account 模拟转账业务分布式事务处理

### TODO List
- 支持消息驱动的微服务架构事务处理（rabbitmq）
- 支持Redis、Zookeeper
