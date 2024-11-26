package com.zlx.webservice.demo.service;


import lombok.extern.slf4j.Slf4j;
import javax.jws.WebService;


/**
 * 服务实现类
 * endpointInterface 端点接口：为父类全路径
 */
@Slf4j
@WebService(name = "MyWebService", targetNamespace = "http://service.demo.webservice.zlx.com/", endpointInterface = "com.zlx.webservice.demo.service.MyWebService")
public class MyWebServiceImpl implements MyWebService {

    /**
     * getInfo对应xmlweb:getInfo属性，匹配则调用，不匹配则无法调用
     * @param xmlString xmlString的值为对应xml标签内的内容
     * @return
     */
    @Override
    public String getInfo(String xmlString) {
        System.out.println("xmlString:" + xmlString);
        return xmlString;
    }
}
