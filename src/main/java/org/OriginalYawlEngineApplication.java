package org;

import edu.sysu.ZookeeperRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.KafkaGaugeWriter;
import org.springframework.boot.actuate.metrics.writer.GaugeWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.yawlfoundation.yawl.engine.interfce.interfaceA.InterfaceA_EngineBasedServer;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EngineBasedServer;
import org.yawlfoundation.yawl.util.CharsetFilter;

/**
 * Created by root on 17-1-10.
 */
@SpringBootApplication
//@ComponentScan("org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceB_EnvironmentBasedServer")
public class OriginalYawlEngineApplication {
    public static void main(String[] args) {

        final ZookeeperRegister register=new ZookeeperRegister();
        register.run();

        SpringApplication.run(OriginalYawlEngineApplication.class, args);
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


    @Autowired
    private InterfaceB_EngineBasedServer b_engineBasedServer;


    @Bean
    public ServletRegistrationBean interfaceBServletRegistration( ){
        ServletRegistrationBean servletRegistrationBean=
                new ServletRegistrationBean(
                        b_engineBasedServer,"/yawl/ib/*"
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
