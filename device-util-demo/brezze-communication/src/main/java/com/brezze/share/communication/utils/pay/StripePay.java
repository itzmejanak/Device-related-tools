package com.brezze.share.communication.utils.pay;

import com.brezze.share.utils.common.enums.hint.Hint;
import com.brezze.share.utils.common.exception.BrezException;
import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.terminal.ConnectionToken;
import com.stripe.param.PaymentIntentCaptureParams;
import com.stripe.param.terminal.ConnectionTokenCreateParams;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * stripe支付
 *
 * @see <a href="https://stripe.com/docs/api">stripe官方文档</a>
 */
@Slf4j
public class StripePay {

    public static final String API_KEY = "";

    public static ConnectionToken createConnectionToken(String apiKey, String locationId) {
        // Set your secret key. Remember to switch to your live secret key in production.
        // See your keys here: https://dashboard.stripe.com/apikeys
        Stripe.apiKey = apiKey;

        // In a new endpoint on your server, create a ConnectionToken and return the
        // `secret` to your app. The SDK needs the `secret` to connect to a reader.
        ConnectionTokenCreateParams params =
                ConnectionTokenCreateParams.builder()
                        .setLocation(locationId)
                        .build();
        try {
            ConnectionToken connectionToken = ConnectionToken.create(params);
            log.info("ConnectionToken:{}", connectionToken);
            return connectionToken;
        } catch (StripeException e) {
            log.error("ConnectionToken", e);
        }
        return null;
    }

    /**
     * 支付意图-取消
     *
     * @param apiKey          秘钥
     * @param paymentIntentId 支付意图ID
     * @return
     */
    public static PaymentIntent paymentIntentCancel(String apiKey, String paymentIntentId) {
        Stripe.apiKey = apiKey;
        try {
            PaymentIntent paymentIntent =
                    PaymentIntent.retrieve(
                            paymentIntentId
                    );
            PaymentIntent updatedPaymentIntent =
                    paymentIntent.cancel();
            return updatedPaymentIntent;
        } catch (StripeException e) {
            log.error("支付意图-取消异常：", e);
        } catch (Exception e) {
            log.error("支付意图-取消异常：", e);
        }
        return null;
    }

    /**
     * 支付意图-捕获
     *
     * @param apiKey          秘钥
     * @param paymentIntentId 支付意图ID
     * @param amount          金额（单位：元）
     * @return
     */
    public static PaymentIntent capture(String apiKey, String paymentIntentId, BigDecimal amount) {
        try {
            Stripe.apiKey = apiKey;
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            PaymentIntentCaptureParams params =
                    PaymentIntentCaptureParams.builder()
                            .setAmountToCapture(amount.multiply(new BigDecimal("100")).longValue())
                            .build();
            if (paymentIntent != null) {
                return paymentIntent.capture(params);
            }

        } catch (StripeException e) {
            log.error("StripeException:", e);
        } catch (Exception e) {
            log.error("Exception:", e);
        }
        return null;
    }
}
