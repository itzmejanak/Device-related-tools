EMQX message listener（open-source version 5.4.1 / 5.9.1）

1.Listening on port 1883

- Online topic：$SYS/brokers/+/clients/+/connected
- Offline topic：$SYS/brokers/+/clients/+/disconnected
- Message topic: /+/+/user/update or /powerbank/+/user/update

Example
$SYS/brokers/emqx@127.0.0.1/clients/864601068953470/disconnected

2.Configure [Webhook](https://docs.emqx.com/en/emqx/v5.4/data-integration/webhook.html) in the EMQX console.
- Configure the Integration module.