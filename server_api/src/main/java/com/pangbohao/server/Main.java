package com.pangbohao.server;

import java.util.ArrayList;
import java.util.List;

import com.pangbohao.server.db.BeanProvider;
import com.pangbohao.server.rest.Api;
import com.pangbohao.server.rest.filter.*;
import com.pangbohao.server.utils.Util;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.providers.*;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * Created by ps_an on 2016/11/1.
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);
    private static int port = 8002;

    public static void main(String[] args) {
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        Main svr = new Main();
        svr.start();
    }

    private void start() {

        BeanProvider.Init();
        List<String> resourceClasses = new ArrayList<String>();
        resourceClasses.add(Api.class.getName());

        List<String> providerClasses = new ArrayList<String>();
        providerClasses.add(SecurityRequestFilter.class.getName());
        providerClasses.add(ETagRequestFilter.class.getName());
        providerClasses.add(ETagResponseFilter.class.getName());
        providerClasses.add(MyJacksonJsonProvider.class.getName());
        providerClasses.add(DefaultTextPlain.class.getName());
        providerClasses.add(ByteArrayProvider.class.getName());
        providerClasses.add(InputStreamProvider.class.getName());
        providerClasses.add(StringTextStar.class.getName());
        providerClasses.add(FormUrlEncodedProvider.class.getName());
        providerClasses.add(StreamingOutputProvider.class.getName());
        // providerClasses.add(JAXBXmlTypeProvider.class.getName());
        // providerClasses.add(JAXBXmlSeeAlsoProvider.class.getName());
        // providerClasses.add(JAXBXmlRootElementProvider.class.getName());
        // providerClasses.add(JAXBElementProvider.class.getName());
        providerClasses.add(MyJaxbProvider.class.getName());
        // providerClasses.add(XmlJAXBContextFinder.class.getName());
        providerClasses.add(OauthRequestFilter.class.getName());
        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setResourceClasses(resourceClasses);
        deployment.setProviderClasses(providerClasses);

        startNetty(deployment);
    }

    public static void startNetty(ResteasyDeployment deployment) {

        NettyJaxrsServer netty = new NettyJaxrsServer();
        netty.setDeployment(deployment);
        netty.setPort(port);
        netty.setRootResourcePath("/ja/");
        netty.setSecurityDomain(null);
        netty.setSSLContext(null);
        netty.start();
        Util.SERVER_ADDRESS = System.getProperty("SERVER_ADDRESS");

        logger.info("Domain name is set to be {0}, server started on port:{1}",
                Util.SERVER_ADDRESS, port + "");
    }

}
