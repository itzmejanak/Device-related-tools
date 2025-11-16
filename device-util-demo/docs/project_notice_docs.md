# No IoT service deployed

Temporary deployment of this project
## Environment

- Java JDK 1.8
- Redis
- Database: MySQL or other（）
- RabbitMQ: Requires the rabbitmq_delayed_message_exchange delay plugin
- EMQX: Install open-source version 5.4.1 / 5.9.1

### Environment Configuration

Open ports on the server
- **RabbitMQ** 15672
- **EMQX（open-source version）** 1883 / 18083

#### RabbitMQ
Add virtual-host; console address is as follows  
http://your-server-IP:15672/#/vhosts

#### MySQL
Initialize the database using db.sql

For the remaining steps, please refer to the documentation.[README.md]()

# IoT services have been deployed.
### Deploying this project as a testing/binding tool

The testing tool retrieves data from the cache; please ensure the [Redis keys](#RedisKeyCst.java) for both projects are consistent. Check for object conversion anomalies.

Binding tool: Uses the actual project's database table structure; GeneratorUtil.java can be used to automatically generate the basic code.

### Not deploying this project

Synchronize the binding tool interface BindingController and the testing tool interface YbtController to the actual project code.

For modifications to the binding and testing tools, please refer to the documentation.[README.md]()