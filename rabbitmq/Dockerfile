FROM rabbitmq:3-management-alpine

# Install wget for downloading the plugin (wget is available in base Alpine)
RUN apk add --no-cache wget

# Download and install the delayed message exchange plugin
RUN wget -O /opt/rabbitmq/plugins/rabbitmq_delayed_message_exchange-3.13.0.ez \
    https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.13.0/rabbitmq_delayed_message_exchange-3.13.0.ez

# Enable the delayed message exchange plugin
RUN rabbitmq-plugins enable --offline rabbitmq_delayed_message_exchange

# Expose the standard ports
EXPOSE 5672 15672