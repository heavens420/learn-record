package com.zlx.webservice.demo.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


/**
 * name: 服务名称
 * targetNamespace: 命名空间 需与xml配置属性xmlns:web配置一致，不配置时，以子类配置为准，子类也不配置则自动生成默认值，子类优先级大于父类
 */

@WebService(name = "MyWebService",targetNamespace = "http://service.demo.webservice.zlx.com/")
public interface MyWebService {

    /**
     *
     * @param serviceName xmlString为参数名，对应xml同名标签 标签内的即为参数
     * @return
     */
    @WebMethod
    String getInfo(@WebParam(name = "xmlString") String serviceName);
}
