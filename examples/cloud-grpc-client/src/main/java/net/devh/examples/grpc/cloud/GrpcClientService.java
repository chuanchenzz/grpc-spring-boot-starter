package net.devh.examples.grpc.cloud;

import net.devh.examples.grpc.lib.HelloReply;
import net.devh.examples.grpc.lib.HelloRequest;
import net.devh.examples.grpc.lib.SimpleGrpc;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;

import org.springframework.stereotype.Service;

import io.grpc.Channel;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 2016/11/8
 */
@Service
public class GrpcClientService {

    @GrpcClient("cloud-grpc-server")
    private Channel serverChannel;
    private SimpleGrpc.SimpleBlockingStub stub;

    public String sendMessage(String name) {
//        SimpleGrpc.SimpleBlockingStub stub = SimpleGrpc.newBlockingStub(serverChannel);
        HelloReply response = null;
        for (int i = 0; i < 10; i++){
            response = getInstance().sayHello(HelloRequest.newBuilder().setName(name).build());
        }
        return response.getMessage();
    }

    public SimpleGrpc.SimpleBlockingStub getInstance(){
        if(stub == null){
            synchronized (this){
                stub = SimpleGrpc.newBlockingStub(serverChannel);
            }
        }
        return stub;
    }
}
