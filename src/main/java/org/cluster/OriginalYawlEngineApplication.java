package org.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.InfluxDBGaugeWriter;
import org.springframework.boot.actuate.metrics.KafkaGaugeWriter;
import org.springframework.boot.actuate.metrics.writer.GaugeWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.yawlfoundation.yawl.engine.interfce.interfaceA.InterfaceA_EngineBasedServer;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EngineBasedServer;
import org.yawlfoundation.yawl.util.CharsetFilter;

/**
 * Created by root on 17-1-10.
 */
@SpringBootApplication
public class OriginalYawlEngineApplication {
    private static final ZookeeperRegister register=new ZookeeperRegister();
    public static void main(String[] args) {

        SpringApplication.run(OriginalYawlEngineApplication.class, args);
    }

    @Bean
    public ApplicationRunner initRunner(){
        return new ApplicationRunner() {

            @Autowired
            private ZookeeperRegister register;

            @Override
            public void run(ApplicationArguments applicationArguments) throws Exception {
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



}
