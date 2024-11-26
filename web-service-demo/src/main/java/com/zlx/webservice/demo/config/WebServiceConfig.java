package com.zlx.webservice.demo.config;


import com.zlx.webservice.demo.service.MyWebService;
import com.zlx.webservice.demo.service.MyWebServiceImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;


/**
 * 发布webservice地址
 */
@Configuration
public class WebServiceConfig {

    @Bean(name = "cxfServlet")
    public ServletRegistrationBean dispatcherServlet(){
        // 地址 /swlh-service/*
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new CXFServlet(), "/swlh-service/*");
        servletRegistrationBean.setName("webService");
        return servletRegistrationBean;
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public MyWebService getWebServiceImpl(){
        return new MyWebServiceImpl();
    }


    /**
     * 最终地址 /swlh-service/services
     * @return
     */
    @Bean
    public Endpoint endpoint(){
        EndpointImpl endpoint = new EndpointImpl(springBus(), getWebServiceImpl());
        endpoint.publish("/services");
        return endpoint;
    }

    /**
     * 请求报文示例：
     * xmlns:web="http://service.demo.webservice.zlx.com/"对应接口或实现类配置的targetNamespace
     * web:getInfo soapenv:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/ getInfo对应实现类方法名
     * xmlString xsi:type="soapenc:string" xmlString对应接口定义的参数名称 该标签内的内容为接口返回的参数
     */
    String demoXml = "<soapenv:Envelope\n" +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "    xmlns:web=\"http://service.demo.webservice.zlx.com/\">\n" +
            "    <soapenv:Header/>\n" +
            "    <soapenv:Body>\n" +
            "        <web:getInfo soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
            "            <xmlString xsi:type=\"soapenc:string\"\n" +
            "                xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
            "                <![CDATA[\n" +
            "                \n" +
            "                <?xml version=\"1.0\" encoding=\"gbk\"?><QueryWsInputInfo><Worksheet WsCode=\"800588057906\"><GeneralInfo><WsStatus>C</WsStatus><StatusTime>2024-11-23 09:56:35</StatusTime><ErrCode>0</ErrCode><WsMessage>成功</WsMessage><BASIP>220.177.232.11</BASIP><BASPort>Eth-Trunk32</BASPort><CustIP>117.40.195.117-117.40.195.117;240E:670:1401::E-240E:670:1401::E;117.40.195.114-117.40.195.114</CustIP><InterConnectIP></InterConnectIP><GWIP>117.40.195.1/25;;117.40.195.1/25</GWIP><IPNum>1</IPNum><SVlan>2521</SVlan><CVLAN>313</CVLAN><SlIPv6></SlIPv6><UserLineNo>10451659060</UserLineNo><BASName>JX-JDZ-GC-BAS-2.MAN.ME60-X16</BASName><BandWidth>100</BandWidth><IsShareGW>Y</IsShareGW><WwwDeny></WwwDeny><WwwPermitIP></WwwPermitIP><OperType>addip</OperType><CustUsingIP>117.40.195.117-117.40.195.117;240E:670:1401::E-240E:670:1401::E;117.40.195.114-117.40.195.114</CustUsingIP><ServPort>Eth-Trunk32.25210313</ServPort><BackFlag>0</BackFlag></GeneralInfo></Worksheet></QueryWsInputInfo>]]>\n" +
            "            \n" +
            "    </xmlString>\n" +
            "</web:getInfo>\n" +
            "</soapenv:Body>\n" +
            "</soapenv:Envelope>";
}
