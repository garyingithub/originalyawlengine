package org.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.InfluxDBGaugeWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;
import org.yawlfoundation.yawl.engine.interfce.interfaceA.InterfaceA_EngineBasedServer;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EngineBasedServer;
import org.yawlfoundation.yawl.util.CharsetFilter;

/**
 * Created by root on 17-1-10.
 */
@ComponentScan({"org.springframework.boot.actuate.metrics","org.cluster"})
@SpringBootApplication
public class OriginalYawlEngineApplication {
    private static final ZookeeperRegister register=new ZookeeperRegister();
    public static void main(String[] args) {

        SpringApplication.run(OriginalYawlEngineApplication.class, args);
    }

    public static String engineAddress;

    public static InfluxDBGaugeWriter writer;



    @Bean
    public ApplicationRunner initRunner(){
        return new ApplicationRunner() {

            @Autowired
            private ZookeeperRegister register;

            @Value("${engine.address}")
            String engineAddress;

            @Autowired
            private InfluxDBGaugeWriter influxDBGaugeWriter;

            @Override
            public void run(ApplicationArguments applicationArguments) throws Exception {
                OriginalYawlEngineApplication.writer=influxDBGaugeWriter;
                OriginalYawlEngineApplication.engineAddress=this.engineAddress;
                register.run();
            }
        };
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CharsetFilter());
        registration.addUrlPatterns("/yawl/*");
        registration.addInitParameter("requestEncoding", "UTF-8");
        registration.setName("CharsetFilter");
        //registration.setOrder(1);
        return registration;
    }


    @Value("${bootstrap.servers}")
    private String kafkaServers;

 //   @Bean
 //   public GaugeWriter kafkaGaugeWriter(){
      //  return new KafkaGaugeWriter(kafkaServers);
   // }

    //@Bean
    //@ExportMetricWriter
    //public GaugeWriter influxDBGaugeWriter(){
   //     return new InfluxDBGaugeWriter();
  //  }
//



    @Bean(name = "interfaceB")
    public ServletRegistrationBean interfaceBServletRegistration( ){
        InterfaceB_EngineBasedServer server=new InterfaceB_EngineBasedServer();
        //server.setWriter(influxDBGaugeWriter());
        ServletRegistrationBean servletRegistrationBean=
                new ServletRegistrationBean(
                        server,"/yawl/ib/*"
                );

        servletRegistrationBean.setOrder(1);
        return servletRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean interfaceAServletRegistration(){
        ServletRegistrationBean servletRegistrationBean=
                new ServletRegistrationBean(
                        new InterfaceA_EngineBasedServer(),"/yawl/ia/*"
                );
        servletRegistrationBean.setOrder(2);
        return servletRegistrationBean;
    }

    @Value("${jmx.rmi.host}")
    private String rmiHost;

    @Value("${jmx.rmi.port}")
    private Integer rmiPort;

    @Bean
    public RmiRegistryFactoryBean rmiRegistry() {
        final RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
        rmiRegistryFactoryBean.setPort(rmiPort);
        rmiRegistryFactoryBean.setAlwaysCreate(true);
        return rmiRegistryFactoryBean;
    }

    @Bean
    @DependsOn("rmiRegistry")
    public ConnectorServerFactoryBean connectorServerFactoryBean() throws Exception {
        final ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
        connectorServerFactoryBean.setObjectName("connector:name=rmi");
        connectorServerFactoryBean.setServiceUrl(String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", rmiHost, rmiPort, rmiHost, rmiPort));
        return connectorServerFactoryBean;
    }


}
