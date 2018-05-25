package net.devh.springboot.autoconfigure.grpc.client;

import io.grpc.ClientInterceptor;

import java.lang.annotation.*;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 2016/12/7
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GrpcClient {

    String value();

    Class<? extends ClientInterceptor>[] interceptors() default {};
}